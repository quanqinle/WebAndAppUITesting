package com.quanql.test.androidui.page;

import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.element.Button;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
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

  public static String familyName = "//android.widget.TextView[@text='亲子乐园']";

  private static final String VI_MAIN_CONTENT_ID = getObfuscatedId("home_page_listview");
  private static final String RL_CLEAR_ID = getObfuscatedId("click_ads_right_bottom_right");
  private static final String HOME_BOTTOM_ID = getObfuscatedId("home_bottom");

  /** 如果广告Dialog存在，则关闭；否则，do nothing */
  public static void closeAdsDialog() {
    if (isAdsDialogShow()) {
      DialogCloseBtn.click();
    }
  }

  /**
   * 新人1元购广告Dialog是否存在
   *
   * @return -
   */
  public static boolean isAdsDialogShow() {
    boolean b;
    b = baseOpt.isElementDisplayed(By.id(getObfuscatedId("ads_dialog")));
    LogUtil.info("新人1元购广告Dialog存在：" + b);
    return b;
  }

  /** 点击特价清仓，进入“特价清仓”页 */
  public static void clickFinalSale() {

    LogUtil.info("点击特价清仓，进入“特价清仓”页");
    baseOpt.waitForElementClickable(By.id(RL_CLEAR_ID));

    if (baseOpt.isCovered(By.id(RL_CLEAR_ID), By.id(HOME_BOTTOM_ID))) {
      TabHomePage.scrollToUp(1, 500);
    }

    baseOpt.click(By.id(RL_CLEAR_ID));
  }

  /** 判断更新版本提示是否存在，存在则取消更新 */
  public static void clickUpdateMessage() {

    String updateApp = "true";
    LogUtil.info("判断是否需要版本更新，存在则取消更新");
    ConfigUtil property = ConfigUtil.getInstance();
    String version = property.getProperty("updateVersion");
    if (updateApp.equals(version)) {
      baseOpt.click(By.id(getObfuscatedId("update_btn_cancel")));
    }
  }

  /** 打开主页 */
  public static void openHomePage() {

    LogUtil.info("打开主页");
    ShellUtils.execCommand("adb shell am start -n com.quanql.demo/.activity.HomeActivity");
  }

  /**
   * 今日特卖页面校验
   *
   * @return -
   */
  public static boolean tabHomePageCheck() {

    WebElement family = baseOpt.findElement(familyName);
    WebElement redPerson = baseOpt.findElement(getObfuscatedId("click_ads_right_top"));
    WebElement brand =
        baseOpt.scrollToView(By.id(VI_MAIN_CONTENT_ID), By.id(getObfuscatedId("iv_banner")), 0, 8);

    return family != null && redPerson != null && brand != null;
  }
}
