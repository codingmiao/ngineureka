package org.wowtools.springcloudext.ngineureka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * web页面
 *
 * @author liuyu
 * @date 2018/7/19
 */
@Controller
public class WebController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
