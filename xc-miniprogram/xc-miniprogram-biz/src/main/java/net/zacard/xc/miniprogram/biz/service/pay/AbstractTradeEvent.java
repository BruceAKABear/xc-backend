package net.zacard.xc.miniprogram.biz.service.pay;

import net.zacard.xc.common.biz.entity.Trade;
import org.springframework.context.ApplicationEvent;

/**
 * @author guoqw
 * @since 2020-08-02 20:43
 */
public abstract class AbstractTradeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 2675054718017430316L;

    private Trade trade;

    public AbstractTradeEvent(Trade trade) {
        super(trade);

        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }
}
