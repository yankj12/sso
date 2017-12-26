package com.yan.sso.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yan.sso.redis.conf.RedisSetting;
import com.yan.sso.redis.service.facade.RedisService;

import redis.clients.jedis.Jedis;

@Service
public class RedisServiceSpringImpl implements RedisService{

	
	private RedisSetting redisSetting;
	
	@Autowired
	RedisServiceSpringImpl(RedisSetting redisSetting){
		this.redisSetting = redisSetting;
	}
	
	public void setKeyValue(String key, String value) {
		Jedis jedis = new Jedis(redisSetting.getIp(), redisSetting.getPort());
		jedis.set(key, value);
		jedis.close();
	}
	
	public String getValueByKey(String key) {
		Jedis jedis = new Jedis(redisSetting.getIp(), redisSetting.getPort());
		String value = jedis.get(key);
		jedis.close();
		
		return value;
	}
	
}
