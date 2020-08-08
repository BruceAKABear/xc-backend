package net.zacard.xc.common.biz.repository.impl;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.entity.stat.PayStatResult;
import net.zacard.xc.common.biz.repository.TradeCustomizedRepository;
import net.zacard.xc.common.biz.util.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    /**
     * 根据条件查询总支付人数
     */
    @Override
    public List<PayStatResult> statPayUser(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("trade_state").is(Constant.CODE_SUCCESS);
        Date start = req.getStart();
        if (start != null) {
            where.and("create_time").gte(start).lt(req.getEnd());
        } else {
            where.and("create_time").lt(req.getEnd());
        }
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        List<String> openids = req.getOpenids();
        if (CollectionUtils.isNotEmpty(openids)) {
            where.and("openid").nin(openids);
        }
        query.addCriteria(where);
        query.fields().include("openid");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(where),
                Aggregation.group("openid").count().as("count")
                        .sum("total_fee").as("amount"),
                Aggregation.project("count", "amount").and("_id").as("openid")
        );
//        List<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "trade", BasicDBObject.class)
//                .getMappedResults();
//        System.out.println("results:" + JSON.toJSONString(results, true));
        return mongoTemplate.aggregate(aggregation, "trade", PayStatResult.class)
                .getMappedResults();
    }

    private Criteria exceptionTradeWhere(long intervalMillisecond) {
        return Criteria.where("create_time")
                .lt(DateTime.now().minusMillis((int) intervalMillisecond).toDate())
                .orOperator(Criteria.where("trade_state").exists(false),
                        Criteria.where("trade_state").in("NOTPAY", "USERPAYING"));
    }
}
