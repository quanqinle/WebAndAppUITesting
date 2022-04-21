package com.quanql.test.core.base;

import com.google.common.base.Strings;
import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * 常用事件封装 <br>
 * 只有 web/android/ios 都适用的，才放在这里<br>
 * 警告:如需修改此 class，请告知项目管理员
 *
 * @author 权芹乐
 */
public class BaseOpt {

  protected static BaseOpt baseOpt;
  /** second */
  protected static int DRIVER_WAIT_TIMEOUT_IN_SECOND =
      Integer.parseInt(ConfigUtil.getInstance().getProperty("waitTime"));
  /** 可配置 */
  protected static String PROJ_PACKAGE_NAME = "com.quanql.test";

  protected RemoteWebDriver driver;
  protected WebDriverWait wait;

  protected BaseOpt() {
    driver = DriverFactory.getInstance().getDriver();
    // WebDriverWait(WebDriver driver, long timeOutInSeconds, long
    // sleepInMillis)
    wait =
        new WebDriverWait(
            driver, Duration.ofSeconds(DRIVER_WAIT_TIMEOUT_IN_SECOND), Duration.ofMillis(500));
  }

  public static BaseOpt getInstance() {
    if (baseOpt == null) {
      LogUtil.debug("BaseOpt-getInstance()-new");
      baseOpt = new BaseOpt();
    }
    return baseOpt;
  }

  /**
   * 兼容循环执行时 不会重新加载构造方法的问题
   *
   * @return -
   */
  public RemoteWebDriver getDriver() {
    if (driver == null || driver.getSessionId() == null) {
      driver = DriverFactory.getInstance().getDriver();
      wait =
          new WebDriverWait(
              driver, Duration.ofSeconds(DRIVER_WAIT_TIMEOUT_IN_SECOND), Duration.ofMillis(500));
    }
    return driver;
  }

  public void setDriver(RemoteWebDriver driver) {
    this.driver = driver;
  }

  /**
   * 线程休眠/等待<br>
   * 注意：click(),type()等自带等待，不需要再wait()!!
   *
   * @param millis 毫秒。<=0时，无效
   */
  public void wait(int millis) {
    if (0 >= millis) {
      return;
    }
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      LogUtil.debug("等待被中断");
    }
  }

  /**
   * 点击元素
   *
   * @param element 元素
   */
  public void click(WebElement element) {
    try {
      element.click();
      LogUtil.debug(getDriver().manage().logs() + "==>" + element + " 点击成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + element.toString() + " 点击失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>" + element + " 点击失败！" + e);
    }
  }

  /**
   * 点击元素
   *
   * @param by -
   */
  public void click(By by) {
    try {
      // 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
      driver = getDriver();
      wait =
          new WebDriverWait(
              driver, Duration.ofSeconds(DRIVER_WAIT_TIMEOUT_IN_SECOND), Duration.ofMillis(500));
      WebElement element = waitForElementClickable(by);
      element.click();
      // findElement(by).click();
      LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 点击成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 点击失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>" + by + " 点击失败！" + e);
    }
  }

  /**
   * 点击对第num个元素by
   *
   * @param by -
   * @param num 相同元素的第num个，从0开始
   */
  public void click(By by, int num) {
    try {
      waitForElementClickable(by);
      findElements(by).get(num).click();
      LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 点击内容成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 点击内容失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>" + by + " 点击内容失败！" + e);
    }
  }

  /**
   * 公用的输入操作
   *
   * @param by -
   * @param element 元素
   * @param text 文本
   */
  private void sendKeys(By by, WebElement element, String text) {
    int tryLoop = 3;

    if ("input".equalsIgnoreCase(element.getTagName())
        || "textarea".equalsIgnoreCase(element.getTagName())) {
      boolean bl = false;
      for (int i = 0; i < tryLoop; i++) {
        element.clear();
        // element.setValue(text); //官网论坛说，执行效率比sendKeys高。但遇到输密码会出错
        element.sendKeys(text);
        if (element.getAttribute("value").equals(text)) {
          bl = true;
          break;
        }
      }
      if (!bl) {
        LogUtil.error(
            getDriver().manage().logs()
                + "==>"
                + by.toString()
                + " 输入内容失败！ 预期输入："
                + text
                + "；实际输入："
                + element.getAttribute("value"));
        screenShot();
        AssertUtil.fail(
            getDriver().manage().logs()
                + "==>"
                + by
                + " 输入内容失败！ 预期输入："
                + text
                + "；实际输入："
                + element.getAttribute("value"));
      }
    } else if ("div".equalsIgnoreCase(element.getTagName())) {
      boolean bl = false;
      for (int i = 0; i < tryLoop; i++) {
        element.clear();
        element.sendKeys(text);
        if (element.getText().equals(text)) {
          bl = true;
          break;
        }
      }
      if (!bl) {
        LogUtil.error(
            getDriver().manage().logs()
                + "==>"
                + by.toString()
                + " 输入内容失败！ 预期输入："
                + text
                + "；实际输入："
                + element.getAttribute("value"));
        screenShot();
        AssertUtil.fail(
            getDriver().manage().logs()
                + "==>"
                + by
                + " 输入内容失败！ 预期输入："
                + text
                + "；实际输入："
                + element.getAttribute("value"));
      }
    } else {
      element.clear();
      element.sendKeys(text);
    }

    LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容成功！");
  }

  /**
   * 输入文本
   *
   * @param by -
   * @param text 文本
   */
  public void sendKeys(By by, String text) {
    WebElement element = waitForElementClickable(by);
    sendKeys(by, element, text);
  }

  /**
   * 在相同元素的第num个输入文本
   *
   * @param by -
   * @param num 序号
   * @param text 文本
   */
  public void sendKeys(By by, int num, String text) {
    waitForElementClickable(by);
    WebElement element = findElements(by).get(num);
    sendKeys(by, element, text);
  }

  /**
   * 查找元素
   *
   * @param by -
   * @return 元素
   */
  public WebElement findElement(By by) {
    WebElement element = null;
    try {
      element = getDriver().findElement(by);
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>" + by + " 未找到！" + e);
    }

    return element;
  }

  /**
   * 几秒后查看元素不存在
   *
   * @param by -
   * @param millis 等待的毫秒
   */
  public void findElementNotExist(By by, int millis) {
    if (millis > 0) {
      wait(millis);
    }
    try {
      getDriver().findElement(by);
      LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 找到了！");
      screenShot();
      AssertUtil.fail(by + " 找到了！");
    } catch (Exception e) {
      LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
    }
  }

  /**
   * 查找元素集合
   *
   * @param by -
   * @return 元素list
   */
  public List<WebElement> findElements(By by) {
    List<WebElement> elementList = null;
    try {
      elementList = getDriver().findElements(by);
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！" + e);
      screenShot();
      AssertUtil.fail(by + " 未找到！");
    }

    return elementList;
  }

  /**
   * 检查元素是否存在
   *
   * @param by -
   * @return true存在
   */
  public boolean isElementExisted(By by) {
    boolean b;
    try {
      getDriver().findElement(by);
      b = true;
    } catch (Exception e) {
      LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
      b = false;
      screenShot();
    }

    return b;
  }

  /**
   * 检查元素是否可见<br>
   * 立即检查，受implicitly.wait影响（同findElement()）
   *
   * @param by -
   * @return true存在
   */
  public boolean isElementDisplayed(By by) {
    boolean b;
    try {
      b = getDriver().findElement(by).isDisplayed();
      LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 可见：" + b);
    } catch (Exception e) {
      LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
      b = false;
      // screenShot();
    }

    return b;
  }

  /** 屏幕截屏 */
  public void screenShot() {
    long ts = System.currentTimeMillis();
    // 截图存放目录
    String dirName = "screenshot";
    if (!(new File(dirName).isDirectory())) {
      // 不存在则新建目录
      (new File(dirName)).mkdirs();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String time = sdf.format(new Date());

    StackTraceElement[] stack = (new Throwable()).getStackTrace();

    String className = "";
    int line = 0;
    // for循环找到项目中最上层的类文件
    for (StackTraceElement ste : stack) {
      /*
       * PROJ_PACKAGE_NAME 可自定义。
       * 作用：调用栈中找到工程的包名，然后截图，即，避免使用底层类命名截图
       */
      if (ste.getClassName().contains(PROJ_PACKAGE_NAME)) {
        className = ste.getFileName();
        line = ste.getLineNumber();
      }
    }
    LogUtil.debug("PROJ_PACKAGE_NAME：" + PROJ_PACKAGE_NAME);

    if (!Strings.isNullOrEmpty(className)) {
      className = className.substring(0, className.indexOf("."));
    }
    LogUtil.info("用例：" + className + "在第" + line + "行失败产生截图：" + time + className + ".png");
    WebDriver augmentedDriver = new Augmenter().augment(driver);
    File sourceFile = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
    try {
      Files.copy(sourceFile.toPath(), Paths.get(dirName, time + className + ".png"));
    } catch (IOException e) {
      LogUtil.error("用例：" + className + "在第" + line + "行失败产生截图失败：" + e);
    }
    long te = System.currentTimeMillis();
    LogUtil.info("截屏耗时ms: " + (te - ts));
  }

  /**
   * 在当前光标处，通过键盘输入文本（适用于无法获取 element 时）
   *
   * @author 权芹乐
   * @param text 输入
   */
  public void sendKeysByKeyboard(String text) {
    try {
      //      getDriver().getKeyboard().sendKeys(text);
      Actions actions = new Actions(getDriver());
      actions.sendKeys(text);
      LogUtil.info(getDriver().manage().logs() + "==>" + text + " 键盘输入成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>" + text + " 键盘输入失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>" + text + " 键盘输入失败！" + e);
    }
  }

  /**
   * Wait for the element displayed in specific duration via locator.
   *
   * @param by locator of the element
   * @return WebElement
   */
  public WebElement waitForElementDisplayed(By by) {
    try {
      // 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
      driver = getDriver();
      wait =
          new WebDriverWait(
              driver, Duration.ofSeconds(DRIVER_WAIT_TIMEOUT_IN_SECOND), Duration.ofMillis(500));
      return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    } catch (Exception e) {
      LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + "判断visibility失败！");
      // LogUtil.info(e.getMessage());
      return null;
    }
  }

  /**
   * 等待默认超时时间可以自己设置， 比如滑动页面寻找元素时，没有必要等待很长时间，不确定出现时间可以使用默认等待时间。
   *
   * @param by -
   * @param outTime 超时时间，秒
   * @return 元素
   */
  public WebElement waitForElementClickable(By by, int outTime) {
    try {
      // 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
      driver = getDriver();
      wait = new WebDriverWait(driver, Duration.ofSeconds(outTime), Duration.ofMillis(200));
      return wait.until(ExpectedConditions.elementToBeClickable(by));
    } catch (Exception e) {
      LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + "判断Clickable失败！");
      return null;
    }
  }

  /** wait util elementToBeClickable */
  public WebElement waitForElementClickable(By by) {
    return waitForElementClickable(by, DRIVER_WAIT_TIMEOUT_IN_SECOND);
  }

  /**
   * 查找元素，找不到则返回null,不截屏抛异常。用户下一步判断. 使用该方法 key必须经过getObfuscatedID 判断混淆ID
   *
   * @param key 元素关键字，//开头代表xpath，否则是id
   * @return 元素
   */
  public WebElement findElement(String key) {

    WebElement element;
    try {
      if (key.startsWith("//")) {
        element = getDriver().findElement(By.xpath(key));
      } else {
        element = getDriver().findElement(By.id(key));
      }
    } catch (Exception e) {
      return null;
    }
    return element;
  }
}
