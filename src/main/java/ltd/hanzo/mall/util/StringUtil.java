package ltd.hanzo.mall.util;

import org.apache.commons.lang.StringUtils;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/4/13 14:03
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: (描述这个类的作用)
 */
public class StringUtil {
    /**
     * 将null转换未空字符串
     * @param str 待转换的String对象
     * @return str== null 返回空串，否则返回本身
     */
    public static String getValue(String str) {
        if (StringUtils.isNotBlank(str)) {
            return str;
        }
        return "";
    }

    /**
     * 是否是空的（包括null、""、空格）
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (null == str)
            return true;
        if ("".equals(str.trim()))
            return true;

        return false;
    }
}

