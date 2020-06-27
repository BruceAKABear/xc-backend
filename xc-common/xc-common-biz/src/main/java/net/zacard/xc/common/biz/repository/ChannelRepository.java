package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-06 10:35
 */
public interface ChannelRepository extends MongoRepository<Channel, String> {

    List<Channel> findByIdIn(List<String> ids);

    List<Channel> findByMiniProgramConfigId(String miniProgramConfigId);

    List<Channel> findByDeletedIsFalse();
}
