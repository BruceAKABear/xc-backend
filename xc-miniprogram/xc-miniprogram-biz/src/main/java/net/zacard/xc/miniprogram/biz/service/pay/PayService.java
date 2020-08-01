package net.zacard.xc.miniprogram.biz.service.pay;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.entity.ChannelCallbackReq;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.OrderQueryReq;
import net.zacard.xc.common.biz.entity.OrderQueryRes;
import net.zacard.xc.common.biz.entity.PayCallbackReq;
import net.zacard.xc.common.biz.entity.PayQueryRes;
import net.zacard.xc.common.biz.entity.PrepareOrderReq;
import net.zacard.xc.common.biz.entity.PrepareOrderRes;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.entity.UnifiedOrder;
import net.zacard.xc.common.biz.entity.UnifiedOrderReq;
import net.zacard.xc.common.biz.entity.UnifiedOrderRes;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.repository.TradeRepository;
import net.zacard.xc.common.biz.repository.UnifiedOrderRepository;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.ExceptionUtil;
import net.zacard.xc.common.biz.util.HttpUtil;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import net.zacard.xc.common.biz.util.ValidateUtils;
import net.zacard.xc.common.biz.util.XmlUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ChannelRepository channelRepository;

    /**
     * 统一下单
     */
    public PrepareOrderRes unifiedOrder(PrepareOrderReq prepareOrderReq) {
        UserAccessLog userAccessLog = Session.user(prepareOrderReq.getUserToken());
        if (userAccessLog == null) {
            // userToken失效或者不合法
            throw BusinessException.withMessage("用户未登录");
        }
        String itemName = prepareOrderReq.getItemName();
        try {
            // 尝试解码一下
            itemName = URLDecoder.decode(itemName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.warn("商品名称url decode失败", e);
        }
        // 根据appId获取小程序app配置
        String appId = userAccessLog.getAppId();
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        // 构建统一下单req
        UnifiedOrderReq req = UnifiedOrderReq.buildForMiniProgram(config, itemName,
                prepareOrderReq.getPrice(), userAccessLog.getOpenid());
        // 将req入库
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setReq(req);
        // 设置订单号
        unifiedOrder.setOrderId(req.getOutTradeNo());
        unifiedOrder.setChannelOther(prepareOrderReq.getOther());
        unifiedOrderRepository.save(unifiedOrder);
        String message = null;
        // 发起请求
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<>(req.xml(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(Constant.UNIFIED_ORDER_URL, request,
                    String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                UnifiedOrderRes res = XmlUtil.toObj(response.getBody(), UnifiedOrderRes.class);
                unifiedOrder.setRes(res);
                String returnCode = res.getReturnCode();
                // 进一步判断返回code
                if (Constant.CODE_SUCCESS.equals(returnCode)) {
                    // 进一步判断业务code
                    String resultCode = res.getResultCode();
                    if (!Constant.CODE_SUCCESS.equals(resultCode)) {
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
            // 保存交易订单信息
            Trade trade = new Trade();
            trade.setOrderId(req.getOutTradeNo());
            trade.setChannelOrderId(prepareOrderReq.getChannelOrderId());
            trade.setChannelOther(prepareOrderReq.getOther());
            trade.setTotalFee(req.getTotalFee());
            trade.setAppId(req.getAppId());
            trade.setChannelId(userAccessLog.getChannelId());
            trade.setItemName(req.getBody());
            trade.setItemId(prepareOrderReq.getItemId());
            trade.setMchId(req.getMchId());
            trade.setOpenid(userAccessLog.getOpenid());
            trade.setUserId(userAccessLog.getUserId());
            trade.setUserToken(userAccessLog.getUserToken());
            trade.setHasSendCallback(Boolean.FALSE);
            tradeRepository.save(trade);
            // 返回支付必要参数
            Map<String, String> signMap = new HashMap<>();
            PrepareOrderRes prepareOrderRes = new PrepareOrderRes();
            prepareOrderRes.setAppid(userAccessLog.getAppId());
            signMap.put("appId", prepareOrderRes.getAppid());

            prepareOrderRes.setNonceStr(RandomStringUtil.getRandomUpperString());
            signMap.put("nonceStr", prepareOrderRes.getNonceStr());

            prepareOrderRes.setTimeStamp((System.currentTimeMillis() / 1000) + "");
            signMap.put("timeStamp", prepareOrderRes.getTimeStamp());

            prepareOrderRes.setSignType("MD5");
            signMap.put("signType", prepareOrderRes.getSignType());

            prepareOrderRes.setPackageStr("prepay_id=" + unifiedOrder.getRes().getPrepayId());
            signMap.put("package", prepareOrderRes.getPackageStr());
            // 预付单参数签名
            prepareOrderRes.setPaySign(EncryptUtil.wxPaySign(signMap, config.getKey(), false));
            return prepareOrderRes;
        } catch (Exception e) {
            log.error("发起wx统一下单异常", e);
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * 接收微信支付的回调
     */
    public void callback(PayCallbackReq payCallbackReq) {
        // 获取系统生成的订单号
        String outTradeNo = payCallbackReq.getOutTradeNo();
        // 判断是否已经处理过
        Trade trade = tradeRepository.findByOrderId(outTradeNo);
        if (trade == null) {
            throw BusinessException.withMessage("没有outTradeNo(" + outTradeNo + ")对应的订单");
        }
        // 校验金额
        if (!payCallbackReq.getTotalFee().equals(trade.getTotalFee())) {
            throw BusinessException.withMessage(
                    "回调的订单金额(" + payCallbackReq.getTotalFee() + ")和原订单(" + trade.getId() + ")金额(" + trade.getTotalFee() + ")不一致");
        }
        // TODO 分布式锁并发(callback和orderQuery)控制, 考虑直接使用mongodb的唯一索引来处理分布式竞争

        // 订单已经支付成功
        if (Constant.CODE_SUCCESS.equals(trade.getTradeState())) {
            // 没有发送过回调
            if (trade.getHasSendCallback() == null || Boolean.FALSE.equals(trade.getHasSendCallback())) {
                trade.setHasSendCallback(Boolean.TRUE);
                tradeRepository.save(trade);
                sendCallback(trade);
            }
            return;
        }
        // 订单已经成功(说明已经接收过回调处理了)
        if (Constant.CODE_SUCCESS.equals(trade.getStatus())) {
            // 订单状态成功，但是订单的支付状态不是成功，主动查询一次
            String orderId = trade.getOrderId();
            trade = query(orderId);
            // 支付成功的情况
            if (Constant.CODE_SUCCESS.equals(trade.getTradeState())) {
                // 没有发送过回调
                if (trade.getHasSendCallback() == null || Boolean.FALSE.equals(trade.getHasSendCallback())) {
                    trade.setHasSendCallback(Boolean.TRUE);
                    tradeRepository.save(trade);
                    sendCallback(trade);
                }
            }
            return;
        }
        // 处理订单
        String resultCode = payCallbackReq.getResultCode();
        // 设置订单状态
        trade.setStatus(resultCode);
        // 设置微信订单号
        trade.setTransactionId(payCallbackReq.getTransactionId());
        if (!Constant.CODE_SUCCESS.equals(resultCode)) {
            // 支付失败的情况
            trade.setErrorCode(payCallbackReq.getErrCode());
            trade.setErrorMessage(payCallbackReq.getErrCodeDes());
            tradeRepository.save(trade);
            // TODO 失败需要回调渠道方？
            return;
        }
        // 订单成功的情况
        String timeEnd = payCallbackReq.getTimeEnd();
        DateTime dateTime = DateTimeFormat.forPattern(Constant.TRADE_START_TIME_FORMAT).parseDateTime(timeEnd);
        trade.setEndTime(dateTime.toDate());
        trade.setHasSendCallback(Boolean.TRUE);
        tradeRepository.save(trade);
        // 异步回调渠道方的支付回调接口
        sendCallback(trade);
    }

    private void sendCallback(Trade trade) { // 查询渠道信息
        Channel channel = channelRepository.findOne(trade.getChannelId());
        if (channel == null) {
            throw BusinessException.withMessage("不存在交易对应的渠道(" + trade.getChannelId() + ")");
        }
        // 创建回调req
        ChannelCallbackReq req = ChannelCallbackReq.build(trade);
        // 生成签名
        req.createSign(channel.getAppSecret());
        // 调用渠道方
        String method = channel.getPayCallbackMethod();
        String callbackUrl = channel.getPayCallbackUrl();
        // 异步多次回调
        // TODO 这里暂时直接新建线程去跑，不用线程池，会干耗线程池资源，后续改为入库定时扫描发送回调
        new Thread(() -> {
            int i = 0;
            for (long interval : Constant.CALLLBACK_INTERVAL) {
                if (interval > 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(interval);
                    } catch (InterruptedException ignore) {
                    }
                }
                i++;
                log.info("开始第{}次回调渠道({}:{})", i, channel.getName(), channel.getId());
                if (method == null || "".equals(method) || "POST".equals(method)) {
                    // 默认使用POST回调
                    try {
                        String callbackResult = HttpUtil.postString(callbackUrl, req);
                        if (!Constant.CODE_SUCCESS.equals(callbackResult)) {
                            log.warn("回调渠道方({}:{})的结果{}不为'SUCCESS'", channel.getName(),
                                    channel.getId(), callbackResult);
                        } else {
                            // 收到渠道方的成功应答，直接退出
                            return;
                        }
                    } catch (Exception e) {
                        log.warn("回调渠道方(" + channel.getName() + ":" + channel.getId() + ")的支付回调接口失败", e);
                    }
                } else {
                    // 暂不支持
                    // TODO GET回调
                    // TODO req对象转为url query string
                    // HttpUtil.get();
                }
            }
        }, "channel-pay-callback-" + Constant.INDEX.incrementAndGet()).start();
    }

    /**
     * 查询订单
     */
    public Trade query(String orderId) {
        Trade trade = tradeRepository.findByOrderId(orderId);
        if (trade == null) {
            throw BusinessException.withMessage("不存在订单(" + orderId + ")");
        }
        String appId = trade.getAppId();
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        if (config == null) {
            throw BusinessException.withMessage("不存在支付订单(" + orderId + ")对应的小程序配置(" + appId + ")");
        }
        // 订单的交易状态已经被更新为支付成功，直接返回
        if (Constant.CODE_SUCCESS.equals(trade.getTradeState())) {
            // 没有发送过回调
            if (trade.getHasSendCallback() == null || Boolean.FALSE.equals(trade.getHasSendCallback())) {
                trade.setHasSendCallback(Boolean.TRUE);
                tradeRepository.save(trade);
                sendCallback(trade);
            }
            return trade;
        }
        // 构建查询订单的req
        OrderQueryReq req = OrderQueryReq.build(trade);
        // 生成签名
        req.sign(config.getKey());
        // 发起查询
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(req.xml(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(Constant.ORDER_QUERY_URL, request,
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw BusinessException.withMessage("查询订单通信异常，code:" + response.getStatusCode().value());
        }
        OrderQueryRes res = XmlUtil.toObj(response.getBody(), OrderQueryRes.class);
        String returnCode = res.getReturnCode();
        if (!Constant.CODE_SUCCESS.equals(returnCode)) {
            log.warn("查询订单的return code(" + returnCode + ") is not SUCCESS");
            // 这里直接更新，因为回调的时候没有trade state
            tradeRepository.save(trade);
            return trade;
        }
        String resultCode = res.getResultCode();
        trade.setStatus(resultCode);
        if (!Constant.CODE_SUCCESS.equals(resultCode)) {
            log.warn("查询订单的result code(" + resultCode + ") is not SUCCESS");
            return trade;
        }
        // 参数校验&验签
        String validateMessage = ValidateUtils.validateParamsProperty(res);
        if (validateMessage != null) {
            throw BusinessException.withMessage("参数(签名)校验未通过：" + validateMessage);
        }
        String tradeState = res.getTradeState();
        trade.setTradeState(tradeState);
        // 支付成功，校验订单金额&更新订单状态
        if (Constant.CODE_SUCCESS.equals(tradeState)) {
            // 校验订单金额
            if (!res.getTotalFee().equals(trade.getTotalFee())) {
                throw BusinessException.withMessage(
                        "回调的订单金额(" + res.getTotalFee() + ")和原订单(" + trade.getId() + ")金额(" + trade.getTotalFee() + ")不一致");
            }
            // TODO 分布式锁并发(callback和orderQuery)控制, 考虑直接使用mongodb的唯一索引来处理分布式竞争

            // 设置订单状态
            trade.setStatus(resultCode);
            // 设置微信订单号
            trade.setTransactionId(res.getTransactionId());
            // 设置订单完成时间
            String timeEnd = res.getTimeEnd();
            DateTime dateTime = DateTimeFormat.forPattern(Constant.TRADE_START_TIME_FORMAT).parseDateTime(timeEnd);
            trade.setEndTime(dateTime.toDate());
            // 没有发送过回调
            if (trade.getHasSendCallback() == null || Boolean.FALSE.equals(trade.getHasSendCallback())) {
                trade.setHasSendCallback(Boolean.TRUE);
                tradeRepository.save(trade);
                sendCallback(trade);
            }
        }
        tradeRepository.save(trade);
        return trade;
    }

    public PayQueryRes payQuery(String channelId, String channelOrderId) {
        Trade trade = tradeRepository.findByChannelIdAndChannelOrderId(channelId,
                channelOrderId);
        if (trade == null) {
            return PayQueryRes.fail("不存在渠道(" + channelId + ")的订单(" + channelOrderId + ")");
        }
        trade = query(trade.getOrderId());
        PayQueryRes res = PayQueryRes.ok();
        res.setChannelOrderId(channelOrderId);
        res.setOpenid(trade.getOpenid());
        res.setPrice(trade.getTotalFee());
        Date endTime = trade.getEndTime();
        if (endTime != null) {
            res.setTime(new DateTime(endTime).toString(Constant.TRADE_START_TIME_FORMAT));
        }
        String tradeState = trade.getTradeState();
        if (tradeState == null) {
            res.setState("NOTPAY");
        } else if (Constant.CODE_SUCCESS.equals(tradeState)) {
            res.setState(tradeState);
        } else if ("NOTPAY".equals(tradeState) || "USERPAYING".equals(tradeState)) {
            res.setState("NOTPAY");
        } else {
            res.setState("FAIL");
        }
        // 生成签名
        Channel channel = channelRepository.findOne(channelId);
        res.setSign(EncryptUtil.wxPaySign(res, channel.getAppSecret(), false));
        return res;
    }
}
