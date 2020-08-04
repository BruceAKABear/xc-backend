package net.zacard.xc.miniprogram.biz.service.user;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.api.entity.UserDto;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.OpenIdRes;
import net.zacard.xc.common.biz.entity.User;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;
import net.zacard.xc.common.biz.repository.UserRepository;
import net.zacard.xc.common.biz.util.BeanMapper;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author guoqw
 * @since 2020-06-05 20:58
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Autowired
    private RestTemplate restTemplate;

    public void register(User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            // 这里应该是重复注册，只记录日志
            log.error("注册用户出错", e);
        }
    }

    public OpenIdRes openid(String code, String appId) {
        MiniProgramConfig miniProgramConfig = miniProgramConfigRepository.findByAppId(appId);
        // 验证appid对应测小程序是否已经在后台注册（新增）
        String url = Constant.CODE_TO_SESSION_UR + "?appid=" + appId + "&secret=" + miniProgramConfig.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
        OpenIdRes openIdRes = HttpUtil.get(url, OpenIdRes.class);
        openIdRes.setAppId(appId);
        return openIdRes;
    }

    /**
     * 登录
     */
    public String signIn(UserDto userDto) {
        User user = userRepository.findByOpenid(userDto.getOpenid());
        Boolean isNewUser = Boolean.FALSE;
        // 用户不存在，直接注册
        if (user == null) {
            try {
                user = BeanMapper.map(userDto, User.class);
                userRepository.insert(user);
                isNewUser = Boolean.TRUE;
            } catch (Exception e) {
                // 这里应该是重复注册，只记录日志
                log.error("注册用户(" + userDto.getOpenid() + ")出错", e);
                // 重新查询
                user = userRepository.findByOpenid(userDto.getOpenid());
            }
        }
        // 登录日志
        UserAccessLog userAccessLog = UserAccessLog.signIn(user, userDto.getChannelId(), userDto.getAppId());
        userAccessLog.setNewUser(isNewUser);
        // 保存登录日志
        userAccessLogRepository.save(userAccessLog);
        return userAccessLog.getUserToken();
    }

    /**
     * 登录
     */
//    public String signIn(UserDto userDto) {
//        User user = new User();
//        BeanUtils.copyProperties(userDto, user);
//        User tmp = userRepository.findByOpenid(userDto.getOpenid());
//        Boolean isNewUser = Boolean.FALSE;
//        if (tmp == null) {
//            // 用户不存在，先主动注册
//            try {
//                userRepository.insert(user);
//                isNewUser = Boolean.TRUE;
//            } catch (Exception e) {
//                // 这里应该是重复注册，只记录日志
//                log.error("注册用户(" + userDto.getOpenid() + ")出错", e);
//                // 重新查询
//                tmp = userRepository.findByOpenid(user.getOpenid());
//            }
//        }
//        user = tmp;
//        // 登录日志
//        UserAccessLog userAccessLog = UserAccessLog.signIn(user, userDto.getChannelId(), userDto.getAppId());
//        userAccessLog.setNewUser(isNewUser);
//        // 保存登录日志
//        userAccessLogRepository.save(userAccessLog);
//        return userAccessLog.getUserToken();
//    }

    /**
     * 登出
     */
    public void signOut(String userToken) {
        UserAccessLog userAccessLog = userAccessLogRepository.findByUserToken(userToken);
        if (userAccessLog != null) {
            userAccessLog.setSignOutTime(new Date());
            userAccessLogRepository.save(userAccessLog);
        }
        Session.clean(userToken);
    }

    /**
     * 获取用户信息
     *
     * @param userToken
     * @return
     */
    public UserDto info(String userToken) {
        UserAccessLog userAccessLog = Session.checkedUser(userToken);
        User user = userRepository.findByOpenid(userAccessLog.getOpenid());
        return BeanMapper.map(user, UserDto.class);
    }

    public void data() {
        // 查询新增用户数

    }
}
