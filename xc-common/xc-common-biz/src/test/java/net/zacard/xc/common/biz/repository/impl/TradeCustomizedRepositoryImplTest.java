package net.zacard.xc.common.biz.repository.impl;

import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.repository.TradeCustomizedRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-08-03 15:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeCustomizedRepositoryImplTest {

    @Autowired
    private TradeCustomizedRepository tradeCustomizedRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findExceptionTrades() {
        List<Trade> trades = tradeCustomizedRepository.findExceptionTradesWith(30 * 60 * 1000);
        System.out.println("trades size:" + trades.size());
        Assert.assertTrue(trades.size() > 0);
    }
}
