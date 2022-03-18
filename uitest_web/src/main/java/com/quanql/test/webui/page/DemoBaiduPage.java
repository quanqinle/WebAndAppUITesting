package com.quanql.test.webui.page;

import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.webui.base.WebBasePage;
import org.openqa.selenium.By;

public class DemoBaiduPage extends WebBasePage {
  private static String edtSearchId = "kw";
  private static String btnSearchXpath = "//input[@value='百度一下']";
  private static String txtNeteaseXpath =
      "//div[@id='content_left']/div//a[contains(.,'www.163.com')]";

  /** 通过url打开登录页面 */
  public static void openBaidu() {
    LogUtil.info("通过url打开百度首页");
    baseOpt.open("https://www.baidu.com");
  }

  /**
   * 搜索框输入内容
   *
   * @param name
   */
  public static void typeInSearchEdt(String name) {
    LogUtil.info("输入搜索内容:" + name);
    baseOpt.sendkeys(By.id(edtSearchId), name);
  }

  /** 点击搜索按钮 */
  public static void clickSearchBtn() {
    LogUtil.info("点击搜索按钮");
    baseOpt.click(By.xpath(btnSearchXpath));
  }

  public static boolean isNeteaseExisted() {
    LogUtil.info("检查163官网是否存在");
    return baseOpt.isElementDisplayed(By.xpath(txtNeteaseXpath));
  }
}
