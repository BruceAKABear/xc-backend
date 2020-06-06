package net.zacard.xc.miniprogram.web.controller.pay;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.PrepareOrderReq;
import net.zacard.xc.common.biz.entity.PrepareOrderRes;
import net.zacard.xc.miniprogram.biz.service.pay.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-06-05 20:21
 */
@RequestMapping(path = "/pay")
@RestController
public class PayController {

    @Autowired
    private PayService payService;

    /**
     * 预下单，返回下单参数
     */
    @RequestMapping(path = "/prepare/info")
    public Response<PrepareOrderRes> prepareOrder(@RequestBody @Validated PrepareOrderReq prepareOrderReq) {
        return Response.success(payService.unifiedOrder(prepareOrderReq));
    }
}
