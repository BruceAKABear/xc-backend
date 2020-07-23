package net.zacard.xc.common.biz.service;

import com.alibaba.fastjson.JSON;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author guoqw
 * @since 2020-07-06 15:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelServiceTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void list() {
    }

    @Test
    public void add() {
        Channel channel = new Channel();
        channel.setName("深海游戏");
        channel.setAppId("wxedf5635dea3def45");
        channel.setAppSecret(RandomStringUtil.getRandomUpperString());
        channel.setMiniProgramConfigId("5f0ff6337ea3c61c3f29f8ab");
//        channel.setPayCallbackMethod("POST");
        channel.setPayCallbackUrl("http://127.0.0.1:8081/test/callback");
        channelService.add(channel);
    }

    @Test
    public void update() {
        Channel channel = new Channel();
        channel.setId("5f02cf117ea3c61f2f3c8e98");
        channel = channelRepository.findOne(channel.getId());
        channel.setGameH5Url("https://h5.binglue.com/plat2/xichen/index?id=8");
        channelService.update(channel);
        channel = channelRepository.findOne(channel.getId());
        System.out.println("channel:" + JSON.toJSONString(channel, true));
    }
}
