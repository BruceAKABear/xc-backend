package net.zacard.xc.common.biz.infra.validator;

import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.WxCommonSign;
import net.zacard.xc.common.biz.infra.MpConfigHolder;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.EncryptUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author guoqw
 * @since 2020-06-11 15:51
 */
public class WxSignValidator implements ConstraintValidator<WxSign, WxCommonSign> {

    @Override
    public void initialize(WxSign constraintAnnotation) {

    }

    @Override
    public boolean isValid(WxCommonSign value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String returnCode = value.getReturnCode();
        // 是否为空不是当前validator的职责
        if (returnCode == null) {
            return true;
        }
        // 非SUCCESS，后面参数都为空，无需校验签名
        if (!Constant.CODE_SUCCESS.equals(returnCode)) {
            return true;
        }
        // sign字段是否为空不是当前validator的职责
        String sign = value.getSign();
        if (sign == null) {
            return true;
        }
        // 查询appSecret
        MiniProgramConfig config = MpConfigHolder.get(value.getAppId());
        String innerSign = EncryptUtil.wxPaySign(value, config.getKey(), true);
        return innerSign.equals(sign);
    }

}
