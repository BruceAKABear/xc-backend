package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author guoqw
 * @since 2020-06-06 16:01
 */
public interface TradeRepository extends MongoRepository<Trade, String> {

    Trade findByOrderId(String orderId);

    Trade findByChannelIdAndChannelOrderId(String channelId, String channelOrderId);
}
