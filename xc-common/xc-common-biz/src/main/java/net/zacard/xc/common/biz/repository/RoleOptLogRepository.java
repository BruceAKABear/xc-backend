package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.RoleOptLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-16 23:25
 */
public interface RoleOptLogRepository extends MongoRepository<RoleOptLog, String> {
}
