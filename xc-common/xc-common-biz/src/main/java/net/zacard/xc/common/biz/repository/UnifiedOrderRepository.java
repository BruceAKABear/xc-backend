package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.UnifiedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-05 21:36
 */
public interface UnifiedOrderRepository extends MongoRepository<UnifiedOrder, String> {
}
