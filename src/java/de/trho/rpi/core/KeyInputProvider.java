package de.trho.rpi.core;

public interface KeyInputProvider {

  void subscribeInput(KeyInputListener listener);

  void unsubscribeInput(KeyInputListener listener);

}
