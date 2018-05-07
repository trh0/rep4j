package de.trho.rpi.com.udp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import de.trho.rpi.com.Packet;

/**
 * Interface enabling DatagramPackets (to be sent - and received - by DatagramSockets) and
 * constructs built around them to define multiple targets.
 * 
 * @author trh0
 *
 */
public interface UDPPacket extends Packet {
  /**
   * 
   * @return 0 - Assuming there is no offset and all of the provided data shall be sent completely.
   */
  @Override
  default int getOffset() {
    return 0;
  }

  /**
   * 
   * @return The payload's length - if there is any. Assuming there all of the provided data shall
   *         be sent completely.
   */
  @Override
  default int getLength() {
    final byte[] b = this.getPayload();
    return b != null ? b.length : -1;
  }

  /**
   * 
   * @param receiver For the usage together with a DatagramPacket constructed from this Packet
   */
  void addReceiver(SocketAddress receiver);

  /**
   * 
   * @return An unmodifiable Collection of SocketAdress instances - all receivers registered for
   *         this package
   */
  Collection<SocketAddress> getReceivers();

  /**
   * For smooth access to the UDPPacket.
   * 
   * @author trh0
   *
   */
  public static class Builder {

    private final UDPPacket packet;

    public Builder() {
      this.packet = new UDPPacket() {

        private final Collection<SocketAddress> receivers =
            Collections.synchronizedCollection(new ArrayList<>());
        private byte[]                          data;
        private boolean                         retry;

        @Override
        public synchronized void addReceiver(SocketAddress receiver) {
          if (receiver != null && !this.receivers.contains(receiver))
            this.receivers.add(receiver);
        }

        @Override
        public Collection<SocketAddress> getReceivers() {
          return Collections.unmodifiableCollection(this.receivers);
        }

        @Override
        public synchronized byte[] getPayload() {
          return this.data;
        }

        @Override
        public synchronized void setPayload(byte[] data) {
          synchronized (data) {
            this.data = data;
          }
        }

        @Override
        public Collection<String> getTargets() {
          return Collections.unmodifiableCollection(
              this.receivers.stream().map(m -> m.toString()).collect(Collectors.toList()));
        }

        @Override
        public void addTarget(String target) throws IllegalArgumentException {
          try {
            String[] hostport = target.split(Pattern.quote(":"));
            this.receivers.add(new InetSocketAddress(hostport[0], Integer.parseInt(hostport[1])));
          } catch (Exception e) {
            throw new IllegalArgumentException(
                "Malformed input. String should be format '<host>:<port>'", e);
          }
        }

        @Override
        public boolean getRetrySending() {
          return retry;
        }

        @Override
        public void setRetrySending(boolean b) {
          this.retry = b;
        }

      };
    }

    /**
     * @return The freshly build packet.
     */
    public UDPPacket build() {
      return this.packet;
    }

    /**
     * 
     * @param retry Shall the packet be cached until a the PacketGateway can send it?
     * @return The builder to continue building.
     */
    public Builder withRetry(boolean retry) {
      this.packet.setRetrySending(retry);
      return this;
    }

    /**
     * 
     * @param addresses Some targets to send the package to.
     * @return The builder to continue building.
     */
    public Builder withTargets(SocketAddress... addresses) {
      if (addresses != null)
        Arrays.stream(addresses).forEach(this.packet::addReceiver);
      return this;
    }

    /**
     * @param payload Some data.
     * @return The builder to continue building.
     */
    public Builder withPayload(byte[] payload) {
      if (payload != null)
        this.packet.setPayload(payload);
      return this;
    }
  }

}
