package de.trho.rpi.core;

import de.trho.rpi.com.PacketGateway;

public interface CoreController extends KeyInputProvider {

  void addShutdownHook(Runnable r);

  PacketGateway getPacketGateway();

  void addModule(AppModule module);

  void removeModule(AppModule module);

}
