package net.zacard.xc.miniprogram.biz.service.pay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.PrepareOrderReq;
import net.zacard.xc.common.biz.entity.PrepareOrderRes;
import net.zacard.xc.common.biz.entity.UnifiedOrder;
import net.zacard.xc.common.biz.entity.UnifiedOrderReq;
import net.zacard.xc.common.biz.entity.UnifiedOrderRes;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.repository.UnifiedOrderRepository;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.ExceptionUtil;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author guoqw
 * @since 2020-06-04 21:22
 */
@Service
@Slf4j
public class PayService {

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UnifiedOrderRepository unifiedOrderRepository;

    /**
     * 统一下单 for test
     */
    public PrepareOrderRes unifiedOrder(PrepareOrderReq prepareOrderReq) {
        UserAccessLog userAccessLog = Session.user(prepareOrderReq.getUserToken());
        if (userAccessLog == null) {
            // userToken失效或者不合法
            throw BusinessException.withMessage("用户未登录");
        }
        // 根据appId获取小程序app配置
        String appId = userAccessLog.getAppId();
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        // 构建统一下单req
        UnifiedOrderReq req = UnifiedOrderReq.buildForMiniProgram(config, prepareOrderReq.getItemName(),
                prepareOrderReq.getPrice(), userAccessLog.getOpenid());
        // 将req入库
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setReq(req);
        unifiedOrder.setChannelOther(prepareOrderReq.getOther());
        unifiedOrderRepository.save(unifiedOrder);
        String message = null;
        // 发起请求
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.add("header", "value");
            headers.setContentType(MediaType.APPLICATION_XML);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(req.xml())));
            HttpEntity<Document> request = new HttpEntity<>(document, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(Constant.UNIFIED_ORDER_URL, request,
                    String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                UnifiedOrderRes res = xmlMapper.readValue(response.getBody(), UnifiedOrderRes.class);
                unifiedOrder.setRes(res);
                String returnCode = res.getReturnCode();
                // 进一步判断返回code
                if ("SUCCESS".equals(returnCode)) {
                    // 进一步判断业务code
                    String resultCode = res.getResultCode();
                    if ("SUCCESS".equals(resultCode)) {

                    } else {
                        message = "统一下单失败：err code(" + res.getErrCode() + "):" + res.getErrCodeDes();
                    }
                } else {
                    message = "统一下单失败：return code(" + returnCode + "):" + res.getReturnMsg();
                    unifiedOrder.setMessage(message);
                }
            } else {
                // 请求失败
                message = "统一下单失败：响应code(" + response.getStatusCodeValue() + ")not 200";
            }
            unifiedOrder.setMessage(message);
            unifiedOrderRepository.save(unifiedOrder);
            if (message != null) {
                throw BusinessException.withMessage(message);
            }
            Map<String, String> signMap = new HashMap<>();
            PrepareOrderRes prepareOrderRes = new PrepareOrderRes();
            prepareOrderRes.setAppid(userAccessLog.getAppId());
            signMap.put("appid", prepareOrderRes.getAppid());
            prepareOrderRes.setNonceStr(RandomStringUtil.getRandomUpperString());
            signMap.put("nonce_str", prepareOrderRes.getNonceStr());
            prepareOrderRes.setTimeStamp((System.currentTimeMillis() / 1000) + "");
            signMap.put("timestamp", prepareOrderRes.getTimeStamp());
            prepareOrderRes.setPackageStr("prepay_id=" + unifiedOrder.getRes().getPrepayId());
            signMap.put("package", prepareOrderRes.getPackageStr());
            prepareOrderRes.setPaySign(EncryptUtil.wxPaySign(signMap, config.getKey()));
            return prepareOrderRes;
        } catch (Exception e) {
            log.error("发起wx统一下单异常", e);
            throw ExceptionUtil.unchecked(e);
        }
    }
}
