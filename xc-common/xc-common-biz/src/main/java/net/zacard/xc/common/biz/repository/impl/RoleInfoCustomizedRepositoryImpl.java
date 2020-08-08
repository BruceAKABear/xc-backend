package net.zacard.xc.common.biz.repository.impl;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.RoleInfo;
import net.zacard.xc.common.biz.repository.RoleInfoCustomizedRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author guoqw
 * @since 2020-08-07 21:29
 */
@Repository
@Slf4j
public class RoleInfoCustomizedRepositoryImpl implements RoleInfoCustomizedRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long newCount(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("create_time").gte(req.getStart()).lt(req.getEnd());
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        query.addCriteria(where);
        log.info("newCount query：" + query);
        return mongoTemplate.count(query, RoleInfo.class);
    }

    @Override
    public long count(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("create_time").lt(req.getEnd());
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        query.addCriteria(where);
        log.info("newCount query：" + query);
        return mongoTemplate.count(query, RoleInfo.class);
    }
}
