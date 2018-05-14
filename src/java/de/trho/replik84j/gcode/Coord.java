package de.trho.replik84j.gcode;

import static de.trho.replik84j.gcode.GCode.*;
import static java.lang.Math.abs;

/**
  * 
  */
public class Coord {
  /**
   * 
   * @param tar target pos
   * @param pos current pos
   * @return The coords constructed from the deltas of target and source
   */
  public static Coord diffCoord(Coord tar, Coord pos) {
    return new Coord().X(tar.x - pos.x).Y(tar.y - pos.y).Z(tar.z - pos.z).E(tar.e - pos.e)
        .C1(tar.c1 - pos.c1).C2(tar.c2 - pos.c2).C3(tar.c3 - pos.c3);
  }

  public static Coord origin() {
    return new Coord().X(0).Y(0).Z(0).E(0).C1(0).C2(0).C3(0);
  }

  private volatile float x, y, z, e, c1, c2, c3;

  public float C1() {
    return c1;
  }

  public Coord C1(float c1) {
    this.c1 = c1;
    return this;
  }

  public float C2() {
    return c2;
  }

  public Coord C2(float c2) {
    this.c2 = c2;
    return this;
  }

  public float C3() {
    return c3;
  }

  public Coord C3(float c3) {
    this.c3 = c3;
    return this;
  }

  public float E() {
    return e;
  }

  public Coord E(float e) {
    this.e = e;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Coord other = (Coord) obj;
    if (Float.floatToIntBits(c1) != Float.floatToIntBits(other.c1))
      return false;
    if (Float.floatToIntBits(c2) != Float.floatToIntBits(other.c2))
      return false;
    if (Float.floatToIntBits(c3) != Float.floatToIntBits(other.c3))
      return false;
    if (Float.floatToIntBits(e) != Float.floatToIntBits(other.e))
      return false;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
      return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
      return false;
    if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
      return false;
    return true;
  }

  public float getAxis(Axis axis) {
    switch (axis) {
      case C1:
        return c1;
      case C2:
        return c2;
      case C3:
        return c3;
      case E:
        return e;
      case X:
        return x;
      case Y:
        return y;
      case Z:
        return z;
      case ALL:
      default:
        return 0f;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(c1);
    result = prime * result + Float.floatToIntBits(c2);
    result = prime * result + Float.floatToIntBits(c3);
    result = prime * result + Float.floatToIntBits(e);
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    result = prime * result + Float.floatToIntBits(z);
    return result;
  }

  public boolean inRange(Object obj, float toleranceXYZ, float toleranceE) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Coord other = (Coord) obj;
    if (Math.abs(x - other.x) > toleranceXYZ)
      return false;
    if (Math.abs(y - other.y) > toleranceXYZ)
      return false;
    if (Math.abs(z - other.z) > toleranceXYZ)
      return false;
    if (Math.abs(e - other.z) > toleranceE)
      return false;
    return true;
  }

  /**
   * 
   * @param axis
   * @param val
   */
  public Coord reduce(Axis axis, float val) {
    val = abs(val);
    int sign;
    switch (axis) {
      case ALL:
        sign = x < 0 ? -1 : 1;
        x = sign * (abs(x) - val);
        sign = y < 0 ? -1 : 1;
        y = sign * (abs(y) - val);
        sign = z < 0 ? -1 : 1;
        z = sign * (abs(z) - val);
        sign = e < 0 ? -1 : 1;
        e = sign * (abs(e) - val);
        sign = c1 < 0 ? -1 : 1;
        c1 = sign * (abs(c1) - val);
        sign = c1 < 0 ? -1 : 1;
        c2 = sign * (abs(c2) - val);
        sign = c1 < 0 ? -1 : 1;
        c3 = sign * (abs(c3) - val);
        break;
      case C1:
        sign = c1 < 0 ? -1 : 1;
        c1 = sign * (abs(c1) - val);
        break;
      case C2:
        sign = c2 < 0 ? -1 : 1;
        c2 = sign * (abs(c2) - val);
        break;
      case C3:
        sign = c3 < 0 ? -1 : 1;
        c3 = sign * (abs(c3) - val);
        break;
      case E:
        sign = e < 0 ? -1 : 1;
        e = sign * (abs(e) - val);
        break;
      case X:
        sign = x < 0 ? -1 : 1;
        x = sign * (abs(x) - val);
        break;
      case Y:
        sign = y < 0 ? -1 : 1;
        y = sign * (abs(y) - val);
        break;
      case Z:
        sign = z < 0 ? -1 : 1;
        z = sign * (abs(z) - val);
        break;
      default:
        break;
    }
    return this;
  }

  public Coord setAxis(Axis axis, float val) {
    switch (axis) {
      case C1:
        c1 = val;
        break;
      case C2:
        c2 = val;
        break;
      case C3:
        c3 = val;
        break;
      case E:
        e = val;
        break;
      case X:
        x = val;
        break;
      case Y:
        y = val;
        break;
      case Z:
        z = val;
        break;
      case ALL:
      default:
        break;
    }
    return this;
  }

  public float X() {
    return x;
  }

  public Coord X(float x) {
    this.x = x;
    return this;
  }

  public float Y() {
    return y;
  }

  public Coord Y(float y) {
    this.y = y;
    return this;
  }

  public float Z() {
    return z;
  }

  public Coord Z(float z) {
    this.z = z;
    return this;
  }
}
