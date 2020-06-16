package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.api.entity.RoleInfoDto;
import net.zacard.xc.common.biz.entity.RoleInfo;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.RoleInfoRepository;
import net.zacard.xc.common.biz.util.BeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqw
 * @since 2020-06-14 12:41
 */
@Service
public class RoleInfoService {

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    public void add(RoleInfoDto roleInfoDto) {
        // 获取用户会话
        UserAccessLog userAccessLog = Session.checkedUser(roleInfoDto.getUserToken());
        // 构建roleInfo信息
        RoleInfo roleInfo = BeanMapper.map(roleInfoDto, RoleInfo.class);
        roleInfo.setChannelId(userAccessLog.getChannelId());
        roleInfo.setOpenid(userAccessLog.getOpenid());
        roleInfo.setUserId(userAccessLog.getUserId());
        roleInfoRepository.save(roleInfo);
        // 更新会话中的用户角色信息
        userAccessLog.setRoleInfoId(roleInfo.getId());
        Session.create(roleInfoDto.getUserToken(), userAccessLog);
    }

    public void update(RoleInfoDto roleInfoDto) {
        // 获取用户会话
        UserAccessLog userAccessLog = Session.checkedUser(roleInfoDto.getUserToken());
        RoleInfo roleInfo = roleInfoRepository.findOne(userAccessLog.getRoleInfoId());
        roleInfo.setName(roleInfoDto.getName());
        roleInfo.setLevel(roleInfoDto.getLevel());
        roleInfo.setMoney(roleInfoDto.getMoney());
        roleInfoRepository.save(roleInfo);
    }
}
