package de.trho.rpi.com;

import java.util.Collection;
import de.trho.rpi.com.Packet;

public interface PacketListener {

  Collection<String> getIntrests();

  void packetArrived(Packet packet);
}
