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
        SpringApplication.run(LifeofbeesMonitorApplication.class,args);
    }

    @Bean
    CommandLineRunner testBeans(WebsiteMonitor websiteMonitor){
        return  args->{
            //System.out.println("Web site monitor bean: "+websiteMonitor);
        };

    }
}
