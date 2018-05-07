package de.trho.rpi.util;

import java.util.Properties;

public class Config {

  private static final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(Config.class);
  private static Properties                            props  = new Properties();

  static {
    try {
      props.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
    } catch (Exception e) {
      logger.error("Unable to load config", e);
    }
  }

  public static String getValue(String key) {
    return props.getProperty(key, "");
  }

}
