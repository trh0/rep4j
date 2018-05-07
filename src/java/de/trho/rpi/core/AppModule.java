package de.trho.rpi.core;

import java.util.concurrent.ScheduledFuture;

public interface AppModule extends Runnable {

  RunMode getRunMode();

  public static enum RunMode {
    LOOP, REPEAT, ONCE;
  }

  boolean isRunning();

  void setController(CoreController c);

  long getScheduleRate();

  void exit();

  void setFuture(ScheduledFuture<?> future);

  ScheduledFuture<?> getFuture();
}
