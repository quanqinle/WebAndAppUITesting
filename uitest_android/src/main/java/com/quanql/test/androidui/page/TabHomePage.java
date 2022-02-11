package com.quanql.test.androidui.page;

import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.androidperfutils.task.FlowTask;
import com.quanql.test.core.element.Button;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * APP主页（今日特卖TAB）
 *
 * @author 权芹乐
 */
public class TabHomePage extends MainFramePage {

  /** 对话框关闭按钮 */
  public static Button DialogCloseBtn = new Button("id=dialog_dismiss");

  /** [进入首页]按钮 */
  public static Button GotoHomepage = new Button("id=tv_bottom");

  /** 主页右上角的搜索 */
  public static Button HomeSearch = new Button("id=ll_top_right");

  /** 头条广告位 */
  public static Button Toutiao = new Button("id=ll_headlines_tap");

  /** 限量购广告位 */
  public static Button Limit = new Button("id=click_ads_left_for_bigdata");

  /** 新品街广告 */
  public static Button NewBrand = new Button("id=click_ads_right_bottom_left");

  /** 辣妈社区icon 点击进入“辣妈社区”页 */
  public static Button Mother = new Button("id=iv_home_btn1");

  /** 亲子玩乐icon 点击可进入“亲子玩乐”页 */
  public static Button Family = new Button("id=iv_home_btn2");

  public static String familyName = "//android.widget.TextView[@text='亲子玩乐']";

  /** 红人街 点击进入“红人街”页 */
  public static Button RedPerson = new Button("id=click_ads_right_top");

  /** 童装分类页婴童入口 */
  public static Button Children = new Button("id=Rv_four_ads_left");

  /** 元素定义，适用于性能测试 */
  private static String tvBrandtitleId = getObfuscatedID("iv_banner"); // 品牌标题

  private static String viMainContentId = getObfuscatedID("home_page_listview");
  private static String rlDiscountId = getObfuscatedID("click_ads_right_top_right"); // 超值量贩
  private static String rlClearId = getObfuscatedID("click_ads_right_bottom_right"); // 清仓
  private static String homeBottom = getObfuscatedID("home_bottom");

  // 今日特卖
  private static String tabHome = getObfuscatedID("tab_home");

  /** 如果广告Dialog存在，则关闭；否则，do nothing */
  public static void closeAdsDialog() {
    if (isAdsDialogShow()) {
      DialogCloseBtn.click();
    }
  }

  /**
   * 新人1元购广告Dialog是否存在
   *
   * @return
   */
  public static boolean isAdsDialogShow() {
    boolean b = false;
    b = baseOpt.isElementDisplayed(By.id(getObfuscatedID("ads_dialog")));
    LogUtil.info("新人1元购广告Dialog存在:" + b);
    return b;
  }

  /**
   * 开屏splash广告是否存在
   *
   * @return
   */
  public static boolean isAdsSplashShow() {
    // FIXME 出现过时效的情况，待查！
    boolean b = false;
    try {
      for (int i = 0; i < 5; i++) {
        if (getCurrentActivity().contains("SplashAdsActivity")) {
          b = true;
          break;
        }
        wait(1000);
      }
    } catch (Exception e) {
    }
    LogUtil.info("开屏splash广告存在:" + b);
    return b;
  }

  /** 如果开屏splash广告存在，则处理并进入首页；否则，do nothing */
  public static void completeAdsSplashPage() {
    if (isAdsSplashShow()) {
      GotoHomepage.click();
    }
  }

  /**
   * 品牌特卖是否存在
   *
   * @return
   */
  public static boolean isBrandDisplay() {
    boolean b = false;
    b = baseOpt.isElementDisplayed(By.id(tvBrandtitleId));
    LogUtil.info("品牌特卖存在:" + b);
    return b;
  }

  /** 找到今日特卖中的品牌入口，并点击，打开“品牌专卖特卖” */
  public static void clickBrand() {
    clickBrand(null);
  }

  /** 找到今日特卖中的品牌入口，并点击，打开“品牌专卖特卖” */
  public static void clickBrand(FlowTask flowTask) {

    baseOpt.waitForElementClickable(By.id(viMainContentId));

    LogUtil.info("查找品牌");
    MobileElement element =
        baseOpt.scrollToView(By.id(viMainContentId), By.id(tvBrandtitleId), 0, 8); // 滚动10屏
    LogUtil.info("点击品牌，进入品牌特卖页");
    if (flowTask != null) {
      flowTask.getfirstFlow();
    }
    element.click();
  }

  /*
   * 点击超值量贩，进入“超值量贩”页
   */
  public static void clickDiscount() {

    LogUtil.info("点击超值量贩，进入“超值量贩”页");

    baseOpt.waitForElementClickable(By.id(rlDiscountId));

    if (baseOpt.isCover(By.id(rlDiscountId), By.id(homeBottom))) {

      TabHomePage.scrollToUp(2, 500);
    }
    baseOpt.click(By.id(rlDiscountId));
  }

  /*
   * 点击特价清仓，进入“特价清仓”页
   */
  public static void clickFinalSale() {

    LogUtil.info("点击特价清仓，进入“特价清仓”页");

    baseOpt.waitForElementClickable(By.id(rlClearId));

    if (baseOpt.isCover(By.id(rlClearId), By.id(homeBottom))) {

      TabHomePage.scrollToUp(1, 500);
    }

    baseOpt.click(By.id(rlClearId));
  }

  /***
   * 判断更新版本提示是否存在，存在则取消更新
   */
  public static void clickUpdateMessage() {

    LogUtil.info("判断是否需要版本更新，存在则取消更新");
    ConfigUtil property = ConfigUtil.getInstance();
    String version = property.getProperty("updateVersion");
    if ("true".equals(version)) {
      baseOpt.click(By.id(getObfuscatedID("update_btn_cancel")));
    }
  }

  /** 打开主页 */
  public static void openHomePage() {

    LogUtil.info("打开主页");
    ShellUtils.execCommand("adb shell am start -n com.quanql.demo/.activity.HomeActivity");
  }

  /** 打开今日特卖页面 */
  public static void clickTabHome() {

    LogUtil.info("点击今日特卖");
    baseOpt.click(By.id(tabHome));
  }

  /**
   * 今日特卖页面校验
   *
   * @return
   */
  public static boolean tabHomePageCheck() {

    WebElement family = baseOpt.findElement(familyName);
    WebElement redPerson = baseOpt.findElement(getObfuscatedID("click_ads_right_top"));
    WebElement brand =
        baseOpt.scrollToView(By.id(viMainContentId), By.id(getObfuscatedID("iv_banner")), 0, 8);

    if (family == null || redPerson == null || brand == null) {

      return false;
    }

    return true;
  }
}
