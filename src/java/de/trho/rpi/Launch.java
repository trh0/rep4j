package de.trho.rpi;

import com.pi4j.wiringpi.Gpio;
import de.trho.rpi.core.AppModule;
import de.trho.rpi.core.Application;
import de.trho.rpi.core.RPi3Pin;
import de.trho.rpi.examples.DTH22Module;

public class Launch {

  public static void main(String[] args) {
    Application app;
    AppModule module;
    Gpio.pinMode(RPi3Pin.GPIO_17.address(), Gpio.OUTPUT);
    Gpio.digitalWrite(RPi3Pin.GPIO_17.address(), Gpio.HIGH);
    app = new Application();
    module = new DTH22Module("liv");
    app.addModule(module);
  }

}
