package com.quanql.test.androidui.page;

import com.quanql.test.androidui.base.AndroidBasePage;
import com.quanql.test.core.element.Button;
import com.quanql.test.core.element.Edit;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;
import org.openqa.selenium.By;

/**
 * 登录页
 *
 * @author qinle.quan
 */
public class LoginPage extends AndroidBasePage {

  /** 用户名、账号编辑框 */
  public static Edit UserName = new Edit("member_login_edt_email");
  /** 密码输入框 */
  public static Edit PassWord = new Edit("member_login_edt_pwd");
  /** 立即登录按钮 */
  public static Button LoginBtn = new Button("name=立即登录");
  /** 忘记密码按钮 */
  public static Button ForgetPasswordBtn = new Button("id=member_login_forget_pwd");
  /** 手机验证码登录 */
  public static Button TelMessageBtn = new Button("id=tv_fast_login");

  /**
   * @author qinle.quan
   * @category 性能测试适用
   */
  private static String memLoginAppId = getObfuscatedID("member_login_btn_login"); // 立即登录按钮

  private static String usernameAppId = getObfuscatedID("member_login_edt_email"); // 用户名
  private static String passwordAppId = getObfuscatedID("member_login_edt_pwd"); // 密码

  /**
   * 输入用户名/密码，点击登陆
   *
   * @author qinle.quan
   * @param name
   * @param passwd
   */
  public static void login(String name, String passwd) {

    if (TabMyPage.isNeedLogin()) {

      TabMyPage.LoginNowBtn.click();
      TabHomePage.wait(2000);
      typeUsername(name);
      typePassword(passwd);
      clickLogin();
    }
  }

  public static void typeUsername(String Name) {
    LogUtil.info("输入用户名");
    baseOpt.sendkeys(By.id(usernameAppId), Name);
  }

  public static void typePassword(String Password) {
    LogUtil.info("输入密码");
    baseOpt.sendkeys(By.id(passwordAppId), Password);
  }

  public static void clickLogin() {
    LogUtil.info("点击立即登录");
    baseOpt.click(By.id(memLoginAppId));
    wait(3000);
  }

  /** 根据配置获取登录名和密码 */
  public static void login() {

    ConfigUtil property = ConfigUtil.getInstance();
    if (TabMyPage.isNeedLogin()) {

      TabMyPage.LoginNowBtn.click();
      TabHomePage.wait(2000);
      typeUsername(property.getProperty("name"));
      typePassword(property.getProperty("passwd"));
      clickLogin();
    }
  }
}
