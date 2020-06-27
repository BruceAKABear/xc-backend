package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.WxMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-27 19:17
 */
public interface WxMessageRepository extends MongoRepository<WxMessage, String> {
}
