package net.zacard.xc.miniprogram.biz.service.message;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
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
        if (req == null) {
            return;
        }
        WxMessage wxMessage = WxMessage.build(req);
        wxMessageRepository.save(wxMessage);
        // 刷新access_token
        MiniProgramConfig config = miniprogramService.refreshAccessToken(appId);
        // 发送消息
        String url = String.format(Constant.MINI_PROGRAM_SEND_MESSAGE_URL_FORMAT, config.getAccessToken());
        WxSendMessageReq wxSendMessageReq = WxSendMessageReq.text(req.getFromUserName(), "ok");
        WxSendMessageRes res = HttpUtil.post(url, wxSendMessageReq, WxSendMessageRes.class);
        if (res == null || res.getErrcode() == null) {
            throw BusinessException.withMessage("回复消息失败(appId:" + appId + ")");
        }
        if (res.getErrcode() != 0) {
            throw BusinessException.withMessage(
                    "回复消息失败(appid:" + appId + "errcode:" + res.getErrcode() + ";errmsg:" + res.getErrmsg() + ")");
        }
    }
}
