package de.trho.rpi.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;
import de.trho.rpi.core.AbstractAppModule;

public class PinTestModule extends AbstractAppModule {

  private final List<Pin>     pins;
  private final List<Boolean> states;
  private final long          wait;

  public PinTestModule(long wait, Pin... pins) {
    super(RunMode.LOOP);
    this.pins = pins != null ? Arrays.stream(pins).collect(Collectors.toList()) : new ArrayList<>();
    this.states = new ArrayList<>();
    this.wait = wait;

    this.pins.forEach(p -> this.states.add(p != null));

    logger.info(Gpio.wiringPiSetup() != 0 ? "Wiring PI setup failed." : "Wiring PI setup OK!");
  }

  @Override
  public void run() {
    this.setRunning(true);
    while (this.isRunning()) {
      try {
        boolean state; // !!
        Pin pin;
        for (int i = 0; i < pins.size(); i++) {
          state = states.get(i); // !!not null-safe!!
          pin = pins.get(i);
          Gpio.digitalWrite(pin.getAddress(), state);
          System.out.printf("Setting %s to %s", pin, state);
          this.states.set(i, !state);
          Thread.sleep(this.wait);
        }

      } catch (Exception e) {
        e.printStackTrace();
        this.exit();
        break;
      }
    }
    this.setRunning(false);
  }

}
