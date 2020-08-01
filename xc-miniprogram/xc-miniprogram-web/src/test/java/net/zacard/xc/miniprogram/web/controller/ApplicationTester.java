package net.zacard.xc.miniprogram.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author guoqw
 * @since 2018-04-26 17:51
 */
@EnableCaching
@EnableMongoRepositories(basePackages = {"net.zacard.xc.*.biz.repository"})
@SpringBootApplication(scanBasePackages = "net.zacard", exclude = DataSourceAutoConfiguration.class)
@EnableMongoAuditing
@Slf4j
public class ApplicationTester {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTester.class, args);
    }
}
