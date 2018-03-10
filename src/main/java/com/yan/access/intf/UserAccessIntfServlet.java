package com.yan.access.intf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.yan.access.dao.UserMongoDaoUtil;
import com.yan.access.intf.vo.ResponseVo;
import com.yan.access.model.User;
import com.yan.access.vo.UserMsgInfo;

public class UserAccessIntfServlet  extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession();
		String sessID = request.getSession().getId();
		
		//获取ServletContext 再获取 WebApplicationContextUtils  
        ServletContext servletContext = this.getServletContext();  
        WebApplicationContext context =   
                WebApplicationContextUtils.getWebApplicationContext(servletContext);  
        UserMongoDaoUtil userMongoDaoUtil = (UserMongoDaoUtil) context.getBean("userMongoDaoUtil"); 
		
		//获取入参
		String intfCode = request.getParameter("intfCode");
		String userCode = request.getParameter("userCode");
		String pwdhash = request.getParameter("pwdhash");
		String ip = request.getRemoteAddr();
		
		// 定义返回
		ResponseVo responseVo = new ResponseVo();
		responseVo.setSuccess(false);
		responseVo.setErrorMsg("check parameters.");
		if(intfCode != null) {
			
			if("checkUserAuth".equals(intfCode.trim())) {
				responseVo = this.checkUserAuth(httpSession, userMongoDaoUtil, userCode, pwdhash);
			}else if("getSession".equals(intfCode.trim())) {
				responseVo = this.getSession(httpSession, sessID);
			}
		}
		
		//fastjson转换为json
		//返回json数据
		String json = JSON.toJSONString(responseVo);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//如果在dopost中不调用doget，那么通过post方式请求过来的连接将不会被做任何处理
		doGet(request, response);
	}
	
	/**
	 * API-1 getSession 根据sessionId获取session
	 * @param httpSession
	 * @param sessID
	 * @return
	 */
	private ResponseVo getSession(HttpSession httpSession, String sessID) {
		ResponseVo responseVo = new ResponseVo();
		boolean success = false;
		String errorMsg = null;
		UserMsgInfo userMsgInfo = (UserMsgInfo)httpSession.getAttribute(sessID);
		if(userMsgInfo != null) {
			success = true;
		}else {
			success = false;
		}
		responseVo.setSuccess(success);
		responseVo.setErrorMsg(errorMsg);
		responseVo.setUserMsgInfo(userMsgInfo);
		return responseVo;
	}
	
	/**
	 * API-2 checkUserAuth 判断用户名密码是否正确
	 * @param httpSession
	 * @param userMongoDaoUtil
	 * @param userCode
	 * @param pwdhash
	 * @return
	 */
	private ResponseVo checkUserAuth(HttpSession httpSession, UserMongoDaoUtil userMongoDaoUtil, String userCode, String pwdhash) {
		
		String sessID = httpSession.getId();
		
		ResponseVo responseVo = new ResponseVo();
		UserMsgInfo userMsgInfo = new UserMsgInfo();
		boolean success = false;
		String errorMsg = null;
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userCode", userCode);
		map.put("pswd", pwdhash);
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
			//userMsgInfo.setIp(ip);
			
			httpSession.setAttribute(sessID, userMsgInfo);
			success = true;
		}else{
			//没有查到数据，则跳转到登陆界面
			errorMsg = "用户名或密码不正确！";
			success = false;
		}
		
		responseVo.setSuccess(success);
		responseVo.setErrorMsg(errorMsg);
		responseVo.setUserMsgInfo(userMsgInfo);
		return responseVo;
	}
	
}
