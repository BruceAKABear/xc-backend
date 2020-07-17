package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.entity.Channel;
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
    }
}
