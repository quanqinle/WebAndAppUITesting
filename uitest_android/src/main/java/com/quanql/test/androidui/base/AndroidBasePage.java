package com.quanql.test.androidui.base;

import com.quanql.test.core.base.BasePage;
import com.quanql.test.core.utils.AndroidIDUtil;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.LogUtil;

/**
 * 页面基础类 <br>
 * 蒙层、顶栏返回之类公共的页面元素，放在这里<br>
 * 警告:如需修改次class，请告知项目管理员
 *
 * @author 权芹乐
 */
public class AndroidBasePage extends BasePage {

  protected static AndroidBaseOpt baseOpt = AndroidBaseOpt.getInstance();

  // for debug
  public static void main(String[] args) {}

  /**
   * 获取当前activity<br>
   * only for Android
   *
   * @return
   */
  public static String getCurrentActivity() {
    return baseOpt.getDriver().currentActivity();
  }

  /**
   * 通过原id获取混淆后的id<br>
   * 找不到映射的关系时，则用原始id
   *
   * @param key 即com.quanql.demo.R.id.key中的key
   * @return 适用于appium的id串，形如com.quanql.demo:id/val
   */
  public static String getObfuscatedId(String key) {
    return AndroidIDUtil.getObfuscatedId(key);
  }

  /**
   * 页面向上滑动1/2屏幕高度，循环loop次<br>
   * 注意：swipe函数在模拟器上不可用！！
   *
   * @author 权芹乐
   * @param loop 循环次数
   * @param period 毫秒。循环间隔时间，可以为0
   */
  public static void scrollToUp(int loop, int period) {
    LogUtil.info("页面向上滑动" + loop / 2 + "屏");
    for (int i = 0; i < loop; i++) {

      baseOpt.swipe(
          Constant.SCREEN_WIDTH / 2,
          Constant.SCREEN_HEIGHT / 5 * 4,
          Constant.SCREEN_WIDTH / 2,
          Constant.SCREEN_HEIGHT / 5,
          Constant.SCROLL_MEDIUM);
      baseOpt.wait(period);
    }
  }

  /**
   * 页面向下滑动1/2屏幕高度，循环loop次
   *
   * @author 权芹乐
   * @param loop 循环次数
   * @param period 毫秒。循环间隔时间，可以为0
   */
  public static void scrollToDown(int loop, int period) {
    LogUtil.info("页面向下滑动" + loop / 2 + "屏");
    for (int i = 0; i < loop; i++) {
      baseOpt.swipe(
          Constant.SCREEN_WIDTH / 2,
          Constant.SCREEN_HEIGHT / 4,
          Constant.SCREEN_WIDTH / 2,
          Constant.SCREEN_HEIGHT / 4 * 3,
          Constant.SCROLL_MEDIUM);
      baseOpt.wait(period);
    }
  }

  /**
   * 页面向左滑动2/3屏幕宽度，循环loop次
   *
   * @author 权芹乐
   * @param loop 循环次数
   * @param period 毫秒。循环间隔时间，可以为0
   */
  public static void scrollToLeft(int loop, int period) {
    LogUtil.info("页面向左滑动" + loop + "屏");
    for (int i = 0; i < loop; i++) {
      baseOpt.swipe(
          Constant.SCREEN_WIDTH / 6 * 5,
          Constant.SCREEN_HEIGHT / 2,
          Constant.SCREEN_WIDTH / 6,
          Constant.SCREEN_HEIGHT / 2,
          Constant.SCROLL_MEDIUM);
      baseOpt.wait(period);
    }
  }

  /**
   * 页面向右滑动2/3屏幕宽度，循环loop次
   *
   * @author 权芹乐
   * @param loop 循环次数
   * @param period 毫秒。循环间隔时间，可以为0
   */
  public static void scrollToRight(int loop, int period) {
    LogUtil.info("页面向右滑动" + loop + "屏");
    for (int i = 0; i < loop; i++) {
      baseOpt.swipe(
          Constant.SCREEN_WIDTH / 6,
          Constant.SCREEN_HEIGHT / 2,
          Constant.SCREEN_WIDTH / 6 * 5,
          Constant.SCREEN_HEIGHT / 2,
          Constant.SCROLL_MEDIUM);
      baseOpt.wait(period);
    }
  }
}
