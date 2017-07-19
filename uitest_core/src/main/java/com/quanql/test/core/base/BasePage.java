package com.quanql.test.core.base;

/**
 * 页面基础类 <br>
 * 只有web/android/ios都适用的，才放在这里<br>
 * 警告:如需修改此class，请告知项目管理员
 * 
 * @author 权芹乐
 *
 */
public class BasePage {
	
	protected static BaseOpt baseOpt = BaseOpt.getInstance();
	
	public BasePage() {
	}

	/**
	 * 线程休眠/等待
	 * @param millis 毫秒
	 */
	public static void wait(int millis) {
		baseOpt.wait(millis);
	}
	
	/**
	 * 获取当前页面元素的DOM树，结果是xml格式的字符串<br>
	 * 当页面无法通过工具识别元素时，可以通过该函数获取页面源码，分析所需元素
	 * 
	 * @author quanqinle
	 * @return xml格式字符串
	 */
	public static String getPageSource() {
		return baseOpt.getDriver().getPageSource();
	}
}
