package com.wellsoft.distributedlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedlogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedlogWebApplication.class, args);
    }


}
