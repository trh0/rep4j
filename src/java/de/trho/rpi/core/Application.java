package de.trho.rpi.core;

import java.util.Deque;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import de.trho.rpi.com.PacketGateway;
import de.trho.rpi.util.Lookup;

/**
 * Implementation of the CoreController
 * 
 * @author trh0
 * 
 */
public class Application implements CoreController {

  private final org.apache.logging.log4j.Logger logger         =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  public static final String                    STOP_SIGNAL    = "!q";
  private final AtomicBoolean                   runFlag        = new AtomicBoolean(true);
  private final Deque<KeyInputListener>         inputListeners = new LinkedBlockingDeque<>();
  private final Deque<Runnable>                 shutdownHooks  = new LinkedBlockingDeque<>();

  private final ExecutorService                 execInput;
  ScheduledExecutorService                      execModule;
  private final Deque<AppModule>                repeat, loop, startOnce;

  private final PacketGateway                   com;

  public Application() {
    logger.info("Init RpiApplication");
    this.execInput = Executors.newSingleThreadExecutor();
    this.execModule = Executors.newScheduledThreadPool(2);
    this.startOnce = new LinkedBlockingDeque<>();
    this.repeat = new LinkedBlockingDeque<>();
    this.loop = new LinkedBlockingDeque<>();

    this.com = Lookup.find(PacketGateway.class);

    setup();
  }

  @Override
  public PacketGateway getPacketGateway() {
    return this.com;
  }

  @Override
  public void addShutdownHook(final Runnable r) {
    if (!this.shutdownHooks.contains(r))
      this.shutdownHooks.add(r);
  }

  @Override
  public void addModule(final AppModule m) {
    m.setController(this);
    if (m != null && m.getRunMode() != null) {
      switch (m.getRunMode()) {
        case LOOP:
        case ONCE:
          if (!this.startOnce.contains(m)) {
            startOnce.add(m);
          }
          if (!m.isRunning())
            this.execModule.execute(m);
          break;
        case REPEAT:
          if (!this.repeat.contains(m)) {
            m.setFuture(
                this.execModule.scheduleAtFixedRate(m, (long) (Math.random() * m.getScheduleRate()),
                    m.getScheduleRate(), TimeUnit.MILLISECONDS));
            this.repeat.offer(m);
          }
          break;
        default:
          logger.warn("No appropiate runmode found for module {}", m);
      }
    }
  }

  @Override
  public void removeModule(AppModule m) {
    this.loop.remove(m);
    this.startOnce.remove(m);
    this.repeat.remove(m);
    m.getFuture().cancel(true);
  }

  private void setup() {

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.debug("Executing shutdown-hook");
      execInput.shutdownNow();
      execModule.shutdownNow();
      this.loop.forEach(AppModule::exit);
      this.repeat.forEach(AppModule::exit);
      this.startOnce.forEach(AppModule::exit);
    }));

    if (com == null) {
      logger.error("No com-controller implementation available. Application will shutdown now");
      System.exit(0);
    }

    execInput.execute(() -> {
      try (final Scanner sc = new Scanner(System.in)) {
        String signal;
        while (!STOP_SIGNAL.equals(signal = sc.next())) {
          final byte[] bytes = signal.getBytes();
          inputListeners.forEach(l -> l.notifyInput(bytes));
        }
        synchronized (runFlag) {
          runFlag.set(false);
        }
      }
    });

  }

  @Override
  public void subscribeInput(KeyInputListener listener) {
    if (!inputListeners.contains(listener))
      this.inputListeners.addLast(listener);
  }

  @Override
  public void unsubscribeInput(KeyInputListener listener) {
    this.inputListeners.remove(listener);
  }

}
