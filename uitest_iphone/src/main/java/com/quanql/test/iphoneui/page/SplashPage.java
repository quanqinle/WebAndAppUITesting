package com.quanql.test.iphoneui.page;

import com.quanql.test.iphoneui.base.IphoneBasePage;
import com.quanql.test.core.utils.LogUtil;

/**
 * 启动页
 * 
 * @author 权芹乐
 *
 */
public class SplashPage extends IphoneBasePage {
	/**
	 * 滑动完新人引导页，到达引导页的最后1页
	 * 
	 * @author quanqinle
	 */
	public static void slideUserGuide() {
		LogUtil.info("右滑完新人引导页");
		scrollToLeft(6, 10);
	}

}
