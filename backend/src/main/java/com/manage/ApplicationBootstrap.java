package com.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luya
 * @created 2018-05-23
 */
@SpringBootApplication
public class ApplicationBootstrap {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.jnu.encoding","utf-8");
        SpringApplication.run(ApplicationBootstrap.class, args);
    }


}
