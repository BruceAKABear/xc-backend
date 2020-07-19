package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-06 16:01
 */
public interface TradeRepository extends MongoRepository<Trade, String> {

    Trade findByOrderId(String orderId);

    Trade findByChannelIdAndChannelOrderId(String channelId, String channelOrderId);

    List<Trade> findByCreateTimeBetween(Date start, Date end);

    Integer countByOpenidAndCreateTimeLessThan(String openid, Date date);
}
