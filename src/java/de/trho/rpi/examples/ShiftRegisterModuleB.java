package de.trho.rpi.examples;

import static com.pi4j.wiringpi.Gpio.*;
import java.util.Random;
import java.util.regex.Pattern;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Shift;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.KeyInputListener;
import de.trho.rpi.core.RPi3Pin;

@SuppressWarnings("unused")
public class ShiftRegisterModuleB extends AbstractAppModule implements KeyInputListener {

  private final byte       data, latch, clock, reset;

  private volatile int     wait, g, totalShifted, lsb = Shift.LSBFIRST, mode = 0;
  private volatile boolean hold;

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

  private static final int[] GUESSES = {0b10000000, 0b10100000, 0b00001000, 0b00001100, 0b11001100,
      0b00110011, 0b00110000, 0b00001111};

  @Override
  public void run() {
    this.setRunning(true);
    getController().subscribeInput(this);
    while (this.isRunning()) {
      try {
        while (hold) {
          Thread.sleep(1000);
        }
        if (mode == 0) {
          shiftModeA();
        } else {
          shiftModeB();
        }
      } catch (InterruptedException e) {
        super.logger.error(e);
      }
    }
    this.setRunning(false);
  }

  private void shiftModeB() {
    hold = true;
    logger.info("jbyte({} dual:{} hex:{}) total: {}", g, Integer.toBinaryString(g),
        Integer.toHexString(g), Integer.toBinaryString(g), Integer.toHexString(totalShifted));
    write(g);
  }

  private void shiftModeA() {
    byte x;
    hold = true;
    x = (byte) g;
    digitalWrite(latch, LOW);
    Shift.shiftOut(data, clock, (byte) 0, x);
    digitalWrite(latch, HIGH);
    logger.info("jbyte({}/{} dual:{} hex:{}) total: {}/{}", x, g, Integer.toBinaryString(x),
        Integer.toHexString(x), Integer.toBinaryString(totalShifted),
        Integer.toHexString(totalShifted));
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
    String bits = "";
    if (lsb == Shift.LSBFIRST) {
      for (int i = 0; i < 16; ++i) {
        shift(((val >> i) & 1) == 1);
        bits += ((val >> i) & 1) == 1 ? 1 : 0;
      } /*  */
    } else {
      for (int i = 15; i >= 0; --i) {
        shift(((val >> i) & 1) == 1);
        bits += ((val >> i) & 1) == 1 ? 1 : 0;
      }
    }
    logger.debug("Wrote {}", bits);
    // and latch it (i.e. output it)
    latch();
  }
  private volatile String now = "b";

  @Override
  public void notifyInput(final byte[] input) {
    now = new String(input);
    if (Pattern.matches("nulls", now)) {
      write(0);
    } else if (Pattern.matches("mode.a(.+)?", now)) {
      logger.info("Shift Mode: A");
      mode = 0;
    } else if (Pattern.matches("mode.b(.+)?", now)) {
      logger.info("Shift Mode: B");
      mode = 1;
    } else if (Pattern.matches("lsb(.+)?", now)) {
      logger.info("LSB FIRST");
      lsb = Shift.LSBFIRST;
    } else if (Pattern.matches("msb(.+)?", now)) {
      logger.info("MSB FIRST");
      lsb = Shift.MSBFIRST;
    } else if (Pattern.matches("clear(.+)?", now)) {
      logger.info("Clearing...");
      totalShifted = 0;
      digitalWrite(reset, LOW);
      delay(500);
      digitalWrite(reset, HIGH);
    }
    if (Pattern.matches("^[01]{16}(.+)?", now)) {
      hold = false;
      g = Integer.parseInt(now.substring(0, 8), 2);
      totalShifted += g;
    } else if (Pattern.matches("^0x[0-9ABCEDF]{4}(.+)?", now)) {
      hold = false;
      g = Integer.parseInt(now.substring(2, 6), 16);
      totalShifted += g;
    } else {
      hold = true;
    }
    now = "";
  }
  // 11111111
  // 00000000
  // 10101010
}
