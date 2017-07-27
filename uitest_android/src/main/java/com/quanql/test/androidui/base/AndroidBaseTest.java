
package com.quanql.test.androidui.base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.quanql.test.androidui.page.TabHomePage;
import com.quanql.test.core.base.BaseTest;
import com.quanql.test.core.base.DriverFactory;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.perfutils.PerfMonitor;
import com.quanql.test.perfutils.PerfMonitor.PoolType;
import com.quanql.test.perfutils.ShellUtils;
import com.quanql.test.perfutils.data.AppInfo;
import com.quanql.test.perfutils.task.CpuTask;
import com.quanql.test.perfutils.task.FrameTask;
import com.quanql.test.perfutils.task.MemTask;

/**
 * testcase基类<br>
 * testng生命周期公共部分
 * 
 * @author 权芹乐 2016-09-2
 *
 */
public class AndroidBaseTest extends BaseTest {

	// 性能
	public AppInfo appInfo;

	@BeforeClass
	public void aBeforeClass() {

		LogUtil.info("AndroidBaseTest-beforeClass-start");

		appInfo = new AppInfo();
	}

	@AfterClass
	public void aAfterClass() {

		// uninstallApp4OPPO();
	}

	@AfterMethod
	public void aAfterMethod() {

		LogUtil.info("AndroidBaseTest-AfterMethod-start");

		// 关闭app并关闭driver
		DriverFactory.getInstance().closeApp();
		DriverFactory.getInstance().quit();

	}

	@BeforeMethod
	public void bBeforeMethod() {

		LogUtil.info("AndroidBaseTest-@BeforeMethod-start");

		// 点击版本更新提示
		TabHomePage.clickUpdateMessage();
	}

	/**
	 * 删除Appium安装的app<br>
	 * 这是临时方案，因为OPPO手机在第二次运行用例时，appium安装app会失败
	 * 
	 * @author 权芹乐
	 */
	public static void uninstallApp4OPPO() {
		try {
			ShellUtils.execCommand("adb uninstall io.appium.settings");
			ShellUtils.execCommand("adb uninstall io.appium.unlock");
			ShellUtils.execCommand("adb uninstall io.appium.android.ime");
			ShellUtils.execCommand("adb shell am kill-all");
		} catch (Exception e) {
		}
	}

	/**
	 * 公用的监控方法
	 */
	public static void perfMonitor() {
		perfMonitor(25, 500, 25, 500);
	}

	/**
	 * 公用的监控方法
	 */
	public static void perfMonitor(int upLoop, int upPeriod, int downLoop, int downPeriod) {

		LogUtil.info("开始滚动屏幕，性能监控...");
		PerfMonitor monitor = new PerfMonitor(PoolType.SCHEDULED, 3);
		monitor.executeAtFixedRate(new FrameTask());
		monitor.executeAtFixedRate(new CpuTask());
		monitor.executeAtFixedRate(new MemTask());

		TabHomePage.scrollToUp(upLoop, upPeriod);
		TabHomePage.scrollToDown(downLoop, downPeriod);

		monitor.shutdown();
	}
}
