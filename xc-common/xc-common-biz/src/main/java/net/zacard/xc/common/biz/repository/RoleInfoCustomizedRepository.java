package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.RoleInfo;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @author guoqw
 * @since 2020-08-07 21:27
 */
@NoRepositoryBean
public interface RoleInfoCustomizedRepository extends Repository<RoleInfo, String> {

    long newCount(DataOverviewReq req);

    long count(DataOverviewReq req);
}
