# sso
单点登录系统（使用SpringCloud实现）

核心功能：
通过接口提供用户的会话管理

设计：
接口数据格式：使用json

API-1 getSession 根据sessionId获取session:
入参
>
?sessionId=XXXXXXX

返回
>{
sessionId:'',
userMsgInfo:{
		userCode:'',
		userCName:'',
		ip:'',
		email:'',
		teamCode:''
	}
}

API-2 checkUserAuth 判断用户名密码是否正确:
入参
>
?userCode=&pwdhash=

返回
>
{
	success:true,
	errorMsg:''
}

API-3 updateActiveTime 每次页面活动，调用接口更新下上次操作时间（这个api待确认）


API-4 invalidate 让session失效：

接口数据日志：采用log4j


会话数据保存：暂时先不进行持久化，保存在项目内存，或者redis中

用户登陆认证：暂时先采用简单的查表管理，后续用户认证相关操作要调用其他系统api



session如何支持X分钟失效
定时任务每隔Y分钟扫描一次？



