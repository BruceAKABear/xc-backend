package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.UserAccessLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-06 09:15
 */
public interface UserAccessLogRepository extends MongoRepository<UserAccessLog, String> {

    UserAccessLog findByUserToken(String userToken);
}
