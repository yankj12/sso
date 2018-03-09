package com.yan.access.model;

import java.util.Date;

public class User implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	
	/** 
	 * 用户编码 
	 * */
	private String userCode;

	/** 客户名称 */
	private String userName;
	
	/** 密码 */
	private String pswd;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/** 个人信息 */
	private String remark;
	
	/**
	 * 有效状态
	 */
	private String validStatus;
	
	/**
	 * 审批状态
	 * 0，未提交审批；1，待审批；2，审批通过；3，打回
	 */
	private String auditStatus;
	
	private String auditOpinion;
	
    private Date insertTime;
    
    private Date updateTime;
    
	public User() {
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getValidStatus() {
		return validStatus;
	}

	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAuditOpinion() {
		return auditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
