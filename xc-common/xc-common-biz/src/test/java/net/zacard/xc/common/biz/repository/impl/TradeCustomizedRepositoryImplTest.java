package net.zacard.xc.common.biz.repository.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.entity.stat.PayStatResult;
import net.zacard.xc.common.biz.repository.TradeCustomizedRepository;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
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

    @Test
    public void findExceptionTradesWith() {
    }

    @Test
    public void findExceptionTradesWithLimit() {
    }

    @Test
    public void totalPayUserCount() {
        Date start = DateTime.now().minusDays(2).toDate();
        Date end = DateTime.now().toDate();
        String openid = "oFtQw5QcTAlsbd_rmQ79AI9w-fVk";
        DataOverviewReq req = DataOverviewReq.builder()
                .start(start)
                .end(end)
                .openids(Lists.newArrayList(openid))
                .build();
        List<PayStatResult> results = tradeCustomizedRepository.statPayUser(req);
        System.out.println("results:" + JSON.toJSONString(results, true));
    }
}
