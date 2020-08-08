package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.UserAccessLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-06 09:15
 */
public interface UserAccessLogRepository extends MongoRepository<UserAccessLog, String> {

    UserAccessLog findByUserToken(String userToken);

    /**
     * 查询一定时间段内的用户情况
     */
    List<UserAccessLog> findDistinctOpenidByCreateTimeBetween(Date start, Date end);

    /**
     * 统计创建时间在指定时间的用户数量
     */
    long countDistinctOpenidByCreateTimeLessThan(Date date);

    List<UserAccessLog> findDistinctOpenidByCreateTimeBetweenAndOpenidIn(Date start, Date end, List<String> openids);
}
