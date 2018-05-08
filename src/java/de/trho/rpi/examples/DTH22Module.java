package de.trho.rpi.examples;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import com.pi4j.wiringpi.Gpio;
import de.trho.rpi.com.Packet;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.RPi3Pin;
import de.trho.rpi.gear.DHT22;
import de.trho.rpi.util.Config;

public class DTH22Module extends AbstractAppModule {

  public DTH22Module(String sensorName) {
    super(RunMode.REPEAT);
    this.dht = new DHT22(PIN);
    this.setScheduleRate(3 * MAXREADTIME);
    this.sensorName = sensorName;
    if (Gpio.wiringPiSetup() == -1) {
      logger.error("WiringPi setup failed");
      return;
    }
    logger.debug("Initialized DTH22 Module");

  }

  private static final int PIN = RPi3Pin.GPIO_17.address(), MAXREADTIME = 10000;
  private DHT22            dht;
  private String           sensorName;

  @Override
  public void run() {
    if (this.isRunning())
      return;
    this.setRunning(true);
    long start = Gpio.millis();
    try {
      while (dht.readData() == 0 && Gpio.millis() < start + MAXREADTIME) {
        Thread.sleep(1000);
      }
      byte[] bytes = String.valueOf(dht.getLastReading()).getBytes();
      Files.write(new File(Config.getValue("data.outpath") + "humid_temperature.out").toPath(),
          bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
      getController().getPacketGateway()
          .sendPacket(new Packet.Builder().withPayload(bytes)
              .withTargets(Config.getValue("mqtt.topic.data") + "/" + sensorName).withRetry(true)
              .build());
      logger.info(bytes);
    } catch (Exception e) {
      logger.error(e);
    }
    this.setRunning(false);
  }

}
