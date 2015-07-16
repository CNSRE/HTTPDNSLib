/**
 * 
 */
package com.sina.util.dnscache.model;

/**
 *
 * 项目名称: DNSCache
 * 类名称: HttpDnsPack
 * 类描述: 将httpdns返回的数据封装一层，方便日后httpdns接口改动不影响数据库模型。 并且该接口还会标识httpdns错误之后的一些信息用来上报
 * 创建人: fenglei
 * 创建时间: 2015-3-30 上午11:20:11
 * 
 * 修改人:
 * 修改时间: 
 * 修改备注:
 * 
 * @version V1.0
 */
public class HttpDnsPack {

	/**
	 * httpdns 接口返回字段 域名信息
	 */
	public String domain = "" ; 
	
	/**
	 * httpdns 接口返回字段 请求的设备ip（也可能是sp的出口ip）
	 */
	public String device_ip = "" ; 
	
	/**
	 * httpdns 接口返回字段 请求的设备sp运营商
	 */
	public String device_sp = "" ; 
	
	/**
	 * httpdns 接口返回的a记录。（目前不包含cname别名信息）
	 */
	public IP[] dns = null ;


    /**
     * 本机识别的sp运营商，手机卡下运营商正常，wifi下为ssid名字
     */
    public String localhostSp = "" ;

	/**
	 * 打印该类相关变量信息
	 */
	public String toString(){
		
		String str = "HttpDnsPack class \n" ; 
		str += "domain:" + domain + "\n" ;
		str += "device_ip:" + device_ip + "\n" ;
		str += "device_sp:" + device_sp + "\n" ;
		
		if( dns != null ){
			str += "-------------------\n"; 
			for( int i = 0 ; i < dns.length ; i++ ){
				str += "dns[" + i + "]:" + dns[i] + "\n" ;
			}
			str += "-------------------\n"; 
		}

		return str ;
	}
	
	/**
	 * A记录相关字段信息
	 */
	public static class IP{
		
		/**
		 * A记录IP
		 */
		public String ip = "" ;
		
		/**
		 * 域名A记录过期时间
		 */
		public String ttl = "" ;
		
		/**
		 * 服务器推荐使用的A记录 级别从0-10
		 */
		public String priority = "" ;

        /**
         * 该服务器速度
         */
        public float speed = 0.0f ;

		/**
		 * 打印该类信息
		 */
		public String toString(){
			String str = "IP class \n" ; 
			str += "ip:" + ip + "\n" ;
			str += "ttl:" + ttl + "\n" ;
			str += "priority:" + priority + "\n" ;
			return str ; 
		}
		
	}
	
}
