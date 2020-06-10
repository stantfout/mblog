package com.usth.mblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {

        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(MainApplication.class, args);
    }

}
