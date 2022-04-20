package com.quanql.test.androidui.page;

import com.quanql.test.core.element.Button;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;

/**
 * 我的页面 @Description
 *
 * @author xueqing.geng @ClassName MyPage 2016年9月22日下午3:44:36
 */
public class TabMyPage extends MainFramePage {

  /** 点击我的信息区域进入个人资料页 */
  public static Button PersonalDataBtn = new Button("id=my_page_header_baby_name");

  /** [立即登录]按钮 tv_login */
  public static Button LoginNowBtn = new Button("id=tv_login");

  /** 签到领钱 */
  public static Button SignBtn = new Button("name=签到领钱");

  /**
   * 我的页面是否存在
   *
   * @return
   */
  public static boolean isMyPageShow() {

    LogUtil.info("我的页面是否存在");
    /** 元素 */
    String myPageAppId = "ll_main";
    return baseOpt.isElementDisplayed(By.id(myPageAppId));
  }

  /**
   * 是否需要登录
   *
   * @return
   */
  public static boolean isNeedLogin() {

    LogUtil.info("用户是否需要登录");

    return baseOpt.isElementDisplayed(By.id(getObfuscatedId("tv_login")));
  }

  /**
   * 判断我的页面打开是否正确
   *
   * @return
   */
  public static boolean tabMyPageCheck() {

    if (baseOpt.findElement("//android.widget.TextView[@text='个人主页']") == null
        || baseOpt.findElement("//android.widget.TextView[@text='我的订单']") == null
        || baseOpt.findElement("//android.widget.TextView[@text='客服中心']") == null) {

      return false;
    }
    return true;
  }
}
