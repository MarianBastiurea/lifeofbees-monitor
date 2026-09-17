package com.lifeofbees.monitor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LifeofbeesMonitorApplication {
    public static void main(String[] args) {

        System.out.println("*********************************************************************");
        String uri = System.getenv("MONGODB_URI");

        System.out.println("MONGODB_URI set: " + (uri != null));
        System.out.println("MONGODB_URI starts correctly: "
                + (uri != null &&
                (uri.startsWith("mongodb://") || uri.startsWith("mongodb+srv://"))));

        SpringApplication.run(LifeofbeesMonitorApplication.class, args);
    }

    @Bean
    CommandLineRunner testBeans(WebsiteMonitor websiteMonitor){
        return  args->{
            System.out.println("MONGODB_URI starts with: "
                    + System.getenv("MONGODB_URI").substring(0, 14));
        };

    }
}
