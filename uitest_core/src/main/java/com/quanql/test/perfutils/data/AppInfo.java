package com.quanql.test.perfutils.data;

import org.apache.commons.lang3.StringUtils;

import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.perfutils.CommandResult;
import com.quanql.test.perfutils.ShellUtils;

/**
 * APP信息/运行时进程信息
 * 
 * @author 权芹乐
 * @since 2016-7-23
 */
public class AppInfo {
	private static String appPackageName = Constant.APP_PACKAGE_NAME;
	private static String version;
	// USER PID PPID VSIZE RSS WCHAN PC NAME
	// u0_a99 19548 383 1705304 129988 ffffffff 00000000 S com.quanql.demo
	private String user;
	private String pid;
	private String ppid; // 父pid
	private String vsize;
	private String rss;
	private String wchan;
	private String pc;
	private String stat;
	private String name;
	private String uid; // TODO


	public static void main(String[] args) {
		LogUtil.info(getAppVersion());
	}

	public AppInfo() {
		
		this(Constant.APP_PACKAGE_NAME);
	}

	public AppInfo(String appPackageName) {
		AppInfo.appPackageName = appPackageName;
		this.getAppProcInfo();
	}

	public void getAppProcInfo() {
		CommandResult cr = ShellUtils.execCommand("adb shell ps |grep " + appPackageName);
		for (String str : cr.getSuccessMsgArray()) {
			if (str.endsWith(appPackageName)) {
				String[] array = str.split(Constant.BLANK_SPLIT);
				if (array.length >= 9) {
					user = array[0];
					pid = array[1];
					ppid = array[2];
					vsize = array[3];
					rss = array[4];
					wchan = array[5];
					pc = array[6];
					stat = array[7];
					name = array[8];
				}
				if (array.length != 9) {
					LogUtil.error("ps " + appPackageName + "成功, 但" + str + "包含字段数与预期(9个)不符!");
				}
			}
		}
	}

	public static String getAppVersion() {
		CommandResult cr = ShellUtils.execCommand("adb shell dumpsys package " + appPackageName + "|grep versionName");
		for (String str : cr.getSuccessMsgArray()) {
				version = str.substring("versionName=".length());
		}
		return version;
	}
	
	public void clearAppData() {
		AppInfo.clearAppData(appPackageName);
	}

	/**
	 * 清除APP缓存
	 * 
	 * @author 权芹乐
	 */
	public static void clearAppData(String appPackageName) {
		CommandResult cr = ShellUtils.execCommand(StringUtils.join("adb shell pm clear ", appPackageName));
		LogUtil.debug(cr.getSuccessMsg());
	}

	public String getAppPackageName() {
		return appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		AppInfo.appPackageName = appPackageName;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public String getVsize() {
		return vsize;
	}

	public void setVsize(String vsize) {
		this.vsize = vsize;
	}

	public String getRss() {
		return rss;
	}

	public void setRss(String rss) {
		this.rss = rss;
	}

	public String getWchan() {
		return wchan;
	}

	public void setWchan(String wchan) {
		this.wchan = wchan;
	}

	public String getPc() {
		return pc;
	}

	public void setPc(String pc) {
		this.pc = pc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVersion() {
		return version;
	}
}
