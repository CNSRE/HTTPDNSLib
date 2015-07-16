package com.sina.util.dnscache.score.plugin;

import java.util.ArrayList;

import com.sina.util.dnscache.model.IpModel;
import com.sina.util.dnscache.score.IPlugIn;

public class SuccessNumPlugin extends IPlugIn{

	public SuccessNumPlugin(float ratio) {
		super(ratio);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(ArrayList<IpModel> list) {
		// TODO Auto-generated method stub
	
		//查找到最大历史成功次数
		float MAX_SUCCESSNUM = 0 ;
		for( IpModel temp : list ){
			if( temp.success_num == null || temp.success_num.equals("") )
				continue ; 
			float successNum = Float.parseFloat(temp.success_num) ;
			if( successNum > MAX_SUCCESSNUM ){
				MAX_SUCCESSNUM = successNum ; 
			}
		}
		
		//计算比值
		if( MAX_SUCCESSNUM == 0 ) return ; 
		float bi = super.ratio / MAX_SUCCESSNUM ; 
		
		//计算得分
		for( IpModel temp : list ){
			if( temp.success_num == null || temp.success_num.equals("") )
				continue ; 
			float successNum = Float.parseFloat(temp.success_num) ;
			temp.grade += successNum * bi ;
		}
		
	}

}
