package de.trho.rpi.examples;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.RPi3Pin;

public class ButtonModule extends AbstractAppModule {

  public ButtonModule() {
    super(RunMode.ONCE);
  }

  private long    last = Gpio.millis(), diff = 250;
  private boolean led  = false;

  @Override
  public void run() {
    final GpioController gpio = GpioFactory.getInstance();
    GpioPinDigitalOutput ledPin =
        gpio.provisionDigitalOutputPin(RPi3Pin.GPIO_17.pin(), PinState.LOW);
    GpioPinDigitalInput btnPin = gpio.provisionDigitalInputPin(RPi3Pin.GPIO_18.pin());
    Gpio.pullUpDnControl(btnPin.getPin().getAddress(), Gpio.PUD_UP);

    btnPin.addListener((GpioPinListenerDigital) event -> {
      if (pushDone()) {
        led = !led;
        if (led)
          ledPin.high();
        else
          ledPin.low();
      }
    });
  }

  private boolean pushDone() {
    long now = Gpio.millis();
    boolean b = now - last > diff;
    last = now;
    return b;
  }

}
