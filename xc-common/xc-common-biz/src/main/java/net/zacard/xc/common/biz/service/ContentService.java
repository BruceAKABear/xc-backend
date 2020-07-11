package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.entity.Content;
import net.zacard.xc.common.biz.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqw
 * @since 2020-07-07 21:15
 */
@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    public void add(Content content) {
        contentRepository.save(content);
    }
}
