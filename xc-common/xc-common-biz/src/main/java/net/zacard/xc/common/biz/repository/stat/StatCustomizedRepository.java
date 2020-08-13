package net.zacard.xc.common.biz.repository.stat;

import net.zacard.xc.common.biz.entity.DataOverviewReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author guoqw
 * @since 2020-08-11 20:03
 */
public interface StatCustomizedRepository {

    long newCount(DataOverviewReq req);

    long count(DataOverviewReq req);


    default Criteria base(Criteria where, DataOverviewReq req, boolean isBetween) {
        if (isBetween) {
            where.and("create_time").gte(req.getStart()).lt(req.getEnd());
        } else {
            where.and("create_time").lt(req.getEnd());
        }
        String channelId = req.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            where.and("channel_id").is(channelId);
        }
        String appId = req.getAppId();
        if (StringUtils.isNotBlank(appId)) {
            where.and("app_id").is(appId);
        }
        return where;
    }
}
