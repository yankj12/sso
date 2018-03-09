package com.yan.access.action;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.yan.access.dao.UserMongoDaoUtil;
import com.yan.access.model.User;
import com.yan.access.vo.UserMsgInfo;

public class LoginAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	private User user;
	
	private String userCode;
	private String password;
	
	private boolean success;
	
	private String errorMsg;
	
	private UserMongoDaoUtil userMongoDaoUtil;
	
	private UserMsgInfo userMsgInfo;
	
	public String prepareLogin(){

		return "success";
	}
	
	public String login(){
		errorMsg = "";
		//System.out.println(authen);
		
		//从session中获取usercode
		//获取Session对象
		//获取sessionid
		HttpServletRequest request = ServletActionContext.getRequest();
		String ip = request.getRemoteAddr();
		
		HttpSession httpSession = request.getSession();
		String sessID = request.getSession().getId();
		
		if(httpSession != null){
			if(httpSession.getAttribute(sessID) != null){
				userMsgInfo = (UserMsgInfo)httpSession.getAttribute(sessID);
				String userCode = userMsgInfo.getUserCode();
				return "success";
			}else{
				//根据用户名密码进行登录
				if(userCode == null || password == null 
						|| "".equals(userCode.trim()) || "".equals(password.trim())){
					errorMsg = "用户名和密码不能为空！";
					
					
					return "login";
				}
				String passwordMD5 = DigestUtils.md5Hex(password);
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userCode", userCode);
				map.put("pswd", passwordMD5);
				map.put("validStatus", "1");    //只查询有效用户
				map.put("auditStatus", "2");    //只查询审批通过用户
				
				
				List<User> users = userMongoDaoUtil.findUserDocumentsByCondition(map);
				
				User user = null;
				if(users != null && users.size() == 1){
					user = users.get(0);
				}
				
				//根据userCode和password去库里查
				//User user = userService.findUserByPK(userCode);
				
				//查到有数据，则向session中加入
				if(user != null){
					userMsgInfo = new UserMsgInfo();
					userMsgInfo.setUserCode(user.getUserCode());
					userMsgInfo.setUserCName(user.getUserName());
					userMsgInfo.setEmail(user.getEmail());
					//userMsgInfo.setTeamCode(user.getTeam());
					userMsgInfo.setIp(ip);
					
					httpSession.setAttribute(sessID, userMsgInfo);
					
					return "success";
				}else{
					//没有查到数据，则跳转到登陆界面
					errorMsg = "用户名或密码不正确！";
					
					return "login";
				}
			}	
		}
		
		return "success";
	}
	
	public String logout(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String ip = request.getRemoteAddr();
		HttpSession httpSession = request.getSession();
		String sessID = request.getSession().getId();
		//从session中获取userCode
		if(httpSession != null){
			if(httpSession.getAttribute(sessID) != null){
				userMsgInfo = (UserMsgInfo)httpSession.getAttribute(sessID);
				if(userMsgInfo != null && userMsgInfo.getUserCode() != null){
					userCode = userMsgInfo.getUserCode().trim();
				}
			}
		}
		httpSession.removeAttribute(sessID);
		
		return "login";
	}
	
	/**
	 * 用户注册界面校验用户编码和邮箱是否唯一
	 * 
	 * 这个方法放在这里，是因为namespace为login的才能免登陆，其他的都要经过单点过滤器
	 * @return
	 */
	public String checkUserUnique(){
		
		if(user != null){
			String userCode = user.getUserCode();
			String email = user.getEmail();
			
			if(userCode != null && !"".equals(userCode.trim())){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userCode", userCode.trim());
				
				long userCodeCount = userMongoDaoUtil.countUserVoDocumentsByCondition(map);
				
				if(userCodeCount > 0){
					success = false;
					errorMsg = "用户编码[" + userCode + "]已经被其他用户使用，请重新出入！";
					
					return "json";
				}else{
					success = true;
					errorMsg = null;
				}
				
			}else{
				success = false;
				errorMsg = "缺少参数userCode";
				return "json";
			}
			
			if(email != null && !"".equals(email.trim())){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("email", email.trim());
				
				long emailCount = userMongoDaoUtil.countUserVoDocumentsByCondition(map);
				
				if(emailCount > 0){
					success = false;
					errorMsg = "邮箱[" + email + "]已经被其他用户使用，请重新出入！";
					
					return "json";
				}else{
					success = true;
					errorMsg = null;
				}
				
			}else{
				success = false;
				errorMsg = "缺少参数email";
			}
			
		}else{
			success = false;
			errorMsg = "录入参数不足，请进行检查！";
		}
		
		return "json";
	}
	
//	/**
//	 * 注册用户的方法
//	 * 这个方法放在这里，是因为namespace为login的才能免登陆，其他的都要经过单点过滤器
//	 * @return
//	 */
//	public String registeUser(){
//		try {
//			
//			UserDaoService userDaoService = new UserDaoServiceImpl();
//				
//			if(user != null){
//				if(user.getUserCode() != null && !"".equals(user.getUserCode().trim())
//						&& user.getPswd() != null && !"".equals(user.getPswd().trim())){
//					User userTmp = userDaoService.getUserByPK(user);
//					if(userTmp == null){
//						//将用户的密码进行MD5再进行保存
//						String passwordMD5 = DigestUtils.md5Hex(user.getPswd().trim());
//						user.setPswd(passwordMD5);
//						
//						//新增时，设置审批状态
//						//如果新增的是admin，那么自动审批通过。
//						//但是只有这么一个用户在注册的时候可以自动审批通过，其他的用户在注册的时候必须经过审批
//						if("admin".equals(user.getUserCode().trim())){
//							//如果是管理岗，自动审批通过
//							user.setAuditStatus("2");    //自动审批通过
//							user.setAuditOpinion("注册admin用户，自动审批通过");
//						}else{
//							//如果是非管理岗，则处于待审批状态
//							user.setAuditStatus("1");    //待审批
//							user.setAuditOpinion("注册用户，进入待审批状态");
//						}
//						
//						//新增时设置有效状态为有效
//						user.setValidStatus("1");
//						String userCode = userDaoService.insertUser(user);
//						success = true;
//						errorMsg = "用户已经生成！用户编码[" + userCode + "]";
//						
//						if("1".equals(user.getAuditStatus().trim())){
//							errorMsg += "，用户处于待审批状态，请等待管理员审批，才能正常使用。";
//						}else if("2".equals(user.getAuditStatus().trim())){
//							errorMsg += "，用户审批通过，可以正常使用。";
//						}
//						
//					}else{
//						String userCode = userTmp.getUserCode();
//						success = false;
//						errorMsg = "用户已经存在！用户编码[" + userCode + "]，不允许重复新增！";
//					}
//				}else{
//					success = false;
//					errorMsg = "用户名或面不能为空，请进行检查！";
//				}
//				
//			}else{
//				success = false;
//				errorMsg = "录入参数不足，请进行检查！";
//			}
//		} catch (Exception e) {
//			success = false;
//			errorMsg = e.getLocalizedMessage();
//		}
//		success = true;
//		return "json";
//	}
	
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserMsgInfo getUserMsgInfo() {
		return userMsgInfo;
	}

	public void setUserMsgInfo(UserMsgInfo userMsgInfo) {
		this.userMsgInfo = userMsgInfo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public UserMongoDaoUtil getUserMongoDaoUtil() {
		return userMongoDaoUtil;
	}

	public void setUserMongoDaoUtil(UserMongoDaoUtil userMongoDaoUtil) {
		this.userMongoDaoUtil = userMongoDaoUtil;
	}
	
}
