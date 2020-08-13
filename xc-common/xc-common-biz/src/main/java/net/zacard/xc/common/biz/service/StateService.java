package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.api.entity.StatDto;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.entity.DataOverviewReq;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.entity.stat.ArpuStat;
import net.zacard.xc.common.biz.entity.stat.KeepStat;
import net.zacard.xc.common.biz.entity.stat.MainStat;
import net.zacard.xc.common.biz.entity.stat.PayStat;
import net.zacard.xc.common.biz.entity.stat.PayStatResult;
import net.zacard.xc.common.biz.entity.stat.RoleStat;
import net.zacard.xc.common.biz.entity.stat.UserStat;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.ChannelRepository;
import net.zacard.xc.common.biz.repository.RoleInfoCustomizedRepository;
import net.zacard.xc.common.biz.repository.RoleInfoRepository;
import net.zacard.xc.common.biz.repository.TradeCustomizedRepository;
import net.zacard.xc.common.biz.repository.TradeRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogCustomizedRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;
import net.zacard.xc.common.biz.util.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author guoqw
 * @since 2020-07-18 12:55
 */
@Service
public class StateService {

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;

    @Autowired
    private UserAccessLogCustomizedRepository userAccessLogCustomizedRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private RoleInfoCustomizedRepository roleInfoCustomizedRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeCustomizedRepository tradeCustomizedRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public List<MainStat> stat(StatDto statDto) {
        String channelId = statDto.getChannelId();
        if (StringUtils.isNotBlank(channelId)) {
            // 校验是否存在改channel
            Channel channel = channelRepository.findOne(channelId);
            if (channel == null) {
                throw BusinessException.withMessage("不存在渠道(channelId:" + channelId + ")");
            }
        }
        Long start = statDto.getStart();
        Long end = statDto.getEnd();
        if (start > end) {
            throw BusinessException.withMessage("开始时间不能大于结束时间");
        }
        Date startDate = new DateTime(start).withTimeAtStartOfDay().toDate();
        Date endDate = new DateTime(end).withTimeAtStartOfDay().toDate();

        Map<String, MainStat> ds2MainStat = new HashMap<>();
        while (!startDate.after(endDate)) {
            DateTime tmp = new DateTime(endDate);
            String dateFormat = tmp.toString(Constant.DS_FORMAT);
            MainStat mainStat = new MainStat();
            mainStat.setDateFormat(dateFormat);
            mainStat.setCurrentDate(tmp.withTimeAtStartOfDay());
            ds2MainStat.put(dateFormat, mainStat);
            endDate = tmp.minusDays(1).toDate();
        }

        DateTime now = DateTime.now().withTimeAtStartOfDay();

        for (Map.Entry<String, MainStat> entry : ds2MainStat.entrySet()) {
            MainStat mainStat = entry.getValue();
            DateTime currentDate = mainStat.getCurrentDate();

            DataOverviewReq dataOverviewReq = DataOverviewReq.builder()
                    .channelId(channelId)
                    .start(currentDate.toDate())
                    .end(currentDate.plusDays(1).withTimeAtStartOfDay().toDate())
                    .build();

            // *******统计用户相关信息*******
            UserStat userStat = new UserStat();
            mainStat.setUserStat(userStat);

            // 新增用户数
            long newUserCount = userAccessLogCustomizedRepository.newCount(dataOverviewReq);
            userStat.setNewUser(newUserCount);

            // 总用户数：当前日期及之前所有用户数
            long totalUserCount = userAccessLogCustomizedRepository.count(dataOverviewReq);
            userStat.setTotalUser(totalUserCount);

            // ********统计角色相关信息**********
            RoleStat roleStat = new RoleStat();
            mainStat.setRoleStat(roleStat);

            // 新增角色
            long newRoleCount = roleInfoCustomizedRepository.newCount(dataOverviewReq);
            roleStat.setNewRole(newRoleCount);
            // 总角色:当前日期及之前所有创建的角色数量
            long totalRoleCount = roleInfoCustomizedRepository.count(dataOverviewReq);
            roleStat.setTotalRole(totalRoleCount);

            // ********统计支付相关********
            PayStat payStat = new PayStat();
            mainStat.setPayStat(payStat);

            DataOverviewReq totalPayStatReq = DataOverviewReq.builder()
                    .channelId(channelId)
                    .end(currentDate.plusDays(1).toDate())
                    .build();
            List<PayStatResult> payStatResults = tradeCustomizedRepository.statPayUser(totalPayStatReq);
            // 总支付人数
            payStat.setTotalPayUsers(payStatResults.size());
            // 总支付次数
            payStat.setTotalPayCount(payStatResults.stream().mapToLong(PayStatResult::getCount).sum());
            // 总支付金额
            payStat.setTotalPaySum(payStatResults.stream().mapToLong(PayStatResult::getAmount).sum());

            // 查询当天之前支付过的用户情况
            DataOverviewReq beforeTotalPayStatReq = DataOverviewReq.builder()
                    .channelId(channelId)
                    .end(currentDate.toDate())
                    .build();
            List<String> beforePayedUsers = tradeCustomizedRepository.statPayUser(beforeTotalPayStatReq)
                    .stream()
                    .map(PayStatResult::getOpenid)
                    .collect(Collectors.toList());

            DataOverviewReq currentPayStatReq = DataOverviewReq.builder()
                    .channelId(channelId)
                    .start(currentDate.toDate())
                    .end(currentDate.plusDays(1).toDate())
                    .openids(beforePayedUsers)
                    .build();
            List<PayStatResult> currentPayStatResults = tradeCustomizedRepository.statPayUser(currentPayStatReq);
            // 新增支付人数
            payStat.setNewPayUsers(currentPayStatResults.size());
            // 新增支付金额
            payStat.setNewPaySum(currentPayStatResults.stream().mapToLong(PayStatResult::getAmount).sum());

            // ********统计ARPU付相关********
            ArpuStat arpuStat = new ArpuStat();
            mainStat.setArpuStat(arpuStat);

            // 总额：总支付金额
            arpuStat.setTotalPayAmount(payStat.getTotalPaySum());
            // 付费：当天支付总金额
            DataOverviewReq currentTotalPayStatReq = DataOverviewReq.builder()
                    .channelId(channelId)
                    .start(currentDate.toDate())
                    .end(currentDate.plusDays(1).toDate())
                    .build();
            List<PayStatResult> currentTotalPayStatResults = tradeCustomizedRepository.statPayUser(
                    currentTotalPayStatReq);
            arpuStat.setCurrentPayAmount(
                    currentTotalPayStatResults.stream().mapToLong(PayStatResult::getAmount).sum());

            // 付费率:总付费人数除以总创角
            long totalPayUsers = payStat.getTotalPayUsers();
            if (totalPayUsers != 0) {
                arpuStat.setPayRate(totalPayUsers / roleStat.getTotalRole());
            }

            // ********统计留存***********
            KeepStat keepStat = new KeepStat();
            mainStat.setKeepStat(keepStat);

            // 次日留存:当天开始，往后推1天，每天都有登录
            if (Days.daysBetween(currentDate, now).getDays() < 1) {
                continue;
            }
            // 获取当天新增的用户集合
            List<String> openids = userAccessLogCustomizedRepository.newUserOpenids(dataOverviewReq)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(UserAccessLog::getOpenid)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(openids)) {
                continue;
            }

            // 查询次日的用户留存情况
            openids = computeKeepStat(openids, currentDate, 1, 1);
            if (CollectionUtils.isEmpty(openids)) {
                continue;
            }
            keepStat.setKeep2Day(openids.size());

            // 三日留存
            openids = computeKeepStat(openids, currentDate, 2, 3);
            if (CollectionUtils.isEmpty(openids)) {
                continue;
            }
            keepStat.setKeep3Day(openids.size());

            // 七日留存
            openids = computeKeepStat(openids, currentDate, 4, 7);
            keepStat.setKeep7Day(openids.size());
        }
        return new ArrayList<>(ds2MainStat.values());
    }

    public List<String> computeKeepStat(List<String> openids, DateTime currentDate, int start, int end) {
        for (int i = start; i <= end; i++) {
            openids = userAccessLogRepository.findDistinctOpenidByCreateTimeBetweenAndOpenidIn(
                    currentDate.plusDays(i).toDate(),
                    currentDate.plusDays(i + 1).toDate(),
                    openids
            )
                    .stream()
                    .filter(Objects::nonNull)
                    .map(UserAccessLog::getOpenid)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(openids)) {
                break;
            }
        }
        return openids;
    }
}
