package de.trho.rpi.examples;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Shift;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.KeyInputListener;
import de.trho.rpi.core.RPi3Pin;
import de.trho.rpi.core.Application;

@SuppressWarnings("unused")
public class ShiftRegisterModule extends AbstractAppModule implements KeyInputListener {

  private static final RPi3Pin       DataPin  = RPi3Pin.GPIO_17;
  private static final RPi3Pin       LatchPin = RPi3Pin.GPIO_27;
  private static final RPi3Pin       ClockPin = RPi3Pin.GPIO_22;

  private static final int[]         LEDS     = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

  private final GpioController       gpio;
  private final GpioPinDigitalOutput data;
  private final GpioPinDigitalOutput latch;
  private final GpioPinDigitalOutput clock;

  private volatile int[]             input    = {};
  private volatile int               wait;

  public ShiftRegisterModule(int wait) {
    super(RunMode.LOOP);
    this.wait = wait;
    this.setScheduleRate(wait);
    gpio = GpioFactory.getInstance();
    data = gpio.provisionDigitalOutputPin(DataPin.pin());
    latch = gpio.provisionDigitalOutputPin(LatchPin.pin());
    clock = gpio.provisionDigitalOutputPin(ClockPin.pin());
    assert (data != null);
    assert (latch != null);
    assert (clock != null);

    super.getController().subscribeInput(this);
  }

  int idx = 0;

  @Override
  public void run() {
    this.setRunning(true);
    while (this.isRunning()) {
      byte x = Byte.MAX_VALUE;
      for (int i = 0; i < codes.length; i++) {
        try {
          latch.low();
          Shift.shiftOut((byte) data.getPin().getAddress(), (byte) clock.getPin().getAddress(),
              (byte) 1, codes[i]);
          Shift.shiftOut((byte) data.getPin().getAddress(), (byte) clock.getPin().getAddress(),
              (byte) 1, (byte) ~x);
          latch.high();
          Thread.sleep(wait);
          if (i > 0 && i + 1 % 8 == 0) {
            Thread.sleep(3 * wait);
            x >>= 1;
          }
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
      // printState();
      idx = idx < LEDS.length - 1 ? idx + 1 : 0;
    }
    this.setRunning(false);
  }

  @Override
  public void exit() {
    super.exit();
    latch();
    gpio.shutdown();
    super.getController().unsubscribeInput(this);
  }

  /**
   * shift in a new bit
   *
   * @param val if true, the shifted bit will be HIGH, else LOW
   */
  private void shift(boolean val) {
    PinState state = val ? PinState.HIGH : PinState.LOW;
    data.setState(state);
    clock.high();
    clock.low();
    data.low();
  }

  /**
   * write the current memory register state to outputs and sleep
   *
   * @param ms time to sleep after writing in ms
   */
  private void latch(int ms) {
    latch.high();
    latch.low();
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

  private void printState() {
    System.out.printf("DATA:%s,", data.getState());
    System.out.printf("CLOCK:%s,", clock.getState());
    System.out.printf("LATCH:%s\n", latch.getState());
  }

  @Override
  public void notifyInput(byte[] input) {
    this.input = new int[input.length];
    System.out.printf("%s\n|> ", new String(input));
    for (int i = 0; i < input.length; i++) {
      byte b = input[i];
      this.input[i] = Byte.toUnsignedInt(b);
      System.out.printf("%s|", Integer.toBinaryString(this.input[i]));
    }
    System.out.print("\n");
  }

  final byte[] codes = {0x00, 0x00, 0x3E, 0x41, 0x41, 0x3E, 0x00, 0x00, 0x00, 0x00, 0x21, 0x7F,
      0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x23, 0x45, 0x49, 0x31, 0x00, 0x00, 0x00, 0x00, 0x22,
      0x49, 0x49, 0x36, 0x00, 0x00, 0x00, 0x00, 0x0E, 0x32, 0x7F, 0x02, 0x00, 0x00, 0x00, 0x00,
      0x79, 0x49, 0x49, 0x46, 0x00, 0x00, 0x00, 0x00, 0x3E, 0x49, 0x49, 0x26, 0x00, 0x00, 0x00,
      0x00, 0x60, 0x47, 0x48, 0x70, 0x00, 0x00, 0x00, 0x00, 0x36, 0x49, 0x49, 0x36, 0x00, 0x00,
      0x00, 0x00, 0x32, 0x49, 0x49, 0x3E, 0x00, 0x00, 0x00, 0x00, 0x3F, 0x44, 0x44, 0x3F, 0x00,
      0x00, 0x00, 0x00, 0x7F, 0x49, 0x49, 0x36, 0x00, 0x00, 0x00, 0x00, 0x3E, 0x41, 0x41, 0x22,
      0x00, 0x00, 0x00, 0x00, 0x7F, 0x41, 0x41, 0x3E, 0x00, 0x00, 0x00, 0x00, 0x7F, 0x49, 0x49,
      0x41, 0x00, 0x00, 0x00, 0x00, 0x7F, 0x48, 0x48, 0x40, 0x00, 0x00};
}
