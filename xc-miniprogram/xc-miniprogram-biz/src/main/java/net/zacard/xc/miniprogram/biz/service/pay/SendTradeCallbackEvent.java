package net.zacard.xc.miniprogram.biz.service.pay;

import net.zacard.xc.common.biz.entity.Trade;

/**
 * @author guoqw
 * @since 2020-08-02 20:41
 */
public class SendTradeCallbackEvent extends AbstractTradeEvent {

    private static final long serialVersionUID = 8793286784046649969L;

    public SendTradeCallbackEvent(Trade trade) {
        super(trade);
    }
}
