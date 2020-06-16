package net.zacard.xc.common.biz.repository;

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
 * @since 2020-06-06 12:44
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void add() throws Exception {
        Channel channel = new Channel();
//        channel.setId("5edb29cfb35908d4f812df9d");
        channel.setName("测试渠道01");
        channel.setAppId("wx0e63bb140eabbcab");
        channel.setAppSecret(RandomStringUtil.getRandomUpperString());
        channel.setMiniProgramConfigId("5edb298eb35908d4df9e111f");
        channel.setPayCallbackMethod("POST");
        channel.setPayCallbackUrl("http://127.0.0.1:8081/test/callback");
        channelRepository.save(channel);
    }
}
