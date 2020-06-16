package net.zacard.xc.miniprogram.web.controller.api;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.api.entity.RoleInfoDto;
import net.zacard.xc.common.api.entity.UserDto;
import net.zacard.xc.common.biz.entity.PayCallbackReq;
import net.zacard.xc.common.biz.entity.PayCallbackRes;
import net.zacard.xc.common.biz.entity.PayQueryRes;
import net.zacard.xc.common.biz.util.ValidateUtils;
import net.zacard.xc.miniprogram.biz.service.pay.PayService;
import net.zacard.xc.miniprogram.biz.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-06-06 16:31
 */
@Slf4j
@RequestMapping(path = "/api")
@RestController
public class ApiController {

    @Autowired
    private PayService payService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/pay/wx/callback",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public PayCallbackRes payCallback(@RequestBody PayCallbackReq payCallbackReq) {
        // 参数校验
        String returnCode = payCallbackReq.getReturnCode();
        String message = null;
        if (!"SUCCESS".equals(returnCode)) {
            message = "收到回调，return code(" + returnCode + ")不为SUCCESS";
            return PayCallbackRes.fail(message);
        }
        // 借助参数校验自定义注解验签(包括其他常规参数校验)
        String validateMessage = ValidateUtils.validateParamsProperty(payCallbackReq);
        if (validateMessage != null) {
            message = validateMessage;
            return PayCallbackRes.fail(message);
        }
        // 回调处理
        try {
            payService.callback(payCallbackReq);
        } catch (Exception e) {
            return PayCallbackRes.fail(e.getMessage());
        }
        return PayCallbackRes.success();
    }

    @RequestMapping(path = "/pay/wx/query")
    public PayQueryRes payQuery(String channelId, String channelOrderId) {
        try {
            return payService.payQuery(channelId, channelOrderId);
        } catch (Exception e) {
            log.error("查询渠道(" + channelId + ")订单(" + channelOrderId + ")失败", e);
            return PayQueryRes.fail(e.getMessage());
        }
    }

    @RequestMapping(path = "/game/role/info")
    public Response roleInfo(@RequestBody @Validated RoleInfoDto roleInfoDto) {
        return Response.success();
    }

    @RequestMapping(path = "/user/info")
    public Response<UserDto> userInfo(String userToken) {
        return Response.success(userService.info(userToken));
    }

}
