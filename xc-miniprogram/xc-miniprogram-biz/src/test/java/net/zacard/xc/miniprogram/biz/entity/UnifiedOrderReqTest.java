package net.zacard.xc.miniprogram.biz.entity;

import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.UnifiedOrderReq;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author guoqw
 * @since 2020-06-05 19:06
 */
public class UnifiedOrderReqTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void buildForMiniProgram() {
        MiniProgramConfig config = new MiniProgramConfig();
        config.setName("test");
        config.setAppId("aaa");
        config.setMchId("bbb");
        config.setKey("ccc");
        UnifiedOrderReq req = UnifiedOrderReq.buildForMiniProgram(config, "测试商品", 10000, "abc");
        System.out.println("sign:" + req.getSign());
        System.out.println("xml:" + req.xml());
    }
}
