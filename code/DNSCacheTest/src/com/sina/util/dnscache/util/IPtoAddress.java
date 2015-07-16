package com.sina.util.dnscache.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;
import android.os.Message;

public class IPtoAddress {

	/////////////////////////////////////////////////////////////////
	
	private static IPtoAddress Instance = null;

    private IPtoAddress() { }

    public static IPtoAddress getInstance() {

        if( Instance == null ){
            Instance = new IPtoAddress();
        }
        return IPtoAddress.Instance;
    }
    
	/////////////////////////////////////////////////////////////////

	/**
     * 缓存初始容量值
     */
    private final int INIT_SIZE = 8;

    /**
     * 缓存最大容量值
     */
    private final int MAX_CACHE_SIZE = 32;
    
    /**
     * 缓存链表
     */
	private ConcurrentHashMap<String, String> data = new ConcurrentHashMap<String, String>(INIT_SIZE, MAX_CACHE_SIZE);

	private final String API_URL = "http://ip138.com/ips1388.asp?ip=";
	
	/////////////////////////////////////////////////////////////////

	public void toAddress( final int id,  final String ip, final Handler handler){
		
		String add = data.get(ip) ; 
		
		if( add != null ){
			
			Message message = new Message();
            message.what = id;
            message.obj = add;
            handler.sendMessage(message);

		}else{
			
			// 请求服务器 查询ip 对应的地址
			new Thread( new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Thread.currentThread().setName("IP to AddressAndSp"); 
					
					String result = executeHttpGet(ip) ;
					result = regularAddress(result) ; 
					data.put(ip, result) ; 
					
					Message message = new Message();
		            message.what = id;
		            message.obj = result;
		            handler.sendMessage(message);
					
				}
			} ).start();
			
		}
		
	}
	
	private String regularAddress( String result ){
		String str = "" ; 
		Pattern p = Pattern.compile("来自：(.*)<br/><br/></td>");  
		Matcher m = p.matcher(result);  
		while(m.find()){
			str += m.group(1) ; 
		}
		return str ; 
	}
	
	private String executeHttpGet( final String ip  ) {
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(API_URL + ip);
            connection = (HttpURLConnection) url.openConnection();
            in = new InputStreamReader(connection.getInputStream(),"gb2312");
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return result;
    }
    
}
