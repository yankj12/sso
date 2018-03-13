package com.yan.access.service.spring;

import java.util.Map;

import com.yan.access.service.facade.RedisSessionService;
import com.yan.access.vo.UserMsgInfo;
import com.yan.common.redis.RedisConfig;

import redis.clients.jedis.Jedis;

public class RedisSessionServiceImpl implements RedisSessionService{

	private RedisConfig redisConfig;
	
	public UserMsgInfo findUserMsgInfoFromRedisSession(String ticket){
		UserMsgInfo userMsgInfo = null;
		Jedis jedis = null;
		
		String key = "ticket:" + ticket;
		try {
			jedis = new Jedis(redisConfig.getIp(), redisConfig.getPort());
			
			userMsgInfo = new UserMsgInfo();
			
			Map<String, String> map = jedis.hgetAll(key);
			userMsgInfo.setUserCode(map.get("userCode"));
			userMsgInfo.setUserCName(map.get("userCName"));
			userMsgInfo.setIp(map.get("ip"));
			userMsgInfo.setEmail(map.get("email"));
			userMsgInfo.setTeamCode(map.get("teamCode"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(jedis != null){
				jedis.close();
			}
		}
		
		return userMsgInfo;
	}
	
	public boolean insertUserMsgInfoToRedisSession(String ticket, UserMsgInfo userMsgInfo, int expireSecond){
		boolean result = false;
		Jedis jedis = null;
		
		String key = "ticket:" + ticket;
		try {
			jedis = new Jedis(redisConfig.getIp(), redisConfig.getPort());
			
			// 向redis中存UserMsgInfo的信息
			jedis.hset(key, "userCode", userMsgInfo.getUserCode());
			jedis.hset(key, "userCName", userMsgInfo.getUserCName());
			jedis.hset(key, "ip", userMsgInfo.getIp());
			jedis.hset(key, "email", userMsgInfo.getEmail());
			jedis.hset(key, "teamCode", userMsgInfo.getTeamCode());
			
			// 设置失效时间
			jedis.expire(key, expireSecond);
			
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(jedis != null){
				jedis.close();
			}
		}
		return result;
	}
	
	public boolean extendRedisSessionExpireTime(String ticket, int expireSecond){
		long result = 0;
		Jedis jedis = null;
		
		String key = "ticket:" + ticket;
		try {
			jedis = new Jedis(redisConfig.getIp(), redisConfig.getPort());
			result = jedis.expire(key, expireSecond);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(jedis != null){
				jedis.close();
			}
		}
		return result>0?true:false;
	}
	
	public boolean deleteRedisSession(String ticket){
		long result = 0;
		Jedis jedis = null;
		
		String key = "ticket:" + ticket;
		try {
			jedis = new Jedis(redisConfig.getIp(), redisConfig.getPort());
			result = jedis.del(key);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(jedis != null){
				jedis.close();
			}
		}
		return result>0?true:false;
	}

	public RedisConfig getRedisConfig() {
		return redisConfig;
	}

	public void setRedisConfig(RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}
}
