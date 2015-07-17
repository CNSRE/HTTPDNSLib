package com.sina.util.dnscache.httpdns;

/**
*
* 项目名称: DNSCache <br>
* 类名称: HttpDnsConfig <br>
* 类描述: HTTPDNS 接口配置类 <br>
* 创建人: fenglei <br>
* 创建时间: 2015-4-15 下午9:10:10 <br>
* 
* 修改人:  <br>
* 修改时间:  <br>
* 修改备注:  <br>
* 
* @version V1.0
*/
public class HttpDnsConfig {

	/**
	 * 是否使用 自己的httpdns 服务器
	 */
	public static boolean isSinaHttpDns = true ; 
	
	/**
	 * DNSPOD http dns 开端
	 */
	public static boolean IS_DNSPOD_HTTPDNS = true ; 
	
    /**
     * httpdns 服务器地址
     */
    public static String HTTPDNS_SERVER_API = "" ;

    /**
     * DNSPOD 服务器地址
     */
    public static String DNSPOD_SERVER_API = "" ;
    
    
}
