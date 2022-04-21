package com.quanql.test.core.base;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * driver 工厂
 *
 * @author 权芹乐 2016-09-04
 */
public class DriverFactory extends RemoteWebDriver {

  private static DriverFactory driverFactory = null;

  protected RemoteWebDriver driver;
  protected DesiredCapabilities capabilities;

  private static ConfigUtil property;
  private static String driverType;
  private static boolean noResetInCap = true;

  private static boolean CLOSED = false;
  private static boolean QUIT = false;
  private static boolean CLOSED_APP = false;

  /** for debug */
  public static void main(String[] args) {
    LogUtil.info(System.getProperty("user.dir"));
    LogUtil.info(System.getProperty("user.home"));
    LogUtil.info(System.getProperty("user.name"));
    LogUtil.info(System.getProperty("java.class.path"));
  }

  protected DriverFactory() {
    LogUtil.info("BaseTest-DriverFactory-start 3");

    property = ConfigUtil.getInstance();
    driverType = property.getProperty("driver.type");

    createDriver();
  }

  public static DriverFactory getInstance() {
    if (driverFactory == null) {
      driverFactory = new DriverFactory();
    }
    return driverFactory;
  }

  protected void createDriver() {

    if ("Android".equalsIgnoreCase(driverType)) {
      createAndroidDriver(noResetInCap);
    } else if ("iOS".equalsIgnoreCase(driverType)) {
      createIosDriver(noResetInCap);
    } else if ("iOSSafari".equalsIgnoreCase(driverType)) {
      createIosSafariDriver(noResetInCap);
    } else if ("Chrome".equalsIgnoreCase(driverType)) {
      createChromeDriver(false);
    } else if ("H5".equalsIgnoreCase(driverType)) {
      createChromeDriver(true);
    } else if ("Firefox".equalsIgnoreCase(driverType)) {
      createFirefoxDriver();
    } else {
      AssertUtil.fail("unknown driverType = " + driverType);
    }

    long waitTimeInSecond =
        Integer.parseInt(property.getProperty("implicitly.wait.in.second", "10"));
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTimeInSecond));
    driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(1));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
  }

  /**
   * Android app 测试
   *
   * @param noReset 不卸载、不重装
   */
  private void createAndroidDriver(Boolean noReset) {

    capabilities = new DesiredCapabilities();

    // app is absolute path
    capabilities.setCapability(
        "app",
        String.join(File.separator, System.getProperty("user.dir"), property.getProperty("app")));
    capabilities.setCapability("platformName", property.getProperty("platformName"));
    capabilities.setCapability("platformVersion", property.getProperty("platformVersion"));
    // On Android this capability is currently ignored, though it remains required.
    capabilities.setCapability("deviceName", property.getProperty("deviceName"));
    capabilities.setCapability("appPackage", property.getProperty("appPackage"));
    capabilities.setCapability("appActivity", property.getProperty("appActivity"));
    // physical device
    capabilities.setCapability("udid", property.getProperty("udid"));
    // debug时，等待时间调大点！！
    capabilities.setCapability("newCommandTimeout", property.getProperty("newCommandTimeout"));
    // 不卸载、不重装
    capabilities.setCapability("noReset", noReset);
    capabilities.setCapability("noSign", "true");
    // 重置输入法，并且设置可以中文输入
    capabilities.setCapability("unicodeKeyboard", "True");
    capabilities.setCapability("resetKeyboard", "True");

    try {
      driver = new AndroidDriver(new URL(property.getProperty("remote.address")), capabilities);
    } catch (Exception e) {
      e.printStackTrace();
      AssertUtil.fail("初始化对象失败，e = " + e.getMessage());
    }
  }

  /**
   * iOS 上启动 app
   *
   * @param noReset 不卸载、不重装
   */
  private void createIosDriver(Boolean noReset) {

    String appiumServerVersion = property.getProperty("appium.server.version", "1.5");

    capabilities = new DesiredCapabilities();

    // iOS被测app路径是server上的路径
    capabilities.setCapability("app", property.getProperty("app"));
    capabilities.setCapability("platformName", property.getProperty("platformName"));
    capabilities.setCapability("platformVersion", property.getProperty("platformVersion"));
    capabilities.setCapability("deviceName", property.getProperty("deviceName"));
    // debug时，等待时间调大点！！
    capabilities.setCapability("newCommandTimeout", property.getProperty("newCommandTimeout"));
    // 不卸载、不重装
    capabilities.setCapability("noReset", noReset);
    capabilities.setCapability("noSign", "true");
    // 重置输入法，并且设置可以中文输入
    capabilities.setCapability("unicodeKeyboard", "True");
    capabilities.setCapability("resetKeyboard", "True");

    capabilities.setCapability(
        "autoAcceptAlerts", property.getProperty("autoAcceptAlerts", "True"));

    try {
      driver = new IOSDriver(new URL(property.getProperty("remote.address")), capabilities);
    } catch (Exception e) {
      e.printStackTrace();
      AssertUtil.fail("初始化对象失败，e = " + e.getMessage());
    }
  }

  /**
   * 启动iOS的safari浏览器
   *
   * @param noReset 不卸载、不重装
   */
  private void createIosSafariDriver(Boolean noReset) {

    capabilities = new DesiredCapabilities();

    capabilities.setCapability("platformName", property.getProperty("platformName"));
    capabilities.setCapability("platformVersion", property.getProperty("platformVersion"));
    capabilities.setCapability("deviceName", property.getProperty("deviceName"));
    capabilities.setCapability("browserName", property.getProperty("browserName", "Safari"));
    capabilities.setCapability(
        "autoAcceptAlerts", property.getProperty("autoAcceptAlerts", "True"));
    // debug时，等待时间调大点！！
    capabilities.setCapability("newCommandTimeout", property.getProperty("newCommandTimeout"));
    // 不卸载、不重装
    capabilities.setCapability("noReset", noReset);
    capabilities.setCapability("noSign", "true");
    // 重置输入法，并且设置可以中文输入
    capabilities.setCapability("unicodeKeyboard", "True");
    capabilities.setCapability("resetKeyboard", "True");

    try {
      driver = new IOSDriver(new URL(property.getProperty("remote.address")), capabilities);
    } catch (Exception e) {
      e.printStackTrace();
      AssertUtil.fail("初始化对象失败，e = " + e.getMessage());
    }
  }

  private void createFirefoxDriver() {
    driver = new FirefoxDriver();
  }

  /**
   * chrome driver
   *
   * @param isMobileChrome 是否模拟手机浏览器
   */
  private void createChromeDriver(boolean isMobileChrome) {
    // local本地, remote远程
    String runningType = property.getProperty("running.type");

    // note: getRescource start with `/`
    //    System.setProperty("webdriver.chrome.driver",
    // getClass().getResource("/chromedriver.exe").getPath());

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    // options.addExtensions(new File("/path/to/extension.crx"));
    // options.setBinary(new File("/path/to/chrome"));

    // 模拟手机浏览器
    if (isMobileChrome) {
      // 有两种方式：1使用预设的配置（如下），2自定义配置（见链接）
      // https://sites.google.com/a/chromium.org/chromedriver/mobile-emulation
      Map<String, String> mobileEmulation = new HashMap<>(3);
      // 可以修改
      mobileEmulation.put("deviceName", "Nexus 6");
      options.setExperimentalOption("mobileEmulation", mobileEmulation);
    }

    try {
      if ("local".equalsIgnoreCase(runningType)) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
      } else {
        // For use with RemoteWebDriver
        driver = new RemoteWebDriver(new URL(property.getProperty("remote.address")), options);
      }
    } catch (MalformedURLException e) {
      AssertUtil.fail("初始化对象失败，e = " + e.getMessage());
    }

    try {
      driver.manage().window().maximize();
    } catch (Exception e) {
      LogUtil.info(driver.manage().logs() + "全屏浏览器失败");
    }
  }

  public static String getDriverType() {
    return driverType;
  }

  public static void setNoReset(boolean noResetInCap) {
    DriverFactory.noResetInCap = noResetInCap;
  }

  public RemoteWebDriver getDriver() {
    return driver;
  }

  /** driver.close() */
  @Override
  public void close() {
    CLOSED = true;
    driver.close();
  }

  public static boolean isClosed() {
    return CLOSED;
  }

  /** driver.quit() */
  @Override
  public void quit() {
    QUIT = true;
    driver.quit();
  }

  public static boolean isQuit() {
    return QUIT;
  }

  /**
   * appiumDriver.closeApp()<br>
   * only for Android/iOS
   */
  public void closeApp() {
    CLOSED_APP = true;
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.close();
    //    appiumDriver.closeApp();
  }

  public static boolean isCloseApp() {
    return CLOSED_APP;
  }

  public static void setDriverFactory(DriverFactory driverFactory) {
    DriverFactory.driverFactory = driverFactory;
  }
}
