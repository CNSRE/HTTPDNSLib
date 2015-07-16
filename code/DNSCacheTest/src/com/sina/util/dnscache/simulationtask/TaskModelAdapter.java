package com.sina.util.dnscache.simulationtask;

import java.util.ArrayList;

import com.sina.util.dnscache.R;
import com.sina.util.dnscache.Tools;
import com.sina.util.dnscache.R.id;
import com.sina.util.dnscache.R.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class TaskModelAdapter extends BaseAdapter {

	public ArrayList<TaskModel> list = null;
	public Context context = null;

	public TaskModelAdapter(Context context) {
		this.context = context;
		initDtaa();
	}
	
	public void initDtaa(){
		this.list = new ArrayList<TaskModel>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (list == null) ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		TaskModel taskModel = (TaskModel)getItem(position) ; 

		if (convertView == null) { 
			convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item, null) ;
		}
		
		TextView taskIDView = (TextView)convertView.findViewById(R.id.taskID) ;
		taskIDView.setText(String.valueOf("任务ID:"+taskModel.taskID));
		
		TextView expendTimeView = (TextView)convertView.findViewById(R.id.expendTime) ;
		long num = taskModel.domainExpendTime - ( taskModel.hostExpendTime + taskModel.httpDnsExpendTime );
		expendTimeView.setText(String.valueOf(num));
		expendTimeView.setTextColor( num > 0 ? 0xFF4b4a4a : 0xFFe90000 );
		
		TextView hostIpView = (TextView)convertView.findViewById(R.id.hostIp) ;
		hostIpView.setText(String.valueOf("HTTP 推荐:"+taskModel.hostIp));
		
		TextView hostCodeView = (TextView)convertView.findViewById(R.id.hostCode) ;
		hostCodeView.setText(String.valueOf( taskModel.hostCode == 200 ? "√" : "×" ));
		hostCodeView.setTextColor( taskModel.hostCode == 200 ? 0xFF00c621 : 0xeFF90000);
		
		TextView domainIpView = (TextView)convertView.findViewById(R.id.domainIp) ;
		domainIpView.setText(String.valueOf("Local 推荐:"+taskModel.domainIp));
		
		TextView domainCodeView = (TextView)convertView.findViewById(R.id.domainCode) ;
		domainCodeView.setText(String.valueOf( taskModel.domainCode == 200 ? "√" : "×" ));
		domainCodeView.setTextColor( taskModel.domainCode == 200 ? 0xFF00c621 : 0xFFe90000);
		
		TextView timeView = (TextView)convertView.findViewById(R.id.time) ;
		timeView.setText(Tools.getStringDateShort(taskModel.taskStartTime));
	
		
		return convertView;
	}


}
