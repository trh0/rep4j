package de.trho.rpi;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSslClient {

  private static final String MQTT_URL                 = "ssl://localhost:8883";

  private static final String clientId                 = "MQTT_SSL_JAVA_CLIENT";
  private static final String keyStoreFile             = "mqttclient.jks";
  private static final String JKS                      = "JKS";
  private static final String TLS                      = "TLS";
  private static final String CLIENT_KEYSTORE_PASSWORD = "password";
  private static final String CLIENT_KEY_PASSWORD      = "password";

  public static void main(String[] args) {

    try {

      URL ksUrl = MqttSslClient.class.getClassLoader().getResource(keyStoreFile);
      File ksFile = new File(ksUrl.toURI());
      URL tsUrl = MqttSslClient.class.getClassLoader().getResource(keyStoreFile);
      File tsFile = new File(tsUrl.toURI());

      TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      KeyStore trustStore = KeyStore.getInstance(JKS);

      trustStore.load(new FileInputStream(tsFile), CLIENT_KEYSTORE_PASSWORD.toCharArray());
      tmf.init(trustStore);
      KeyStore ks = KeyStore.getInstance(JKS);

      ks.load(new FileInputStream(ksFile), CLIENT_KEYSTORE_PASSWORD.toCharArray());
      KeyManagerFactory kmf =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, CLIENT_KEY_PASSWORD.toCharArray());

      KeyManager[] km = kmf.getKeyManagers();
      TrustManager[] tm = tmf.getTrustManagers();
      SSLContext sslContext = SSLContext.getInstance(TLS);
      sslContext.init(km, tm, null);

      MqttConnectOptions options = new MqttConnectOptions();
      options.setSocketFactory(sslContext.getSocketFactory());
      MqttAsyncClient client = new MqttAsyncClient(MQTT_URL, clientId);
      client.connect(options);
      Thread.sleep(3000);
      MqttMessage message = new MqttMessage();
      message.setPayload(
          "{\"key1\":\"value1\", \"key2\":true, \"key3\": 3.0, \"key4\": 4}".getBytes());
      client.publish("v1/devices/me/telemetry", message);
      client.disconnect();
      System.out.println("Disconnected");
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
