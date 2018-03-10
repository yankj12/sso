package com.yan.access.intf.vo;

import java.io.Serializable;

import com.yan.access.vo.UserMsgInfo;

public class ResponseVo implements Serializable{

	private boolean success;
	
	private String errorMsg;
	
	private String sessionId;
	
	private UserMsgInfo userMsgInfo;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public UserMsgInfo getUserMsgInfo() {
		return userMsgInfo;
	}

	public void setUserMsgInfo(UserMsgInfo userMsgInfo) {
		this.userMsgInfo = userMsgInfo;
	}
	
}
