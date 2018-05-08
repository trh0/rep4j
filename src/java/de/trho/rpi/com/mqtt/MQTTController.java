package de.trho.rpi.com.mqtt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;
import de.trho.rpi.com.Packet;
import de.trho.rpi.com.PacketGateway;
import de.trho.rpi.com.PacketListener;
import de.trho.rpi.util.Config;
import de.trho.rpi.util.SecureSocketFactoryBuilder;

@SuppressWarnings("unused")
public class MQTTController implements PacketGateway, MqttCallback, IMqttActionListener {

  private final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  /**
   * 
   * @param is The input stream to read into a byte[]
   * @return All bytes read from the input stream
   * @throws IOException
   */
  public static byte[] toByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] b = new byte[1024];
    int n = 0;
    while (-1 != (n = is.read(b))) {
      output.write(b, 0, n);
    }
    return output.toByteArray();
  }

  private InputStream stream(String res) {
    return MQTTController.class.getClassLoader().getResourceAsStream(res);
  }

  private static int                       QOS         = 2, NAMESEED = 1337 << 4;

  // TODO confg
  private String                           OutCertFile =
      (System.getProperty("java.io.tmpdir") + Base64.encode(String.valueOf(NAMESEED)));

  private State                            state;
  private MqttAsyncClient                  client;
  private IMqttToken                       connectToken;
  private final ScheduledExecutorService   exec;
  private final Collection<PacketListener> packetListeners;
  private int                              qualityCount;

  /**
   * 
   */
  public MQTTController() {
    this.packetListeners = Collections.synchronizedCollection(new ArrayList<>());
    this.state = State.UNCONNECTED;
    this.exec = Executors.newScheduledThreadPool(1);
    QOS = Integer.parseInt(Config.getValue("mqtt.qos"));

    try {
      initClient(Config.getValue("mqtt.broker"), Config.getValue("mqtt.clientid"),
          stream(Config.getValue("mqtt.certificate.file")));
    } catch (Exception e) {
      logger.error("Can not init client", e);
    }
  }

  /**
   * 
   * @param uri
   * @param clientId
   * @param keyFileData
   * @throws Exception Either
   *         <ul>
   *         <li>an MQTTException (instantiating MQTTClient gone wrong),</li>
   *         <li>an IOException (Unable to create certificate tempfile)</li>
   *         <li>or an untyped Exception (Can not create SecureSocketFactory with given
   *         certificate)</li>
   *         </ul>
   */
  public void initClient(String uri, String clientId, InputStream keyFileData) throws Exception {
    try {
      client = new MqttAsyncClient(uri, clientId);
      logger.debug("Created MQTT client object for {} with id {}", uri, clientId);
    } catch (MqttException e) {
      logger.error("Unable to instantiate MQTT client with given URI and cliendId", e);
      throw e;
    }
    try {
      Files.write(new File(OutCertFile).toPath(), toByteArray(keyFileData),
          StandardOpenOption.WRITE, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      logger.debug("Stored certificate tempfile to {}", OutCertFile);
    } catch (java.io.IOException e) {
      logger.error("Unable to store given bytes of the certificate to temporary file", e);
      throw e;
    }

    final MqttConnectOptions conOps = new MqttConnectOptions();
    conOps.setWill(Config.getValue("mqtt.lastwill.topic"),
        new java.util.Date().toString().getBytes(), QOS, false);
    conOps.setCleanSession(Boolean.parseBoolean(Config.getValue("mqtt.clean")));
    conOps.setKeepAliveInterval(Integer.parseInt(Config.getValue("mqtt.keepalive")));
    conOps.setConnectionTimeout(Integer.parseInt(Config.getValue("mqtt.timeout")));
    conOps.setAutomaticReconnect(Boolean.parseBoolean(Config.getValue("mqtt.keepalive")));
    conOps.setMqttVersion(Integer.parseInt(Config.getValue("mqtt.version")));
    conOps.setUserName(Config.getValue("mqtt.username"));
    conOps.setPassword(Config.getValue("mqtt.password").toCharArray());
    try {
      SSLSocketFactory fac = SecureSocketFactoryBuilder.getSocketFactory("TLSv1", OutCertFile);
      conOps.setSocketFactory(fac);
      logger.debug("Created SecureSocketFactory {}", fac);
    } catch (Exception e) {
      logger.error("Unable to create socketfactory", e);
      throw e;
    }

    try {
      this.connectToken = client.connect(conOps);
      logger.debug("Requested connection with connection options {}, {}", conOps,
          client.isConnected());
    } catch (Exception e) {
      logger.error("Unable to connect mqtt client", e);
      throw e;
    }
    this.state = State.CONNECTING;
    exec.execute(() -> {
      while (!this.client.isConnected()) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
        }
      }
      state = State.CONNECTED;
    });
  }

  @Override
  public void addListener(PacketListener l) {
    if (!this.packetListeners.contains(l)) {
      this.packetListeners.add(l);
      l.getIntrests().forEach(t -> {
        try {
          client.subscribe(t, QOS, (topic, message) -> {
            l.packetArrived(new Packet.Builder().withPayload(message.getPayload()).withRetry(false)
                .withTargets(topic).build());
            logger.debug("Subscribed topic {}", topic);
          });
        } catch (MqttException e) {
          logger.error("Unable to subscribe topic", e);
        }
      });
    }
  }

  @Override
  public void removeListener(PacketListener l) {
    this.packetListeners.remove(l);
    l.getIntrests().forEach(intrest -> {
      if (!this.packetListeners.stream().filter(li -> li.getIntrests().contains(intrest))
          .findFirst().isPresent()) {
        try {
          client.unsubscribe(intrest);
          logger.debug("Unsubscribed topic {}", intrest);
        } catch (MqttException e) {
          logger.error("Unable to unsubscribe topic", e);
        }
      }
    });
  }

  @Override
  public void sendPacket(final Packet p) {
    logger.info("Packet-send requested");
    Runnable send = () -> {
      logger.info("Sending packet with qualityIndicator {} {}", qualityCount, p);
      p.getTargets().forEach(t -> {
        try {
          this.client.publish(t, p.getPayload(), QOS, false, null, this);
          qualityCount++;
        } catch (MqttException e) {
          logger.error("Unable to send mqtt message", e);
        }
      });
    };

    if (this.state == State.CONNECTING) {
      logger.info("Connection not ready - waiting for connection.");
      exec.execute(() -> {
        while (state.equals(State.CONNECTING)) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
          }
        }
        this.exec.execute(send);
      });
    } else if (this.state.betterOr(State.CONNECTED)) {
      this.exec.execute(send);
    } else {
      logger.warn("No connection - packet cached.");
    }
  }

  @Override
  public void connectionLost(Throwable t) {
    logger.warn("Connection lost", t);
    this.state = State.CONNECTED_BAD;
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken tkn) {
    logger.info("Delivery complete {}", ReflectionToStringBuilder.toString(tkn));
  }

  @Override
  public void messageArrived(String top, MqttMessage msg) throws Exception {

  }

  @Override
  public void onSuccess(IMqttToken tkn) {
    logger.info("Submission success {}", ReflectionToStringBuilder.toString(tkn));
    qualityCount--;
  }

  @Override
  public void onFailure(IMqttToken tkn, Throwable ex) {
    logger.warn("Submission failed {} {}", ex, ReflectionToStringBuilder.toString(tkn));
  }

  @Override
  public void shutdown() {
    try {
      this.exec.shutdown();
      this.client.disconnect();
    } catch (MqttException e) {
      logger.error("Error disconnecting client", e);
    }
  }

  @Override
  public State getState() {
    return state;
  }
}
