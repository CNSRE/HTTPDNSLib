package com.sina.util.dnscache.simulationtask;

import com.sina.util.dnscache.DomainInfo;

public class TaskModel {

	
	//---------------任务---------------
	
	public long taskID  = 0 ; 
	
	public int status = 0 ;  //状态：（完成\错误）
	
	public long taskStartTime = 0 ; //开始时间：
	
	public long taskStopTime = 0 ;//结束时间：
	
	public long taskExpendTime = 0 ;//消耗时间：
	
	public String url = "" ; //任务传入URL
	
	//-------------HTTPDNS库-------------
	
	public String httpDnsResult = "" ;  //返回结果：
	
	public long httpDnsExpendTime = 0 ; //响应耗时：
	
	

	//------------IP+HOST请求------------
	
	public String hostUrl = "" ; //访问URL：
	
	public String hostIp = "" ; //访问服务器IP：
	
	public int hostCode = 0 ; //服务器返回CODE：
	
	public byte[] hostInputStreamResult = null ;
	
	public String hostResult = "" ; //服务器返回结果：（可点击看详情）
	
	public long hostExpendTime = 0 ; //请求耗时:
	
	
	
	//------------域名直接请求------------
	
	public String domainUrl = "" ; //访问URL：
	
	public String domainIp = "" ; //访问服务器IP：
	
	public int domainCode = 0 ; //服务器返回CODE：
	
	public byte[] domainInputStreamResult = null ;
	
	public String domainResult = "" ; //服务器返回结果：（可点击看详情）
	
	public long domainExpendTime = 0 ; //请求耗时:
	
	
	
	//------------设备环境信息------------
	
	public String netType = "";  //网络环境（WIFI\2G\3G\4G\未知）
	
	public String spName = "" ;  //运营商：(WIFI名字\手机卡运营商\未知)
	
	
	
	
	//------------HttpDns返回相关信息------------
	
	public DomainInfo[] domainInfo = null ;
	
	
}
