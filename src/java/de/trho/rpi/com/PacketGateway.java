package de.trho.rpi.com;

/**
 * Object to send packets with and receive packets from.
 * 
 * @author trh0
 *
 */
public interface PacketGateway {
  /**
   * Indicating the Gateway's state
   * 
   * @author trh0
   *
   */
  public static enum State {
    SHUTDOWN(-1), UNCONNECTED(0), CONNECTING(1), CONNECTED_ERROR(2), CONNECTED_BAD(3), CONNECTED(
        4), CONNECTED_GOOD(5);
    private final int lvl;

    private State(int lvl) {
      this.lvl = lvl;
    }

    public boolean betterOr(State state) {
      return this.lvl >= state.lvl;
    }
  }

  /**
   * 
   * @param listener The listener to notify whenever a packet was received
   */
  void addListener(PacketListener listener);

  /**
   * 
   * @param listener the listener to remove from observer
   */
  void removeListener(PacketListener listener);

  /**
   * providing a gateway to send packets
   * 
   * @param packet the packet to send to defined targets
   */
  void sendPacket(Packet packet);

  State getState();

  void shutdown();
}
