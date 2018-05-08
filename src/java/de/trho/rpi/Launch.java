package de.trho.rpi;

import de.trho.rpi.core.AppModule;
import de.trho.rpi.core.Application;
import de.trho.rpi.core.RPi3Pin;
import de.trho.rpi.examples.ShiftRegisterModuleB;

public class Launch {

  public static void main(String[] args) {
    byte val = 1;
    int val2 = -128;

    String vals = "", val2s = "";

    for (int i = 0; i < 8; i++) {
      vals += Integer.toBinaryString(val & (1 << i)).substring(0, 1);
      val2s += Integer.toBinaryString(val2 & (1 << i)).substring(0, 1);
    }
    System.out.printf("%s %s\n", vals, val2s);
    run();
  }

  private static void run() {
    Application app;
    AppModule module;
    app = new Application();
    module = new ShiftRegisterModuleB(500, RPi3Pin.GPIO_17.address(), RPi3Pin.GPIO_27.address(),
        RPi3Pin.GPIO_22.address(), RPi3Pin.GPIO_05.address());
    app.addModule(module);
  }

}
