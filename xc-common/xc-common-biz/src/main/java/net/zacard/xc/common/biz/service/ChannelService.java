package net.zacard.xc.common.biz.service;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-09 13:46
 */
@Service
@Slf4j
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    public List<Channel> list() {
        return channelRepository.findByDeletedIsFalse();
    }

    public void add(Channel channel) {
        // 生成appSecret
        channel.setAppSecret(RandomStringUtil.getRandomUpperString());
        channel.setOnline(Boolean.FALSE);
        // 设置回调方式
        if (StringUtils.isBlank(channel.getPayCallbackMethod())) {
            channel.setPayCallbackMethod("POST");
        }
        channelRepository.save(channel);
    }

    public void update(Channel channel) {
        String id = channel.getId();
        if (StringUtils.isBlank(id)) {
            throw BusinessException.withMessage("渠道id不能为空");
        }
        Channel tmp = channelRepository.findOne(id);
        if (tmp == null) {
            throw BusinessException.withMessage("不存在指定id(" + id + ")的渠道信息");
        }
        String oldSecret = tmp.getAppSecret();
        if (!oldSecret.equals(channel.getAppSecret())) {
            log.warn("渠道({})的appSecret从{}变更为{}", channel.getId(), oldSecret, channel.getAppSecret());
        }
        channelRepository.save(channel);
    }
}
