package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.entity.Content;
import net.zacard.xc.common.biz.entity.Info;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.ContentRepository;
import net.zacard.xc.common.biz.repository.InfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author guoqw
 * @since 2020-07-07 21:12
 */
@Service
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private ContentRepository contentRepository;

    public Info get(String id) {
        Info info = infoRepository.findOne(id);
        List<Content> contents = contentRepository.findByInfoId(info.getId())
                .stream()
                .peek(content -> {
                    if (content.getOrder() == null) {
                        content.setOrder(100);
                    }
                })
                .sorted(Comparator.comparing(Content::getOrder))
                .collect(Collectors.toList());
        info.setContents(contents);
        return info;
    }

    public List<Info> list() {
        return infoRepository.findAll();
    }

    public void add(Info info) {
        infoRepository.save(info);
        List<Content> contents = info.getContents();
        for (Content content : contents) {
            content.setInfoId(info.getId());
        }
        contentRepository.save(contents);
    }

    public void update(Info info) {
        if (StringUtils.isBlank(info.getId())) {
            throw BusinessException.withMessage("咨询页id不能为空");
        }
        infoRepository.save(info);
        contentRepository.save(info.getContents());
    }


}
