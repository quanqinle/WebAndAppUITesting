package com.quanql.test.androidperfutils;

import com.quanql.test.androidperfutils.data.AppInfo;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.TimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局常量
 *
 * @author 权芹乐 2017/8/1
 */
public class AndroidConstant {

  /** 被测APP包名 */
  public static final String APP_PACKAGE_NAME = Constant.APP_PACKAGE_NAME;

  /** Android系统版本 */
  public static final int ANDROID_4X = 0;

  public static final int ANDROID_2X = 1;

  /**
   * 性能数据<br>
   * quanql: 部分借鉴GT。以下待整理 TODO
   */
  /** test suite 序列号 */
  public static String
      TEST_SUITE_INDEX /* = DRIVER_TYPE.equals("android")?"":TimeUtil.getCurrentDateTime(2) + "_" + AppInfo.getAppVersion()*/;

  {
    if ((com.quanql.test.core.utils.Constant.DRIVER_TYPE).equals("android")) {
      TEST_SUITE_INDEX = TimeUtil.getCurrentDateTime(2) + "_" + AppInfo.getAppVersion();
    }
  }

  /** 是否记录性能，以及所记录的文件 */
  /** 性能测试汇总记录文件 */
  public static String PERF_SUMMARY_FILE = "-perfSummary";
  /** 在每步业务操作中采集性能 */
  public static final boolean PERF_SAMPLE_AT_OPTS = false;

  public static String PERF_AT_OPTS_FILE = "-perfAtOpts";
  /** 在测试用例中定时采集性能 */
  public static final boolean PERF_SAMPLE_AT_RATE = false;

  public static String PERF_AT_FIXED_RATE_FILE = "-perfAtFixedRate";

  /** 解析adb shell命令中用到的常量 */
  public static final String CMD_RESULT_SPLIT = "\r\n";

  public static final String BLANK_SPLIT = "\\s+";
  public static final String DATAITEM_SPLIT = ";";

  /** JFreechart相关常量 */
  public static final float LINE_WIDTH = 2.0f;

  /** CPU测试相关 */
  public static final int ALL_CPU_KIND_COUNT = 2;

  public static final int CPU_PERSENT_INDEX = 0;
  public static final int CPU_JIFFIES_INDEX = 1;

  public static final String CPU_ITEM_TITLES[] = {"CPU%", "Jiffies"};

  /** CPU百分比获取的两种方式 */
  public static final int CPU_TESTMETHOD_NUMBER = 2;

  public static final int TOP_INDEX = 0;
  public static final int DUMPSYS_CPUINFO_INDEX = 1;
  public static final String[] CPU_TESTMETHOD_TITLES = {"top", "dumpsys cpuinfo"};

  public static final Map<String, Integer> CPU_ITEM_MAPS =
      new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;

        {
          put("top", 0);
          put("dumpsys cpuinfo", 1);
        }
      };

  /** CPU采样频率建议值() */
  public static final int TOP_UPDATE_PERIOD = 3;

  /** 内存测试相关 */
  public static final int ALL_MEM_KIND_COUNT = 9;

  public static final int PRIV_NATIVE_INDEX = 0;
  public static final int PRIV_DALVIK_INDEX = 1;
  public static final int PRIV_TOTAL_INDEX = 2;

  public static final int PSS_NATIVE_INDEX = 3;
  public static final int PSS_DALVIK_INDEX = 4;
  public static final int PSS_TOTAL_INDEX = 5;

  public static final int HEAPALLOC_NATIVE_INDEX = 6;
  public static final int HEAPALLOC_DALVIK_INDEX = 7;
  public static final int HEAPALLOC_TOTAL_INDEX = 8;

  public static final String MEM_ITEM_TITLES[] = {
    "PrivNative",
    "PrivDalvik",
    "PrivTotal",
    "PSSNative",
    "PSSDalvik",
    "PSSTotal",
    "HeapAllocNative",
    "HeapAllocDalvik",
    "HeapAllocTotal"
  };

  /** 获取的内存数据中包括下面9中数据 */
  public static final Map<String, Integer> MEM_ITEM_MAPS =
      new HashMap<String, Integer>() {
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

  /** 目前APT提供CPU、jiffies和内存这三方面的测试数据 */
  public static final int TEST_ITEM_COUNT = 2;

  public static final int CPU_INDEX = 0;
  public static final int MEM_INDEX = 1;
  public static final String TEXT_ITEM_TITLES[] = {"CPU", "Memory"};
}
