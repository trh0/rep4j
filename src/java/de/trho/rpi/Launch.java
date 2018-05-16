package de.trho.rpi;

import de.trho.rpi.core.AppModule;
import de.trho.rpi.core.Application;
import de.trho.rpi.examples.ShiftMotorModule;

public class Launch {

  public static void main(String[] args) {
    run();
  }

  private static void run() {
    Application app;
    AppModule module;
    app = new Application();
    module = new ShiftMotorModule();
    app.addModule(module);
  }

}
