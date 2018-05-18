package de.trho.replik84j.ctrl;

import static com.pi4j.wiringpi.Gpio.delay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import de.trho.replik84j.ctrl.StepperMotor.Address;
import de.trho.replik84j.ctrl.StepperMotor.Microstepping;
import de.trho.replik84j.ctrl.StepperMotor.Direction;
import de.trho.replik84j.gcode.GCode.Axis;
import de.trho.replik84j.gcode.Coord;

/**
 * 
 * @author trh0
 *
 */
public class MotorShiftController extends ShiftRegister {

  protected final org.apache.logging.log4j.Logger                 logger   =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  private final java.util.Map<Axis, java.util.List<StepperMotor>> motors;
  private Axis[]                                                  ax       =
      {Axis.X, Axis.Y, Axis.Z, Axis.E};
  private volatile int                                            state    = 0x0000;
  private final Object                                            lock     = new Object();
  private volatile Coord                                          pos;
  private final Coord                                             origin;
  private float                                                   accuracy = 0.01f;

  /**
   * 
   * @param bitResolution
   * @param latchDelay
   * @param order
   * @param dataPin
   * @param clockPin
   * @param latchPin
   * @param resetPin
   */
  public MotorShiftController(int bitResolution, int latchDelay, int order, int dataPin,
      int clockPin, int latchPin, int resetPin) {
    super(latchDelay, bitResolution, order, dataPin, clockPin, latchPin, resetPin);
    this.motors = Collections.synchronizedMap(new HashMap<>());
    for (Axis a : Axis.values()) {
      motors.put(a, Collections.synchronizedList(new ArrayList<>()));
    }
    pos = Coord.origin();
    origin = Coord.origin();
  }

  /**
   * 
   * @param axis
   * @param enable
   */
  public synchronized void enableSw(Axis axis, boolean enable) {
    int wr = 0x00;
    final List<StepperMotor> tm;
    if (axis == Axis.ALL) {
      tm = new ArrayList<>();
      motors.values().forEach(c -> tm.addAll(c));
    } else {
      tm = motors.get(axis);
    }
    for (StepperMotor m : tm) {
      wr |= m.getAddress(Address.ENABLE);
    }
    synchronized (lock) {
      state |= wr;
      if (enable)
        state ^= wr;
      write(state);
    }
  }

  /**
   * 
   * @param axis
   * @param sleep
   */
  public synchronized void sleepSw(Axis axis, boolean sleep) {
    int wr = 0x00;
    final List<StepperMotor> tm;
    if (axis == Axis.ALL) {
      tm = new ArrayList<>();
      motors.values().forEach(c -> tm.addAll(c));
    } else {
      tm = motors.get(axis);
    }
    for (StepperMotor m : tm) {
      if (axis == Axis.ALL || axis == m.getAxis()) {
        wr |= m.getAddress(Address.SLEEP);
      }
    }
    synchronized (lock) {
      state |= wr;
      if (sleep)
        state ^= wr;
      write(state);
    }
  }

  /**
   * 
   * @param axis
   * @param msc
   */
  public synchronized void setMicroStepping(Axis axis, Microstepping msc) {
    int wr = 0x00;
    final List<StepperMotor> tm;
    if (axis == Axis.ALL) {
      tm = new ArrayList<>();
      motors.values().forEach(c -> tm.addAll(c));
    } else {
      tm = motors.get(axis);
    }
    for (StepperMotor m : tm) {
      if (axis == Axis.ALL || axis == m.getAxis()) {
        int ms1 = m.getAddress(Address.MS1), ms2 = m.getAddress(Address.MS2),
            ms3 = m.getAddress(Address.MS3);
        for (int i = 0; i < 3; i++) {
          if (msc.mcs[i] == 1) {
            switch (i) {
              case 0:
                wr |= ms1;
                break;
              case 1:
                wr |= ms2;
                break;
              case 2:
                wr |= ms3;
                break;
              default:
            }
          } else {
            switch (i) {
              case 0:
                wr |= ms1;
                wr ^= ms1;
                break;
              case 1:
                wr ^= ms1;
                break;
              case 2:
                wr ^= ms1;
                break;
              default:
            }
          }
        }
      }
    }
    synchronized (lock) {
      state |= wr;
      state ^= wr;
      state |= wr;
      write(state);
    }
  }

  /**
   * 
   * @param axis
   */
  public synchronized void reset(Axis axis) {
    int wr = 0x00;
    final List<StepperMotor> tm;
    if (axis == Axis.ALL) {
      tm = new ArrayList<>();
      motors.values().forEach(c -> tm.addAll(c));
    } else {
      tm = motors.get(axis);
    }
    for (StepperMotor m : tm) {
      if (axis.equals(m.getAxis())) {
        wr |= m.getAddress(Address.RESET);
      }
    }
    synchronized (lock) {
      // ensure bits are all set before xor
      state |= wr;
      // low to all reset pins
      state ^= wr;
      write(state);
    }
    delay(50);
    synchronized (lock) {
      // high to all reset pins
      state |= wr;
      write(state);
    }
  }

  /**
   * 
   * @param tar
   * @param speed
   */
  public void move(Coord tar, int speed) {
    final Coord d = Coord.diffCoord(pos, tar);
    float axisValue;
    Direction moveDir;
    int tempMotorAdr;
    List<StepperMotor> axisMotors;
    while (!pos.inRange(origin, 0.01f, 0.1f)) {
      for (Axis cax : this.ax) {
        moveDir = d.getAxis(cax) < 0f ? Direction.REV : Direction.FWD;
        axisValue = d.getAxis(cax);
        tempMotorAdr = 0x0;
        if (axisValue != 0) {
          axisMotors = motors.get(cax);
          if (axisMotors.size() > 0) {
            for (StepperMotor m : axisMotors) {
              int dir = m.getAddress(Address.DIR);
              tempMotorAdr |= m.getAddress(Address.STEP);
              state |= dir;
              if (moveDir == Direction.FWD) {
                state ^= dir;
              }
            }
            state |= tempMotorAdr;
            write(state);
            delay(super.getLatchDelay());
            state ^= tempMotorAdr;
            write(state);
            // DO # of steps according to speed
            d.reduce(cax, axisMotors.get(0).getUps());
            axisValue = d.getAxis(cax);
            if (Math.abs(axisValue) <= accuracy) {
              axisValue = 0;
              d.setAxis(cax, axisValue);
            }
          }
        }
      }

    }
    pos.X(tar.X()).Y(tar.Y()).Z(tar.Z()).E(tar.E()).C1(tar.C1()).C2(tar.C2()).C3(tar.C3());
  }

  /**
   * 
   * @return
   */
  public Coord getPosition() {
    return this.pos;
  }

  /**
   * 
   * @param m
   */
  public void addMotor(StepperMotor m) {
    final List<StepperMotor> li = motors.get(m.getAxis());
    if (!li.contains(m)) {
      li.add(m);
    }
  }

}
