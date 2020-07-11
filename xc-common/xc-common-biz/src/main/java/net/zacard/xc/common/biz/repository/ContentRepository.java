package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-07-07 21:10
 */
public interface ContentRepository extends MongoRepository<Content, String> {

    List<Content> findByInfoId(String infoId);
}
