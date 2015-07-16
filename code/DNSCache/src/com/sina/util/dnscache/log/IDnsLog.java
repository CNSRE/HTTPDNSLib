package com.sina.util.dnscache.log;

import java.io.File;
import java.util.HashMap;

public interface IDnsLog {
    
    /**
     * 记录日志
     * @param type 日志类型
     * @param body 日志信息，建议用json字符串
     */
    void writeLog(int type, String body);
    
    void writeLog(int type, HashMap<String, String> body);
    
    /**
     * 获取日志文件
     * @return
     */
    File getLogFile();
    
    /**
     * 删除文件
     * @return
     */
    boolean deleteLogFile();
}
