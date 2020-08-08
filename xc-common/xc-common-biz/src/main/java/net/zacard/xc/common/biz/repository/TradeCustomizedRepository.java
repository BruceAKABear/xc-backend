package net.zacard.xc.common.biz.repository;

import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.entity.stat.PayStatResult;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-08-03 14:22
 */
@NoRepositoryBean
public interface TradeCustomizedRepository extends Repository<Trade, String> {

    /**
     * 查找异常的订单：超过interval(毫秒)未达到终态
     */
    List<Trade> findExceptionTradesWith(long intervalMillisecond);

    List<Trade> findExceptionTradesWithLimit(long intervalMillisecond, int limit);

    /**
     * 根据条件统计用户支付情况
     */
    List<PayStatResult> statPayUser(DataOverviewReq req);
}
