package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author guoqw
 * @since 2020-06-06 12:37
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MiniProgramConfigRepositoryTest {

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    public void add() throws Exception {
        MiniProgramConfig miniProgramConfig = new MiniProgramConfig();
        miniProgramConfig.setName("畅玩经典趣游");
        miniProgramConfig.setAppId("wxedf5635dea3def45");
        miniProgramConfig.setMchId("1597282921");
        miniProgramConfig.setKey("2782F226BBAE4C4BA136686AB90DF43F");
        miniProgramConfig.setAppSecret("bd49956f1c6855326907b3e0215b540b");
        miniProgramConfig.setMessageToken(RandomStringUtil.getRandomUpperString());
        miniProgramConfig.setMessageEncodingAESKey("dfk3lwJS1tZ5riaLrq04u6ByKsbCh6MyxBmeZ2BncK4");
        miniProgramConfigRepository.save(miniProgramConfig);
    }

    @Test
    public void update() throws Exception {
        String id = "5edb298eb35908d4df9e111f";
        MiniProgramConfig miniProgramConfig = miniProgramConfigRepository.findOne(id);
        miniProgramConfig.setAppSecret("8d205d7245a469305865127cfb92d936");
        miniProgramConfig.setMessageToken(RandomStringUtil.getRandomUpperString());
        miniProgramConfig.setMessageEncodingAESKey("dLCQpFRsGmHePXd1B6tz4m93R7hF4wj8nH7xS5uKRv9");
        miniProgramConfigRepository.save(miniProgramConfig);
    }
}
