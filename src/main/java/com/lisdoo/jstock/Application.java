package com.lisdoo.jstock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.health.ObjectMapperDateFormatExtend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.DateFormat;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
}
