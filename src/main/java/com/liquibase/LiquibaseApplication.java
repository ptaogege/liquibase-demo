package com.liquibase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class LiquibaseApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(LiquibaseApplication.class, args);
        /*String list = "mvn clean package liquibase:update";
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(list);
        Process p = Runtime.getRuntime().exec(list);
        System.out.println(p+"---------------我被执行了");
        System.out.println(processBuilder.command());*/
    }

}
