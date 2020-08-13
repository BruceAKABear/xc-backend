package net.zacard.xc.common.biz.repository;

import com.alibaba.fastjson.JSON;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.util.Constant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-08-11 20:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeRepositoryTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByOrderId() {
    }

    @Test
    public void findByChannelIdAndChannelOrderId() {
    }

    @Test
    public void findByCreateTimeBetween() {
    }

    @Test
    public void countByOpenidAndCreateTimeLessThan() {
    }

    @Test
    public void findTop100ByHasSendCallbackIsFalseAndTradeStateOrderByCreateTimeDesc() {
        List<Trade> trades = tradeRepository.findTop100ByHasSendCallbackIsFalseAndTradeStateOrderByCreateTimeDesc(
                Constant.CODE_SUCCESS);
        System.out.println("result:" + JSON.toJSONString(trades, true));
    }
}
