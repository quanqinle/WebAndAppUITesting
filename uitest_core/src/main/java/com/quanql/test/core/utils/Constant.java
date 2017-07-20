package com.quanql.test.core.utils;

import java.util.HashMap;
import java.util.Map;

import com.quanql.test.perfutils.data.AppInfo;

/**
 * 全局常量
 * 
 * @author 权芹乐
 * 
 */
public final class Constant {
	
	public static final String ROOT = System.getProperty("user.dir");
	
	/**
	 * 测试类型，全小写，如android、chrome等
	 */
	public static final String DRIVER_TYPE = ConfigUtil.getInstance().getProperty("driver.type", "android").toLowerCase();
	
	/**
	 * 被测APP包名
	 */
	public static final String APP_PACKAGE_NAME = ConfigUtil.getInstance().getProperty("appPackage", "com.quanqinle.android");
	
	public static int SCREEN_WIDTH = 0; // 屏幕宽度
	public static int SCREEN_HEIGHT = 0;

	public static final int SCROLL_FAST = 500; // 拖动速度，毫秒
	public static final int SCROLL_MEDIUM = 1000;
	public static final int SCROLL_SLOW = 3000;

	// 测试账号
	public static final String USERNAME0 = ""; // 登陆账号
	public static final String PASSWORD0 = ""; // 密码。建议所有测试账号使用相同的密码！
	public static final String NICKNAME0 = ""; // 博客名称（昵称）

	/**
	 * 性能数据<br>
	 * quanql: 部分借鉴GT。以下待整理 TODO
	 */
	/**
	 * test suite 序列号
	 */
	public static String TEST_SUITE_INDEX/* = DRIVER_TYPE.equals("android")?"":TimeUtil.getCurrentDateTime(2) + "_" + AppInfo.getAppVersion()*/;
	{
		if (DRIVER_TYPE.equals("android")) {
			TEST_SUITE_INDEX = TimeUtil.getCurrentDateTime(2) + "_" + AppInfo.getAppVersion();
		}
	}
		
	/**
	 * 是否记录性能，以及所记录的文件
	 */
	/**
	 * 性能测试汇总记录文件
	 */
	public static String PERF_SUMMARY_FILE = "-perfSummary";
	/**
	 * 在每步业务操作中采集性能
	 */
	public static final boolean PERF_SAMPLE_AT_OPTS = false;
	public static String PERF_AT_OPTS_FILE = "-perfAtOpts";
	/**
	 * 在测试用例中定时采集性能
	 */
	public static final boolean PERF_SAMPLE_AT_RATE = false;
	public static String PERF_AT_FIXED_RATE_FILE = "-perfAtFixedRate";

	/**
	 * 解析adb shell命令中用到的常量
	 */
	public static final String CMD_RESULT_SPLIT = "\r\n";
	public static final String BLANK_SPLIT = "\\s+";
	public static final String DATAITEM_SPLIT = ";";

	/**
	 * JFreechart相关常量
	 */
	public static final float LINE_WIDTH = 2.0f;

	/**
	 * CPU测试相关
	 */
	public final static int ALL_CPU_KIND_COUNT = 2;

	public final static int CPU_PERSENT_INDEX = 0;
	public final static int CPU_JIFFIES_INDEX = 1;

	public final static String CPU_ITEM_TITLES[] = { "CPU%", "Jiffies" };

	/**
	 * CPU百分比获取的两种方式
	 */
	public final static int CPU_TESTMETHOD_NUMBER = 2;
	public final static int TOP_INDEX = 0;
	public final static int DUMPSYS_CPUINFO_INDEX = 1;
	public final static String[] CPU_TESTMETHOD_TITLES = { "top", "dumpsys cpuinfo" };

	public final static Map<String, Integer> CPU_ITEM_MAPS = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("top", 0);
			put("dumpsys cpuinfo", 1);
		}
	};

	/**
	 * CPU采样频率建议值()
	 */
	public final static int TOP_UPDATE_PERIOD = 3;

	/**
	 * 内存测试相关
	 */
	public final static int ALL_MEM_KIND_COUNT = 9;

	public final static int PRIV_NATIVE_INDEX = 0;
	public final static int PRIV_DALVIK_INDEX = 1;
	public final static int PRIV_TOTAL_INDEX = 2;

	public final static int PSS_NATIVE_INDEX = 3;
	public final static int PSS_DALVIK_INDEX = 4;
	public final static int PSS_TOTAL_INDEX = 5;

	public final static int HEAPALLOC_NATIVE_INDEX = 6;
	public final static int HEAPALLOC_DALVIK_INDEX = 7;
	public final static int HEAPALLOC_TOTAL_INDEX = 8;

	public final static String MEM_ITEM_TITLES[] = { "PrivNative", "PrivDalvik", "PrivTotal", "PSSNative", "PSSDalvik",
			"PSSTotal", "HeapAllocNative", "HeapAllocDalvik", "HeapAllocTotal" };

	/**
	 * 获取的内存数据中包括下面9中数据
	 */
	public final static Map<String, Integer> MEM_ITEM_MAPS = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			put("PrivNative", 0);
			put("PrivDalvik", 1);
			put("PrivTotal", 2);
			put("PSSNative", 3);
			put("PSSDalvik", 4);
			put("PSSTotal", 5);
			put("HeapAllocNative", 6);
			put("HeapAllocDalvik", 7);
			put("HeapAllocTotal", 8);
		}

	};

	/**
	 * 目前APT提供CPU、jiffies和内存这三方面的测试数据
	 */
	public final static int TEST_ITEM_COUNT = 2;
	public final static int CPU_INDEX = 0;
	public final static int MEM_INDEX = 1;
	public final static String TEXT_ITEM_TITLES[] = { "CPU", "Memory" };

	/**
	 * 系统版本
	 */
	public final static int ANDROID_4X = 0;
	public final static int ANDROID_2X = 1;

	/**
	 * Return code
	 */
	public enum PhoneState {
		STATE_OK, STATE_NOT_ADB, STATE_NOT_FOUND_PHONE, STATE_MULTI_FOUND_PHONE
	}

	public static final String OFFLINE = "device offline";
	public static final String NOT_FOUND = "device not found";
}
