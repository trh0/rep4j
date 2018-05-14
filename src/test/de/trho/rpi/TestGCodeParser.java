package de.trho.rpi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import de.trho.replik84j.gcode.GCode;
import de.trho.replik84j.gcode.GCode.Parser;

public class TestGCodeParser {

  public static void main(String[] args) throws IOException, URISyntaxException {
    parse();
  }

  public static void parse() throws IOException, URISyntaxException {
    GCode.Parser p = new Parser();

    p.parse(Files.readAllLines(
        new File(TestGCodeParser.class.getClassLoader().getResource("test.gcode").toURI())
            .toPath()));

  }

}
