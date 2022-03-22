package com.quanql.test.core.base;

import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.Dimension;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;

/**
 * testcase基类<br>
 * testng生命周期公共部分<br>
 * NOTE:如需修改此class，请告知项目管理员
 *
 * @author 权芹乐 2015-01-12
 */
public class BaseTest {

  @BeforeClass
  public void aBeforeClass() {}

  @AfterClass
  public void aAfterClass() {
    LogUtil.info("BaseTest-AfterClass-start");
    DriverFactory.getInstance().quit();
  }

  @BeforeSuite
  public void aBeforeSuite() {}

  @AfterSuite
  public void aAfterSuite() {}

  @BeforeMethod
  public void aBeforeMethod() {
    LogUtil.info("BaseTest-BeforeMethod-start");
    // 启动app
    initParameters();
  }

  @AfterMethod
  public void aAfterMethod() {}

  /**
   * 从config.properties中获取测试环境类型，并根据@Test的函数名，查找读取数据驱动<br>
   * 测试用例函数名，形如testHelloWorld_01()，“必须test开头，可以没有_”
   *
   * @author quanqinle
   * @param m 反射得到的test函数名，函数名必须遵循规范“必须test开头，可以没有_”
   * @return
   */
  @DataProvider
  public Object[][] providerMethod(Method m) {
    String resSonFolder = "testdata";
    String methodName = m.getName();
    String runningEnv = ConfigUtil.getInstance().getProperty("running.env", "online");

    /*
     * 测试用例函数名，形如testHelloWorld_01()，“必须test开头，可以没有_”
     */
    int beginIndex = "test".length();
    int endIndex = !methodName.contains("_") ? methodName.length() : methodName.indexOf("_");
    String fileName = methodName.substring(beginIndex, endIndex) + ".csv";
    String csvFullPath =
        String.join(File.separator, Constant.ROOT, resSonFolder, runningEnv, fileName);
    LogUtil.info("数据驱动：" + csvFullPath);
    return FileUtil.readFromCsv(csvFullPath, 2);
  }

  /** 参数初始化 */
  protected void initParameters() {

    Dimension dimension;

    // 兼容循环执行时，如果sessionId为空，则重新启动
    if (DriverFactory.getInstance().getDriver().getSessionId() == null) {
      DriverFactory.setDriverFactory(null);
    }
    dimension = DriverFactory.getInstance().getDriver().manage().window().getSize();
    Constant.SCREEN_WIDTH = dimension.getWidth();
    Constant.SCREEN_HEIGHT = dimension.getHeight();
    LogUtil.info("界面宽高：" + Constant.SCREEN_WIDTH + "x" + Constant.SCREEN_HEIGHT);
  }
}
