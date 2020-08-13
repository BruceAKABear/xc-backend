package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.RoleInfo;
import net.zacard.xc.common.biz.repository.stat.StatCustomizedRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @author guoqw
 * @since 2020-08-07 21:27
 */
@NoRepositoryBean
public interface RoleInfoCustomizedRepository extends StatCustomizedRepository,Repository<RoleInfo, String> {

}
