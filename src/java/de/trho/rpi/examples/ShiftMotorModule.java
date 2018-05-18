package de.trho.rpi.examples;

import com.pi4j.wiringpi.Shift;
import de.trho.replik84j.ctrl.MotorShiftController;
import de.trho.replik84j.ctrl.StepperMotor;
import de.trho.replik84j.ctrl.StepperMotor.Microstepping;
import de.trho.replik84j.gcode.Coord;
import de.trho.replik84j.gcode.GCode.Axis;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.RPi3Pin;
import static com.pi4j.wiringpi.Gpio.*;

//@formatter:off
/**
 * <pre>
 * |01   -> x steo 
 * |02   -> x dir
 * |03   -> y step
 * |04   -> y dir
 * |05   -> z step
 * |06   -> z dir
 * |07   -> driver sleep 
 * |08   -> driver reset
 * |09   -> driver enable 
 * |10   -> MS A
 * |11   -> MS B
 * |12   -> MS C
 * |13   -> free 
 * |14   -> free
 * |15   -> free
 * |16   -> free
 * </pre>
 * <code><pre>
 * DIR = 20   # Direction GPIO Pin
STEP = 21  # Step GPIO Pin
CW = 1     # Clockwise Rotation
CCW = 0    # Counterclockwise Rotation
SPR = 48   # Steps per Revolution (360 / 7.5)

GPIO.setmode(GPIO.BCM)
GPIO.setup(DIR, GPIO.OUT)
GPIO.setup(STEP, GPIO.OUT)
GPIO.output(DIR, CW)

step_count = SPR
delay = .0208

for x in range(step_count):
    GPIO.output(STEP, GPIO.HIGH)
    sleep(delay)
    GPIO.output(STEP, GPIO.LOW)
    sleep(delay)

sleep(.5)
GPIO.output(DIR, CCW)
for x in range(step_count):
    GPIO.output(STEP, GPIO.HIGH)
    sleep(delay)
    GPIO.output(STEP, GPIO.LOW)
    sleep(delay)

GPIO.cleanup()
 * </pre></code>
 * @author trh0
 * @see https://www.rototron.info/raspberry-pi-stepper-motor-tutorial/
 *
 */
//@formatter:on
public class ShiftMotorModule extends AbstractAppModule {
  private MotorShiftController mc;
  private int                  step = RPi3Pin.GPIO_27.address(), dir = RPi3Pin.GPIO_17.address(),
      ms1 = RPi3Pin.GPIO_18.address(), ms2 = RPi3Pin.GPIO_23.address(),
      ms3 = RPi3Pin.GPIO_24.address();

  public ShiftMotorModule() {
    super(RunMode.LOOP);
    // mc = new MotorShiftController(8, 10, Shift.LSBFIRST, RPi3Pin.GPIO_17.address(),
    // RPi3Pin.GPIO_27.address(), RPi3Pin.GPIO_22.address(), 0);
    // mc.addMotor(new StepperMotor(Axis.X, 200, 0.5f, 0x80, 0x40, 0x2, 0x4, 0x8, 0x01, 0x20,
    // 0x10));
    // mc.setMicroStepping(Axis.X, Microstepping.FULL);
    // mc.reset();
    logger.info("WiringPi setup {}", (wiringPiSetup() != 0) ? "not successfull" : "successfull");
    pinMode(step, OUTPUT);
    pinMode(dir, OUTPUT);
    pinMode(ms1, OUTPUT);
    pinMode(ms2, OUTPUT);
    pinMode(ms3, OUTPUT);
    digitalWrite(step, LOW);
    digitalWrite(dir, LOW);
    digitalWrite(ms1, LOW);
    digitalWrite(ms2, LOW);
    digitalWrite(ms3, LOW);
  }
  private int del = 10, steps = 200;

  @Override
  public void run() {
    this.setRunning(true);
    while (this.isRunning()) {

      mv(0, steps, del, Microstepping.FULL);
      mv(1, steps, del, Microstepping.HALF);
      mv(0, steps, del, Microstepping.QUARTER);
      mv(1, steps, del, Microstepping.EIGHTH);
      mv(0, steps, del / 2, Microstepping.SIXTEENTH);

      delay(2500);
      mv(1, steps, del, Microstepping.FULL);
      mv(0, steps, del, Microstepping.FULL);
      delay(1500);
      mv(1, steps, del - 3, Microstepping.FULL);
      mv(0, steps, del - 3, Microstepping.FULL);
      delay(1500);
      mv(1, steps, del - 4, Microstepping.FULL);
      mv(0, steps, del - 4, Microstepping.FULL);
      delay(1500);
      mv(1, steps, del - 5, Microstepping.FULL);
      mv(0, steps, del - 5, Microstepping.FULL);
      delay(500);
      // logger.info("Using MotorController to move now");
      // if (i % 2 == 0) {
      // mc.move(Coord.origin(), 1);
      // } else
      // mc.move(new Coord().X((float) (i + Math.random() * 30)).Y(0).Z(0), 1);
      // i++;
    }
    this.setRunning(false);
  }

  private void mv(int dirv, int steps, int speed, Microstepping mcs) {
    logger.info("Dir {}, Steps {}, speed {}, mcs {}", dirv, steps, speed, mcs);
    digitalWrite(ms1, mcs.mcs1());
    digitalWrite(ms2, mcs.mcs2());
    digitalWrite(ms3, mcs.mcs3());
    digitalWrite(this.dir, dirv);
    for (int i = 0; i < steps; i++) {
      digitalWrite(step, HIGH);
      digitalWrite(step, LOW);
      delay(speed);
    }
  }

}
