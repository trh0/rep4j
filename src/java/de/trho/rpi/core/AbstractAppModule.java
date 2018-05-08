package de.trho.rpi.core;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractAppModule implements AppModule {

  protected final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  private CoreController                          ctrl;
  private final RunMode                           runMode;
  private final AtomicBoolean                     running;

  private volatile long                           rate   = 10000;

  private ScheduledFuture<?>                      future;

  public AbstractAppModule(RunMode runMode) {
    this.runMode = runMode;
    this.running = new AtomicBoolean(false);
  }

  @Override
  public void setController(final CoreController c) {
    this.ctrl = c;
  }

  protected CoreController getController() {
    if (this.ctrl == null) {
      throw new IllegalStateException("Module not yet added by CoreController");
    }
    return this.ctrl;
  }

  protected synchronized void setRunning(boolean running) {
    this.running.set(running);
  }

  @Override
  public long getScheduleRate() {
    return this.rate;
  }

  public void setScheduleRate(long millis) {
    this.rate = millis;
  }

  @Override
  public RunMode getRunMode() {
    return this.runMode;
  }

  @Override
  public synchronized boolean isRunning() {
    return this.running.get();
  }

  @Override
  public void exit() {
    this.setRunning(false);
    ScheduledFuture<?> fut = this.getFuture();
    if (fut != null)
      fut.cancel(true);
  }

  @Override
  public ScheduledFuture<?> getFuture() {
    return future;
  }

  @Override
  public void setFuture(ScheduledFuture<?> future) {
    this.future = future;
  }

}
