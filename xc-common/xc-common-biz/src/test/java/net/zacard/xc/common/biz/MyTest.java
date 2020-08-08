package net.zacard.xc.common.biz;

import net.zacard.xc.common.biz.entity.stat.MainStat;
import net.zacard.xc.common.biz.util.Constant;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author guoqw
 * @since 2020-08-08 09:29
 */
public class MyTest {

    @Test
    public void test1() throws Exception {
        DateTime now = DateTime.now().withTimeAtStartOfDay();
        String dateFormat = "2020-08-07 20:30:59";
        DateTime current = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
                .parseDateTime(dateFormat)
                .withTimeAtStartOfDay();
        System.out.println("days：" + Days.daysBetween(current, now).getDays());
    }

    @Test
    public void test2() throws Exception {
        Date start = DateTime.now().minusDays(2).toDate();
        Date end = DateTime.now().toDate();

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

        for (Map.Entry<String, MainStat> entry : ds2MainStat.entrySet()) {
            String key = entry.getKey();
            MainStat mainStat = entry.getValue();
            DateTime currentDate = mainStat.getCurrentDate();
            System.out.println(key + " : " + currentDate.toString("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
