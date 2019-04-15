package org.wowtools.springcloudext.ngineureka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wowtools.springcloudext.ngineureka.service.NgineurekaService;

/**
 * 操作Controller
 *
 * @author liuyu
 * @date 2018/3/9
 */
@RestController()
@RequestMapping("/cmd")
public class CommandController {
    @Autowired
    private NgineurekaService ngineurekaService;

    @RequestMapping("reload")
    public String reload() {
        ngineurekaService.doOne(true);
        return "success";
    }
}
