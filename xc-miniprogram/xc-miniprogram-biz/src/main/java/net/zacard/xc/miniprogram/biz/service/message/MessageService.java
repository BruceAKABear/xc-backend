package net.zacard.xc.miniprogram.biz.service.message;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.MiniProgramExtraConfig;
import net.zacard.xc.common.biz.entity.WxMessage;
import net.zacard.xc.common.biz.entity.WxMessageReq;
import net.zacard.xc.common.biz.entity.WxSendMessageReq;
import net.zacard.xc.common.biz.entity.WxSendMessageRes;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.WxMessageRepository;
import net.zacard.xc.common.biz.service.MiniprogramService;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.HttpUtil;
import net.zacard.xc.common.biz.util.RetryUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqw
 * @since 2020-06-27 15:26
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private MiniprogramService miniprogramService;

    @Autowired
    private WxMessageRepository wxMessageRepository;

    public boolean messageCheck(String appId, String timestamp, String nonce, String signature) {
        if (StringUtils.isBlank(appId)) {
            throw BusinessException.withMessage("appId cannot be blank");
        }
        MiniProgramConfig config = miniprogramService.getByAppId(appId);
        if (config == null) {
            throw BusinessException.withMessage("cannot find appId(" + appId + ")");
        }
        String token = config.getMessageToken();
        if (StringUtils.isBlank(token)) {
            throw BusinessException.withMessage(
                    "mini program(appId:" + appId + ")还未设置token，请前往设置https://mp.weixin.qq.com/wxopen/devprofile?action=get_callback&token=1423025648&lang=zh_CN");
        }
        // 验证签名
        String sign = EncryptUtil.wxMessageCheckSign(token, timestamp, nonce);
        return StringUtils.equals(sign, signature);
    }

    /**
     * 回复消息
     */
    public void replay(String appId, WxMessageReq req) {
        log.info("收到客服消息：{}", JSON.toJSONString(req, true));
        if (req == null) {
            return;
        }
        WxMessage wxMessage = WxMessage.build(req);
        wxMessageRepository.save(wxMessage);
        // 刷新access_token
        MiniProgramConfig config = miniprogramService.refreshAccessToken(appId);
        // 用户进入小程序事件
        if ("event".equals(req.getMsgType())) {
            return;
        }
        //构建支付请求参数
        MiniProgramExtraConfig extraConfig = config.getExtraConfig();
        String reqPayPagePath = extraConfig.getReqPayPagePath();
        String payParam = "";
        String pagePath = req.getPagePath();
        String[] pagePathArray = pagePath.split("\\?");
        if (StringUtils.isBlank(pagePath)) {
            // TODO 修改为默认充值10元
            payParam = "";
        } else if (StringUtils.isNotBlank(reqPayPagePath) && !reqPayPagePath.equals(pagePath)) {
            // 再比较请求支付的pagepath是否一致
            // TODO 修改为默认充值10元
            payParam = "";
        } else if (pagePathArray.length != 2) {
            // TODO 修改为默认充值10元
            payParam = "";
        } else {
            payParam = pagePathArray[1];
        }
        WxSendMessageReq messageReq = WxSendMessageReq.mini(req.getFromUserName(), extraConfig.getPayTitle(),
                extraConfig.getResPayPagePath() + "?" + payParam, extraConfig.getPayThumbMediaId());
        // 发送消息
        RetryUtil.retry(() -> {
            String url = String.format(Constant.MINI_PROGRAM_SEND_MESSAGE_URL_FORMAT, config.getAccessToken());
//        WxSendMessageReq wxSendMessageReq = WxSendMessageReq.text(req.getFromUserName(), "您好");
            WxSendMessageRes res = HttpUtil.post(url, messageReq, WxSendMessageRes.class);
            if (res == null || res.getErrcode() == null) {
                throw BusinessException.withMessage("回复消息失败(appId:" + appId + ")");
            }
            if (res.getErrcode() != 0) {
                // 这里应该是accessToken过期了，强制刷新
                MiniProgramConfig tmp = miniprogramService.refreshAccessToken(appId, true);
                config.setAccessToken(tmp.getAccessToken());
                throw BusinessException.withMessage(
                        "回复消息失败(appid:" + appId + "errcode:" + res.getErrcode() + ";errmsg:" + res.getErrmsg() + ")");
            }
        });
    }
}
