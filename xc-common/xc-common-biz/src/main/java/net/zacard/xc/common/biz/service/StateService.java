package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.api.entity.StatDto;
import net.zacard.xc.common.biz.entity.RoleInfo;
import net.zacard.xc.common.biz.entity.Trade;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.entity.stat.MainStat;
import net.zacard.xc.common.biz.entity.stat.PayStat;
import net.zacard.xc.common.biz.entity.stat.RoleStat;
import net.zacard.xc.common.biz.entity.stat.UserStat;
import net.zacard.xc.common.biz.repository.RoleInfoRepository;
import net.zacard.xc.common.biz.repository.TradeRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;
import net.zacard.xc.common.biz.util.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/**
 * @author guoqw
 * @since 2020-07-18 12:55
 */
@Service
public class StateService {

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private TradeRepository tradeRepository;

    public List<MainStat> stat(StatDto statDto) {
        String channelId = statDto.getChannelId();
        Long start = statDto.getStart();
        Long end = statDto.getEnd();
        Date startDate = new DateTime(start).withTimeAtStartOfDay().toDate();
        Date endDate = new DateTime(end).plusDays(1).withTimeAtStartOfDay().toDate();
        // 收集日期分区
        Map<String, MainStat> ds2MainStat = new HashMap<>();
        Date tmpDate = new DateTime(end).toDate();
        while (tmpDate.after(startDate)) {
            DateTime tmp = new DateTime(tmpDate);
            String dateFormat = tmp.toString(Constant.DS_FORMAT);
            MainStat mainStat = new MainStat();
            mainStat.setDateFormat(dateFormat);
            ds2MainStat.put(dateFormat, mainStat);
            tmpDate = tmp.minusDays(1).toDate();
        }

        // ********统计用户相关信息********
        List<UserAccessLog> userAccessLogs = userAccessLogRepository.findDistinctOpenidByCreateTimeBetween(
                startDate, endDate);
        // 按照日期分组,构造出日期和用户openid(去重)集合的map
        Map<String, List<UserAccessLog>> date2Users = userAccessLogs.stream()
                // 根据channelId筛选
                .filter(userAccessLog -> StringUtils.isBlank(channelId) || channelId.equals(
                        userAccessLog.getChannelId()))
                .collect(groupingBy(
                        userAccessLog -> new DateTime(userAccessLog.getCreateTime()).toString(Constant.DS_FORMAT),
                        mapping(Function.identity(), toList())));
        for (Map.Entry<String, MainStat> statEntry : ds2MainStat.entrySet()) {
            MainStat mainStat = statEntry.getValue();
            UserStat userStat = new UserStat();
            mainStat.setUserStat(userStat);

            String dateFormat = statEntry.getKey();
            List<UserAccessLog> userAccessLogList = date2Users.get(dateFormat);
            if (CollectionUtils.isEmpty(userAccessLogList)) {
                continue;
            }

            // 新增用户数
            long newUserCount = userAccessLogList.stream()
                    .filter(userAccessLog -> userAccessLog.getNewUser() != null && userAccessLog.getNewUser())
                    .map(UserAccessLog::getOpenid)
                    .distinct()
                    .count();
            userStat.setNewUser(newUserCount);
            // 总用户数
            long totalUserCount = userAccessLogList.stream().map(UserAccessLog::getOpenid).distinct().count();
            userStat.setTotalUser(totalUserCount);
        }

        // ********统计角色相关信息**********
        List<RoleInfo> newRoleInfos = roleInfoRepository.findByCreateTimeBetween(startDate, endDate)
                .stream()
                // 根据channelId筛选
                .filter(roleInfo -> StringUtils.isBlank(channelId) || channelId.equals(
                        roleInfo.getChannelId()))
                .collect(toList());
        List<RoleInfo> roleInfos = new ArrayList<>(newRoleInfos);
        List<RoleInfo> updateRoleInfos = roleInfoRepository.findByUpdateTimeBetween(startDate, endDate);
        roleInfos.addAll(updateRoleInfos);
        roleInfos = roleInfos.stream()
                // 根据channelId筛选
                .filter(roleInfo -> StringUtils.isBlank(channelId) || channelId.equals(
                        roleInfo.getChannelId()))
                .collect(toList());

        for (Map.Entry<String, MainStat> statEntry : ds2MainStat.entrySet()) {
            String dateFormat = statEntry.getKey();
            MainStat mainStat = statEntry.getValue();
            RoleStat roleStat = new RoleStat();
            mainStat.setRoleStat(roleStat);

            // 新增角色
            long newRoleCount = newRoleInfos.stream()
                    .filter(roleInfo -> dateFormat.equals(
                            new DateTime(roleInfo.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .count();
            roleStat.setNewRole(newRoleCount);
            // 总角色
            long totalRoleCount = roleInfos.stream()
                    .filter(roleInfo -> dateFormat.equals(
                            new DateTime(roleInfo.getCreateTime()).toString(Constant.DS_FORMAT))
                            || dateFormat.equals(new DateTime(roleInfo.getUpdateTime()).toString(Constant.DS_FORMAT)))
                    .map(RoleInfo::getId)
                    .distinct()
                    .count();
            roleStat.setTotalRole(totalRoleCount);
        }

        // ********统计支付相关********
        List<Trade> trades = tradeRepository.findByCreateTimeBetween(startDate, endDate)
                .stream()
                // 根据channelId筛选
                .filter(roleInfo -> StringUtils.isBlank(channelId) || channelId.equals(
                        roleInfo.getChannelId()))
                .collect(toList());
        for (Map.Entry<String, MainStat> statEntry : ds2MainStat.entrySet()) {
            String dateFormat = statEntry.getKey();
            MainStat mainStat = statEntry.getValue();
            PayStat payStat = new PayStat();
            mainStat.setPayStat(payStat);

            // 总支付人数
            long totalPayUsers = trades.stream()
                    .filter(trade -> dateFormat.equals(
                            new DateTime(trade.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .filter(trade -> Constant.CODE_SUCCESS.equals(trade.getTradeState()))
                    .map(Trade::getOpenid)
                    .distinct()
                    .count();
            payStat.setTotalPayUsers(totalPayUsers);
            // 总支付金额
            int totalPaySum = trades.stream()
                    .filter(trade -> dateFormat.equals(
                            new DateTime(trade.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .filter(trade -> Constant.CODE_SUCCESS.equals(trade.getTradeState()))
                    .mapToInt(Trade::getTotalFee)
                    .sum();
            payStat.setTotalPaySum(totalPaySum);
            // 总支付次数
            long totalPayCount = trades.stream()
                    .filter(trade -> dateFormat.equals(
                            new DateTime(trade.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .filter(trade -> Constant.CODE_SUCCESS.equals(trade.getTradeState()))
                    .count();
            payStat.setTotalPayCount(totalPayCount);
            // 新增支付人数
            long newPayUsers = trades.stream()
                    .filter(trade -> dateFormat.equals(
                            new DateTime(trade.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .filter(trade -> Constant.CODE_SUCCESS.equals(trade.getTradeState()))
                    // 过滤掉之前支付过的用户
                    .filter(trade -> tradeRepository.countByOpenidAndCreateTimeLessThan(trade.getOpenid(),
                            new DateTime(trade.getCreateTime()).withTimeAtStartOfDay().toDate()) == 0)
                    .map(Trade::getOpenid)
                    .distinct()
                    .count();
            payStat.setNewPayUsers(newPayUsers);
            // 新增支付金额
            int newPaySum = trades.stream()
                    .filter(trade -> dateFormat.equals(
                            new DateTime(trade.getCreateTime()).toString(Constant.DS_FORMAT)))
                    .filter(trade -> Constant.CODE_SUCCESS.equals(trade.getTradeState()))
                    // 过滤掉之前支付过的用户
                    .filter(trade -> tradeRepository.countByOpenidAndCreateTimeLessThan(trade.getOpenid(),
                            new DateTime(trade.getCreateTime()).withTimeAtStartOfDay().toDate()) == 0)
                    .mapToInt(Trade::getTotalFee)
                    .sum();
            payStat.setNewPaySum(newPaySum);
        }

        return new ArrayList<>(ds2MainStat.values());
    }
}
