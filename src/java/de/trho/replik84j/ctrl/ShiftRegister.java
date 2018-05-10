package de.trho.replik84j.ctrl;

import static com.pi4j.wiringpi.Gpio.*;
import static com.pi4j.wiringpi.Shift.MSBFIRST;
import static com.pi4j.wiringpi.Shift.LSBFIRST;

/**
 * 
 * @author trh0
 *
 */
public class ShiftRegister {

  private final int DATA, CLOCK, LATCH, RESET;
  private int       latchDelay, bits, order = MSBFIRST;

  public ShiftRegister(int latchDelay, int bits, int order, int dataPin, int clockPin, int latchPin,
      int reset) {
    super();
    this.bits = bits;
    DATA = dataPin;
    CLOCK = clockPin;
    LATCH = latchPin;
    RESET = reset;
    this.latchDelay = latchDelay;
    this.order = order;

    if (wiringPiSetup() != 0) {
      throw new RuntimeException("WiringPI setup failed.");
    }

    pinMode(DATA, OUTPUT);
    pinMode(LATCH, OUTPUT);
    pinMode(CLOCK, OUTPUT);
    pinMode(reset, OUTPUT);
    digitalWrite(reset, HIGH);
  }

  /**
   * 
   * @param millis The latch hibernation
   */
  public void setLatchDelay(int millis) {
    if (millis < 0) {
      throw new IllegalArgumentException("Negative millis");
    }
    this.latchDelay = millis;
  }

  /**
   * 
   * @return The latch hibernation
   */
  public int getLatchDelay() {
    return latchDelay;
  }

  /**
   * 
   * @return The defined resolution / register size to shift values into
   */
  public int getBits() {
    return bits;
  }

  /**
   * 
   * @param bits The resolution / register size to shift values into
   */
  public void setBits(int bits) {
    if (bits < 4) {
      throw new IllegalArgumentException("Cmon...at least 4 bits...Gave " + bits);
    }
    this.bits = bits;
  }

  /**
   * Write a value (the first x bits, depending on what you set {@linkplain #setBits(int)}) to the
   * register
   * 
   * @param val The x Bit value matching the value set here {@linkplain #setBits(int)}
   */

  public void write(int val) {
    // String bitsStr = "";
    // bitsStr += ((val >> i) & 1) == 1 ? 1 : 0;
    // bitsStr += ((val >> i) & 1) == 1 ? 1 : 0;
    if (order == LSBFIRST) {
      for (int i = 0; i < bits; ++i) {
        shift(((val >> i) & 1) == 1);
      }
    } else {
      for (int i = bits - 1; i >= 0; --i) {
        shift(((val >> i) & 1) == 1);
      }
    }
    // System.out.printf();("%s %s", order == MSBFIRST ? "MSB" : "LSB", bits);
    latch();
  }

  /**
   * Reset the shift register.
   */
  public void reset() {
    digitalWrite(RESET, LOW);
    delay(latchDelay * 2);
    digitalWrite(RESET, HIGH);
  }

  /**
   * Shift in a new bit
   *
   * @param val HIGH or LOW
   */
  private void shift(boolean val) {
    digitalWrite(DATA, val ? HIGH : LOW);
    digitalWrite(CLOCK, HIGH);
    digitalWrite(CLOCK, LOW);
    digitalWrite(DATA, LOW);
  }

  /**
   * Store / copy / write the current register state to registers outputs
   *
   * @param ms latchTime = hibernation
   */
  private void latch(int ms) {
    digitalWrite(LATCH, HIGH);
    digitalWrite(LATCH, LOW);
    delay(ms);
  }

  /**
   * Store / copy / write the current register state to registers outputs.
   */
  private void latch() {
    latch(this.latchDelay);
  }

}
