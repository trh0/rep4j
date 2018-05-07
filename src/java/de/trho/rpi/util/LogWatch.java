package de.trho.rpi.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "LogWatch", category = "Core", elementType = "appender", printObject = true)
public class LogWatch extends AbstractAppender {

  /**
   * 
   * @param lvl Integer representation of the level
   * @return The integer representation of the given log level (Integer.MAX_VALUE if unable to parse
   *         the lvl)
   */
  public static Level parseLevel(int lvl) {
    for (Level lev : Level.values()) {
      if (lev.intLevel() == lvl) {
        return lev;
      }
    }
    return Level.ALL;
  }
  /**
   * The minimum Level to register by this Appender.
   */
  @SuppressWarnings("unused")
  private Level        minLevel;
  private final String logfile;

  /**
   * Construct a new instance of the CustomAppender
   * 
   * @param name The Appender name.
   * @param filter The Filter to associate with the Appender.
   * @param layout The layout to use to format the event.
   * @param ignoreExceptions If true, exceptions will be logged and suppressed. If false errors will
   *        be logged and then passed to the application.
   */
  protected LogWatch(String name, Filter filter, Layout<? extends Serializable> layout,
      boolean ignoreExceptions) {
    this(name, filter, layout, ignoreExceptions, Level.ALL);
  }

  /**
   * Construct a new instance of the CustomAppender
   * 
   * @param name The Appender name.
   * @param filter The Filter to associate with the Appender.
   * @param layout The layout to use to format the event.
   * @param ignoreExceptions If true, exceptions will be logged and suppressed. If false errors will
   *        be logged and then passed to the application.
   * @param minlvl Minimum Level to log
   */
  protected LogWatch(String name, Filter filter, Layout<? extends Serializable> layout,
      boolean ignoreExceptions, final Level minlvl) {
    super(name, filter, layout, ignoreExceptions);
    this.minLevel = minlvl;
    logfile = Config.getValue("log.outpath") + Config.getValue("log.filename");
  }

  @PluginFactory
  public static LogWatch createAppender(@PluginAttribute("name") String name,
      @PluginElement("Layout") Layout<? extends Serializable> layout,
      @PluginElement("Filter") final Filter filter,
      @PluginAttribute("minlevel") final String minlevel) {
    if (name == null) {
      LOGGER.error("No name provided for CustomAppender LogWatch");
      return null;
    }
    if (layout == null) {
      layout = PatternLayout.createDefaultLayout();
    }
    return new LogWatch(name, filter, layout, true, Level.valueOf(minlevel));
  }

  @Override
  public synchronized void append(final LogEvent event) {
    byte[] byteArray = getLayout().toByteArray(event);
    if (logfile != null) {
      try {
        Files.write(new File(logfile).toPath(), byteArray, StandardOpenOption.APPEND,
            StandardOpenOption.CREATE);
      } catch (IOException e) {
        System.err.println(e);
      }
    }
    System.out.print(new String(byteArray));
  }

}
