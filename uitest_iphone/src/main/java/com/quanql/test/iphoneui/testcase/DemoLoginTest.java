package com.quanql.test.iphoneui.testcase;

import org.testng.annotations.Test;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.iphoneui.base.IphoneBaseTest;
import com.quanql.test.iphoneui.page.LoginPage;
import com.quanql.test.iphoneui.page.TabHomePage;
import com.quanql.test.iphoneui.page.TabMyPage;

/**
 * Demo
 * 
 * @author qinle.quan
 *
 */
public class DemoLoginTest extends IphoneBaseTest {
	
	@Test
	public void testLogin() {
		TabHomePage.wait(20000);
		TabHomePage.TabMine.click();
		TabMyPage.LoginNowBtn.click();
		LoginPage.UserName.sendkeys("111111");
		LoginPage.PassWord.sendkeys("2222");
		LoginPage.LoginBtn.click();
		AssertUtil.assertTrue(LoginPage.LoginFailAlert.isDisplayed(), "期望登录失败提示框出现");
		LoginPage.AlertOKBtn.click();
		AssertUtil.assertTrue(LoginPage.LoginBtn.isDisplayed(), "期望立即登录按钮仍存在");
	}
}
