package net.zacard.xc.miniprogram.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.InetAddress;

/**
 * @author guoqw
 * @since 2020-06-01 15:59
 */
@EnableCaching
@EnableMongoRepositories(basePackages = {"net.zacard.xc.*.biz.repository"})
@SpringBootApplication(scanBasePackages = "net.zacard", exclude = DataSourceAutoConfiguration.class)
@EnableMongoAuditing
@Slf4j
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableEnvironment env = SpringApplication
                .run(Application.class, args)
                .getEnvironment();
        log.info(
                "xc-mini:Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
