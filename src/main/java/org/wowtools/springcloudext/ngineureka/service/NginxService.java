package org.wowtools.springcloudext.ngineureka.service;

import org.jdom.Element;
import org.springframework.stereotype.Service;
import org.wowtools.springcloudext.ngineureka.pojo.Record;
import org.wowtools.springcloudext.ngineureka.pojo.ServiceRecord;
import org.wowtools.springcloudext.ngineureka.util.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 操作nginx的服务
 *
 * @author liuyu
 * @date 2018/3/9
 */
@Service
public class NginxService {

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
                sbUpstream.append("upstream upstream_").append(appName).append("{\n");
                sbUpstream.append("\tleast_conn;\n");

                sbServer.append("location ^~ /").append(appName).append("/ {\n");
                sbServer.append("\tproxy_pass http://upstream_").append(appName).append(";\n");
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
            exeCmd(Constant.rootPath + fileName);
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
                sb.append(line + "\n");
            }

            return sb.toString();
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
