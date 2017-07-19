package com.quanql.test.webui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.webui.base.WebBasePage;

public class LoginPage extends WebBasePage {
	private static String editUsernameName = "username"; //input[@name='username']
	private static String editPasswordName = "passwd";
	private static String btnLoginClass = "ipt-btn J_iptBtn";
	/*
	 * 滑动条
	 */
	private static String viSliderId = "nc_1_n1z";
	private static String viSliderParentId = "dom_id";
	/*
	 * 验证码
	 */
//	private static String viCaptchaParentId = "nc_1_imgCaptcha"; //nc_1_clickCaptcha
//	private static String edtCaptchaId = "nc_1_captcha_input";
	private static String imgCaptchaXpath = "//div[@id='nc_1__imgCaptcha_img']/img"; 
	private static String imgCaptchaBigImgXpath = "//div[@id='clickCaptcha_img']/img";
//	private static String btnCaptchaId = "nc_1_scale_submit";
	
	

	/**
	 * 通过url打开登录页面
	 */
	public static void openUrl() {
		LogUtil.info("通过url打开登录页面");
		baseOpt.open("https://login.beibei.com/member/login.html");
	}
	
	/**
	 * 输入用户名密码，滑动滚动条，点击登录
	 * 
	 * @param usrname
	 * @param password
	 */
	public static void login(String usrname, String password) {
		typeUsername(usrname);
		typePassword(password);
		
		if (baseOpt.isElementDisplayed(By.id(viSliderId))) {
			dragSliderBar();
		}
		
		baseOpt.wait(2000);
		if (baseOpt.isElementDisplayed(By.xpath(imgCaptchaXpath))) {
			AssertUtil.fail("需要输入验证吗!");
		}
		if (baseOpt.isElementDisplayed(By.xpath(imgCaptchaBigImgXpath))) {
			AssertUtil.fail("需要输入验证吗!");
		}
		
		clickLoginBtn();
	}
	
	/**
	 * 输入用户名
	 * @param name
	 */
	public static void typeUsername(String name) {
		LogUtil.info("输入用户名:" + name);
		baseOpt.sendkeys(By.name(editUsernameName), name);
	}
	
	/**
	 * 输入密码
	 * @param password
	 */
	public static void typePassword(String password) {
		LogUtil.info("输入密码:" + password);
		baseOpt.sendkeys(By.name(editPasswordName), password);
	}
	
	/**
	 * 滑动滚动条
	 */
	public static void dragSliderBar() {
		Actions actions = new Actions(baseOpt.getDriver());
		int yOffset=0;
		int xOffset=baseOpt.findElement(By.id(viSliderParentId)).getSize().getWidth();
		actions.dragAndDropBy(baseOpt.findElement(By.id(viSliderId)), xOffset, yOffset).build().perform();
	}
	
	/**
	 * 点击登录按钮
	 */
	public static void clickLoginBtn() {
		LogUtil.info("点击登录按钮");
		baseOpt.click(By.className(btnLoginClass));
	}
	
	public static void handleCaptcha() {
		// 识别图片
		// 输入验证码
		// 点击提交
	}
	
}
