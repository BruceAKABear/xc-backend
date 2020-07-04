package net.zacard.xc.website;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;

/**
 * @author guoqw
 * @since 2020-06-01 15:59
 */
@SpringBootApplication(scanBasePackages = "net.zacard", exclude = DataSourceAutoConfiguration.class)
@Slf4j
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableEnvironment env = SpringApplication
                .run(Application.class, args)
                .getEnvironment();
        log.info(
                "xc-website:Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
