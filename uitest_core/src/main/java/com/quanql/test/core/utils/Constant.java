package com.quanql.test.core.utils;

/**
 * 全局常量
 * 
 * @author 权芹乐
 * 
 */
public final class Constant {
	
	public static final String ROOT = System.getProperty("user.dir");
	
	/**
	 * 测试类型，全小写，如android、chrome等
	 */
	public static final String DRIVER_TYPE = ConfigUtil.getInstance().getProperty("driver.type", "android").toLowerCase();

	/**
	 * 被测APP包名
	 */
	public static final String APP_PACKAGE_NAME = ConfigUtil.getInstance().getProperty("appPackage", "com.quanqinle.android");

	public static int SCREEN_WIDTH = 0; // 屏幕宽度
	public static int SCREEN_HEIGHT = 0;

	public static final int SCROLL_FAST = 500; // 拖动速度，毫秒
	public static final int SCROLL_MEDIUM = 1000;
	public static final int SCROLL_SLOW = 3000;

	// 测试账号
	public static final String USERNAME0 = ""; // 登陆账号
	public static final String PASSWORD0 = ""; // 密码。建议所有测试账号使用相同的密码！
	public static final String NICKNAME0 = ""; // 用户昵称

	/**
	 * Return code
	 */
	public enum PhoneState {
		STATE_OK, STATE_NOT_ADB, STATE_NOT_FOUND_PHONE, STATE_MULTI_FOUND_PHONE
	}

	public static final String OFFLINE = "device offline";
	public static final String NOT_FOUND = "device not found";
}
