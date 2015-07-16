package com.sina.util.dnscache.score;

import java.util.ArrayList;

import com.sina.util.dnscache.model.IpModel;


public abstract class IPlugIn {

	/**
	 * 插件所占总分数的比值
	 */
	public float ratio = 0 ; 

	/**
	 * 构造
	 * @param ratio 分数比值
	 */
	public IPlugIn(float ratio){
		this.ratio = ratio ; 
	}
	
	/**
	 * 插件实现计算分值的方法
	 */
	public abstract void run( ArrayList<IpModel> list );
	
}
