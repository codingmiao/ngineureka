package org.wowtools.springcloudext.ngineureka.pojo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 运行记录，存放每次心跳的运行信息
 *
 * @author liuyu
 * @date 2018/3/9
 */
public class Record {
    /**
     * 开始时的时间戳
     */
    private long startTimestamp;
    /**
     * 查询注册中心开始时的时间戳
     */
    private long queryStartTimestamp;
    /**
     * 查询注册中心完毕时的时间戳
     */
    private long queryEndTimestamp;
    /**
     * 执行nginx -s reload开始时的时间戳
     */
    private long reloadStartTimestamp;
    /**
     * 执行nginx -s reload完成时的时间戳
     */
    private long reloadEndTimestamp;

    /**
     * 本次查询到的服务
     */
    private ServiceRecord[] services;

    /**
     * 异常
     */
    private Exception exception;
    /**
     * 处理状态分类
     */
    private State state;

    /**
     * 异常类型
     */
    public enum State {
        Success("success"),
        ServiceNotChange("ServiceNotChange"),
        ErrOnQueryEureka("ErrOnQueryEureka"),
        ErrOnParseEurekaRes("ErrOnParseEurekaRes"),
        ErrOnWriteNginxCfg("ErrOnWriteNginxCfg"),
        ErrOnReloadNginx("ErrOnReloadNginx");
        private final String type;

        State(String type) {
            this.type = type;
        }
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        if (null != services) {
            JSONArray jaService = new JSONArray();
            for (ServiceRecord service : services) {
                jaService.put(service.toJson());
            }
            jo.put("services", jaService);
        }
        if (null != state) {
            jo.put("exceptionType", state.type);
        }
        if(null!=exception){
            jo.put("exception", exception.getMessage());
        }

        jo.put("startTimestamp", startTimestamp);
        jo.put("queryStartTimestamp", queryStartTimestamp);
        jo.put("queryEndTimestamp", queryEndTimestamp);
        jo.put("reloadStartTimestamp", reloadStartTimestamp);
        jo.put("reloadEndTimestamp", reloadEndTimestamp);
        return jo;
    }

    public void setException(State exceptionType, Exception exception) {
        this.exception = exception;
        this.state = exceptionType;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getQueryStartTimestamp() {
        return queryStartTimestamp;
    }

    public void setQueryStartTimestamp(long queryStartTimestamp) {
        this.queryStartTimestamp = queryStartTimestamp;
    }

    public long getQueryEndTimestamp() {
        return queryEndTimestamp;
    }

    public void setQueryEndTimestamp(long queryEndTimestamp) {
        this.queryEndTimestamp = queryEndTimestamp;
    }

    public long getReloadStartTimestamp() {
        return reloadStartTimestamp;
    }

    public void setReloadStartTimestamp(long reloadStartTimestamp) {
        this.reloadStartTimestamp = reloadStartTimestamp;
    }

    public long getReloadEndTimestamp() {
        return reloadEndTimestamp;
    }

    public void setReloadEndTimestamp(long reloadEndTimestamp) {
        this.reloadEndTimestamp = reloadEndTimestamp;
    }

    public ServiceRecord[] getServices() {
        return services;
    }

    public void setServices(ServiceRecord[] services) {
        this.services = services;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
