package com.quanql.test.androidui.page;

import com.quanql.test.androidui.base.AndroidBasePage;
import com.quanql.test.core.element.Button;
import com.quanql.test.core.element.Text;
import com.quanql.test.core.utils.AndroidIDUtil;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * APP内主框架<br>
 * 包括，底部TAB切换等。直接调用请使用它的子类，如TabHomePage
 *
 * @author 权芹乐
 */
public class MainFramePage extends AndroidBasePage {
  /** 今日特卖TAB */
  public static Button TabHome = new Button("id=tab_home");
  /** 全球购TAB */
  public static Button TabOversee = new Button("id=tab_oversea");
  /** 拼团TAB */
  public static Button TabTuan = new Button("id=tab_tuan");
  /** 购物车TAB */
  public static Button TabCart = new Button("id=tab_cart");
  /** 我的TAB */
  public static Button TabMine = new Button("id=tab_mine");

  public static Text TabMineText =
      new Text(
          "//android.widget.RelativeLayout[@resource-id='"
              + AndroidIDUtil.getObfuscatedID("tab_mine")
              + "']//android.widget.TextView");

  /**
   * 点击 顶部TAB
   *
   * @param barname
   */
  public static void clickTopTab(String barname) {
    LogUtil.info("点击 顶部TAB:" + barname);
    By parentBy = By.xpath("//android.widget.HorizontalScrollView");
    By findBy = By.name(barname);
    WebElement element = baseOpt.scrollToView(parentBy, findBy, 2, 5);
    element.click();
  }
}
