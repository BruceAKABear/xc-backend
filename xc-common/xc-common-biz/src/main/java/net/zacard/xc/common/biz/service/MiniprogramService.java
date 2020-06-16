package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-06 15:08
 */
@Service
public class MiniprogramService {

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public List<Channel> channels(String appId) {
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        return channelRepository.findByMiniProgramConfigId(config.getId());
    }
}
