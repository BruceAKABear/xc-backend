package net.zacard.xc.website.repository;

import net.zacard.xc.website.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-07-11 14:31
 */
public interface UserRepository extends MongoRepository<User,String> {
}
