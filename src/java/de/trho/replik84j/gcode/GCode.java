package de.trho.replik84j.gcode;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author trh0 Put <a href="http://reprap.org/wiki/G-code">this information</a> in here
 */
public class GCode {

  public static class Parser {

    public Cmd parse(String line) {

      final Cmd cmd;
      line = line.trim().replaceAll("(\\ +)?\\;.+", "");
      if (line.isEmpty()) {
        return Cmd.nil;
      }

      int len;
      String[] split;
      split = line.split("\\ ");
      len = split.length;
      if (len > 0) {
        String token = split[0];
        char c = token.charAt(0);
        if (c == 'M' || c == 'm') {
          cmd = Cmd.M.find(token);
        } else if (c == 'G' || c == 'g') {
          cmd = Cmd.G.find(token);
        } else {
          cmd = null;
        }
        if (cmd == null)
          return Cmd.nil;
        for (int i = 1; i < len; i++) {
          token = split[i];
          final Variable v = Variable.Var.find(token.charAt(0) + "");
          if (v == null)
            continue;
          try {
            v.setValue(Double.parseDouble(token.substring(1)));
          } catch (Exception e) {
          }
          cmd.addVar(v);
        }
      } else
        return Cmd.nil;
      return cmd;
    }

    public List<Cmd> parse(List<String> lines) {
      final List<Cmd> cmds = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
      for (String ln : lines) {
        Cmd cmd = parse(ln);
        if (Cmd.nil.equals(cmd))
          continue;
        cmds.add(cmd);
      }
      return cmds;
    }
  }

  /**
  * 
  */
  public static enum Axis {
    //@formatter:off
      ALL, C1, C2, C3, E, X, Y, Z
    //@formatter:on
  }
  public interface Variable {

    Number getValue();

    boolean clean();

    String getName();

    void setValue(Number value);

    public enum Var {
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
        H(),
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
      public static Variable find(String s) {
        return new Variable() {
          private Number  value;
          private boolean clean = true;
          private final String name = Var.valueOf(s).name();
          @Override
          public void setValue(Number value) {
            this.value = value;
          }
          @Override
          public boolean clean() {
            return this.clean;
          }

          @Override
          public String toString() {
            return "var:{name:" + this.getName() + ", value:" + this.value + "}";
          }

          @Override
          public Number getValue() {
            return value;
          }

          @Override
          public String getName() {
            return name;
          }
      };
    }
    //@formatter:on
    }

  }

  public interface Cmd {

    String name();

    List<Variable> getVars();

    void addVar(Variable var);

    public static final Cmd nil = new Cmd() {
      @Override
      public String name() {
        return "null";
      }

      @Override
      public List<Variable> getVars() {
        return null;
      }

      @Override
      public void addVar(Variable var) {}
    };

    public enum G {
      G0(), // Move
      G1(), // Move
      G2(), // Controlled Arc Move
      G3(), // Controlled Arc Move
      G4(), // Dwell
      G6(), // Direct Stepper Move
      G10(), // Tool Offset Retract
      G11(), // Unretract
      G12(), // Clean Tool
      G17(), // Plane Selection (CNC specific)
      G18(), // :Plane Selection (CNC specific)
      G19(), // Plane Selection (CNC specific)
      G20(), // Set Units to Inches
      G21(), // Set Units to Millimeters
      G22(), // Firmware controlled Retract/Precharge
      G23(), // Firmware controlled Retract/Precharge
      G26(), // Mesh Validation Pattern
      G28(), // Move to Origin (Home)
      G29(), // Detailed Z-Probe, Auto Bed Leveling (Marlin) Unified Bed Leveling (Marlin) Manual
             // Bed
             // Leveling (Marlin) Auto Bed Leveling (Repetier-Firmware) Auto Bed
             // Compensation(RepRapFirmware)
      G29_1(), // Set Z probe head offset
      G29_2(), // Set Z probe head offset calculated from toolhead position
      G30(), // Single Z-Probe
      G31(), // Set or Report Current Probe status Dock Z Probe sled
      G32(), // Probe Z and calculate Z plane Probe and calculate in Reprapfirmware Probe and
             // calculate
             // in Repetier firmware Undock Z Probe sled
      G33(), // Firmware dependent Measure/List/Adjust Distortion Matrix (Repetier - Redeem) Delta
             // Auto
             // Calibration (Marlin 1.1.x or MK4duo)
      G38_x(), // Straight Probe (CNC specific)
      G38_2(), // probe toward workpiece, stop on contact, signal error if failure
      G38_3(), // probe toward workpiece, stop on contact
      G38_4(), // probe away from workpiece, stop on loss of contact, signal error if failure
      G38_5(), // probe away from workpiece, stop on loss of contact
      G40(), // Compensation Off (CNC specific)
      G42(), // Move to Grid Point
      G53_59(), // Coordinate System Select (CNC specific)
      G60(), // save current position to slot
      G61(), // Apply/restore saved coordinates to the active extruder.
      G80(), // Cancel Canned Cycle (CNC specific)
      G90(), // Set to Absolute Positioning
      G91(), // Set to Relative Positioning
      G92(), // Set Position
      G92_x(), // Reset Coordinate System Offsets (CNC specific)
      G93(), // Feed Rate Mode (Inverse Time Mode) (CNC specific)
      G94(), // Feed Rate Mode (Units per Minute) (CNC specific)
      G100(), // Calibrate floor or rod radius
      G130(), // Set digital potentiometer value
      G131(), // Remove offset
      G132(), // Calibrate endstop offsets
      G133(), // Measure steps to top
      G161(), // Home axes to minimum
      G162();

      public static Cmd find(String s) {
        return new Cmd() {
          private final List<Variable> vars = new java.util.ArrayList<>();
          private final String         name = G.valueOf(s).name();

          @Override
          public String name() {
            return name;
          }

          @Override
          public List<Variable> getVars() {
            return vars;
          }

          @Override
          public void addVar(Variable var) {
            this.vars.add(var);
          }

          @Override
          public String toString() {
            return "cmd: {name: " + this.name() + " , vars: [" + Arrays.toString(vars.toArray())
                + "]}";
          }
        };
      }
    }
    public enum M {
      M0(), // Stop or Unconditional stop
      M1(), // Sleep or Conditional stop
      M2(), // Program End
      M3(), // Spindle On, Clockwise (CNC specific)
      M4(), // Spindle On, Counter-Clockwise (CNC specific)
      M5(), // Spindle Off (CNC specific)
      M6(), // Tool change
      M7(), // Mist Coolant On (CNC specific)
      M8(), // Flood Coolant On (CNC specific)
      M9(), // Coolant Off (CNC specific)
      M10(), // Vacuum On (CNC specific)
      M11(), // Vacuum Off (CNC specific)
      M17(), // Enable/Power all stepper motors
      M18(), // Disable all stepper motors
      M20(), // List SD card
      M21(), // Initialize SD card
      M22(), // Release SD card
      M23(), // Select SD file
      M24(), // Start/resume SD print
      M25(), // Pause SD print
      M26(), // Set SD position
      M27(), // Report SD print status
      M28(), // Begin write to SD card
      M29(), // Stop writing to SD card
      M30(), // Delete a file on the SD card
      M31(), // Output time since last M109 or SD card start to serial
      M32(), // Select file and start SD print
      M33(), // Get the long name for an SD card file or folder Stop and Close File and save
             // restart.gcode
      M34(), // Set SD file sorting options
      M35(), // Upload firmware NEXTION from SD
      M36(), // Return file information
      M37(), // Simulation mode
      M38(), // Compute SHA1 hash of target file
      M39(), // Report SD card information
      M40(), // Eject
      M41(), // Loop
      M42(), // Switch I/O pin
      M43(), // Stand by on material exhausted Pin report and debug
      M48(), // Measure Z-Probe repeatability
      M70(), // Display message
      M72(), // Play a tone or song
      M73(), // Set build percentage
      M80(), // ATX Power On
      M81(), // ATX Power Off
      M82(), // Set extruder to absolute mode
      M83(), // Set extruder to relative mode
      M84(), // Stop idle hold
      M85(), // Set inactivity shutdown timer
      M92(), // Set axis_steps_per_unit
      M93(), // Send axis_steps_per_unit
      M98(), // Call Macro/Subprogram Get axis_hysteresis_mm
      M99(), // Return from Macro/Subprogram M99(), // Set axis_hysteresis_mm
      M101(), // Turn extruder 1 on (Forward), Undo Retraction
      M102(), // Turn extruder 1 on (Reverse)
      M103(), // Turn all extruders off, Extruder Retraction
      M104(), // Set Extruder Temperature
      M105(), // Get Extruder Temperature
      M106(), // Fan On
      M107(), // Fan Off
      M108(), // Cancel Heating (Marlin) Set Extruder Speed (BFB)
      M109(), // Set Extruder Temperature and Wait
      M110(), // Set Current Line Number
      M111(), // Set Debug Level
      M112(), // Emergency Stop
      M113(), // Set Extruder PWM
      M114(), // Get Current Position
      M115(), // Get Firmware Version and Capabilities
      M116(), // Wait
      M117(), // Get Zero Position Display Message
      M118(), // Echo message on host Negotiate Features
      M119(), // Get Endstop Status
      M120(), // Push Enable endstop detection
      M121(), // Disable endstop detection Pop
      M122(), // Firmware dependent Diagnose (RepRapFirmware) Set Software Endstop (MK4duo)
      M123(), // Tachometer value
      M124(), // Immediate motor stop
      M126(), // Open Valve
      M127(), // Close Valve
      M128(), // Extruder Pressure PWM
      M129(), // Extruder pressure off
      M130(), // Set PID P value
      M131(), // Set PID I value
      M132(), // Set PID D value
      M133(), // Set PID I limit value
      M134(), // Write PID values to EEPROM
      M135(), // Set PID sample interval
      M136(), // Print PID settings to host
      M140(), // Set Bed Temperature (Fast)
      M141(), // Set Chamber Temperature (Fast)
      M142(), // Holding Pressure
      M143(), // Maximum heater temperature
      M144(), // Bed Standby
      M146(), // Set Chamber Humidity
      M149(), // Set temperature units
      M150(), // Set display color
      M155(), // Automatically send temperatures
      M160(), // Number of mixed materials
      M163(), // Set weight of mixed material
      M164(), // Store weights
      M165(), // Set multiple mix weights
      M190(), // Wait for bed temperature to reach target temp
      M191(), // Wait for chamber temperature to reach target temp
      M200(), // Set filament diameter
      M201(), // Set max printing acceleration
      M202(), // Set max travel acceleration
      M203(), // Set maximum feedrate Repetier
      M204(), // Set default acceleration Repetier
      M205(), // Advanced settings Repetier
      M206(), // Offset axes Repetier(),
      M207(), // Calibrate z axis by detecting z max length Set eeprom value Set retract length
      M208(), // Set axis max travel Set unretract length
      M209(), // Enable automatic retract
      M210(), // Set homing feedrates
      M211(), // Disable/Enable software endstops
      M212(), // Set Bed Level Sensor Offset
      M218(), // Set Hotend Offset
      M220(), // Set speed factor override percentage Turn off AUX V
      M221(), // Set extrude factor override percentage Turn on AUX V
      M222(), // Set speed of fast XY moves
      M223(), // Set speed of fast Z moves
      M224(), // Enable extruder during fast moves
      M225(), // Disable on extruder during fast moves
      M226(), // Gcode Initiated Pause Wait for pin state
      M227(), // Enable Automatic Reverse and Prime
      M228(), // Disable Automatic Reverse and Prime
      M229(), // Enable Automatic Reverse and Prime
      M230(), // Disable / Enable Wait for Temperature Change
      M231(), // Set OPS parameter
      M232(), // Read and reset max. advance values
      M240(), // Trigger camera Start conveyor belt motor / Echo off
      M241(), // Stop conveyor belt motor / echo on
      M245(), // Start cooler
      M246(), // Stop cooler
      M250(), // Set LCD contrast
      M251(), // Measure Z steps from homing stop (Delta printers)
      M260(), // i2c Send Data
      M261(), // i2c Request Data
      M280(), // Set servo position
      M290(), // Babystepping
      M291(), // Display message and optionally wait for response
      M292(), // Acknowledge message
      M300(), // Play beep sound
      M301(), // Set PID parameters Marlin RepRapFirmwareonwards RepRapFirmwaretoinclusive
              // Smoothie
              // Other implementations Teacup
      M302(), // Allow cold extrudes
      M303(), // Run PID tuning
      M304(), // Set PID parameters - Bed M304 in RepRapPro version of Marlin(),
      M305(), // Set thermistor and ADC parameters //Set thermistor values
      M306(), // Set home offset calculated from toolhead position
      M307(), // Set or report heating process parameters
      M320(), // Activate autolevel (Repetier)
      M321(), // Deactivate autolevel (Repetier)
      M322(), // Reset autolevel matrix (Repetier)
      M323(), // Distortion correction on/off (Repetier)
      M340(), // Control the servos
      M350(), // Set microstepping mode
      M351(), // Toggle MS1 MS2 pins directly
      M355(), // Turn case lights on/off
      M360(), // Report firmware configuration SCARA calibration codes (Morgan) M360(),//Move to
              // Theta 0 degree position
      M361(), // Move to Theta 90 degree position
      M362(), // Move to Psi 0 degree position
      M363(), // Move to Psi 90 degree position
      M364(), // Move to Psi + Theta 90 degree position
      M365(), // SCARA scaling factor
      M366(), // SCARA convert trim
      M370(), // Morgan manual bed level - clear map
      M371(), // Move to next calibration position
      M372(), // Record calibration value, and move to next position
      M373(), // End bed level calibration mode
      M374(), // Save calibration grid
      M375(), // Display matrix / Load Matrix
      M376(), // Set bed compensation taper
      M380(), // Activate solenoid
      M381(), // Disable all solenoids
      M400(), // Wait for current moves to finish
      M401(), // Lower z-probe
      M402(), // Raise z-probe
      M404(), // Filament width and nozzle diameter
      M405(), // Filament Sensor on
      M406(), // Filament Sensor off
      M407(), // Display filament diameter
      M408(), // Report JSON-style response
      M420(), // Set RGB Colors as PWM (MachineKit) Leveling On/Off/Fade (Marlin)
      M421(), // Set a Mesh Bed Leveling Z coordinate
      M450(), // Report Printer Mode
      M451(), // Select FFF Printer Mode
      M452(), // Select Laser Printer Mode
      M453(), // Select CNC Printer Mode
      M460(), // Define temperature range for thermistor-controlled fan
      M500(), // Store parameters in non-volatile storage
      M501(), // Read parameters from EEPROM
      M502(), // Revert to the default "factory settings."
      M503(), // Print settings
      M504(), // Validate EEPROM
      M530(), // Enable printing mode
      M531(), // Set print name
      M532(), // Set print progress
      M540(), // Enable/Disable "Stop SD Print on Endstop Hit" Set MAC address
      M550(), // Set Name
      M551(), // Set Password
      M552(), // Set IP address, enable/disable network interface
      M553(), // Set Netmask
      M554(), // Set Gateway
      M555(), // Set compatibility
      M556(), // Axis compensation
      M557(), // Set Z probe point or define probing grid
      M558(), // Set Z probe type
      M559(), // Upload configuration file
      M560(), // Upload web page file
      M561(), // Set Identity Transform
      M562(), // Reset temperature fault
      M563(), // Define or remove a tool
      M564(), // Limit axes
      M565(), // Set Z probe offset
      M566(), // Set allowable instantaneous speed change
      M567(), // Set tool mix ratios
      M568(), // Turn off/on tool mix ratios
      M569(), // Stepper driver control
      M570(), // Configure heater fault detection
      M571(), // Set output on extrude
      M572(), // Set or report extruder pressure advance
      M573(), // Report heater PWM
      M574(), // Set endstop configuration
      M575(), // Set serial comms parameters
      M577(), // Wait until endstop is triggered
      M578(), // Fire inkjet bits
      M579(), // Scale Cartesian axes
      M580(), // Select Roland
      M581(), // Configure external trigger
      M582(), // Check external trigger
      M583(), // Wait for pin
      M584(), // Set drive mapping
      M585(), // Probe Tool
      M586(), // Configure network protocols
      M587(), // Store WiFi host network in list, or list stored networks
      M588(), // Forget WiFi host network
      M589(), // Configure access point parameters
      M590(), // Report current tool type and index
      M591(), // Configure filament sensing
      M592(), // Configure nonlinear extrusion
      M600(), // Set line cross section Filament change pause
      M605(), // Set dual x-carriage movement mode
      M665(), // Set delta configuration
      M666(), // Set delta endstop adjustment
      M667(), // Select CoreXY mode
      M668(), // Set Z-offset compensations polynomial
      M669(), // Set kinematics type and kinematics parameters
      M670(), // Set IO port bit mapping
      M671(), // Define positions of Z leadscrews or bed leveling screws
      M672(), // Program Z probe
      M700(), // Level plate
      M701(), // Load filament
      M702(), // Unload filament
      M703(), // Get Board Type
      M710(), // Erase the EEPROM and reset the board
      M750(), // Enable 3D scanner extension
      M751(), // Register 3D scanner extension over USB
      M752(), // Start 3D scan
      M753(), // Cancel current 3D scanner action
      M754(), // Calibrate 3D scanner
      M755(), // Set alignment mode for 3D scanner
      M756(), // Shutdown 3D scanner
      M800(), // Fire start print procedure
      M801(), // Fire end print procedure
      M851(), // Set Z-Probe Offset M851 in Marlin M851 in Marlin M851 in MK4duo M900 Set Linear
              // Advance Scaling Factors
      M905(), // Set local date and time
      M906(), // Set motor currents
      M907(), // Set digital trimpot motor
      M908(), // Control digital trimpot directly
      M909(), // Set microstepping
      M910(), // Set decay mode
      M911(), // Configure auto save on loss of power
      M912(), // Set electronics temperature monitor adjustment
      M913(), // Set motor percentage of normal current
      M914(), // Set/Get Expansion Voltage Level Translator
      M915(), // Configure motor stall detection
      M916(), // Resume print after power failure
      M917(), // Set motor standstill current reduction
      M918(), // Configure direct-connect display
      M928(), // Start SD logging
      M929(), // Start/stop event logging to SD card
      M997(), // Perform in-application firmware update
      M998(), // Request resend of line
      M999();// Restart after being stopped by error

      public static Cmd find(String s) {
        return new Cmd() {
          private final List<Variable> vars = new java.util.ArrayList<>();
          private final String         name = M.valueOf(s).name();

          @Override
          public String name() {
            return name;
          }

          @Override
          public List<Variable> getVars() {
            return vars;
          }

          @Override
          public void addVar(Variable var) {
            this.vars.add(var);
          }

          @Override
          public String toString() {
            return "cmd: {name: " + this.name() + " , vars: [" + Arrays.toString(vars.toArray())
                + "]}";
          }
        };
      }

    }

  }

}
