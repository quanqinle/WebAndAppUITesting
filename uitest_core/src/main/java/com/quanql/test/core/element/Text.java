package com.quanql.test.core.element;

import org.openqa.selenium.By;

/**
 * 文本框。可以读取其中文字
 *
 * @author quanqinle
 */
public class Text extends BaseElement {

  public Text(By by) {
    super(by);
  }

  public Text(String strElement) {
    super(strElement);
  }
}
