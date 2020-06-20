package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.RoleInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-14 13:00
 */
public interface RoleInfoRepository extends MongoRepository<RoleInfo, String> {

    RoleInfo findByToken(String token);

}
