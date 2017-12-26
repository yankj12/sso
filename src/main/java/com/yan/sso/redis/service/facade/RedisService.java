package com.yan.sso.redis.service.facade;

public interface RedisService {

	public void setKeyValue(String key, String value);
	
	public String getValueByKey(String key);
}
