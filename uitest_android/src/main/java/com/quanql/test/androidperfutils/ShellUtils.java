package com.quanql.test.androidperfutils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.quanql.test.core.utils.LogUtil;

/**
 * 在本地执行命令行<br>
 * 针对Android机需要adb shell后跟命令
 * 
 * @author 权芹乐
 */
public class ShellUtils {

	public static void main(String[] args) {
		CommandResult cr = execCommand("adb devices");
		System.out.println(cr.getSuccessMsg());
	}

	private ShellUtils() {
		throw new AssertionError();
	}

	/**
	 * execute shell commands, default return result msg
	 * 
	 * @param command
	 *          command
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean)
	 */
	public static CommandResult execCommand(String command) {
		return execCommand(command, true);
	}

	/**
	 * execute shell commands
	 * 
	 * @param commands
	 *          command
	 * @param isNeedResultMsg
	 *          whether need result msg
	 * @return
	 * 
	 * 				if isNeedResultMsg is false, {@link CommandResult#successMsg} is
	 *         null and {@link CommandResult#errorMsg} is null.
	 * 
	 *         if {@link CommandResult#result} is -1, there maybe some excepiton.
	 * 
	 */
	public static CommandResult execCommand(String command, boolean isNeedResultMsg) {
		long ts = System.currentTimeMillis();

		int result = -1;
		if (command == null) {
			return new CommandResult(result, null, null);
		}

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		// StringBuilder successMsg = null;
		// StringBuilder errorMsg = null;
		List<String> successMsgList = null;
		List<String> errorMsgList = null;

		DataOutputStream outputSteam = null;
		try {
			long ts1 = System.currentTimeMillis();
			process = Runtime.getRuntime().exec(command);
			LogUtil.debug("execCommand-exec耗时ms: " + (System.currentTimeMillis() - ts1));
			outputSteam = new DataOutputStream(process.getOutputStream()); // 子进程的输入pipe输出流

			ts1 = System.currentTimeMillis();
			// result = process.waitFor(); //quanqinle:在测试掉帧率时，大概率失败
			result = process.waitFor(5, TimeUnit.SECONDS) ? 0 : 1;
			LogUtil.debug("execCommand-waitFor耗时ms: " + (System.currentTimeMillis() - ts1));
			LogUtil.debug("cmd result:" + result);

			// get command result
			if (isNeedResultMsg) {
				// successMsg = new StringBuilder();
				// errorMsg = new StringBuilder();
				successMsgList = new ArrayList<String>();
				errorMsgList = new ArrayList<String>();
				successResult = new BufferedReader(new InputStreamReader(process.getInputStream())); // 子进程的输出pipe输入流
				errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line = null;
				while ((line = successResult.readLine()) != null) {
					if (StringUtils.isNotEmpty(line) && StringUtils.isNotBlank(line)) {
						successMsgList.add(line.trim());
						// successMsg.append(line);
					}
				}
				while ((line = errorResult.readLine()) != null) {
					if (StringUtils.isNotEmpty(line) && StringUtils.isNotBlank(line)) {
						errorMsgList.add(line.trim());
						// errorMsg.append(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputSteam != null) {
					outputSteam.close();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}

		long te = System.currentTimeMillis();
		LogUtil.debug("execCommand耗时ms: " + (te - ts));
		return new CommandResult(result, successMsgList == null ? null : successMsgList.toArray(new String[] {}),
				errorMsgList == null ? null : errorMsgList.toArray(new String[] {}));
	}

}