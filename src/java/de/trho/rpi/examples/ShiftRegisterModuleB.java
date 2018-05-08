package de.trho.rpi.examples;

import static com.pi4j.wiringpi.Gpio.*;
import java.util.Random;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Shift;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.KeyInputListener;
import de.trho.rpi.core.RPi3Pin;

@SuppressWarnings("unused")
public class ShiftRegisterModuleB extends AbstractAppModule implements KeyInputListener {

  private final byte   data, latch, clock, reset;

  private volatile int wait;

  public ShiftRegisterModuleB(int wait, int data, int latch, int clock, int reset) {
    super(RunMode.LOOP);
    this.wait = wait;
    this.reset = (byte) reset;
    this.data = (byte) data;
    this.latch = (byte) latch;
    this.clock = (byte) clock;
    if (wiringPiSetup() != 0) {
      logger.error("WiringPI setup failed.");
    } else {
      logger.info("Wiring PI setup completed.");
    }
    pinMode(data, OUTPUT);
    pinMode(latch, OUTPUT);
    pinMode(clock, OUTPUT);
    pinMode(reset, OUTPUT);
    digitalWrite(reset, HIGH);
  }

  private static final int[] VALS = {0x01, 0x02, 0x04, 0x08, 0x0F, 0x20, 0x40, 0x80, 0xFF};

  @Override
  public void run() {
    this.setRunning(true);
    while (this.isRunning()) {
      byte x = 0x01;
      for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
        try {
          digitalWrite(latch, LOW);
          Shift.shiftOut(data, clock, (byte) 1, x);
          digitalWrite(latch, HIGH);
          Thread.sleep(wait);
          logger.info("Shifting {} {} {}", x, Integer.toBinaryString(x), Integer.toHexString(x));
        } catch (InterruptedException e) {
          super.logger.error(e);
        }
      }
      digitalWrite(reset, LOW);
      delay(50);
      digitalWrite(reset, HIGH);
      for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
        try {
          digitalWrite(latch, LOW);
          x = i;
          Shift.shiftOut(data, clock, (byte) 1, i);
          digitalWrite(latch, HIGH);
          Thread.sleep(wait);
          logger.info("Shifting {} {} {}", x, Integer.toBinaryString(x), Integer.toHexString(x));
        } catch (InterruptedException e) {
          super.logger.error(e);
        }
      }
    }
    this.setRunning(false);
  }

  @Override
  public void exit() {
    super.exit();
    latch();
    super.getController().unsubscribeInput(this);
  }

  /**
   * shift in a new bit
   *
   * @param val if true, the shifted bit will be HIGH, else LOW
   */
  private void shift(boolean val) {
    digitalWrite(data, val ? HIGH : LOW);
    digitalWrite(clock, HIGH);
    digitalWrite(clock, LOW);
    digitalWrite(data, LOW);
  }

  /**
   * write the current memory register state to outputs and sleep
   *
   * @param ms time to sleep after writing in ms
   */
  private void latch(int ms) {
    digitalWrite(latch, HIGH);
    digitalWrite(latch, LOW);
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      System.err.println(e);
      exit();
    }
  }

  /**
   * write with default SLEEP_MS sleep
   */
  private void latch() {
    latch(this.wait);
  }

  /**
   * write a value to the register and latch it**
   * 
   * @param val the value to write
   */

  private void write(int val) {
    // write each bit of val to register
    for (int i = 0; i < 8; i++) {
      shift(((val >> i) & 1) == 1);
    }
    // and latch it (i.e. output it)
    latch();
  }

  @Override
  public void notifyInput(byte[] input) {}

}
