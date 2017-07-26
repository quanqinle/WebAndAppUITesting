package com.quanql.test.perfutils;

/**
 * result of command
 * 
 * {@link CommandResult#returnCode} means result of command, 0 means normal,
 * else means error, same to excute in linux shell
 * 
 * {@link CommandResult#successMsg} means success message of command result
 * 
 * {@link CommandResult#errorMsg} means error message of command result
 * 
 * @author 权芹乐 2016-7-15
 */
public class CommandResult {

	private String delimiter = "\n";
	/** result of command **/
	private int returnCode;
	/** success message of command result **/
	private String[] successMsgArray;
	private String successMsg;
	/** error message of command result **/
	private String[] errorMsgArray;
	private String errorMsg;

	public int getResult() {
		return returnCode;
	}

	public void setResult(int result) {
		this.returnCode = result;
	}

	/**
	 * 获取String数组形式的成功结果
	 * 
	 * @return
	 */
	public String[] getSuccessMsgArray() {
		return successMsgArray;
	}

	public void setSuccessMsgArray(String[] successMsgArray) {
		this.successMsgArray = successMsgArray;
	}

	/**
	 * 获取String形式的成功结果
	 * 
	 * @return
	 */
	public String getSuccessMsg() {
		return successMsg;
	}

	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}

	public String[] getErrorMsgArray() {
		return errorMsgArray;
	}

	public void setErrorMsgArray(String[] errorMsgArray) {
		this.errorMsgArray = errorMsgArray;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public CommandResult(int result) {
		this.returnCode = result;
	}

	public CommandResult(int result, String[] successMsgArray, String[] errorMsgArray) {
		this.returnCode = result;
		this.successMsgArray = successMsgArray;
		this.errorMsgArray = errorMsgArray;
		this.successMsg = successMsgArray == null ? null : String.join(delimiter, successMsgArray);
		this.errorMsg = errorMsgArray == null ? null : String.join(delimiter, errorMsgArray);
	}
}
