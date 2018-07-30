package org.wowtools.springcloudext.ngineureka.util;

import org.springframework.util.StringUtils;
import org.wowtools.common.utils.ResourcesReader;

import java.io.File;
import java.util.Properties;

/**
 * 常量
 *
 * @author liuyu
 * @date 2018/2/8
 */
public class Constant {
    /**
     * nginx用到的服务配置文件ngineureka_xx.conf的路径
     */
    public static final String confPath;


    /**
     * 心跳周期，定时获取注册中心的情况的周期
     */
    public static final long heartbeatCycle;

    /***
     * 项目根路径
     */
    public static final String rootPath;

    /**
     * 缓存最近运行记录的次数
     */
    public static final int RecordCacheSize;

    static {
        try {
            {
                String str = ResourcesReader.getRootPath(Constant.class);
                int i = str.indexOf("!");
                if (i > 0) {
                    str = str.substring(0, str.lastIndexOf(File.separator, i));
                }
                if (str.charAt(str.length() - 1) != '/') {
                    str += "/";
                }
                rootPath = str;
            }
            Properties p = new Properties();
            p.load(ResourcesReader.readStream(rootPath + "/config.properties"));
            confPath = p.getProperty("confPath");

            long t;
            try {
                t = (long) (Double.valueOf(p.getProperty("heartbeatCycle")) * 1000);
            } catch (Exception e) {
                t = 300000;
            }
            heartbeatCycle = t;

            RecordCacheSize = getCfgOrDefault(p, "recordCacheSize", 10);
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件异常", e);
        }
    }

    /**
     * 获取一个配置，若未配置，则使用默认值
     *
     * @param key
     * @param defaultV
     * @return
     */
    private static String getCfgOrDefault(Properties p, String key, String defaultV) {
        String v = p.getProperty(key);
        if (StringUtils.isEmpty(v)) {
            return defaultV;
        }
        return v;
    }

    /**
     * 获取一个配置，若未配置，则使用默认值
     *
     * @param key
     * @param defaultV
     * @return
     */
    private static int getCfgOrDefault(Properties p, String key, int defaultV) {
        String v = p.getProperty(key);
        if (StringUtils.isEmpty(v)) {
            return defaultV;
        }
        return Integer.valueOf(v.trim());
    }
}
