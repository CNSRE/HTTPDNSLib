package com.sina.util.dnscache.score.plugin;

import java.util.ArrayList;

import com.sina.util.dnscache.model.IpModel;
import com.sina.util.dnscache.score.IPlugIn;

public class SpeedTestPlugin extends IPlugIn{

	public SpeedTestPlugin(float ratio) {
		super(ratio);
	}

	@Override
	public void run( ArrayList<IpModel> list ) {
		// TODO Auto-generated method stub
		
		//查找到最大速度
		float MAX_SPEED = 0 ;
		for( IpModel temp : list ){
			if( temp.finally_speed == null || temp.finally_speed.equals("") )
				continue ; 
			float finallySpeed = Float.parseFloat(temp.finally_speed) ;
			if( finallySpeed > MAX_SPEED ){
				MAX_SPEED = finallySpeed ; 
			}
		}
		
		//计算比值
		if( MAX_SPEED == 0 ) return ; 
		float bi = super.ratio / MAX_SPEED ; 
		
		//计算得分
		for( IpModel temp : list ){
			if( temp.finally_speed == null || temp.finally_speed.equals("") )
				continue ; 
			float finallySpeed = Float.parseFloat(temp.finally_speed) ;
			temp.grade += finallySpeed * bi ;
		}
	}

}
