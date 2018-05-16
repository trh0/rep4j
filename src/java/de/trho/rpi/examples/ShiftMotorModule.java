package de.trho.rpi.examples;

import com.pi4j.wiringpi.Shift;
import de.trho.replik84j.ctrl.MotorShiftController;
import de.trho.replik84j.ctrl.StepperMotor;
import de.trho.replik84j.ctrl.StepperMotor.Microstepping;
import de.trho.replik84j.gcode.Coord;
import de.trho.replik84j.gcode.GCode.Axis;
import de.trho.rpi.core.AbstractAppModule;
import de.trho.rpi.core.RPi3Pin;

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
public class ShiftMotorModule extends AbstractAppModule {
private MotorShiftController mc;

//@formatter:on
  public ShiftMotorModule() {
    super(RunMode.LOOP);
    mc = new MotorShiftController(16, 1, Shift.MSBFIRST, RPi3Pin.GPIO_17.address(),
        RPi3Pin.GPIO_22.address(), RPi3Pin.GPIO_27.address(), 0);
    mc.addMotor(
        new StepperMotor(Axis.X, 200, 0.5f, 0x8000, 0x4000, 0x40, 0x20, 0x10, 0x80, 0x200, 0x100));
    mc.addMotor(
        new StepperMotor(Axis.Y, 200, 0.5f, 0x2000, 0x1000, 0x40, 0x20, 0x10, 0x80, 0x200, 0x100));
    mc.addMotor(
        new StepperMotor(Axis.Z, 200, 0.5f, 0x800, 0x400, 0x40, 0x20, 0x10, 0x80, 0x200, 0x100));
    mc.setMicroStepping(Axis.X, Microstepping.FULL);
    mc.setMicroStepping(Axis.Y, Microstepping.FULL);
    mc.setMicroStepping(Axis.Z, Microstepping.FULL);
    mc.reset();
  }
  private int i = 1;

  @Override
  public void run() {
    while (this.isRunning()) {
      if (i % 2 == 0) {
        mc.move(Coord.origin(), 1);
      } else
        mc.move(new Coord().X((float) (i + Math.random() * 30)).Y((float) (i + Math.random() * 30))
            .Z((float) (i + Math.random() * 30)), 1);
      i++;
    }
  }

}
