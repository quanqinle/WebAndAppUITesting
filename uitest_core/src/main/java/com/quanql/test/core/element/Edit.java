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
    // TODO Auto-generated constructor stub
  }

  public Edit(String strElement) {
    super(strElement);
    // TODO Auto-generated constructor stub
  }

  public void sendkeys(String text) {
    LogUtil.info("在<" + oriElementStr + ">中输入：" + text);
    baseOpt.sendkeys(by, text);
  }
}
