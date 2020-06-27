package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-05 20:59
 */
public interface UserRepository extends MongoRepository<User, String> {

    User findByOpenid(String openid);
}

