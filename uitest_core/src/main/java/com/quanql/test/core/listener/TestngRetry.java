package com.quanql.test.core.listener;

import com.quanql.test.core.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * TestNG retry Analyzer.
 *
 * <p>TODO replace testNg with Junit
 *
 * @author kevinkong
 */
public class TestngRetry implements IRetryAnalyzer {
  private static final Logger logger = LoggerFactory.getLogger(TestngRetry.class);
  private int retryCount = 1;
  private static int maxRetryCount;

  static {
    ConfigUtil config = ConfigUtil.getInstance();
    maxRetryCount = config.getRetryCount();
    logger.info("retrycount=" + maxRetryCount);
    logger.info("sourceCodeDir=" + config.getSourceCodeDir());
    logger.info("sourceCodeEncoding=" + config.getSourceCodeEncoding());
  }

  @Override
  public boolean retry(ITestResult result) {
    if (retryCount <= maxRetryCount) {
      String message =
          "Retry for ["
              + result.getName()
              + "] on class ["
              + result.getTestClass().getName()
              + "] Retry "
              + retryCount
              + " times";
      logger.info(message);
      Reporter.setCurrentTestResult(result);
      Reporter.log("RunCount=" + (retryCount + 1));
      retryCount++;
      return true;
    }
    return false;
  }

  public static int getMaxRetryCount() {
    return maxRetryCount;
  }

  public int getRetryCount() {
    return retryCount;
  }
}
