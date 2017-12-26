package com.yan.common.mongodb;

public class MongoDBConfig {

	private String ip;
	private Integer port;
	private String database;
	
	private String dbUserDefined;
	
	private String user;
	
	private String password;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDbUserDefined() {
		return dbUserDefined;
	}
	public void setDbUserDefined(String dbUserDefined) {
		this.dbUserDefined = dbUserDefined;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
}
