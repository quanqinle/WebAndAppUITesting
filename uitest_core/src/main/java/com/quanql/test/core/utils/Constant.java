package com.quanql.test.core.utils;

/**
 * 全局常量
 *
 * @author 权芹乐
 */
public final class Constant {

  public static final String ROOT = System.getProperty("user.dir");

  /** 测试类型，全小写，如android、chrome等 */
  public static final String DRIVER_TYPE =
      ConfigUtil.getInstance().getProperty("driver.type", "android").toLowerCase();

  /** 被测APP包名 */
  public static final String APP_PACKAGE_NAME =
      ConfigUtil.getInstance().getProperty("appPackage", "com.quanqinle.android");

  /** 屏幕宽度 */
  public static int SCREEN_WIDTH = 0;

  public static int SCREEN_HEIGHT = 0;

  /** 拖动速度，毫秒 */
  public static final int SCROLL_FAST = 500;

  public static final int SCROLL_MEDIUM = 1000;
  public static final int SCROLL_SLOW = 3000;

  /** Return code */
  public enum PhoneState {
    STATE_OK,
    STATE_NOT_ADB,
    STATE_NOT_FOUND_PHONE,
    STATE_MULTI_FOUND_PHONE
  }

  public static final String OFFLINE = "device offline";
  public static final String NOT_FOUND = "device not found";
}
