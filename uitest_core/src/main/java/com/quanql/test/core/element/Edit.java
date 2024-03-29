package com.quanql.test.core.element;

import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;

/**
 * 输入框
 *
 * @author quanqinle
 */
public class Edit extends BaseElement {

  public Edit(By by) {
    super(by);
  }

  public Edit(String strElement) {
    super(strElement);
  }

  public void input(String text) {
    LogUtil.info("在<" + oriElementStr + ">中输入：" + text);
    baseOpt.input(by, text);
  }
}
