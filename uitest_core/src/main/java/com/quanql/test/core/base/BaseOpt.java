package com.quanql.test.core.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.quanql.test.core.utils.AssertUtil;
import com.quanql.test.core.utils.ConfigUtil;
import com.quanql.test.core.utils.LogUtil;

/**
 * 常用事件封装 <br>
 * 只有web/android/ios都适用的，才放在这里<br>
 * 警告:如需修改此class，请告知项目管理员
 * 
 * @author 权芹乐
 *
 */
public class BaseOpt {

	protected static BaseOpt baseOpt;
	protected RemoteWebDriver driver;
	protected WebDriverWait wait;
	protected int DRIVER_WAIT_TIMEOUT_IN_SECOND = Integer.valueOf(ConfigUtil.getInstance().getProperty("waitTime")); // second
	protected static String PROJ_PACKAGE_NAME = "com.quanql.test"; // 可配置

	protected BaseOpt() {
		driver = DriverFactory.getInstance().getDriver();
		// WebDriverWait(WebDriver driver, long timeOutInSeconds, long
		// sleepInMillis)
		wait = new WebDriverWait(driver, DRIVER_WAIT_TIMEOUT_IN_SECOND, 500);
	}

	public static BaseOpt getInstance() {
		if (baseOpt == null) {
			LogUtil.debug("BaseOpt-getInstance()-new");
			baseOpt = new BaseOpt();
		}
		return baseOpt;
	}

	/**
	 * 兼容循环执行时 不会重新加载构造方法的问题
	 * 
	 * @return
	 */
	public RemoteWebDriver getDriver() {
		if (driver == null || driver.getSessionId() == null) {
			driver = DriverFactory.getInstance().getDriver();
			wait = new WebDriverWait(driver, DRIVER_WAIT_TIMEOUT_IN_SECOND, 500);
		}
		return driver;
	}

	public void setDriver(RemoteWebDriver driver) {
		this.driver = driver;
	}

	/**
	 * 线程休眠/等待<br>
	 * 注意：click(),type()等自带等待，不需要再wait()!!
	 * 
	 * @param millis
	 *            毫秒。<=0时，无效
	 */
	public void wait(int millis) {
		if (0 >= millis) {
			return;
		}
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LogUtil.debug("等待被中断");
		}
	}

	/**
	 * 点击元素
	 * 
	 * @param element
	 */
	public void click(WebElement element) {
		try {
			element.click();
			LogUtil.debug(getDriver().manage().logs() + "==>" + element.toString() + " 点击成功！");
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + element.toString() + " 点击失败！" + e);
			screenShot();
			AssertUtil.fail(getDriver().manage().logs() + "==>" + element.toString() + " 点击失败！" + e);
		}
	}

	/**
	 * 点击元素
	 * 
	 * @param by
	 */
	public void click(By by) {
		try {
			// 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
			driver = getDriver();
			wait = new WebDriverWait(driver, DRIVER_WAIT_TIMEOUT_IN_SECOND, 500);
			WebElement element = waitForElementClickable(by);
			element.click();
			// findElement(by).click();
			LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 点击成功！");
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 点击失败！" + e);
			screenShot();
			AssertUtil.fail(getDriver().manage().logs() + "==>" + by.toString() + " 点击失败！" + e);
		}
	}

	/**
	 * 点击对第num个元素by
	 * 
	 * @param by
	 * @param num
	 *            相同元素的第num个，从0开始
	 */
	public void click(By by, int num) {
		try {
			waitForElementClickable(by);
			findElements(by).get(num).click();
			LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 点击内容成功！");
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 点击内容失败！" + e);
			screenShot();
			AssertUtil.fail(getDriver().manage().logs() + "==>" + by.toString() + " 点击内容失败！" + e);
		}
	}

	/**
	 * 公用的输入操作
	 * 
	 * @param by
	 * @param element
	 * @param text
	 */
	private void sendkeys(By by, WebElement element, String text) {

		if (element.getTagName().equalsIgnoreCase("input") || element.getTagName().equalsIgnoreCase("textarea")) {
			boolean bl = false;
			for (int i = 0; i < 3; i++) {
				element.clear();
				// element.setValue(text); //官网论坛说，执行效率比sendKeys高。但遇到输密码会出错
				element.sendKeys(text);
				if (element.getAttribute("value").equals(text)) {
					bl = true;
					break;
				}
			}
			if (!bl) {
				LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容失败！ 预期输入：" + text + "；实际输入："
						+ element.getAttribute("value"));
				screenShot();
				AssertUtil.fail(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容失败！ 预期输入：" + text + "；实际输入："
						+ element.getAttribute("value"));
			}
		} else if (element.getTagName().equalsIgnoreCase("div")) {
			boolean bl = false;
			for (int i = 0; i < 3; i++) {
				element.clear();
				element.sendKeys(text);
				if (element.getText().equals(text)) {
					bl = true;
					break;
				}
			}
			if (!bl) {
				LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容失败！ 预期输入：" + text + "；实际输入："
						+ element.getAttribute("value"));
				screenShot();
				AssertUtil.fail(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容失败！ 预期输入：" + text + "；实际输入："
						+ element.getAttribute("value"));
			}
		} else {
			element.clear();
			element.sendKeys(text);
		}

		LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 输入内容成功！");
	}

	/**
	 * 输入文本
	 * 
	 * @param by
	 * @param text
	 */
	public void sendkeys(By by, String text) {
		WebElement element = waitForElementClickable(by);
		sendkeys(by, element, text);
	}

	/**
	 * 在相同元素的第num个输入文本
	 * 
	 * @param by
	 * @param num
	 * @param text
	 */
	public void sendkeys(By by, int num, String text) {
		waitForElementClickable(by);
		WebElement element = findElements(by).get(num);
		sendkeys(by, element, text);
	}

	/**
	 * 查找元素
	 * 
	 * @param by
	 * @return
	 */
	public WebElement findElement(By by) {
		WebElement element = null;
		try {
			element = getDriver().findElement(by);
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！" + e);
			screenShot();
			AssertUtil.fail(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！" + e);
		}

		return element;
	}

	/**
	 * 几秒后查看元素不存在
	 * 
	 * @param by
	 * @param millis
	 *            等待的毫秒
	 */
	public void findElementNotExist(By by, int millis) {
		if (millis > 0) {
			wait(millis);
		}
		try {
			getDriver().findElement(by);
			LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 找到了！");
			screenShot();
			AssertUtil.fail(by.toString() + " 找到了！");
		} catch (Exception e) {
			LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
		}
	}

	/**
	 * 查找元素集合
	 * 
	 * @param by
	 * @return
	 */
	public List<WebElement> findElements(By by) {
		List<WebElement> elementList = null;
		try {
			elementList = getDriver().findElements(by);
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！" + e);
			screenShot();
			AssertUtil.fail(by.toString() + " 未找到！");
		}

		return elementList;
	}

	/**
	 * 检查元素是否存在
	 * 
	 * @param by
	 * @return true存在
	 */
	public boolean isElementExisted(By by) {
		boolean b = false;
		try {
			getDriver().findElement(by);
			b = true;
		} catch (Exception e) {
			LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
			b = false;
			screenShot();
		}

		return b;
	}

	/**
	 * 检查元素是否可见<br>
	 * 立即检查，受implicitly.wait影响（同findElement()）
	 * 
	 * @param by
	 * @return true存在
	 */
	public boolean isElementDisplayed(By by) {
		boolean b = false;
		try {
			b = getDriver().findElement(by).isDisplayed();
			LogUtil.debug(getDriver().manage().logs() + "==>" + by.toString() + " 可见:" + b);
		} catch (Exception e) {
			LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + " 未找到！");
			b = false;
			// screenShot();
		}

		return b;
	}

	/**
	 * 屏幕截屏
	 */
	public void screenShot() {
		long ts = System.currentTimeMillis();
		String dir_name = "screenshot"; // 截图存放目录
		if (!(new File(dir_name).isDirectory())) {
			new File(dir_name).mkdirs(); // 不存在则新建目录
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(new Date());

		StackTraceElement stack[] = (new Throwable()).getStackTrace();

		String cn = "";
		int line = 0;
		// for循环找到项目中最上层的类文件
		for (int i = 0; i < stack.length; i++) {
			StackTraceElement ste = stack[i];
			/*
			 * PROJ_PACKAGE_NAME可自定义。
			 * 作用：调用栈中找到工程的包名，然后截图，即，避免使用底层类命名截图
			 */
			if (ste.getClassName().contains(PROJ_PACKAGE_NAME)) {
				cn = ste.getFileName();
				line = ste.getLineNumber();
			}
		}
		LogUtil.debug("PROJ_PACKAGE_NAME：" + PROJ_PACKAGE_NAME);

		if (cn != "" && !cn.isEmpty()) {
			cn = cn.substring(0, cn.indexOf("."));
		}
		LogUtil.info("用例：" + cn + "在第" + line + "行失败产生截图：" + time + cn + ".png");
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		File source_file = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(source_file, new File(dir_name + File.separator + time + cn + ".png"));
		} catch (IOException e) {
			LogUtil.error("用例：" + cn + "在第" + line + "行失败产生截图失败：" + e);
		}
		long te = System.currentTimeMillis();
		LogUtil.info("截屏耗时ms: " + (te - ts));
	}

	/**
	 * 在当前光标处，通过键盘输入文本（适用于无法获取element时）
	 * 
	 * @author 权芹乐
	 * @param text
	 */
	public void sendKeysByKeyboard(String text) {
		try {
			getDriver().getKeyboard().sendKeys(text);
			LogUtil.info(getDriver().manage().logs() + "==>" + text + " 键盘输入成功！");
		} catch (Exception e) {
			LogUtil.error(getDriver().manage().logs() + "==>" + text + " 键盘输入失败！" + e);
			screenShot();
			AssertUtil.fail(getDriver().manage().logs() + "==>" + text + " 键盘输入失败！" + e);
		}
	}

	/**
	 * Wait for the element displayed in specific duration via locator.
	 * 
	 * @param by
	 *            locator of the element
	 * @return WebElement
	 */
	public WebElement waitForElementDisplayed(By by) {
		try {
			// 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
			driver = getDriver();
			wait = new WebDriverWait(driver, DRIVER_WAIT_TIMEOUT_IN_SECOND, 500);
			return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + "判断visibility失败！");
			// LogUtil.info(e.getMessage());
			return null;
		}
	}

	/**
	 * 等待默认超时时间可以自己设置， 比如滑动页面寻找元素时，没有必要等待很长时间，不确定出现时间可以使用默认等待时间。
	 * 
	 * @param by
	 * @param outTime
	 * @return
	 */
	public WebElement waitForElementClickable(By by, int outTime) {
		try {
			// 兼容循环执行时 不会重新加载构造方法导致driver为空的问题
			driver = getDriver();
			wait = new WebDriverWait(driver, outTime, 200);
			return wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			LogUtil.info(getDriver().manage().logs() + "==>" + by.toString() + "判断Clickable失败！");
			// LogUtil.info(e.getMessage());
			return null;
		}

	}

	/*
	 * wait util elementToBeClickable
	 */
	public WebElement waitForElementClickable(By by) {
		return waitForElementClickable(by, DRIVER_WAIT_TIMEOUT_IN_SECOND);
	}

	/**
	 * 查找元素，找不到则返回null,不截屏抛异常。用户下一步判断. 使用该方法 key必须经过getObfuscatedID 判断混淆ID
	 * 
	 * @param key
	 * @return
	 */
	public WebElement findElement(String key) {

		WebElement element = null;
		try {
			if (key.startsWith("//")) {

				element = getDriver().findElement(By.xpath(key));
			} else {

				element = getDriver().findElement(By.id(key));
			}
		} catch (Exception e) {

			return null;
		}
		return element;
	}

}
