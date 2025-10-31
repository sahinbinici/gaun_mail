package com.mobilewebservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MobileWebServices {

    public static void main(String[] args) {
        SpringApplication.run(MobileWebServices.class, args);
    }

}
