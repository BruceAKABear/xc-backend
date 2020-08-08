package net.zacard.xc.common.biz.repository.impl;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.repository.UserAccessLogCustomizedRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-08-07 19:50
 */
@Repository
@Slf4j
public class UserAccessLogCustomizedRepositoryImpl implements UserAccessLogCustomizedRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据条件查询新增用户数
     */
    @Override
    public long newCount(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("new_user").is(true)
                .and("create_time").gte(req.getStart()).lt(req.getEnd());
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        query.addCriteria(where);
        log.info("newCount query：" + query);
        return mongoTemplate.count(query, UserAccessLog.class);
    }

    /**
     * 根据条件查询总用户数
     */
    @Override
    public long count(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("new_user").is(true)
                .and("create_time").lt(req.getEnd());
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        query.addCriteria(where);
        log.info("count query：" + query);
        return mongoTemplate.count(query, UserAccessLog.class);
    }

    /**
     * 查询指定时间新增的用户openid集合
     */
    @Override
    public List<UserAccessLog> newUserOpenids(DataOverviewReq req) {
        Query query = new Query();
        Criteria where = Criteria.where("new_user").is(true)
                .and("create_time").gte(req.getStart()).lt(req.getEnd());
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        query.addCriteria(where);
        query.fields().include("openid");
        log.info("newCount query：" + query);
        return mongoTemplate.find(query, UserAccessLog.class);
    }
}
