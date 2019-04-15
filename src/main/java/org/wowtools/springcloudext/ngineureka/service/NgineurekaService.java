package org.wowtools.springcloudext.ngineureka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.wowtools.springcloudext.ngineureka.pojo.Record;
import org.wowtools.springcloudext.ngineureka.util.Constant;

/**
 * @author liuyu
 * @date 2018/3/9
 */
@Service
@Order(value = 1)
public class NgineurekaService implements CommandLineRunner {
    @Autowired
    private EurekaReadService eurekaReadService;
    @Autowired
    private NginxService nginxService;
    @Autowired
    private RecordService recordService;

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            try {
                doOne(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Constant.heartbeatCycle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行一次操作
     * @param ignoreNotChange 即使服务没有更新，也继续操作
     */
    public synchronized void doOne(boolean ignoreNotChange) {
        Record record = new Record();
        record.setStartTimestamp(System.currentTimeMillis());
        recordService.save(record);
        boolean changed = eurekaReadService.queryService(record,ignoreNotChange);
        if (!ignoreNotChange&&!changed) {
            return;
        }
        nginxService.reload(record);
        record.setState(Record.State.Success);
    }
}
