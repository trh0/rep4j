package de.trho.rpi.com;

/**
 * Interface to construct data packets no matter what technology is used to submit them.
 * 
 * @author trh0
 *
 */
public interface Packet {
  /**
   * 
   * @return 0 - Assuming there is no offset and all of the provided data shall be sent completely.
   */
  default int getOffset() {
    return 0;
  }

  /**
   * 
   * @return The payload's length - if there is any. Assuming there all of the provided data shall
   *         be sent completely.
   */
  default int getLength() {
    final byte[] b = this.getPayload();
    return b != null ? b.length : -1;
  }

  /**
   * 
   * @param receiver Depending on the way this packet shall be sent either
   *        <ul>
   *        <b>UDP</b>
   *        <li>a String like 'host:port'</li> <b>MQTT</b>
   *        <li>a String like '/topic/suptopic/etc'</li>
   *        </ul>
   */
  void addTarget(String target);

  /**
   * 
   * @return An unmodifiable java.util.Collection of Strings- all targets registered for this
   *         package
   */
  java.util.Collection<String> getTargets();

  /**
   * 
   * @return This Packets payload as an array of bytes.
   */
  byte[] getPayload();

  /**
   * @param data An array of bytes that will be referenced to as the packets payload.
   */
  void setPayload(byte[] data);

  /**
   * 
   * @return true if the packet shall be resent if the connection was not available on submission.
   */
  boolean getRetrySending();

  /**
   * Set whether or not the packet shall be cached by the sender until a connection is avaiable to
   * send it.
   */
  void setRetrySending(boolean b);
  /**
   * For smooth access to the Packet.
   * 
   * @author trh0
   *
   */
  public static class Builder {

    private final Packet packet;

    public Builder() {
      this.packet = new Packet() {

        private final java.util.Collection<String> targets =
            java.util.Collections.synchronizedCollection(new java.util.ArrayList<>());
        private byte[]                             data;
        private boolean                            retry;

        @Override
        public synchronized void addTarget(String receiver) {
          if (receiver != null && !this.targets.contains(receiver))
            this.targets.add(receiver);
        }

        @Override
        public java.util.Collection<String> getTargets() {
          return java.util.Collections.unmodifiableCollection(this.targets);
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
        public boolean getRetrySending() {
          return retry;
        }

        @Override
        public void setRetrySending(boolean b) {
          this.retry = b;
        }

        @Override
        public String toString() {
          return "Packet[payload=" + new String(getPayload()) + ",targets="
              + java.util.Arrays.deepToString(getTargets().toArray()) + "]";
        }

      };
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
     * @return The freshly build packet.
     */
    public Packet build() {
      return this.packet;
    }

    /**
     * 
     * @param addresses Some targets to send the package to.
     * @return The builder to continue building.
     */
    public Builder withTargets(String... targets) {
      if (targets != null)
        java.util.Arrays.stream(targets).forEach(this.packet::addTarget);
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
