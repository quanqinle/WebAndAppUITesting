package com.quanql.test.androidperfutils.task;

import com.quanql.test.androidperfutils.data.AppInfo;
import com.quanql.test.androidperfutils.data.MemoryInfo;
import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;

/**
 * 内存测试线程任务
 *
 * @author 权芹乐
 */
public class MemTask implements Callable<String>, Runnable {
  private MemoryInfo memInfo = null;
  private String sDataLine = null;
  private boolean bWriteFile = false;
  private String file = "-" + AppInfo.getAppVersion() + "-mem";
  private static String eventName = "";

  public MemTask() {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(true, getFile());
  }

  public MemTask(String eventName) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(true, getFile());
    MemTask.eventName = eventName;
  }

  public MemTask(boolean bWriteFile) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(bWriteFile, getFile());
  }

  public MemTask(boolean bWriteFile, String filename) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(bWriteFile, filename);
  }

  private void initFields(boolean bWriteFile, String filename) {
    this.memInfo = new MemoryInfo();
    this.bWriteFile = bWriteFile;
    setFile(filename);
    if (bWriteFile) {
      FileUtil.write2Csv(
          getFile(), String.join(",", "SamplingTime", MemoryInfo.getPrintTitle(), "EventName\n"));
    }
  }

  @Override
  public String call() throws Exception {
    return getMemData();
  }

  @Override
  public void run() {
    getMemData();
  }

  private String getMemData() {
    LogUtil.info(Thread.currentThread().getName() + " mem");
    memInfo.getCurrentMemData();
    sDataLine = memInfo.getPrintLine();
    if (bWriteFile) {
      FileUtil.write2Csv(
          getFile(), String.join(",", TimeUtil.getCurrentDateTime(4), sDataLine, eventName) + "\n");
    }

    return sDataLine;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  /**
   * 获取类名
   *
   * @return
   */
  private String getfileName() {

    StackTraceElement[] temp = Thread.currentThread().getStackTrace();
    for (int i = 3; i < temp.length; i++) {
      if (!"AndroidBaseTest.java".equals(temp[i].getFileName())) {
        return temp[i].getFileName();
      }
    }

    return null;
  }
}
