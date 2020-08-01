package net.zacard.xc.miniprogram.biz.service.pay;

import net.zacard.xc.common.biz.entity.Trade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author guoqw
 * @since 2020-07-31 21:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PayServiceTest {

    @Autowired
    private PayService payService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void unifiedOrder() {
    }

    @Test
    public void callback() {
    }

    @Test
    public void query() throws InterruptedException {
        String orderId = "202007312121024360000140000";
        Trade trade = payService.query(orderId);
        System.out.println("trade:" + trade);
        TimeUnit.SECONDS.sleep(30);
    }

    @Test
    public void payQuery() {
    }
}
