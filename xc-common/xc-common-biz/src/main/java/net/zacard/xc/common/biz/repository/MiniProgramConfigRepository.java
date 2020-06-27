package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-05 20:39
 */
public interface MiniProgramConfigRepository extends MongoRepository<MiniProgramConfig, String> {

    MiniProgramConfig findByAppId(String appId);

    List<MiniProgramConfig> findByDeletedIsFalse();
}
