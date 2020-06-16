package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
        miniProgramConfig.setName("测试小程序01");
        miniProgramConfig.setAppId("wx0e63bb140eabbcab");
        miniProgramConfig.setMchId("1597282921");
        miniProgramConfig.setKey("2782F226BBAE4C4BA136686AB90DF43F");
        miniProgramConfigRepository.save(miniProgramConfig);
    }

    @Test
    public void update() throws Exception {
        String id = "5edb298eb35908d4df9e111f";
        MiniProgramConfig miniProgramConfig = miniProgramConfigRepository.findOne(id);
        List<String> channels = new ArrayList<>();
        channels.add("5edb29cfb35908d4f812df9d");
        miniProgramConfigRepository.save(miniProgramConfig);
    }
}
