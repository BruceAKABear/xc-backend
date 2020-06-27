package net.zacard.xc.manage.web.config;

import net.zacard.xc.common.biz.infra.mongo.MongoDocumentEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guoqw
 * @since 2019-04-16 16:25
 */
@Configuration
public class MongoAutoConfig {

    @Bean
    public MongoDocumentEventListener mongoDocumentEventListener() {
        return new MongoDocumentEventListener();
    }

}
