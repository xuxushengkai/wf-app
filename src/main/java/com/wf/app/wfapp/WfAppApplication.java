package com.wf.app.wfapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class WfAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WfAppApplication.class, args);
    }

}
