package de.trho.rpi.com.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import de.trho.rpi.com.Packet;
import de.trho.rpi.com.PacketGateway;
import de.trho.rpi.com.PacketListener;

// TODO DEV
@SuppressWarnings({"resource", "unused"})
public class UDPController implements PacketGateway {

  private final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  private int                                   port, remotePort;
  private String                                remoteAddress;

  private DatagramSocket                        socket;

  public UDPController(int port, int remotePort, String remoteAddress) {
    super();
    this.port = port;
    this.remotePort = remotePort;
    this.remoteAddress = remoteAddress;
    init();
  }

  private void init() {
    try {
      DatagramSocket sock = new DatagramSocket(port);
      DatagramPacket p = new DatagramPacket(null, 1);

      sock.connect(new InetSocketAddress(InetAddress.getByName(remoteAddress), remotePort));
    } catch (Exception e) {
    }

  }

  @Override
  public void addListener(PacketListener listener) {

  }

  @Override
  public void removeListener(PacketListener listener) {

  }

  @Override
  public void sendPacket(Packet packet) {

  }

  @Override
  public State getState() {
    return null;
  }

  @Override
  public void shutdown() {

  }

}
