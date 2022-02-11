package com.quanql.test.iphoneui.base;

import com.quanql.test.core.base.BaseTest;
import com.quanql.test.core.base.DriverFactory;
import com.quanql.test.core.utils.LogUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * testcase基类<br>
 * testng生命周期公共部分
 *
 * @author 权芹乐 2016-09-2
 */
public class IphoneBaseTest extends BaseTest {

  @Override
  @BeforeClass
  public void aBeforeClass() {
    LogUtil.info("iOSBaseTest-beforeClass-start");
  }

  @Override
  @AfterClass
  public void aAfterClass() {}

  @Override
  @AfterMethod
  public void aAfterMethod() {
    LogUtil.info("iOSBaseTest-AfterMethod-start");
    // 关闭app并关闭driver
    DriverFactory.getInstance().closeApp();
    DriverFactory.getInstance().quit();
  }

  /** this will not overwrite parent's aBeforeMethod() */
  @BeforeMethod
  public void bBeforeMethod() {
    LogUtil.info("iOSBaseTest-@BeforeMethod-start");
    // 点击版本更新提示
    //		TabHomePage.clickUpdateMessage();
  }
}
