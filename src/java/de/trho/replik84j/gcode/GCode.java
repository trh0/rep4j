package de.trho.replik84j.gcode;

import static java.lang.Math.abs;

/**
 * 
 * @author trh0
 * Put <a href="http://reprap.org/wiki/G-code">this information</a> in here
 */
public class GCode {

   /**
   * 
   */
   public static enum Axis {
    //@formatter:off
      ALL, C1, C2, C3, E, X, Y, Z
    //@formatter:on
   }

   public static enum Var {
    //@formatter:off
      /**
       * Absolute or incremental position of A axis (rotational axis around X axis)
       */
      A(),
      /**
       * Absolute or incremental position of B axis (rotational axis around Y axis)
       */
      B(),
      /**
       * Absolute or incremental position of C axis (rotational axis around Z axis)
       */
      C(),
      /**
       * Defines diameter or radial offset used for cutter compensation. D is used for depth of cut on lathes. It is used for aperture selection and commands on photoplotters.
       */
      D(),
      /**
       * Precision feedrate for threading on lathes
       */
      E(),
      /**
       * Defines feed rate
       */
      F(),
      /**
       * Address for preparatory commands
       */
      G(),
      H,
      /**
       * Defines arc center in X axis for G02 or G03 arc commands. Also used as a parameter within some fixed cycles.
       */
      I(),
      /**
       * Defines arc center in Y axis for G02 or G03 arc commands. Also used as a parameter within some fixed cycles.
       */
      J(),
      /**
       * Defines arc center in Z axis for G02 or G03 arc commands. Also used as a parameter within some fixed cycles, equal to L address.
       */
      K(),
      /**
       * Fixed cycle loop count; Specification of what register to edit using G10
       */
      L(),
      /**
       * Miscellaneous function.
       */
      M(),
      /**
       * Line (block) number in program; System parameter number to change using G10
       */
      N(),
      /**
       * Program name
       */
      O(),
      /**
       * Serves as parameter address for various G and M codes
       */
      P(),
      /**
       * Defines size of arc radius, or defines retract height in milling canned cycles
       */
      R(),
      /**
       * Defines speed, either spindle speed or surface speed depending on mode
       */
      S(),
      /**
       * Tool selection
       */
      T(),
      /**
       * Incremental axis corresponding to X axis (typically only lathe group A controls). Also defines dwell time on some machines (instead of "P" or "X").
       */
      U(),
      /**
       * Incremental axis corresponding to Y axis
       */
      V(),
      /**
       * Incremental axis corresponding to Z axis (typically only lathe group A controls)
       */
      W(),
      /**
       * Absolute or incremental position of X axis. Also defines dwell time on some machines (instead of "P" or "U").
       */
      X(),
      /**
       * Absolute or incremental position of Y axis
       */
      Y(),
      /**
       * Absolute or incremental position of Z axis
       */
      Z();
      private Var(String...strs) {}
    //@formatter:on
   }

   public static enum G {
      /** Rapid positioning */
      G00(),
      /** Linear interpolation */
      G01(),
      /** Circular interpolation, clockwise */
      G02(),
      /** Circular interpolation, counterclockwise */
      G03(),
      /** Dwell - Pause the machine for a period of time. */
      G04(),
      /** P10000 High-precision contour control (HPCC) */
      G05(),
      /** .1 Q1.AI Advanced Preview Control */
      G05_1(),
      /** .1 Non-uniform rational B-spline (NURBS) Machining */
      G06_1(),
      /** Imaginary axis designation */
      G07(),
      /** Exact stop check, non-modal */
      G09(),
      /** Programmable data input */
      G10(),
      /** Data write cancel */
      G11(),
      /** XY plane selection */
      G17(),
      /** ZX plane selection */
      G18(),
      /** YZ plane selection */
      G19(),
      /** Programming in inches */
      G20(),
      /** Programming in millimeters (mm) */
      G21(),
      /** Return to home position (machine zero, aka machine reference point) */
      G28(),
      /** Return to secondary home position (machine zero, aka machine reference point) */
      G30(),
      /** Feed until skip function */
      G31(),
      /** Single-point threading, longhand style (if not using a cycle, e.g., G76) */
      G32(),
      /**
       * Constant-pitch threading T: Single-point threading, longhand style (if not using a cycle,
       * e.g., G76)
       */
      G33(),
      /** Variable-pitch threading */
      G34(),
      /** Tool radius compensation off */
      G40(),
      /**
       * Tool radius compensation left Milling: Given righthand-helix cutter and M03 spindle
       * direction, G41 corresponds to climb milling (down milling).<br>
       * Takes an address (D or H) that calls an offset register value for radius. <br>
       * Turning: Often needs no D or H address on lathes, because whatever tool is active
       * automatically calls its geometry offsets with it. (Each turret station is bound to its
       * geometry offset register.)<br>
       * G41 and G42 for milling has been partially automated and obviated (although not completely)
       * since CAM programming has become more common. <br>
       * CAM systems let the user program as if using a zero-diameter cutter. <br>
       * The fundamental concept of cutter radius compensation is still in play (i.e., that the
       * surface produced will be distance R away from the cutter center), but the programming
       * mindset is different.<br>
       * The human does not choreograph the toolpath with conscious, painstaking attention to G41,
       * G42, and G40, because the CAM software takes care of that. <br>
       * The software has various CRC mode selections, such as computer, control, wear, reverse
       * wear, off, some of which do not use G41/G42 at all (good for roughing, or wide finish
       * tolerances), and others that use it so that the wear offset can still be tweaked at the
       * machine (better for tight finish tolerances).
       */
      G41(),

      /** Tool radius compensation right */
      G42(),
      /** Tool height offset compensation negative */
      G43(),
      /** Tool height offset compensation positive */
      G44(),
      /** Axis offset single increase */
      G45(),
      /** Axis offset single decrease */
      G46(),
      /** Axis offset double increase */
      G47(),
      /** Axis offset double decrease */
      G48(),
      /** Tool length offset compensation cancel */
      G49(),
      /** Define the maximum spindle speed T:Scaling function cancel*/
      G50(),
      /** Position register (programming of vector from part zero to tool tip) */
      /** Local coordinate system (LCS) */
      G52(),
      /** Machine coordinate system */
      G53(),
      /** to G59 Work coordinate systems (WCSs) */
      G54(),
      /** .1 P1 to P48 Extended work coordinate systems */
      G54_1(),
      /** Exact stop check, modal */
      G61(),
      /** Automatic corner override */
      G62(),
      /** Default cutting mode (cancel exact stop check mode) */
      G64(),
      /** Rotate coordinate system */
      G68(),
      /** Turn off coordinate system rotation */
      G69(),
      /** Fixed cycle, multiple repetitive cycle, for finishing (including contours) */
      G70(),
      /** Fixed cycle, multiple repetitive cycle, for roughing (Z-axis emphasis) */
      G71(),
      /** Fixed cycle, multiple repetitive cycle, for roughing (X-axis emphasis) */
      G72(),
      /**
       * T: Fixed cycle, multiple repetitive cycle, for roughing, with pattern repetition M: Peck
       * drilling cycle for milling – high-speed (NO full retraction from pecks)
       */
      G73(),
      /**
       * M: Tapping cycle for milling, lefthand thread, M04 spindle direction T: Peck drilling cycle
       * for turning
       */
      G74(),
      /** Peck grooving cycle for turning */
      G75(),
      /**
       * M: Fine boring cycle for milling T: Threading cycle for turning, multiple repetitive cycle
       */
      G76(),
      /**
       * Cancel canned cycle Turning: Usually not needed on lathes, because a new group-1 G address
       * (G00 to G03) cancels whatever cycle was active.
       */
      G80(),
      /** Simple drilling cycle */
      G81(),
      /** Drilling cycle with dwell */
      G82(),
      /** Peck drilling cycle (full retraction from pecks) */
      G83(),
      /** Tapping cycle, righthand thread, M03 spindle direction */
      G84(),
      /** .2 Tapping cycle, righthand thread, M03 spindle direction, rigid toolholder */
      G84_2(),

      /** .3 Tapping cycle, lefthand thread, M04 spindle direction, rigid toolholder */
      G84_3(),

      /**
       * boring cycle, feed in/feed out Good cycle for a reamer.In some cases good for single-point
       * boring tool, although in other cases the lack of depth of cut on the way back out is bad
       * for surface finish, in which case, G76 (OSS/shift) can be used instead. If need dwell at
       * hole bottom, see G89.
       */
      G85(),

      /** boring cycle, feed in/spindle stop/rapid out */
      G86(),

      /** boring cycle, backboring */
      G87(),

      /** boring cycle, feed in/spindle stop/manual operation */
      G88(),

      /** boring cycle, feed in/dwell/feed out */
      G89(),

      /**
       * Absolute programming Milling: Always as above.Turning: Sometimes as above (Fanuc group type
       * B and similarly designed), but on most lathes (Fanuc group type A and similarly designed),
       * G90/G91 are not used for absolute/incremental modes. Instead, U and W are the incremental
       * addresses and X and Z are the absolute addresses. On these lathes, G90 is instead a fixed
       * cycle address for roughing.<br>
       * T: Fixed cycle, simple cycle, for roughing (Z-axis emphasis)
       */
      G90(),

      /**
       * Incremental programming Milling: Always as above. Turning: Sometimes as above (Fanuc group
       * type B and similarly designed), but on most lathes (Fanuc group type A and similarly
       * designed), G90/G91 are not used for absolute/incremental modes. Instead, U and W are the
       * incremental addresses and X and Z are the absolute addresses. On these lathes, G90 is a
       * fixed cycle address for roughing.
       */
      G91(),

      /**
       * Position register (programming of vector from part zero to tool tip) Milling: Always as
       * above.Turning: Sometimes as above (Fanuc group type B and similarly designed), but on most
       * lathes (Fanuc group type A and similarly designed), position register is G50.T: Threading
       * cycle, simple cycle
       */
      G92(),

      /** Feedrate per minute T: Fixed cycle, simple cycle, for roughing (X-axis emphasis) */
      G94(),

      /** Feedrate per revolution */
      G95(),

      /** Constant surface speed (CSS) */
      G96(),

      /** Constant spindle speed */
      G97(),

      /** Return to initial Z level in canned cycle T: Feedrate per minute (group type A) */
      G98(),

      /** Return to R level in canned cycle T: Feedrate per revolution (group type A) */
      G99(),

      /** Tool length measurement */
      G100();
   }
   public static enum M {
      /** Compulsory stop */
      M00(),
      /** Optional stop */
      M01(),
      /** End of program */
      M02(),
      /**
       * Spindle on (clockwise rotation) Right-hand-helix screws moving in the tightening direction
       * (and right-hand-helix flutes spinning in the cutting direction) are defined as moving in
       * the M03 direction, and are labeled "clockwise" by convention. The M03 direction is always
       * M03 regardless of local vantage point and local CW/CCW distinction.
       */
      M03(),
      /** Spindle on (counterclockwise rotation) */
      M04(),
      /** Spindle stop */
      M05(),
      /**
       * Automatic tool change (ATC) Programming on any particular machine tool requires knowing
       * which method that machine uses. To understand how the T address works and how it interacts
       * (or not) with M06, one must study the various methods, such as lathe turret programming,
       * ATC fixed tool selection, ATC random memory tool selection, the concept of "next tool
       * waiting", and empty tools.[4]
       */
      M06(),
      /** Coolant on (mist) */
      M07(),
      /** Coolant on (flood) */
      M08(),
      /** Coolant off */
      M09(),
      /** Pallet clamp on */
      M10(),
      /** Pallet clamp off */
      M11(),
      /** Spindle on (clockwise rotation) and coolant on (flood) */
      M13(),
      /**
       * Spindle orientation. The relevance of spindle orientation has increased as technology has
       * advanced. Although 4- and 5-axis contour milling and CNC single-pointing have depended on
       * spindle position encoders for decades, before the advent of widespread live tooling and
       * mill-turn/turn-mill systems, it was not as often relevant in "regular" (non-"special")
       * machining for the operator (as opposed to the machine) to know the angular orientation of a
       * spindle as it is today, except in certain contexts (such as tool change, or G76 fine boring
       * cycles with choreographed tool retraction). Most milling of features indexed around a
       * turned workpiece was accomplished with separate operations on indexing head setups; in a
       * sense, indexing heads were originally invented as separate pieces of equipment, to be used
       * in separate operations, which could provide precise spindle orientation in a world where it
       * otherwise mostly didn't exist (and didn't need to). But as CAD/CAM and multiaxis CNC
       * machining with multiple rotary-cutter axes becomes the norm, even for "regular"
       * (non-"special") applications, machinists now frequently care about stepping just about any
       * spindle through its 360° with precision.
       */
      M19(),
      /** Mirror, X-axis T: Tailstock forward */
      M21(),
      /** Mirror, Y-axis. T:Tailstock backward */
      M22(),
      /** Mirror OFF T: Thread gradual pullout ON */
      M23(),
      /** Thread gradual pullout OFF */
      M24(),
      /** End of program, with return to program top */
      M30(),
      /** Gear select – gear 1 */
      M41(),
      /** Gear select – gear 2 */
      M42(),
      /** Gear select – gear 3 */
      M43(),
      /** Gear select – gear 4 */
      M44(),
      /** Feedrate override allowed */
      M48(),
      /** Feedrate override NOT allowed */
      M49(),
      /** Unload Last tool from spindle */
      M52(),
      /** Automatic pallet change (APC) */
      M60(),
      /** Subprogram call */
      M98(),
      /** Subprogram end */
      M99(),
      /** SXXX: Set temperature and wait for it to be reached */
      M104()
      ;
   }
   public static enum T {

   }

   /**
   * 
   */
   public static class Coord {
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

}
