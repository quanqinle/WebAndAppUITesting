package com.quanql.test.iphoneui.page;

import com.quanql.test.core.element.Button;
import com.quanql.test.core.element.Edit;
import com.quanql.test.core.element.Text;
import com.quanql.test.iphoneui.base.IphoneBasePage;

/**
 * 登录页
 *
 * @author qinle.quan
 */
public class LoginPage extends IphoneBasePage {

  public static Edit UserName = new Edit("xpath=//UIATextField[contains(@value,'请输入手机号码')]");
  public static Edit PassWord = new Edit("xpath=//UIASecureTextField");
  public static Button LoginBtn = new Button("xpath=//UIAButton[@name='立即登录']");
  public static Text LoginFailAlert =
      new Text("xpath=//UIAAlert[@name='提示']//UIAStaticText[@name='用户名或密码错误']");
  public static Button AlertOKBtn =
      new Button("xpath=//UIAAlert[@name='提示']//UIAButton[@name='确定']");
}
