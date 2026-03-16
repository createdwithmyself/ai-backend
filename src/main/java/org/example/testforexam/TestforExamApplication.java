package org.example.testforexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TestforExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestforExamApplication.class, args);
    }
}