package com.sina.util.dnscache.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sina.util.dnscache.Tools;

import android.content.Context;
import android.util.Log;

public class FileTools {

	public static ArrayList<String> getFromAssets(Context context, String fileName) {
		ArrayList<String> strList = new ArrayList<String>();
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				strList.add(line);
		} catch (Exception e) {
			Tools.log("TAG", "读取文件错误") ;
			e.printStackTrace();
		}
		return strList;
	}

}
