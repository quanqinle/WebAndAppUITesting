package com.quanql.test.androidui.base;

import com.quanql.test.core.base.BaseOpt;
import com.quanql.test.core.base.DriverFactory;
import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.LogUtil;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.LongPressOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * 常用事件封装
 *
 * @author 权芹乐
 */
public class AndroidBaseOpt extends BaseOpt {

  private static AndroidBaseOpt baseOpt = null;
  protected AndroidDriver driver;
  protected TouchAction touchAction;

  private AndroidBaseOpt() {
    driver = (AndroidDriver) DriverFactory.getInstance().getDriver();
    touchAction = new TouchAction(driver);
  }

  public static AndroidBaseOpt getInstance() {
    if (baseOpt == null) {
      LogUtil.debug("AndroidBaseOpt-getInstance()-new");
      baseOpt = new AndroidBaseOpt();
    }
    return baseOpt;
  }

  /** 兼容循环执行时 不会重新加载构造方法的问题 */
  @Override
  @SuppressWarnings("unchecked")
  public AndroidDriver getDriver() {
    if (driver == null || driver.getSessionId() == null) {
      driver = (AndroidDriver) DriverFactory.getInstance().getDriver();
      touchAction = new TouchAction(driver);
    }

    return driver;
  }

  /**
   * 通过键盘换行（不会清除当前行已有的内容）
   *
   * @author 权芹乐
   */
  public void switch2Newline() {
    try {
      getDriver().findElement(By.name("下一个")).click(); // 点击键盘上的换行
      LogUtil.info(getDriver().manage().logs() + "==>键盘点击NEXT成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>键盘点击NEXT失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>键盘点击NEXT失败！" + e);
    }
  }

  /**
   * 长按元素
   *
   * @author 权芹乐
   * @param by
   */
  public void longPress(By by) {
    try {
      // 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
      driver = getDriver();
      touchAction = new TouchAction(driver);
      touchAction.longPress((LongPressOptions) findElement(by)).perform();
      LogUtil.debug(getDriver().manage().logs() + "==>长按" + by.toString() + "成功！");
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>长按" + by.toString() + "失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>长按" + by.toString() + "失败！" + e);
    }
  }

  /** 滑动 */
  public void swipe(int startx, int starty, int endx, int endy, int duration) {
    try {
      // fixme
      //      getDriver().swipe(startx, starty, endx, endy, duration);
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>滚屏失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>滚屏失败！" + e);
    }
  }

  /** 点击物理返回键 */
  public void clickBack() {
    try {
      //      getDriver().pressKeyCode(AndroidKeyCode.BACK); fixme
    } catch (Exception e) {
      LogUtil.error(getDriver().manage().logs() + "==>点击返回失败！" + e);
      screenShot();
      AssertUtil.fail(getDriver().manage().logs() + "==>点击返回失败！" + e);
    }
  }

  /**
   * 在可滚动的父控件parentBy中查找findBy，父控件滚动方向direction，每次滚动耗时duration，最大滑动次数steps<br>
   * 超时，则断言失败
   *
   * @param parentBy 在此元素中查找
   * @param findBy 所要查找的元素
   * @param direction 页面运动方向:0向上, 1向下, 2向左, 3向右
   * @param steps 控件最大滚动次数
   * @return 找到findBy，则返回元素；否则，断言失败
   */
  public WebElement scrollToView(By parentBy, By findBy, int direction, int steps) {
    /*
     * duration:amount of time in milliseconds for the entire swipe action
     * to take
     */
    int duration = 500;
    WebElement parentElement = getDriver().findElement(parentBy);

    // 获取控件开始位置的坐标轴
    Point start = parentElement.getLocation();
    int startX = start.x;
    int startY = start.y;

    // 获取控件坐标轴差
    Dimension q = parentElement.getSize();
    int x = q.getWidth();
    int y = q.getHeight();
    // 计算出控件结束坐标
    int endX = x + startX;
    int endY = y + startY;

    // 计算中间点坐标
    int centreX = (endX + startX) / 2;
    int centreY = (endY + startY) / 2;

    WebElement findElement;
    for (int i = 0; i < steps; i++) {
      // parentElement.findElement(findBy); //在父元素中查找。只xpath适用
      findElement = (WebElement) waitForElementClickable(findBy, 3);
      if (findElement != null) {
        return findElement;
      }
      /*fixme
      switch (direction) {
          // 向上滑动
        case 0:
          getDriver().swipe(centreX, endY * 3 / 4, centreX, endY * 1 / 4, duration);
          break;
          // 向下滑动
        case 1:
          getDriver().swipe(centreX, endY * 1 / 4, centreX, endY * 3 / 4, duration);
          break;
          // 向左滑动
        case 2:
          getDriver().swipe(endX * 3 / 4, centreY, endX * 1 / 4, centreY, duration);
          break;
          // 向右滑动
        case 3:
          getDriver().swipe(endX * 1 / 4, centreY, endX * 3 / 4, centreY, duration);
          break;
        default:
          LogUtil.error("unknown direction:" + direction);
          break;
      }
      */
    } // end for

    LogUtil.error(getDriver().manage().logs() + "==>" + findBy.toString() + " 未找到！");
    screenShot();
    AssertUtil.fail(getDriver().manage().logs() + "==>" + findBy.toString() + " 未找到！");
    return null;
  }

  /**
   * 判断要点击的元素是否被其它元素覆盖
   *
   * @param clickBy
   * @param coverBy
   * @return
   */
  public boolean isCovered(By clickBy, By coverBy) {

    WebElement clickElement = getDriver().findElement(clickBy);
    WebElement coverElement = getDriver().findElement(coverBy);

    // 获取控件开始位置高度
    Point clickstart = clickElement.getLocation();
    int clickStartY = clickstart.y;

    Point coverstart = coverElement.getLocation();
    int coverStartY = coverstart.y;

    // 获取控件高度
    Dimension firstq = clickElement.getSize();
    int height = firstq.getHeight();

    // 控件中间高度是否大于底部高度
    if (clickStartY + height / 2 >= coverStartY) {

      return true;
    }
    return false;
  }
}
