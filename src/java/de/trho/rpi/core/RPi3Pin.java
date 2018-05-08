package de.trho.rpi.core;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Maps the Raspberry 3b pins of wiringPi / pi4j to the actual pin.
 * 
 * @author trh0
 *
 */
//@formatter:off
public enum RPi3Pin {
  CE0(RaspiPin.GPIO_10),
  CE1(RaspiPin.GPIO_11),
  SDA0(RaspiPin.GPIO_30),
  SDA1(RaspiPin.GPIO_08),
  SCL0(RaspiPin.GPIO_31),
  SCL1(RaspiPin.GPIO_09),
  SCLK(RaspiPin.GPIO_14),
  MOSI(RaspiPin.GPIO_12),
  MISO(RaspiPin.GPIO_13),
  RXD0(RaspiPin.GPIO_16),
  TXD0(RaspiPin.GPIO_15),
  GPIO_04(RaspiPin.GPIO_07),
  GPIO_05(RaspiPin.GPIO_21),
  GPIO_06(RaspiPin.GPIO_22),
  GPIO_12(RaspiPin.GPIO_26),
  GPIO_13(RaspiPin.GPIO_23),
  GPIO_16(RaspiPin.GPIO_27),
  GPIO_17(RaspiPin.GPIO_00),
  GPIO_18(RaspiPin.GPIO_01),
  GPIO_19(RaspiPin.GPIO_24),
  GPIO_20(RaspiPin.GPIO_28),
  GPIO_21(RaspiPin.GPIO_29),
  GPIO_22(RaspiPin.GPIO_03),
  GPIO_23(RaspiPin.GPIO_04),
  GPIO_24(RaspiPin.GPIO_05),
  GPIO_26(RaspiPin.GPIO_25),
  GPIO_25(RaspiPin.GPIO_06),
  GPIO_27(RaspiPin.GPIO_02);
  //@formatter:on
  private final Pin pin;

  private RPi3Pin(final Pin pin) {
    this.pin = pin;

  }

  public String pinName() {
    return this.pin().getName();
  }

  public Pin pin() {
    return this.pin;
  }

  public int address() {
    return this.pin.getAddress();
  }
}
