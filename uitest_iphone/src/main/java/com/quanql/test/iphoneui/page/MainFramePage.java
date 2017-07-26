package com.quanql.test.iphoneui.page;

import com.quanql.test.iphoneui.base.IphoneBasePage;
import com.quanql.test.core.element.Button;

/**
 * APP内主框架<br>
 * 包括，底部TAB切换等。直接调用请使用它的子类，如TabHomePage
 * 
 * @author 权芹乐
 *
 */
public class MainFramePage extends IphoneBasePage {
	/**
	 * 今日特卖TAB
	 */
	public static Button TabHome = new Button("xpath=//UIAStaticText[@name='今日特卖']");
	/**
	 * 全球购TAB
	 */
	public static Button TabOversee = new Button("xpath=//UIAStaticText[@name='全球购']");
	/**
	 * 拼团TAB
	 */
	public static Button TabTuan = new Button("xpath=//UIAStaticText[@name='拼团']");
	/**
	 * 购物车TAB
	 */
	public static Button TabCart = new Button("xpath=//UIAStaticText[@name='购物车']");
	/**
	 * 我的TAB
	 */
	public static Button TabMine = new Button("xpath=//UIAStaticText[@name='我的']");

}
