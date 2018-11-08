package org.wowtools.springcloudext.ngineureka.service;

import org.springframework.stereotype.Service;
import org.wowtools.common.utils.ResourcesReader;
import org.wowtools.springcloudext.ngineureka.pojo.Record;
import org.wowtools.springcloudext.ngineureka.pojo.ServiceRecord;
import org.wowtools.springcloudext.ngineureka.util.Constant;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * 操作nginx的服务
 *
 * @author liuyu
 * @date 2018/3/9
 */
@Service
public class NginxService {
    /**
     * location块中扩展的参数
     */
    private final HashMap<String, String> locationParams;

    {
        String path = Constant.rootPath + "locationParam.txt";
        if (!new File(path).exists()) {
            locationParams = new HashMap<>(0);
        } else {
            try {
                String locationParam = ResourcesReader.readStr(path);
                System.out.println("locationParam:\n" + locationParam);
                String[] strs = locationParam.split("\n");
                HashMap<String, StringBuilder> kv = new HashMap<>();
                StringBuilder param = null;
                for (String str : strs) {
                    if (str.length() > 0 && str.charAt(0) == '>') {
                        param = new StringBuilder();
                        String key = str.substring(1).trim().toLowerCase();
                        kv.put(key, param);
                    } else {
                        param.append('\t').append(str).append('\n');
                    }
                }
                locationParams = new HashMap<>(kv.size());
                kv.forEach((k, sb) -> locationParams.put(k, sb.toString()));
            } catch (Exception e) {
                throw new RuntimeException("解析配置文件locationParam.txt异常", e);
            }
        }

    }

    /**
     * 负载均衡策略
     */
    private final HashMap<String, String> loadBalancingStrategys;

    {
        String path = Constant.rootPath + "loadBalancingStrategy.properties";
        if (new File(path).exists()) {
            Properties p = new Properties();
            try {
                p.load(ResourcesReader.readStream(path));
            } catch (IOException e) {
                throw new RuntimeException("loadBalancingStrategy.properties config err", e);
            }
            Set<String> appNames = p.stringPropertyNames();
            loadBalancingStrategys = new HashMap<>(appNames.size());
            for (String appName : appNames) {
                String value = p.getProperty(appName);
                String strategy;
                switch (value) {
                    case "least_conn":
                        strategy = "\tleast_conn;\n";
                        break;
                    case "ip_hash":
                        strategy = "\tip_hash;\n";
                        break;
                    case "polling":
                        strategy = "";
                        break;
                    case "url_hash":
                        strategy = "\thash $request_uri;\n";
                        break;
                    case "fair":
                        strategy = "\tfair;\n";
                        break;
                    default:
                        strategy = "\tleast_conn;\n";
                }
                loadBalancingStrategys.put(appName, strategy);
            }
        } else {
            loadBalancingStrategys = new HashMap<>(0);
        }

    }

    /**
     * 将服务信息写入nginx配置并reload
     *
     * @param record
     */
    public void reload(Record record) {
        writeCfg(record);
        record.setReloadStartTimestamp(System.currentTimeMillis());
        reloadCfg(record);
        record.setReloadEndTimestamp(System.currentTimeMillis());
    }

    /**
     * 将服务信息写入nginx配置
     *
     * @param record
     */
    private void writeCfg(Record record) {
        try {
            StringBuilder sbUpstream = new StringBuilder();
            StringBuilder sbServer = new StringBuilder();
            ServiceRecord[] services = record.getServices();
            for (ServiceRecord service : services) {
                String[] urls = service.getServiceUrls();
                String appName = service.getName();
                //upstream
                sbUpstream.append("upstream upstream-").append(appName).append("{\n");
                String loadBalancingStrategy = loadBalancingStrategys.get(appName);
                if (null == loadBalancingStrategy) {
                    loadBalancingStrategy = "\tleast_conn;\n";
                }
                sbUpstream.append(loadBalancingStrategy);
                //location
                sbServer.append("location ^~ /").append(appName).append("/ {\n");
                sbServer.append("\tproxy_pass http://upstream-").append(appName).append(";\n");
                String param = locationParams.get(appName);
                if (null != param) {
                    sbServer.append(param);
                }
                sbServer.append("}\n\n");
                for (String url : urls) {
                    sbUpstream.append("\tserver ").append(url).append(";\n");
                }
                sbUpstream.append("}\n\n");
            }
            write(sbUpstream.toString(), "/ngineureka_upstream.conf");
            write(sbServer.toString(), "/ngineureka_location.conf");
        } catch (Exception e) {
            record.setException(Record.State.ErrOnWriteNginxCfg, e);
            throw new RuntimeException(e);
        }
    }

    private static void write(String cfg, String name) throws Exception {
        String path = Constant.confPath + name;
        File f = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            out.write(cfg.getBytes());
        } finally {
            out.close();
        }
    }

    /**
     * 重新加载nginx配置
     */
    private static void reloadCfg(Record record) {
        String fileName = "/".equals(File.separator) ? "reload.sh" : "reload.bat";
        try {
            String res = exeCmd(Constant.rootPath + fileName);
            if (null != res && res.length() > 0) {
                record.addMsg("nginx reload:" + res);
            }
        } catch (Exception e) {
            record.setException(Record.State.ErrOnReloadNginx, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行控制台命令
     *
     * @param commandStr
     */
    private static String exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("执行控制台命令异常:" + commandStr, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
