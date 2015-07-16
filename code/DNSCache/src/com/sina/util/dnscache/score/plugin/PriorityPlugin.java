package com.sina.util.dnscache.score.plugin;

import java.util.ArrayList;

import com.sina.util.dnscache.model.IpModel;
import com.sina.util.dnscache.score.IPlugIn;

public class PriorityPlugin extends IPlugIn{

	public PriorityPlugin(float ratio) {
		super(ratio);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(ArrayList<IpModel> list) {
		// TODO Auto-generated method stub
		
		//查找到最大优先级
		float MAX_PRIORITY = 0 ; 
		for( IpModel temp : list ){
			if( temp.priority == null || temp.priority.equals("") )
				continue ; 
			float priority = Float.parseFloat(temp.priority) ;
			if( priority > MAX_PRIORITY ){
				MAX_PRIORITY = priority ; 
			}
		}
		
		
		//计算比值
		if( MAX_PRIORITY == 0 ) return ; 
		float bi = super.ratio / MAX_PRIORITY ; 
		
		
		//计算得分
		for( IpModel temp : list ){
			if( temp.priority == null || temp.priority.equals("") )
				continue ; 
			float priority = Float.parseFloat(temp.priority) ;
			temp.grade += priority * bi ;
		}
		
	}

}
