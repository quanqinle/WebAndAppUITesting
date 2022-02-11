package com.quanql.test.core.element;

import com.quanql.test.core.base.BaseOpt;
import com.quanql.test.core.utils.AndroidIDUtil;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 控件原始的基础类<br>
 * 注：组员提出要求简化控件定义和使用，没办法，增加一种元素定义和操作方式[无奈]
 *
 * @author quanqinle
 */
public class BaseElement {
  protected WebElement element = null;
  protected BaseOpt baseOpt = BaseOpt.getInstance();
  protected String oriElementStr;
  protected By by;

  public BaseElement(By by) {
    this.by = by;
    oriElementStr = by.toString();
  }

  /**
   * 根据字符串构建元素
   *
   * @param strElement 支持如下格式：<br>
   *     1."id=xx" android项目自带解混淆功能，只要传${pakagename}.R.id.original_id中的original_id<br>
   *     2."rawid=xx" 任何项目类型，底层都是By.id(xx)。尤其适用android非被测app的id<br>
   *     3."name=xx" 底层是By.name(xx)<br>
   *     4."xpath=xx" 底层是By.xpath(xx)<br>
   *     5."//xx" 等同于"xpath=xx"<br>
   *     6."xx" 默认等同于"id=xx"
   */
  public BaseElement(String strElement) {
    oriElementStr = strElement;
    parseStr2By(strElement);
  }

  /**
   * 根据字符串构建元素
   *
   * @author quanqinle
   * @param strElement 支持如下格式：<br>
   *     1."id=xx" android项目自带解混淆功能，只要传${pakagename}.R.id.original_id中的original_id<br>
   *     2."rawid=xx" 任何项目类型，底层都是By.id(xx)。尤其适用android非被测app的id<br>
   *     3."name=xx" 底层是By.name(xx)<br>
   *     4."xpath=xx" 底层是By.xpath(xx)<br>
   *     5."//xx" 等同于"xpath=xx"<br>
   *     6."xx" 默认等同于"id=xx"
   */
  private void parseStr2By(String strElement) {
    String lowerElement = strElement.toLowerCase();
    if (lowerElement.startsWith("id=")) {
      parseElement("id", strElement.substring("id=".length()));
    } else if (lowerElement.startsWith("rawid=")) {
      parseElement("rawid", strElement.substring("rawid=".length()));
    } else if (lowerElement.startsWith("name=")) {
      parseElement("name", strElement.substring("name=".length()));
    } else if (lowerElement.startsWith("xpath=")) {
      parseElement("xpath", strElement.substring("xpath=".length()));
    } else if (lowerElement.startsWith("//")) {
      parseElement("xpath", strElement);
    } else {
      parseElement("id", strElement);
    }
  }

  /**
   * 根据By的类型和值，返回By
   *
   * @author quanqinle
   * @param byType 支持：id、name、xpath。其中，android id自带解混淆的功能
   * @param byValue
   * @return
   */
  private By parseElement(String byType, String byValue) {
    String lowerByType = byType.toLowerCase();
    if (lowerByType.equals("id")) {
      if (Constant.DRIVER_TYPE.equalsIgnoreCase("android")) {
        this.by = By.id(AndroidIDUtil.getObfuscatedID(byValue));
      } else {
        this.by = By.id(byValue);
      }
    } else if (lowerByType.equals("rawid")) {
      this.by = By.id(byValue);
    } else if (lowerByType.equals("name")) {
      this.by = By.name(byValue);
    } else if (lowerByType.equals("xpath")) {
      this.by = By.xpath(byValue);
    } else {
      this.by = By.id(byValue);
    }
    return by;
  }

  /**
   * 获取元素对象。对象未构建时，则findElement创建
   *
   * @return
   */
  public WebElement getElement() {
    if (element == null) {
      element = baseOpt.findElement(by);
    }
    return element;
  }

  public By getBy() {
    return by;
  }

  /**
   * 控件是否显示
   *
   * @return
   */
  public boolean isDisplayed() {
    boolean b = baseOpt.isElementDisplayed(by);
    LogUtil.info("检查<" + oriElementStr + ">可见：" + b);
    return b;
  }

  /** 获取页面元素的值 */
  public String getText() {
    String str = getElement().getText();
    LogUtil.info("获取<" + oriElementStr + ">值：" + str);
    return str;
  }

  /** 点击 */
  public void click() {
    LogUtil.info("点击<" + oriElementStr + ">");
    baseOpt.click(by);
  }
}
