package com.yan.sso.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yan.access.dao.UserMongoDaoUtil;
import com.yan.access.model.User;
import com.yan.sso.redis.service.impl.RedisServiceSpringImpl;
import com.yan.sso.vo.SessionResVo;
import com.yan.sso.vo.UserMsgInfo;

@CrossOrigin(allowCredentials="true", allowedHeaders="*", methods={RequestMethod.GET,  
        RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,  
        RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins="*")  
@RestController
public class SsoController {
	
	private RedisServiceSpringImpl redisService;
	
	@Autowired
	public SsoController(RedisServiceSpringImpl redisService) {
		this.redisService = redisService;
	}
	
	/**
	 * 根据userId查询这个用户的数据权限
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/getSession")
    public SessionResVo getSession(@RequestParam String sessionId) {
		SessionResVo sessionResVo = new SessionResVo();
    	
    	
    	if(sessionId != null && !"".equals(sessionId.trim())) {
    		
    		
    		
    		sessionResVo.setSuccess(true);
    		sessionResVo.setErrorMsg(null);
    	}else {
    		
    		
    		sessionResVo.setSuccess(false);
    		sessionResVo.setErrorMsg("缺少必要参数userId！");
    	}

        return sessionResVo;
    }
	
	
	/**
	 * 根据userId查询这个用户的数据权限
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/checkUserAuth")
    public SessionResVo checkUserAuth(@RequestParam String userCode, @RequestParam String pwdhash) {
		SessionResVo sessionResVo = new SessionResVo();
		boolean success = false;
    	String errorMsg = null;
    	
    	if(userCode != null && !"".equals(userCode.trim())
    			&& pwdhash != null && !"".equals(pwdhash.trim())) {
    		
    		UserMongoDaoUtil userMongoDaoUtil = new UserMongoDaoUtil();
    		UserMsgInfo userMsgInfo = null;
    		
    		//根据用户名密码进行登录
			if(userCode == null || pwdhash == null 
					|| "".equals(userCode.trim()) || "".equals(pwdhash.trim())){
				errorMsg = "用户名和密码不能为空！";
				
				success = false;
				sessionResVo.setSuccess(success);
				sessionResVo.setErrorMsg(errorMsg);
				
				return sessionResVo;
			}
			String passwordMD5 = DigestUtils.md5DigestAsHex(pwdhash.getBytes());
			
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
				//userMsgInfo.setIp(ip);
				
				//TODO 向session中加入s
				
				success = true;
				sessionResVo.setUserMsgInfo(userMsgInfo);
				sessionResVo.setSuccess(success);
				sessionResVo.setErrorMsg(errorMsg);
				
			}else{
				//没有查到数据，则跳转到登陆界面
				errorMsg = "用户名或密码不正确！";
				
				success = false;
				sessionResVo.setSuccess(success);
				sessionResVo.setErrorMsg(errorMsg);
				
			}
    	}else {
    		
    		
    		sessionResVo.setSuccess(false);
    		sessionResVo.setErrorMsg("缺少必要参数userCode或pwdhash！");
    	}

        return sessionResVo;
    }
	
	@RequestMapping(value="/setKeyValue")
    public String setKeyValue(@RequestParam String key, @RequestParam String value) {
		redisService.setKeyValue(key, value);
		
		return "{success:true}";
	}
	
	@RequestMapping(value="/getValueByKey")
    public String getValueByKey(@RequestParam String key) {
		String value = redisService.getValueByKey(key);
		
		return "{success:true, key:" + key + ", value:" + value + "}";
	}
}