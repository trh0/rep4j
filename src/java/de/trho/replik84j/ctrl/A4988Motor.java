package de.trho.replik84j.ctrl;

import java.util.Arrays;
import de.trho.replik84j.gcode.GCode.Axis;

/**
 * <pre>
 * <table> 
 * <tr> <th>MS1</th> <th>MS2</th>  <th>MS3</th> <th>MCS Reso</th>  <th>Excitation Mode</th> </tr>
 * <tr> <td>L</td> <td>L</td> <td>L</td> <td>Full</td> <td>2 Phase</td> </tr>
 * <tr> <td>H</td> <td>L</td> <td>L</td> <td>Half</td> <td>1-2 Phase</td> </tr>
 * <tr> <td>L</td> <td>H</td> <td>L</td> <td>Quarter</td> <td>W1-2 Phase</td> </tr>
 * <tr> <td>H</td> <td>H</td> <td>L</td> <td>Eighth</td> <td>2W1-2 Phase</td> </tr>
 * <tr> <td>H</td> <td>H</td> <td>H</td> <td>Sixteenth</td> <td>41-2 Phase</td> </tr>
 * </table>
 * </pre>
 * 
 * @author trh0
 * @see #A4988Motor(Axis, int, float, int, int, int, int, int, int, int, int)
 */
public class A4988Motor {
  /**
   * 
   */
  public static enum Movement {
    FWD, REV, STP
  };
  /**
   * 
   *
   */
  public static enum Address {
    STEP(0), DIR(1), MS1(2), MS2(3), MS3(4), ENABLE(5), SLEEP(6), RESET(7);
    protected final int idx;

    private Address(final int idx) {
      this.idx = idx;
    }
  }
  /**
   * 
   *
   */
  public static enum Microstepping {
    FULL(0, 0, 0), HALF(1, 0, 0), QUARTER(0, 1, 0), EIGHTH(1, 1, 0), SIXTEENTH(1, 1, 1);
    protected final int[] mcs;

    private Microstepping(int... mcs) {
      this.mcs = mcs;
    }
  }
  private final int[]       adresses;
  private volatile Movement moveState = Movement.STP;
  private Axis              axis;
  private int               spr;
  private float             ups;

  /**
   * 
   * @param axis
   * @param offset Address-offset for the pins. If > 0, shifts the given int for the pins
   * @param spr
   * @param ups
   * @param adresses Bit sequence where to find a pin - e.g.
   * 
   *        <p>
   *        <i>Pin addresses for 4 Motors</i>
   *        </p>
   * 
   *        <pre>
   *        bit         pin     adr
   *        1,3,5,7,9   Step    0x0000,0x0004,0x0010,0x0040,0x0100
   *        2,4,6,8,10  dir     0x0002,0x0008,0x0020,0x0080,0x0200
   *        11          MS1     0x0400
   *        12          MS2     0x0800
   *        13          MS3     0x1000
   *        14          Enable  0x2000
   *        15          Sleep   0x4000
   *        16          Reset   0x8000
   *        </pre>
   * 
   *        <pre></pre>
   */
  public A4988Motor(Axis axis, int spr, float ups, int stepAdr, int dirAdr, int ms1Adr, int ms2Adr,
      int ms3Adr, int enableAdr, int sleepAdr, int resetAdr) {
    this();
    this.spr = spr;
    this.ups = ups;
    this.axis = axis;
  }

  /**
   * @see #A4988Motor(Axis, int, float, int, int, int, int, int, int, int, int)
   */
  public A4988Motor() {
    this.adresses = new int[Address.values().length];
  }

  /**
   * 
   * @param spr
   */
  public void setSpr(int spr) {
    this.spr = spr;
  }

  /**
   * 
   * @param ups
   */
  public void setUps(float ups) {
    this.ups = ups;
  }

  /**
   * 
   * @return
   */
  public float getUps() {
    return ups;
  }

  /**
   * 
   * @return
   */
  public Movement getMoveState() {
    return this.moveState;
  }

  /**
   * 
   * @param mv
   */
  public void setMoveState(Movement mv) {
    this.moveState = mv;
  }

  /**
   * 
   * @return
   */
  public int getSpr() {
    return spr;
  }

  /**
   * 
   * @return
   */
  public Axis getAxis() {
    return axis;
  }

  /**
   * 
   * @param axis
   */
  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  /**
   * 
   * @param adr
   * @param address
   */
  public void setAddress(Address adr, int address) {
    this.adresses[adr.idx] = address;
  }

  /**
   * 
   * @param adr
   * @return
   */
  public int getAddress(Address adr) {
    return this.adresses[adr.idx];
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(adresses);
    result = prime * result + ((axis == null) ? 0 : axis.hashCode());
    result = prime * result + ((moveState == null) ? 0 : moveState.hashCode());
    result = prime * result + spr;
    result = prime * result + Float.floatToIntBits(ups);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    A4988Motor other = (A4988Motor) obj;
    if (!Arrays.equals(adresses, other.adresses))
      return false;
    if (axis != other.axis)
      return false;
    if (moveState != other.moveState)
      return false;
    if (spr != other.spr)
      return false;
    if (Float.floatToIntBits(ups) != Float.floatToIntBits(other.ups))
      return false;
    return true;
  }

}
