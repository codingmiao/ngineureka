package org.wowtools.springcloudext.ngineureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author liuyu
 * @date 2018/2/8
 */
@SpringBootApplication
@EnableEurekaClient
public class Startup {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Startup.class, args);
    }

}
