package com.quanql.test.androidperfutils;

import com.quanql.test.androidperfutils.task.CpuTask;
import com.quanql.test.androidperfutils.task.FlowTask;
import com.quanql.test.androidperfutils.task.FrameTask;
import com.quanql.test.androidperfutils.task.MemTask;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.TimeUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控
 *
 * @author 权芹乐
 */
public class PerfMonitor {

  public ExecutorService executor = null; // 暂时不hide，方便灵活使用
  private int poolSize = 3;
  private int period =
      Integer.parseInt(ConfigUtil.getInstance().getProperty("sample.rate")); // ms.采样频率

  public enum PoolType {
    SCHEDULED,
    FIXED;
  }

  // for debug
  public static void main(String[] args) {
    LogUtil.info("开始性能监控...");

    PerfMonitor monitor = new PerfMonitor(PoolType.SCHEDULED, 4);
    monitor.executeAtFixedRate(new CpuTask());
    monitor.executeAtFixedRate(new MemTask());
    monitor.executeAtFixedRate(new FlowTask());
    monitor.executeAtFixedRate(new FrameTask());

    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      LogUtil.debug(TimeUtil.getCurrentDateTime(4));
    }
    // monitor.shutdown();
  }

  public PerfMonitor() {
    this(PoolType.SCHEDULED, 1);
    this.poolSize = 1;
  }

  /**
   * 创建线程池
   *
   * @param poolType 线程池类型 0-scheduled 1-fixed
   * @param poolSize
   */
  public PerfMonitor(PoolType style, int poolSize) {
    this.poolSize = poolSize;
    switch (style) {
      case SCHEDULED:
        this.initScheduledThreadPool(this.poolSize);
        break;
      case FIXED:
        this.initFixedThreadPool(this.poolSize);
        break;
      default:
        this.initScheduledThreadPool(this.poolSize);
        break;
    }
  }

  /**
   * Creates a thread pool that can schedule commands to run after a given delay, or to execute
   * periodically.
   *
   * @param poolSize
   */
  private void initScheduledThreadPool(int poolSize) {
    this.executor = Executors.newScheduledThreadPool(poolSize);
  }

  /**
   * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded
   * queue.
   *
   * @param poolSize
   */
  private void initFixedThreadPool(int poolSize) {
    this.executor = Executors.newFixedThreadPool(poolSize);
  }

  /**
   * 以固定周期频率执行任务
   *
   * @param command the task to execute
   */
  public void executeAtFixedRate(Runnable command) {
    this.executeAtFixedRate(command, 0, this.period, TimeUnit.MILLISECONDS);
  }

  /**
   * 以固定周期频率执行任务
   *
   * @param command the task to execute
   * @param initialDelay the time to delay first execution
   * @param period the period between successive executions
   * @param unit the time unit of the initialDelay and period parameters
   * @return a ScheduledFuture representing pending completion of the task, and whose {@code get()}
   *     method will throw an exception upon cancellation
   */
  public void executeAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    ((ScheduledExecutorService) executor).scheduleAtFixedRate(command, initialDelay, period, unit);
    LogUtil.info(Thread.currentThread().getName() + " executeAtFixedRate()");
  }

  /**
   * 执行任务，for固定大小线程池的ExecutorService
   *
   * @param command
   */
  public void executeTask(Runnable command) {
    executor.execute(command);
  }
  /** 关闭线程池，等待任务终止timeout 5s */
  public void shutdown() {
    this.shutdownAndAwaitTermination(executor);
  }

  /**
   * from javase7 doc
   *
   * @param pool
   */
  private void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(5, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}
