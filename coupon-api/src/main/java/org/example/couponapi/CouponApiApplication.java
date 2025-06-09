package org.example.couponapi;

import org.example.couponcore.CouponCoreConfiguration;
import org.example.couponcore.exception.CouponIssueException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Import(CouponCoreConfiguration.class)
@SpringBootApplication
public class CouponApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name","application-core,application-api");
        SpringApplication.run(CouponApiApplication.class, args);
    }

}
