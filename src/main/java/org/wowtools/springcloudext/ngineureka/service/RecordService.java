package org.wowtools.springcloudext.ngineureka.service;

import org.springframework.stereotype.Service;
import org.wowtools.springcloudext.ngineureka.pojo.Record;
import org.wowtools.springcloudext.ngineureka.util.Constant;

import java.util.ArrayDeque;

/**
 * 运行记录service
 *
 * @author liuyu
 * @date 2018/3/9
 */
@Service
public class RecordService {
    private final ArrayDeque<Record> recordCache = new ArrayDeque<>(Constant.RecordCacheSize);

    /**
     * 获取最近几次运行记录
     *
     * @return
     */
    public Record[] getLately() {
        synchronized (recordCache) {
            Record[] res = new Record[recordCache.size()];
            int i = 0;
            for (Record record : recordCache) {
                res[i] = record;
                i++;
            }
            return res;
        }
    }

    /**
     * 存储一个运行记录
     *
     * @param record
     */
    public void save(Record record) {
        synchronized (recordCache) {
            if (recordCache.size() == Constant.RecordCacheSize) {
                recordCache.poll();
            }
            recordCache.add(record);
        }
    }
}
