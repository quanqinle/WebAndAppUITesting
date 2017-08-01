package com.quanql.test.androidperfutils.task;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.StringUtil;
import com.quanql.test.core.utils.TimeUtil;
import com.quanql.test.androidperfutils.data.AppInfo;
import com.quanql.test.androidperfutils.data.FrameInfo;

/**
 * 帧率测试线程任务
 * 
 * @author 权芹乐
 *
 */
public class FrameTask implements Callable<String>, Runnable {
	private FrameInfo frameInfo = null;

	private String sDataLine = null;
	private boolean bWriteFile = false;
	private String file = "-" + AppInfo.getAppVersion() + "-frame";

	private static String eventName = "";

	public static void main(String[] args) {
	}

	public FrameTask() {

		file = StringUtils.stripEnd(getfileName(), ".java") + file;
		initFields(true, getFile());
	}

	public FrameTask(String eventName) {

		file = StringUtils.stripEnd(getfileName(), ".java") + file;
		initFields(true, getFile());
		FrameTask.eventName = eventName;
	}

	public FrameTask(boolean bWriteFile) {

		file = StringUtils.stripEnd(getfileName(), ".java") + file;
		initFields(bWriteFile, getFile());
	}

	public FrameTask(boolean bWriteFile, String filename) {

		file = StringUtils.stripEnd(getfileName(), ".java") + file;
		initFields(bWriteFile, filename);
	}

	private void initFields(boolean bWriteFile, String filename) {
		this.frameInfo = new FrameInfo();
		this.bWriteFile = bWriteFile;
		setFile(filename);
		if (bWriteFile) {
			if (!StringUtil.isEmptyOrWhitespaceOnly(FrameInfo.getPrintTitle())) {
				FileUtil.write2Csv(getFile(),
						String.join(",", "SamplingTime", FrameInfo.getPrintTitle(), "EventName\n"));
			}
		}
	}

	@Override
	public String call() throws Exception {
		return getFrameData();
	}

	@Override
	public void run() {
		getFrameData();
	}

	private String getFrameData() {
		LogUtil.info(Thread.currentThread().getName() + " frame");
		frameInfo.getCurrentFrameData();
		sDataLine = frameInfo.getPrintLine();
		if (bWriteFile) {
			if (!StringUtil.isEmptyOrWhitespaceOnly(sDataLine)) {
				FileUtil.write2Csv(getFile(),
						String.join(",", TimeUtil.getCurrentDateTime(4), sDataLine, eventName) + "\n");
			}
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
			if (!temp[i].getFileName().equals("AndroidBaseTest.java")) {
				return temp[i].getFileName();
			}
		}

		return null;
	}
}
