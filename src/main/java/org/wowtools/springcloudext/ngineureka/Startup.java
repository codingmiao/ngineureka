package org.wowtools.springcloudext.ngineureka;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.wowtools.common.utils.SimpleHttpUtil;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.List;

/**
 * @author liuyu
 * @date 2018/2/8
 */
public class Startup {

    private static String lastXml;

    public static void main(String[] args) throws Exception {
        System.out.println("启动完毕，初始化配置文件");
        update();
        System.out.println("配置文件初始化完毕，开始定期监控注册中心，可通过检查/ngineureka_upstream.conf来观察服务注册情况");
        while (true) {
            try {
                Thread.sleep(Constant.heartbeatCycle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void update() throws Exception {
        String xml = SimpleHttpUtil.sendGet(Constant.eurekaUrl + "/apps");
        if (xml.equals(lastXml)) {//数据无变化，终止
            return;
        }
        lastXml = xml;
        xml2Config(xml);

        reloadCfg();
    }


    /**
     * 重新加载nginx配置
     */
    private static void reloadCfg() {
        String fileName = "/".equals(File.separator) ? "/reload.sh" : "/reload.bat";
        exeCmd(Constant.rootPath + fileName);
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

    /**
     * 将http://10.111.58.121:10000/eureka/apps返回的xml转为nginx的配置
     *
     * @param xml
     */
    private static void xml2Config(String xml) throws Exception {
        StringReader read = new StringReader(xml);
        InputSource source = new InputSource(read);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document doc = saxBuilder.build(source);
        Element root = doc.getRootElement();
        List<Element> apps = root.getChildren("application");
        StringBuilder sbUpstream = new StringBuilder();
        StringBuilder sbServer = new StringBuilder();
        for (Element app : apps) {
            String appName = app.getChild("name").getText().toLowerCase();
            List<Element> instances = app.getChildren("instance");

            sbUpstream.append("upstream upstream_").append(appName).append("{\n");
            sbUpstream.append("\tleast_conn;\n");

            sbServer.append("location ^~ /").append(appName).append("/ {\n");
            sbServer.append("\tproxy_pass http://upstream_").append(appName).append(";\n");
            sbServer.append("}\n\n");

            for (Element instance : instances) {
                String ip = instance.getChild("ipAddr").getText();
                String port = instance.getChild("port").getText();

                sbUpstream.append("\tserver ").append(ip).append(":").append(port).append(";\n");
            }
            sbUpstream.append("}\n\n");

        }
        writeConfig(sbUpstream.toString(), "/ngineureka_upstream.conf");
        writeConfig(sbServer.toString(), "/ngineureka_location.conf");
    }

    private static void writeConfig(String cfg, String name) throws Exception {
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
}
