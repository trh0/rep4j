package de.trho.rpi;

import de.trho.rpi.com.Packet;
import de.trho.rpi.core.Application;

@SuppressWarnings("unused")
public class TestMQTTSSLConnection {
  private static final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(TestMQTTSSLConnection.class);

  void main() {

    Application app = new Application();
    app.getPacketGateway().sendPacket(new Packet.Builder()
        .withPayload("TestMQTTSSLConnection".getBytes()).withTargets("/test").build());
  }
}
