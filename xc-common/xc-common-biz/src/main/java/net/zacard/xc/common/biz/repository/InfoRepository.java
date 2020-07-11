package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.Info;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-07-07 21:12
 */
public interface InfoRepository extends MongoRepository<Info, String> {
}
