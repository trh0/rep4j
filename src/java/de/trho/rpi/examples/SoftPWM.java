package de.trho.rpi.examples;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.SoftPwm;
import de.trho.rpi.core.RPi3Pin;

public class SoftPWM {

  private final static AtomicBoolean run  = new AtomicBoolean(true);
  private final static String        stop = "\\q";

  public void run() {

    @SuppressWarnings("unused")
    final GpioController gpio = GpioFactory.getInstance();

    int suc = SoftPwm.softPwmCreate(RPi3Pin.GPIO_17.address(), 10, 100);
    suc += SoftPwm.softPwmCreate(RPi3Pin.GPIO_27.address(), 10, 100);
    suc += SoftPwm.softPwmCreate(RPi3Pin.GPIO_22.address(), 10, 100);

    if (suc > 0) {
      System.err.println("No success creating pwm pins");
      System.exit(0);
    }

    Thread t = new Thread(() -> {
      try (final Scanner sc = new Scanner(System.in)) {
        String signal;
        while (!stop.equals(signal = sc.nextLine())) {
          System.out.printf("Got %s!", signal);
        }
        sendBreak();
        Thread.currentThread().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    t.start();

    int inc = 1;
    int val = 0;
    int mode = 1, addr;
    try {
      boolean r = run.get();
      while (r) {
        mode = (mode == 1 && val >= 100) ? -1 : (mode == -1 && val <= 0) ? 1 : mode;
        val = (mode < 0) ? val - inc : val + inc;

        addr = RPi3Pin.GPIO_17.address();
        SoftPwm.softPwmWrite(addr, val);
        addr = RPi3Pin.GPIO_27.address();
        SoftPwm.softPwmWrite(addr, val);
        addr = RPi3Pin.GPIO_22.address();
        SoftPwm.softPwmWrite(addr, val);

        Thread.sleep(25);
        System.out.printf("Mode %s, Val %s\n", mode, val);
        synchronized (run) {
          r = run.get();
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }
    System.exit(0);
  }

  synchronized static void sendBreak() {
    System.out.println("sending break");
    run.set(false);
  }
}
