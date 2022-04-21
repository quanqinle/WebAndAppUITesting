package com.quanql.test.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.*;

/**
 * Test result Listener.
 *
 * @author kevinkong
 */
public class TestResultListener extends TestListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(TestResultListener.class);

  @Override
  public void onTestFailure(ITestResult tr) {
    super.onTestFailure(tr);
    logger.info(tr.getName() + " Failure");
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
    super.onTestSkipped(tr);
    logger.info(tr.getName() + " Skipped");
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
    super.onTestSuccess(tr);
    logger.info(tr.getName() + " Success");
  }

  @Override
  public void onTestStart(ITestResult tr) {
    super.onTestStart(tr);
    logger.info(tr.getName() + " Start");
  }

  @Override
  public void onFinish(ITestContext testContext) {
    super.onFinish(testContext);

    // List of test results which we will delete later
    ArrayList<ITestResult> testsToBeRemoved = new ArrayList<>();
    // collect all id's from passed test
    Set<Integer> passedTestIds = new HashSet<>();
    for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
      logger.info("PassedTests = " + passedTest.getName());
      passedTestIds.add(getId(passedTest));
    }

    // Eliminate the repeat methods
    Set<Integer> skipTestIds = new HashSet<>();
    for (ITestResult skipTest : testContext.getSkippedTests().getAllResults()) {
      logger.info("skipTest = " + skipTest.getName());
      // id = class + method + dataprovider
      int skipTestId = getId(skipTest);

      if (skipTestIds.contains(skipTestId) || passedTestIds.contains(skipTestId)) {
        testsToBeRemoved.add(skipTest);
      } else {
        skipTestIds.add(skipTestId);
      }
    }

    // Eliminate the repeat failed methods
    Set<Integer> failedTestIds = new HashSet<>();
    for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
      logger.info("failedTest = " + failedTest.getName());
      // id = class + method + dataprovider
      int failedTestId = getId(failedTest);

      // if we saw this test as a failed test before we mark as to be
      // deleted
      // or delete this failed test if there is at least one passed
      // version
      if (failedTestIds.contains(failedTestId)
          || passedTestIds.contains(failedTestId)
          || skipTestIds.contains(failedTestId)) {
        testsToBeRemoved.add(failedTest);
      } else {
        failedTestIds.add(failedTestId);
      }
    }

    // finally delete all tests that are marked
    for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator();
        iterator.hasNext(); ) {
      ITestResult testResult = iterator.next();
      if (testsToBeRemoved.contains(testResult)) {
        logger.info("Remove repeat Fail Test: " + testResult.getName());
        iterator.remove();
      }
    }
  }

  private int getId(ITestResult result) {
    int id = result.getTestClass().getName().hashCode();
    id = id + result.getMethod().getMethodName().hashCode();
    id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
    return id;
  }
}
