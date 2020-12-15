package com.ljj.bledemo.util;


        import java.io.File;

/**
 * Log逻辑管理类
 * <p>
 * Created by ChenJian
 * 2016.10.8 14:22:22.
 */

public class LogManager {

    private static volatile LogManager mInstance;
    private String logFilePath;

    private LogManager() {

    }

    public static LogManager getInstance() {
        if (mInstance == null) {
            synchronized (LogManager.class) {
                if (mInstance == null) {
                    mInstance = new LogManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化log配置
     *
     * @param logFilePath log文件路径，必须是完整的全路径
     */
    public void init(String logFilePath) {
        this.logFilePath = logFilePath;
        createLogFile();
    }

    private boolean createLogFile() {
        boolean ret = false;
        File file = new File(logFilePath);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ret = file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    String getLogFilePath() {
        return logFilePath;
    }
}