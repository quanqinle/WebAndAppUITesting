package com.quanql.test.webui.page;

import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.webui.base.WebBasePage;

public class SellerPage extends WebBasePage {

	/**
	 * 通过url打开卖家中心页面
	 */
	public static void openUrl() {
		LogUtil.info("通过url打开卖家中心页面");
		baseOpt.open("http://seller.beibei.com/portal.html");
	}
}
