package com.yan.access.service.facade;

import com.yan.access.vo.UserMsgInfo;

public interface RedisSessionService {

	public UserMsgInfo findUserMsgInfoFromRedisSession(String ticket);
	
	public boolean insertUserMsgInfoToRedisSession(String ticket, UserMsgInfo userMsgInfo, int expireSecond);
	
	public boolean extendRedisSessionExpireTime(String ticket, int expireSecond);
	
	public boolean deleteRedisSession(String ticket);
}
