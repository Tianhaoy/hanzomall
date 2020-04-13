package ltd.hanzo.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/4/13 14:00
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: (时间公共类)
 */
@Slf4j
public class DateUtil {

    /**
     * 日期时间格式
     */
    public static final String FORMAT_PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();


    /**
     * 判断日期格式是否为 yyyy-MM-dd HH:mm:ss
     * @param datetimeStr
     * @return
     */
    public static boolean isLegalDateTime(String datetimeStr) {
        int legalLength = 19;
        if(datetimeStr == null || datetimeStr.trim().length() != legalLength) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(FORMAT_PATTERN_DATE_TIME);
        try {
            Date date = formatter.parse(datetimeStr);
            return datetimeStr.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    System.out.println("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    public static String format(String pattern) {
        return getSdf(pattern).format(new Date());
    }
    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }

    /**
     * 获取当月起始时间
     * @param format
     * @return
     */
    public static String getCurMonthStartDateTime(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND,0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        Date sdate = calendar.getTime();
        //获取当月起始时间
        Date startDate = calendar.getTime();
        return format(startDate,format);
    }

    /**
     * 获取当月结束时间
     * @param format
     * @return
     */
    public static String getCurMonthEndDateTime(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND,0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        Date sdate = calendar.getTime();
        //将当前月加1；
        calendar.add(Calendar.MONTH, 1);
        //在当前月的下一月基础上减去1毫秒
        calendar.add(Calendar.MILLISECOND, -1);
        //获得当前月最后一天
        Date endDate = calendar.getTime();
        return format(endDate,format);
    }

    /**
     * 日期（精确到日）转字符串
     *
     * @param date
     * @return
     */
    public static String date2Str(Date date) {
        String str = "";
        if (null != date) {
            str = DateUtil.to_char(date, "yyyy-MM-dd");
        }
        return str;
    }

    /**
     * 日期（精确到秒）转字符串
     *
     * @param date
     * @return
     */
    public static String dateTime2Str(Date date) {
        String str = "";
        if (null != date) {
            str = DateUtil.to_char(date, "yyyy-MM-dd HH:mm:ss");
        }
        return str;
    }

    /**
     * 按指定的格式将日期对象转换为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String to_char(Date date, String format) {
        if (date == null)
            return null;
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 字符串转日期（精确到日）
     *
     * @param str
     * @return
     * @throws
     * @throws
     */
    public static Date str2Date(String str) throws Exception  {
        Date date = null;
        if (!StringUtil.isBlank(str)) {
            date = DateUtil.to_date(str, "yyyy-MM-dd");
        }
        return date;

    }
    /**
     * 按指定格式将字符串转换为日期对象
     *
     * @param dateStr
     * @param format
     * @return
     * @throws
     *
     */
    public static Date to_date(String dateStr, String format)throws Exception{
        if (StringUtils.isEmpty(dateStr))
            return null;
        DateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw new Exception("系统转换日期字符串时出错！", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(getCurMonthStartDateTime("yyyy-MM-dd HH:mm:ss"));
        System.out.println(getCurMonthEndDateTime("yyyy-MM-dd HH:mm:ss"));
    }
}
