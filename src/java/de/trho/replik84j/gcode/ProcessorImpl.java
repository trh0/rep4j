package de.trho.replik84j.gcode;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.trho.replik84j.gcode.GCode.Cmd;
import de.trho.replik84j.gcode.GCode.Handler;

/**
 * 
 * @author trh0
 *
 */
public class ProcessorImpl implements GCode.Processor {
   protected final org.apache.logging.log4j.Logger           logger      =
         org.apache.logging.log4j.LogManager.getLogger(getClass());

   private final java.util.Map<GCode.Command, GCode.Handler> handlers;
   private volatile int                                      currentLine = 0, noLines = 1;
   private String                                            gcode;
   private final GCode.Parser                                parser;

   public ProcessorImpl() {
      super();
      this.handlers = new HashMap<>();
      parser = new GCode.Parser();
   }

   @Override
   public void setHandler(GCode.Command c, GCode.Handler h) {
      if (c != null && h != null)
         this.handlers.put(c, h);
   }

   @Override
   public void setGCode(String gcode) {
      this.gcode = gcode;
      countLines();
   }

   @Override
   public double getProgress() {
      return currentLine * 1d / noLines;
   }

   private void countLines() {
      Matcher m = Pattern.compile("(\r\n)|\r|\n").matcher(gcode);
      noLines = 1;
      while (m.find()) {
         noLines++;
      }
   }
   @Override
   public void process() {
      String line;
      try (final Scanner sc = new Scanner(gcode)) {
         while (sc.hasNext()) {
            currentLine++;
            line = sc.nextLine();
            Cmd cmd = parser.parse(line);
            if (Cmd.NIL.equals(cmd)) {
               continue;
            }
            Handler h = handlers.get(cmd.getCommand());
            if (h == null) {
               logger.warn("No handler registered for command {} with args {}", cmd.getCommand(),
                     cmd.getVars());
            } else {
               h.handle(cmd);
            }
         }
      }

   }

}
