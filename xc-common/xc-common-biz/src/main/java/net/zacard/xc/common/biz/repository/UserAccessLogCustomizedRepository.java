package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @author guoqw
 * @since 2020-06-22 08:33
 */
@NoRepositoryBean
public interface UserAccessLogCustomizedRepository extends Repository<UserAccessLog, String> {

    /**
     * 根据条件查询新增用户数
     */
    int newCount(DataOverviewReq req);

    /**
     * 根据条件查询总用户数
     */
    int count(DataOverviewReq req);

}
