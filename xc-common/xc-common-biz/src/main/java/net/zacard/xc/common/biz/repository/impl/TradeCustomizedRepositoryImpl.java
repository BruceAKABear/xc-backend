package net.zacard.xc.common.biz.repository.impl;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.repository.TradeCustomizedRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-08-03 14:25
 */
@Repository
@Slf4j
public class TradeCustomizedRepositoryImpl implements TradeCustomizedRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查找异常的订单：超过interval(毫秒)未达到终态
     */
    @Override
    public List<Trade> findExceptionTradesWith(long intervalMillisecond) {
        Query query = new Query();
        query.addCriteria(exceptionTradeWhere(intervalMillisecond));
        return mongoTemplate.find(query, Trade.class);
    }

    @Override
    public List<Trade> findExceptionTradesWithLimit(long intervalMillisecond, int limit) {
        Query query = new Query();
        query.limit(limit);
        // 设置排序字段
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.addCriteria(exceptionTradeWhere(intervalMillisecond));
        return mongoTemplate.find(query, Trade.class);
    }

    private Criteria exceptionTradeWhere(long intervalMillisecond) {
        return Criteria.where("create_time")
                .lt(DateTime.now().minusMillis((int) intervalMillisecond).toDate())
                .orOperator(Criteria.where("trade_state").exists(false),
                        Criteria.where("trade_state").in("NOTPAY", "USERPAYING"));
    }
}
