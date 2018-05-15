package de.trho.rpi.examples;

import de.trho.rpi.core.AbstractAppModule;

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
//@formatter:on
  public ShiftMotorModule() {
    super(RunMode.LOOP);
  }

  @Override
  public void run() {

  }

}
