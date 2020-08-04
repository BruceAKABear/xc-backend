package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqw
 * @since 2020-08-02 20:25
 */
@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    public Trade query(String orderId) {
        Trade trade = tradeRepository.findByOrderId(orderId);
        if (trade == null) {
            throw BusinessException.withMessage("不存在订单(" + orderId + ")");
        }
        return trade;
    }
}
