package org.wowtools.springcloudext.ngineureka.controller;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wowtools.springcloudext.ngineureka.pojo.Record;
import org.wowtools.springcloudext.ngineureka.service.RecordService;

/**
 * 运行记录Controller
 *
 * @author liuyu
 * @date 2018/3/9
 */
@RestController()
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * 获取最近几次运行记录
     *
     * @return
     */
    @RequestMapping("lately")
    public String getLately() {
        Record[] lately = recordService.getLately();
        JSONArray ja = new JSONArray();
        for (Record record : lately) {
            ja.put(record.toJson());
        }
        return ja.toString();
    }
}
