package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.repository.stat.StatCustomizedRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-22 08:33
 */
@NoRepositoryBean
public interface UserAccessLogCustomizedRepository extends StatCustomizedRepository,Repository<UserAccessLog, String> {

    /**
     * 查询指定时间新增的用户openid集合
     */
    List<UserAccessLog> newUserOpenids(DataOverviewReq req);

}
