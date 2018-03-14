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
			
			Map<String, String> map = jedis.hgetAll(key);
			
			// 当redis中没有对应的key的时候，返回的map不为null，而是size为0的map对象
			if(map != null && map.size() > 0){
				userMsgInfo = new UserMsgInfo();
				
				userMsgInfo.setUserCode(map.get("userCode"));
				userMsgInfo.setUserCName(map.get("userCName"));
				userMsgInfo.setIp(map.get("ip"));
				userMsgInfo.setEmail(map.get("email"));
				userMsgInfo.setTeamCode(map.get("teamCode"));
			}
			
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
			if(userMsgInfo.getUserCode() != null){
				jedis.hset(key, "userCode", userMsgInfo.getUserCode());
			}
			if(userMsgInfo.getUserCName() != null){
				jedis.hset(key, "userCName", userMsgInfo.getUserCName());
			}
			if(userMsgInfo.getIp() != null){
				jedis.hset(key, "ip", userMsgInfo.getIp());
			}
			if(userMsgInfo.getEmail() != null){
				jedis.hset(key, "email", userMsgInfo.getEmail());
			}
			if(userMsgInfo.getTeamCode() != null){
				jedis.hset(key, "teamCode", userMsgInfo.getTeamCode());
			}
			
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
