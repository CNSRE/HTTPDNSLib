package com.sina.util.dnscache.log;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONStringer;

import com.sina.util.dnscache.AppConfigUtil;

public class HttpDnsLogManager implements IDnsLog {

    /**
     * 错误类型
     */
    public static final int TYPE_ERROR = 1;
    /**
     * 调试信息类型
     */
    public static final int TYPE_INFO = 2;
    /**
     * 测速类型
     */
    public static final int TYPE_SPEED = 3;
    /**
     * 日志文件的最大容量。8MB
     */
    private static final int DEFAULT_MAX_SIZE = 8 * 1024 * 1024;
    /**
     * 调整因子。取值大于0小于1
     */
    private static final float DEFAULT_FACTOR = 0.5f;
    /**
     * 日志文件
     */
    private File mLogFile;
    
    private static HttpDnsLogManager mDnsLogManager;

    public static synchronized HttpDnsLogManager getInstance() {
        if (null == mDnsLogManager) {
            mDnsLogManager = new HttpDnsLogManager();
        }
        return mDnsLogManager;
    }

    private HttpDnsLogManager(){
        tryCreateLogFile();
    }
    
    private void tryCreateLogFile() {
        if (FileUtil.haveFreeSpaceInSD()) {
            mLogFile = new File(AppConfigUtil.getExternalCacheDir(), "httpdns.log");
        } else {
            mLogFile = null;
        }
    }
    @Override
    public synchronized void writeLog(int type, String body) {
        if (null != mLogFile && !mLogFile.exists()) {
            tryCreateLogFile();
        }
        if (null == mLogFile) {
            return;
        }
        adjustFileSize(mLogFile);
        String line = generateJsonStr(type, body);
        FileUtil.writeFileLine(mLogFile, true, line);
    }

    private void adjustFileSize(File file) {
        FileUtil.adjustFileSize(file, DEFAULT_MAX_SIZE, DEFAULT_FACTOR);
    }

    private String generateJsonStr(int type,String body) {
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object()
            .key("type").value(type)
            .key("logInfo").value(body)
            .key("versionName").value(AppConfigUtil.getAppVersionName())
            .key("timestamp").value(System.currentTimeMillis())
            .endObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
        return jsonStringer.toString();
    }
    @Override
    public synchronized File getLogFile() {
        return mLogFile;
    }

    @Override
    public synchronized boolean deleteLogFile() {
        if (null != mLogFile) {
            return mLogFile.delete();
        }
        return false;
    }

    private String generateJsonStrFromMap(HashMap<String, String> map) {
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer = jsonStringer.object();
            Set<Entry<String, String>> entrySet = map.entrySet();
            for (Entry<String, String> entry : entrySet) {
                jsonStringer = jsonStringer.key(entry.getKey()).value(entry.getValue());
            }
            jsonStringer = jsonStringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
        return jsonStringer.toString();
    }
    
    @Override
    public void writeLog(int type, HashMap<String, String> map) {
        String body = generateJsonStrFromMap(map);
        writeLog(type, body);
    }

}
