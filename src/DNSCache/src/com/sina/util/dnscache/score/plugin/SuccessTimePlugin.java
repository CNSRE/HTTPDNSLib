package com.sina.util.dnscache.score.plugin;

import java.util.ArrayList;

import com.sina.util.dnscache.model.IpModel;
import com.sina.util.dnscache.score.IPlugIn;

public class SuccessTimePlugin extends IPlugIn{

	public SuccessTimePlugin(float ratio) {
		super(ratio);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(ArrayList<IpModel> list) {
		// TODO Auto-generated method stub

		final float dayTime = 24 * 60 ; 
		final float bi = ratio / dayTime ; 
		
		for( IpModel temp : list ){
			
			if( temp.finally_success_time == null || temp.finally_success_time.equals("") ) continue ;
			
			long outTime = Long.parseLong( temp.finally_success_time ) ; 
			long offTime = ( System.currentTimeMillis() - outTime ) / 1000 / 60; //除1000 是为了干掉毫秒  /60 是为了干掉秒
			
			if( offTime > dayTime ){ continue ; }
			else{ temp.grade += super.ratio - ( bi * offTime ) ; }
		}
		
	}

	
	
}
