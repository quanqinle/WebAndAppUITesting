package com.quanql.test.androidui.page;

import com.quanql.test.androidui.base.AndroidBasePage;
import com.quanql.test.core.element.Button;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;

/**
 * 启动页
 *
 * @author 权芹乐
 */
public class SplashPage extends AndroidBasePage {
  /** [开启特卖之旅]按钮 */
  public static Button EnterAppBtn = new Button("id=btn_next");

  /** 控件 */
  private static final String VI_GUIDE_PAGE_ID = getObfuscatedId("user_guide_viewpager");

  /**
   * 新人引导页是否存在
   *
   * @return
   */
  public static boolean isUserGuideShow() {
    boolean b = false;
    b = baseOpt.isElementDisplayed(By.id(VI_GUIDE_PAGE_ID));
    LogUtil.info("新人引导页存在：" + b);
    return b;
  }

  /**
   * 滑动完新人引导页，到达引导页的最后1页
   *
   * @author quanqinle
   */
  public static void slideUserGuide() {
    LogUtil.info("右滑完新人引导页");
    scrollToLeft(6, 10);
  }

  //	/**
  //	 * 点击【开启特卖之旅】
  //	 */
  //	public static void clickGotoHomePage() {
  //		LogUtil.info("点击[开启特卖之旅]");
  //		baseOpt.click(By.id(btnEnterAppId));
  //	}

  /** 如果开屏新手引导页存在，则处理并进入首页；否则，do nothing */
  public static void completeUserGuide() {
    if (isUserGuideShow()) {
      slideUserGuide();
      EnterAppBtn.click();
    }
  }
}
