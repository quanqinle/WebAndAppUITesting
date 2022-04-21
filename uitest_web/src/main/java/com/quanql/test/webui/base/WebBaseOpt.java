package com.quanql.test.webui.base;

import com.quanql.test.core.base.BaseOpt;
import com.quanql.test.core.base.DriverFactory;
import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * web
 *
 * @author quanqinle
 */
public class WebBaseOpt extends BaseOpt {

  private static WebBaseOpt baseOpt;

  private WebBaseOpt() {
    driver = DriverFactory.getInstance().getDriver();
  }

  public static WebBaseOpt getInstance() {
    if (baseOpt == null) {
      LogUtil.info("WebBaseOpt-getInstance()-new");
      baseOpt = new WebBaseOpt();
    }
    return baseOpt;
  }

  /**
   * 打开url页面
   *
   * @param url -
   */
  public void open(String url) {
    try {
      driver.get(url);
      LogUtil.info(driver.manage().logs() + "==>打开URL：" + url + "成功！");
    } catch (Exception e) {
      LogUtil.error(
          driver.manage().logs() + "==>打开URL：" + url + "失败！当前url：" + driver.getCurrentUrl() + e);
      this.screenShot();
      AssertUtil.fail(driver.manage().logs() + "==>打开URL：" + url + "失败！" + e);
    }
  }

  /**
   * 获取窗口数目
   *
   * @return -
   */
  public int getWindowCount() {
    return driver.getWindowHandles().size();
  }

  /**
   * 切换窗口
   *
   * @param i 从0开始
   */
  public void changeWindow(int i) {
    // TODO 等待新窗口出现。以后可以优化
    baseOpt.wait(50);
    Set<String> wh = driver.getWindowHandles();
    List<String> w = new ArrayList<>(wh);
    try {
      driver.switchTo().window(w.get(i));
    } catch (Exception e) {
      LogUtil.info(driver.manage().logs() + "==> 窗口数=" + w.size());
      this.screenShot();
      AssertUtil.fail("切换窗口失败");
    }
  }

  public void changeNewWindow() {
    Set<String> wh = driver.getWindowHandles();
    List<String> w = new ArrayList<>(wh);
    try {
      driver.switchTo().window(w.get(w.size() - 1));
    } catch (Exception e) {
      LogUtil.info(driver.manage().logs() + "==>窗口数=" + w.size());
      LogUtil.info(driver.manage().logs() + "==>窗口内容=" + w);
      this.screenShot();
      AssertUtil.fail("切换窗口失败");
    }
  }

  /** 切换到alter，并点击确认框 */
  public void clickAlert(String msg) {
    WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Alert alert;
    baseOpt.wait(20);
    // Alert alert = driver.switchTo().alert();

    try {
      // 不稳定，原因待查。还是用上面的吧
      // wait and switchTo, Otherwise, throws a TimeoutException
      alert = webDriverWait.until(ExpectedConditions.alertIsPresent());
    } catch (Exception e) {
      this.screenShot();
      LogUtil.info(driver.manage().logs() + "==>alert等待超时!");
      // 与 wait until 功能重复，但until经常失败，为了增强健壮性才如此写
      alert = driver.switchTo().alert();
    }

    if (msg != null) {
      AssertUtil.assertEquals(alert.getText(), msg, "提示语错误");
    }

    alert.accept();
    baseOpt.wait(30);
  }

  /**
   * 比较url与预期值。当然url包含期望url
   *
   * @param loop 次数。每秒重试一次
   * @param url -
   * @return -
   */
  public boolean compareUrl(String url, int loop) {
    boolean isSame = false;
    String currentUrl = null;
    for (int i = 0; i < loop; i++) {
      currentUrl = driver.getCurrentUrl();
      if (currentUrl.contains(url)) {
        isSame = true;
        break;
      } else {
        wait(10);
      }
    }
    if (!isSame) {
      this.screenShot();
      LogUtil.error(
          driver.manage().logs() + "==>" + "url(" + currentUrl + ")与基准值(" + url + ")不一致！");
    } else {
      LogUtil.info(driver.manage().logs() + "==>" + "url与基准值一致！");
    }
    return isSame;
  }

  /** 刷新页面 */
  public void refresh() {
    driver.navigate().refresh();
  }

  /**
   * 等待frame可以用，切换到指定的frame
   *
   * @param frameBy -
   */
  public void switchTo(By frameBy) {
    WebDriverWait webDriverWait =
        new WebDriverWait(driver, Duration.ofSeconds(DRIVER_WAIT_TIMEOUT_IN_SECOND));
    try {
      // wait and switchTo, Otherwise, throws a TimeoutException
      webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameBy));
      // driver.switchTo().frame(this.findElement(frameBy));
      LogUtil.info(driver.manage().logs() + "==>switchTo frame：" + frameBy.toString() + "成功.");
    } catch (Exception e) {
      this.screenShot();
      LogUtil.info(driver.manage().logs() + "==>switchTo frame：" + frameBy.toString() + "等待超时!");
      // 与wait until功能重复，但until经常失败，为了增强健壮性才如此写
      driver.switchTo().frame(this.findElement(frameBy));
    }
  }

  /**
   * 点击frame中的元素
   *
   * @param frameBy -
   * @param by -
   */
  public void clickInFrame(By frameBy, By by) {
    this.switchTo(frameBy);
    this.click(by);
    // tip：在此增加wait()将增加click的成功率！
    baseOpt.wait(20);
    driver.switchTo().defaultContent();
  }

  /**
   * 在frame中的输入框输入
   *
   * @param frameBy -
   * @param by -
   * @param text -
   */
  public void typeInFrame(By frameBy, By by, String text) {
    this.switchTo(frameBy);
    this.input(by, text);
    driver.switchTo().defaultContent();
  }

  /**
   * 在frame中查找元素
   *
   * @param frameBy -
   * @param by -
   */
  public void findElementInFrame(By frameBy, By by) {
    this.switchTo(frameBy);
    this.findElement(by);
    driver.switchTo().defaultContent();
  }

  /**
   * Scroll the page to the specific element via xpath.
   *
   * @param by -
   */
  public void scrollToElement(By by) {
    WebElement target = driver.findElement(by);
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", target);
  }
}
