package net.zacard.xc.miniprogram.biz.service.pay;

import net.zacard.xc.common.biz.entity.Trade;

/**
 * 更新订单交易状态事件
 *
 * @author guoqw
 * @since 2020-08-02 20:36
 */
public class TradeStateUpdateEvent extends AbstractTradeEvent {

    private static final long serialVersionUID = -7167228201544554411L;

    public TradeStateUpdateEvent(Trade trade) {
        super(trade);
    }
}
