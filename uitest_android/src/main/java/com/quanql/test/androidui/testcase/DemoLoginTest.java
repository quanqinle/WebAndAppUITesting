package com.quanql.test.androidui.testcase;

import com.quanql.test.androidui.base.AndroidBaseTest;
import com.quanql.test.androidui.page.LoginPage;
import com.quanql.test.androidui.page.SplashPage;
import com.quanql.test.androidui.page.TabHomePage;
import com.quanql.test.androidui.page.TabMyPage;
import com.quanql.test.core.base.DriverFactory;
import com.quanql.test.core.utils.AssertUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * DEMO：手机号登录
 *
 * @author 权芹乐
 */
public class DemoLoginTest extends AndroidBaseTest {

  @Override
  @BeforeMethod
  public void aBeforeMethod() {
    // 重构父类方法，先设置全局变量值 cap_noReset值为false，该启动app
    DriverFactory.setNoReset(false);
    initParameters();
  }

  @Test(dataProvider = "providerMethod")
  public void testLoginIn(String username, String passwd) {

    // 增加等待时间
    SplashPage.wait(20000);
    // 完成新人引导并关闭新人一元购提示框
    SplashPage.completeUserGuide();
    TabHomePage.clickUpdateMessage();
    TabHomePage.closeAdsDialog();
    // 关闭弹窗
    // TabHomePage.DialogCloseBtn.click();
    // 点 我的TAB
    TabHomePage.TabMine.click();
    // 点 立即登录
    TabMyPage.LoginNowBtn.click();
    // 输入用户名
    LoginPage.UserName.input(username);
    // 输入密码
    LoginPage.PassWord.input(passwd);
    // 点击
    LoginPage.LoginBtn.click();
    // 断言判断登录成功
    AssertUtil.assertTrue(!TabMyPage.isNeedLogin(), "没有登录成功");
  }
}
