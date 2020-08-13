package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户访问日志
 * <p>
 * TODO 增加清理机制
 *
 * @author guoqw
 * @since 2020-06-04 07:50
 */
@Data
@Document
@CompoundIndexes({
        @CompoundIndex(name = "keep_stat", def = "{'create_time': 1, 'openid': 1}", background = true),
        @CompoundIndex(name = "user_stat", def = "{'new_user': 1, 'create_time': 1, 'channel_id': 1, 'app_id': 1}", background = true)
})
public class UserAccessLog extends AuditDocument {

    private static final long serialVersionUID = 3786316032636395166L;

    @Indexed(background = true)
    private String userToken;

    private String userId;

    private String channelId;

    private String appId;

    private String openid;

    private String roleInfoId;

    /**
     * 是否新用户
     */
    private Boolean newUser;

    /**
     * 登陆时间
     */
    private Date signInTime;

    /**
     * 退出时间
     */
    private Date signOutTime;

    public static UserAccessLog signIn(User user, String channelId, String appId) {
        UserAccessLog userAccessLog = new UserAccessLog();
        userAccessLog.setChannelId(channelId);
        userAccessLog.setAppId(appId);
        userAccessLog.setOpenid(user.getOpenid());
        // 生成token
        userAccessLog.setUserToken(RandomStringUtil.getUUID());
        userAccessLog.setSignInTime(new Date());
        // 创建会话
        Session.create(userAccessLog.getUserToken(), userAccessLog);
        return userAccessLog;
    }
}
