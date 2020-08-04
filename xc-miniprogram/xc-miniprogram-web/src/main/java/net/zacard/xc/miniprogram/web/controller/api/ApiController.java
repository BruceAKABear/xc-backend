package net.zacard.xc.miniprogram.web.controller.api;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.api.entity.RoleInfoDto;
import net.zacard.xc.common.api.entity.UserDto;
import net.zacard.xc.common.biz.entity.PayCallbackReq;
import net.zacard.xc.common.biz.entity.PayCallbackRes;
import net.zacard.xc.common.biz.entity.PayQueryRes;
import net.zacard.xc.common.biz.entity.WxMessageReq;
import net.zacard.xc.common.biz.service.RoleInfoService;
import net.zacard.xc.common.biz.util.ValidateUtils;
import net.zacard.xc.miniprogram.biz.service.message.MessageService;
import net.zacard.xc.miniprogram.biz.service.pay.PayService;
import net.zacard.xc.miniprogram.biz.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private RoleInfoService roleInfoService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(path = "/pay/wx/callback",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = MediaType.APPLICATION_XML_VALUE)
    public PayCallbackRes payCallback(@RequestBody PayCallbackReq payCallbackReq) {
//        log.info("收到回调，payCallbackReq：" + JSON.toJSONString(payCallbackReq, true));
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
            log.info("参数校验不通过：" + validateMessage);
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

    /**
     * 微信消息接入校验
     */
    @GetMapping(path = "/wx/message/{appId}")
    public String wxMessageCheck(@PathVariable String appId,
                                 @RequestParam String signature,
                                 @RequestParam String timestamp,
                                 @RequestParam String nonce,
                                 @RequestParam String echostr) {
        if (messageService.messageCheck(appId, timestamp, nonce, signature)) {
            return echostr;
        }
        return "";
    }

    @PostMapping(path = "/wx/message/{appId}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public String wxMessage(@PathVariable String appId, @RequestBody @Validated WxMessageReq wxMessageReq) {
        messageService.replay(appId, wxMessageReq);
        return "success";
    }

    @RequestMapping(path = "/pay/wx/query")
    public PayQueryRes payQuery(String channelId, String channelOrderId) {
        try {
            return payService.payQuery(channelId, channelOrderId);
        } catch (Exception e) {
            log.error("查询渠道(" + channelId + ")订单(" + channelOrderId + ")失败,msg:" + e.getMessage(), e);
            return PayQueryRes.fail(e.getMessage());
        }
    }

    @RequestMapping(path = "/game/role/info")
    public Response roleInfo(@RequestBody @Validated RoleInfoDto roleInfoDto) {
        String type = roleInfoDto.getType();
        if ("CREATE".equals(type)) {
            roleInfoService.add(roleInfoDto);
        } else {
            roleInfoService.update(roleInfoDto);
        }
        return Response.success();
    }

    @RequestMapping(path = "/user/info")
    public Response<UserDto> userInfo(String userToken) {
        return Response.success(userService.info(userToken));
    }

}
