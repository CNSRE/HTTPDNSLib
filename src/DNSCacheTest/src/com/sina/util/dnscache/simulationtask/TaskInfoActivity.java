package com.sina.util.dnscache.simulationtask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.util.dnscache.R;
import com.sina.util.dnscache.Tools;
import com.sina.util.dnscache.util.IPtoAddress;

public class TaskInfoActivity extends Activity {

	public ImageButton leftBtn = null;

	public TextView taskID = null;
	public TextView status = null;
	public TextView taskStartTime = null;
	public TextView taskStopTime = null;
	public TextView taskExpendTime = null;

	public TextView httpDnsResult = null;
	public TextView httpDnsExpendTime = null;

	public TextView hostUrl = null;
	public TextView hostIp = null;
	public TextView hostCode = null;
	public TextView hostResult = null;
	public ImageView hostImageResutl = null;
	public TextView hostExpendTime = null;

	public TextView domainUrl = null;
	public TextView domainIp = null;
	public TextView domainCode = null;
	public TextView domainResult = null;
	public ImageView domainImageResutl = null;
	public TextView domainExpendTime = null;

	public TextView netType = null;
	public TextView spName = null;

	public static TaskModel taskModel = null;

	public static void InitData(TaskModel taskModel) {
		TaskInfoActivity.taskModel = taskModel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_info);

		leftBtn = (ImageButton) findViewById(R.id.left);
		leftBtn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		IPtoAddress.getInstance().toAddress(1, taskModel.hostIp, handler);
		IPtoAddress.getInstance().toAddress(2, taskModel.domainIp, handler);

		// ////////////////////////////////////////////////////////////

		taskID = (TextView) findViewById(R.id.taskID);
		status = (TextView) findViewById(R.id.status);
		taskStartTime = (TextView) findViewById(R.id.taskStartTime);
		taskStopTime = (TextView) findViewById(R.id.taskStopTime);
		taskExpendTime = (TextView) findViewById(R.id.taskExpendTime);

		httpDnsResult = (TextView) findViewById(R.id.httpDnsResult);
		httpDnsExpendTime = (TextView) findViewById(R.id.httpDnsExpendTime);

		hostUrl = (TextView) findViewById(R.id.hostUrl);
		hostIp = (TextView) findViewById(R.id.hostIp);
		hostCode = (TextView) findViewById(R.id.hostCode);
		hostResult = (TextView) findViewById(R.id.hostResult);
		hostImageResutl = (ImageView) findViewById(R.id.hostImageResult);
		hostExpendTime = (TextView) findViewById(R.id.hostExpendTime);

		domainUrl = (TextView) findViewById(R.id.domainUrl);
		domainIp = (TextView) findViewById(R.id.domainIp);
		domainCode = (TextView) findViewById(R.id.domainCode);
		domainResult = (TextView) findViewById(R.id.domainResult);
		domainImageResutl = (ImageView) findViewById(R.id.domainImageResult);
		domainExpendTime = (TextView) findViewById(R.id.domainExpendTime);

		netType = (TextView) findViewById(R.id.netType);
		spName = (TextView) findViewById(R.id.spName);

		// ////////////////////////////////////////////////////////////

		taskID.setText(String.valueOf("ID: " + taskModel.taskID));
		status.setText(String.valueOf("状态: "
				+ (taskModel.status == 1 ? "成功" : "失败")));
		taskStartTime.setText(String.valueOf("开始时间: "
				+ Tools.getStringDateShort(taskModel.taskStartTime)));
		taskStopTime.setText(String.valueOf("结束时间: "
				+ Tools.getStringDateShort(taskModel.taskStopTime)));
		taskExpendTime.setText(String.valueOf("消耗时间: "
				+ taskModel.taskExpendTime + "毫秒"));

		httpDnsResult.setText(String
				.valueOf("返回结果: " + taskModel.httpDnsResult));
		httpDnsExpendTime.setText(String.valueOf("响应时间: "
				+ taskModel.httpDnsExpendTime + "毫秒"));

		hostUrl.setText(String.valueOf("访问URL: " + taskModel.hostUrl));
		hostIp.setText(String.valueOf("访问服务器IP: " + taskModel.hostIp));
		hostCode.setText(String.valueOf("服务器返回CODE: " + taskModel.hostCode));

		if (isImage(taskModel.hostUrl) == false) {
			hostResult.setText(String.valueOf("服务器返回结果: "
					+ taskModel.hostResult));
		} else {
			hostResult.setText(String.valueOf("服务器返回结果: "));
			hostImageResutl.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Bitmap bit = InputStream2Bimap( taskModel.hostInputStreamResult ) ; 
					Message msg = new Message(); 
					msg.obj = bit ; 
					msg.what = 3 ; 
					handler.sendMessage(msg) ;
				}
			}).start();
		}
		hostExpendTime.setText(String.valueOf("请求耗时: "
				+ taskModel.hostExpendTime + "毫秒"));

		domainUrl.setText(String.valueOf("访问URL: " + taskModel.domainUrl));
		domainIp.setText(String.valueOf("访问服务器IP: " + taskModel.domainIp));
		domainCode
				.setText(String.valueOf("服务器返回CODE: " + taskModel.domainCode));
		if (isImage(taskModel.hostUrl) == false) {
			domainResult.setText(String.valueOf("服务器返回结果: "
					+ taskModel.domainResult));
		} else {
			domainResult.setText(String.valueOf("服务器返回结果: "));
			domainImageResutl.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Bitmap bit = InputStream2Bimap( taskModel.domainInputStreamResult ) ; 
					Message msg = new Message(); 
					msg.obj = bit ; 
					msg.what = 4 ; 
					handler.sendMessage(msg) ;
				}
			}).start();
		}
		domainExpendTime.setText(String.valueOf("请求耗时: "
				+ taskModel.domainExpendTime + "毫秒"));

		netType.setText(String.valueOf("网络环境：" + taskModel.netType));
		spName.setText(String.valueOf("运营商: " + taskModel.spName));

	}

	public boolean isImage(String url) {
		String str = url.substring(url.length() - 4, url.length());
		if (str.equals(".jpg") || str.equals(".gif") || str.equals(".png") ) {
			return true;
		}
		return false;
	}


	@SuppressLint("HandlerLeak")
	public final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				hostIp.setText(String.valueOf("访问服务器IP: " + taskModel.hostIp)
						+ " [" + msg.obj + "]");
				break;
			case 2:
				domainIp.setText(String.valueOf("访问服务器IP: "
						+ taskModel.domainIp + " [" + msg.obj + "]"));
				break;
			case 3:
				hostImageResutl.setImageBitmap((Bitmap)msg.obj);
				break;
			case 4:
				domainImageResutl.setImageBitmap((Bitmap)msg.obj);
				break;
			}
		}
	};

	
	
	
	public Bitmap InputStream2Bimap(byte[] data) {
		
		if( data == null ){
			Tools.log("TAG", "byte[] data IS NULL") ; 
			return null; 
		}
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		if( bitmap == null ){
			
		}
		return bitmap ;
	}
	
	/*
	   * 得到图片字节流 数组大小
	   * */
	public static byte[] readStream(InputStream inStream)  {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.close();
			inStream.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		Tools.log("TAG", "outStream.toByteArray()="+outStream.toByteArray());
		

		return outStream.toByteArray();
	}

}
