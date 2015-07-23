package com.sina.util.dnscache.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONStringer;

import com.sina.util.dnscache.DnsCacheApplication;
import com.sina.util.dnscache.simulationtask.TaskModel;
/**
 * httpdns请求数据对比的缓存文件
 * @author xingyu10
 *
 */
public class HttpDnsRecordUtil {

    private static final String DNS_LOG_FOLDER = "/Android/data/.log/com.sina.util.dnscache/cache/";

    /**
     * 将任务数缓存至本地
     * @param model
     */
    public static void record(TaskModel model) {
        File logFolder = getLogFolder();
        if (null == logFolder) {
            return;
        }
        File writtingFile = getWrittingFile(logFolder);
        if (null == writtingFile) {
            return;
        }
        FileWriter writer = null;
        String content = generateLogContent(model);
        try {
            writer = new FileWriter(writtingFile, true);
            String lineSeparator = System.getProperty("line.separator");
            writer.write(content + lineSeparator);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获得最近使用的缓存文件
     * @return
     */
    public static File getRecentlyRecordFile(){
        File logFolder = getLogFolder();
        if (null == logFolder) {
            return null;
        }
        return getWrittingFile(logFolder);
    }
    /**
     * 获取所有的缓存文件
     * @return
     */
    public static File[] getRecordFiles(){
        File logFolder = getLogFolder();
        if (null == logFolder) {
            return null;
        }
        return logFolder.listFiles();
    }
    /**
     * 删除缓存文件
     * @return
     */
    public static boolean removeFiles(){
        File logFolder = getLogFolder();
        if (null == logFolder) {
            return false;
        }
        boolean succ = true;
        File[] files = logFolder.listFiles();
        if (null != files) {
            for (File dstFile : files) {
                succ &= dstFile.delete();
            }
        }
        return succ;
    }
    private static String generateLogContent(TaskModel model) {
        String result = null;
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object().key("taskID").value(model.taskID).key("taskExpendTime").value(model.taskExpendTime).key("hostExpendTime")
                    .value(model.hostExpendTime).key("domainExpendTime").value(model.domainExpendTime).endObject();
            result = jsonStringer.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * eg: httpdns.0.log httpdns.1.log
     * 
     * @param folder
     * @return
     */
    static File getWrittingFile(File folder) {
        File file = null;
        if (null != folder) {
            String[] list = folder.list();
            if (null == list || list.length == 0) {
                file = new File(folder, "httpdns." + 0 + ".log");
                return file;
            } else {
                int lastIndex = list.length - 1;
                file = new File(folder, "httpdns." + lastIndex + ".log");
                // 10MB则继续新建
                if (file.length() > 10 * 1024 * 1024) {
                    file = new File(folder, "httpdns." + (lastIndex + 1) + ".log");
                }
                return file;
            }
        }
        return file;
    }

    static File getLogFolder() {
        return createLogFolderIfNecessary();
    }

    static File createLogFolderIfNecessary() {
        File cacheDir = StorageUtils.getCacheDirectory(DnsCacheApplication.mGlobalInstance);
        File logdir = new File(cacheDir, ".log");
        if (!logdir.exists()) {
            logdir.mkdirs();
        }
        return logdir;
    }
}
