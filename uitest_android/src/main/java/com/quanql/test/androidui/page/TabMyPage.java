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

  // 点击我的信息区域进入个人资料页
  public static Button PersonalDataBtn = new Button("id=my_page_header_baby_name");

  /** [立即登录]按钮 tv_login */
  public static Button LoginNowBtn = new Button("id=tv_login");
  /** [我的订单] */
  public static Button MyOrders = new Button("id=my_page_ordes");
  /** 新人注册按钮 */
  public static Button MyRegister = new Button("id=tv_register");
  /** 点击收藏 */
  public static Button MyCollections = new Button("id=ll_my_page_collection");

  /** 我的拼團 */
  public static Button MyFightGroup = new Button("name=我的拼团");

  /** 点击消息中心 */
  public static Button Message = new Button("id=ll_top_left");

  /** 点击现金券~余额 */
  public static Button Cash = new Button("id=my_page_ticket");

  /** 点击贝壳、余额 */
  public static Button Shell = new Button("id=my_page_balance");
  /** 注册完成弹出的起名字弹窗，弹窗的取消按钮 */
  public static Button CalcelNameBtn = new Button("id=iv_modify_nick_cancel");
  /** 设置按钮 */
  public static Button SetBtn = new Button("id=ll_top_right");
  /** 签到领钱 */
  public static Button SignBtn = new Button("name=签到领钱");
  /** 登录后，宝宝年龄 */
  public static Button ageBtn = new Button("id=my_page_header_baby_name");

  /** 客服中心 */
  public static Button customerSerBtn = new Button("id=ll_my_page_serveice_center");
  /** [HTTPS开关] */
  public static Button Https = new Button("id=cb_dev_https");
  // Debug
  public static Button Developer = new Button("id=rl_new_setting_dev");
  // 元素
  private static String myPageAppId = "ll_main";

  /**
   * 我的页面是否存在
   *
   * @return
   */
  public static boolean isMyPageShow() {

    LogUtil.info("我的页面是否存在");
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
