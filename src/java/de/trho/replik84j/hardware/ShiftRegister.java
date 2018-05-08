package de.trho.replik84j.hardware;

@SuppressWarnings("unused")
public class ShiftRegister {

  private final int resolution, DATA, CLOCK, LATCH;

  public ShiftRegister(int resolution, int dataPin, int clockPin, int latchPin) {
    super();
    this.resolution = resolution;
    DATA = dataPin;
    CLOCK = clockPin;
    LATCH = latchPin;
  }

}
