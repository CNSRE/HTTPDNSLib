package com.sina.util.dnscache.query;

import java.net.InetAddress;
import java.util.ArrayList;

import com.sina.util.dnscache.cache.IDnsCache;
import com.sina.util.dnscache.model.DomainModel;
import com.sina.util.dnscache.model.IpModel;

/**
*
* 项目名称: DNSCache <br>
* 类名称: QueryManager <br>
* 类描述: 查询模块管理类 <br>
* 创建人: fenglei <br>
* 创建时间: 2015-4-15 下午5:23:06 <br>
* 
* 修改人:  <br>
* 修改时间:  <br>
* 修改备注:  <br>
* 
* @version V1.0
*/
public class QueryManager implements IQuery {

    private IDnsCache dnsCache = null ;

    public QueryManager( IDnsCache dnsCache ){

        this.dnsCache = dnsCache ;
    }

    /**
     * 根据host名字查询server ip
     * @return
     */
    @Override
    public DomainModel queryDomainIp(String sp, String host) {

        //从缓存中查询，如果为空 情况有两种 1：没有缓存数据  2：数据过期
    	DomainModel domainModel = getCacheDomainIp(sp, host) ; 

        //如果从缓存中没有获取到数据，从本地dns获取数据
        if( domainModel == null || domainModel.ipModelArr == null || domainModel.ipModelArr.size() == 0 ){

        	String[] ipList = null ; 
        	try {
            	InetAddress[] addresses = InetAddress.getAllByName(host);  
            	ipList = new String[addresses.length];
                for (int i = 0; i < addresses.length; i++) {  
                	ipList[i] = addresses[i].getHostAddress() ;
                }  
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
            
            domainModel = new DomainModel();
            domainModel.id = -1 ;
            domainModel.domain = host ;
            domainModel.sp = sp ;
            domainModel.ipModelArr = new ArrayList<IpModel>() ;
            
            for( int i = 0 ; i < ipList.length ; i++ ){
                domainModel.ipModelArr.add(new IpModel()) ;
                domainModel.ipModelArr.get(i).ip = ipList[i] ;
                domainModel.ipModelArr.get(i).sp = sp ;
            }
            
            dnsCache.addMemoryCache(host, domainModel);
        }

        return domainModel;
    }
    
    
    /**
     * 从缓存层获取获取数据
     * @param sp
     * @param host
     * @return
     */
    public DomainModel getCacheDomainIp(String sp, String host){
    	return dnsCache.getDnsCache(sp, host) ;
    }
}
