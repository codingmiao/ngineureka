package org.wowtools.springcloudext.ngineureka.pojo;

import org.json.JSONObject;

/**
 * 服务运行记录
 *
 * @author liuyu
 * @date 2018/3/9
 */
public class ServiceRecord {
    /**
     * 服务名
     */
    private String name;
    /**
     * 可用服务列表
     */
    private String[] serviceUrls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getServiceUrls() {
        return serviceUrls;
    }

    public void setServiceUrls(String[] serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        jo.put("name", name);
        jo.put("serviceUrls", serviceUrls);
        return jo;
    }
}
