package com.quanql.test.webui.testcase;

import org.testng.annotations.Test;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.webui.base.WebBaseTest;
import com.quanql.test.webui.page.LoginPage;
import com.quanql.test.webui.page.SellerPage;

/**
 * just a demo
 * 
 * @author 权芹乐
 *
 */
public class WebDemoTest extends WebBaseTest{

	@Test
	public void testLogin() {
		LoginPage.openUrl();
		LoginPage.login("oversea_nfc_test@beibei.com", "oversea_nfc_test");
		LoginPage.wait(10000);
		AssertUtil.assertEquals("", "", "搜索失败");
		SellerPage.openUrl();
	}
}
