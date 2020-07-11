package net.zacard.xc.common.biz.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.entity.AccessTokenRes;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.entity.Info;
import net.zacard.xc.common.biz.entity.MediaUploadRes;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.MiniProgramDto;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.util.BeanMapper;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.ExceptionUtil;
import net.zacard.xc.common.biz.util.HttpUtil;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import net.zacard.xc.common.biz.util.RetryUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-06 15:08
 */
@Slf4j
@Service
public class MiniprogramService {

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private InfoService infoService;

    public MiniProgramConfig getByAppId(String appId) {
        return miniProgramConfigRepository.findByAppId(appId);
    }

    public List<Channel> channels(String appId) {
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        return channelRepository.findByMiniProgramConfigId(config.getId());
    }

    public List<MiniProgramConfig> list() {
        return miniProgramConfigRepository.findByDeletedIsFalse();
    }

    public void add(MiniProgramConfig miniProgramConfig) {
        if (StringUtils.isBlank(miniProgramConfig.getShowType())) {
            miniProgramConfig.setShowType("info");
        }
        if (StringUtils.isBlank(miniProgramConfig.getMessageToken())) {
            miniProgramConfig.setMessageToken(RandomStringUtil.getRandomUpperString());
        }
        try {
            updateAccessToken(miniProgramConfig);
            miniProgramConfigRepository.save(miniProgramConfig);
        } catch (Exception e) {
            // 可能是appId重复
            log.error("保存小程序错误，appId(" + miniProgramConfig.getAppId() + ")重复", e);
            throw BusinessException.withMessage("appId(" + miniProgramConfig.getAppId() + ")重复");
        }
    }

    /**
     * 刷新小程序的access_token
     * <p>
     * TODO 定时刷新access_token,通常情况下2小时即过期
     *
     * @return accessToken
     */
    public MiniProgramConfig refreshAccessToken(String appId) {
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(appId);
        return refreshAccessTokenAndSave(config);
    }

    public MiniProgramConfig refreshAccessTokenAndSave(MiniProgramConfig config) {
        if (config == null) {
            throw BusinessException.withMessage("不存在小程序");
        }
        String appId = config.getAppId();
        // 先判断是否过期
        Long expiresIn = config.getAccessTokenExpiresIn();
        String accessToken = config.getAccessToken();
        Date refreshTime = config.getAccessTokenRefreshTime();
        if (expiresIn != null && accessToken != null && refreshTime != null) {
            // 没有过期直接返回accessToken
            if (new DateTime(refreshTime).plusSeconds(expiresIn.intValue()).isAfter(DateTime.now())) {
                return config;
            }
        }
//        String url = String.format(Constant.MINI_PROGRAM_GET_ACCESS_TOKEN_URL_FORMAT, config.getAppId(),
//                config.getAppSecret());
        RetryUtil.retry(() -> {
            updateAccessToken(config);
            miniProgramConfigRepository.save(config);
        });
        return config;
    }

    public void updateAccessToken(MiniProgramConfig config) {
        String url = String.format(Constant.MINI_PROGRAM_GET_ACCESS_TOKEN_URL_FORMAT, config.getAppId(),
                config.getAppSecret());
        String appId = config.getAppId();
        AccessTokenRes accessTokenRes = HttpUtil.get(url, AccessTokenRes.class);
        if (accessTokenRes == null) {
            throw BusinessException.withMessage("请求access_token异常(appId:" + appId + ")");
        }
        String access_token = accessTokenRes.getAccess_token();
        // 这里一定要先判断access_token,如果获取成功，是没有errcode的
        if (StringUtils.isBlank(access_token)) {
            Long errcode = accessTokenRes.getErrcode();
            // 请求失败的情况
            if (errcode == null || errcode != 0) {
                throw BusinessException.withMessage("请求access_token异常(appId:" + appId
                        + ";errcode:" + errcode + ";errmsg:" + accessTokenRes.getErrmsg() + ")");
            }
        }
        // 保存access_token
        config.setAccessToken(access_token);
        config.setAccessTokenExpiresIn(accessTokenRes.getExpires_in());
        config.setAccessTokenRefreshTime(new Date());
    }

    public void update(MiniProgramConfig miniProgramConfig) {
        if (StringUtils.isBlank(miniProgramConfig.getId())) {
            throw BusinessException.withMessage("小程序的id不能为空");
        }
        miniProgramConfigRepository.save(miniProgramConfig);
    }

    /**
     * 上传小程序素材(用于客服消息回复)
     *
     * @return media_id
     */
    public String uploadMedia(String accessToken, MultipartFile file) {
        String url = String.format(Constant.MINI_PROGRAM_UPLOAD_MEDIA_URL_FORMAT, accessToken, "image");
        try {
            String json = HttpUtil.uploadFile(url, file.getOriginalFilename(), "media", file.getBytes());
            MediaUploadRes mediaUploadRes = JSON.parseObject(json, MediaUploadRes.class);
            if (Integer.valueOf(40004).equals(mediaUploadRes.getErrcode())) {
                throw BusinessException.withMessage("无效媒体文件类型");
            }
            return mediaUploadRes.getMedia_id();
        } catch (Exception e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    public MiniProgramDto get(String appId) {
        MiniProgramConfig mini = miniProgramConfigRepository.findByAppId(appId);
        if (mini == null) {
            throw BusinessException.withMessage("小程序appId不能为空");
        }
        MiniProgramDto dto = BeanMapper.map(mini, MiniProgramDto.class);
        String showType = mini.getShowType();
        if ("info".equals(showType)) {
            Info info = infoService.get(mini.getInfoId());
            dto.setInfo(info);
        } else {
            List<Channel> channels = channelRepository.findByMiniProgramConfigId(mini.getId());
            dto.setChannels(channels);
        }
        return dto;
    }

}
