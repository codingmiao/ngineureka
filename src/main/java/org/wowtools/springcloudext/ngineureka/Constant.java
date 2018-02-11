package org.wowtools.springcloudext.ngineureka;

import org.wowtools.common.utils.ResourcesReader;

import java.util.Properties;

/**
 * 常量
 *
 * @author liuyu
 * @date 2018/2/8
 */
public class Constant {
    /**
     * 注册中心url，如 http://10.111.58.121:10000/eureka
     */
    public static final String eurekaUrl;
    /**
     * nginx用到的服务配置文件ngineureka_xx.conf的路径
     */
    public static final String confPath;


    /**
     * 心跳周期，定时获取注册中心的情况的周期
     */
    public static final long heartbeatCycle;

    public static final String rootPath;

    static {
        try {
            rootPath = ResourcesReader.getRootPath(Constant.class);
            Properties p = new Properties();
            p.load(ResourcesReader.readStream(rootPath + "/config.properties"));
            eurekaUrl = p.getProperty("eurekaUrl");
            confPath = p.getProperty("confPath");

            long t;
            try {
                t = (long) (Double.valueOf(p.getProperty("heartbeatCycle")) * 1000);
            } catch (Exception e) {
                t = 300000;
            }
            heartbeatCycle = t;
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件异常", e);
        }
    }
}
