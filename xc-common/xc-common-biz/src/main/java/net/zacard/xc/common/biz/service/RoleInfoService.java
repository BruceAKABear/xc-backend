package net.zacard.xc.common.biz.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.api.entity.RoleInfoDto;
import net.zacard.xc.common.biz.entity.RoleInfo;
import net.zacard.xc.common.biz.entity.RoleOptLog;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.RoleInfoRepository;
import net.zacard.xc.common.biz.repository.RoleOptLogRepository;
import net.zacard.xc.common.biz.util.BeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqw
 * @since 2020-06-14 12:41
 */
@Slf4j
@Service
public class RoleInfoService {

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private RoleOptLogRepository roleOptLogRepository;

    public void add(RoleInfoDto roleInfoDto) {
        // 获取用户会话
        UserAccessLog userAccessLog = Session.checkedUser(roleInfoDto.getUserToken());
        // 构建roleInfo信息
        RoleInfo roleInfo = BeanMapper.map(roleInfoDto, RoleInfo.class);
        roleInfo.setChannelId(userAccessLog.getChannelId());
        roleInfo.setOpenid(userAccessLog.getOpenid());
        roleInfo.setUserId(userAccessLog.getUserId());
        roleInfo.setAppId(userAccessLog.getAppId());
        // 生成role token
        roleInfo.buildToken();
        try {
            roleInfoRepository.save(roleInfo);
            // 保存操作日志
            saveRoleOptLog(roleInfo, roleInfoDto.getType());
        } catch (Exception e) {
            // 这里因为是roleInfo重复创建了(roleInfo的token字段重复了)
            log.error("用户角色信息重复：" + JSON.toJSONString(roleInfo), e);
            // 重新查询
            roleInfo = roleInfoRepository.findByToken(roleInfo.getToken());
        }
        // 更新会话中的用户角色信息
        userAccessLog.setRoleInfoId(roleInfo.getId());
        Session.create(roleInfoDto.getUserToken(), userAccessLog);
    }

    public void update(RoleInfoDto roleInfoDto) {
        // 获取用户会话
        UserAccessLog userAccessLog = Session.checkedUser(roleInfoDto.getUserToken());
        if (userAccessLog.getRoleInfoId() == null) {
            // 可能重新登录过，使用roleInfo的token获取
            RoleInfo roleInfo = BeanMapper.map(roleInfoDto, RoleInfo.class);
            roleInfo.setChannelId(userAccessLog.getChannelId());
            roleInfo.setOpenid(userAccessLog.getOpenid());
            roleInfo.setUserId(userAccessLog.getUserId());
            // 生成role token
            roleInfo.buildToken();
            roleInfo = roleInfoRepository.findByToken(roleInfo.getToken());
            if (roleInfo == null) {
                log.info("角色还未创建，roleInfoDto:" + roleInfoDto);
                // 直接创建
                add(roleInfoDto);
                userAccessLog = Session.checkedUser(roleInfoDto.getUserToken());
            } else {
                userAccessLog.setRoleInfoId(roleInfo.getId());
            }
        }
        RoleInfo roleInfo = roleInfoRepository.findOne(userAccessLog.getRoleInfoId());
        // 用户角色还未新建好,直接新建
        if (roleInfo == null) {
            add(roleInfoDto);
            return;
        }
        roleInfo.setName(roleInfoDto.getName());
        roleInfo.setLevel(roleInfoDto.getLevel());
        roleInfo.setMoney(roleInfoDto.getMoney());
        roleInfoRepository.save(roleInfo);
        // 保存操作日志
        saveRoleOptLog(roleInfo, roleInfoDto.getType());
    }

    /**
     * 保存操作日志
     */
    private void saveRoleOptLog(RoleInfo roleInfo, String type) {
        RoleOptLog roleOptLog = new RoleOptLog();
        roleOptLog.setType(type);
        roleOptLog.setChannelId(roleInfo.getChannelId());
        roleOptLog.setOpenid(roleInfo.getOpenid());
        roleOptLog.setUserId(roleInfo.getUserId());
        roleOptLog.setRoleInfoId(roleInfo.getId());
        roleOptLogRepository.save(roleOptLog);
    }
}
