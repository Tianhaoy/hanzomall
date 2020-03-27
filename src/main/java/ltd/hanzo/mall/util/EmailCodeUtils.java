package ltd.hanzo.mall.util;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * 2020年3月16日 17:50:24
 * @邮箱手机号验证码随机数
 */
public class EmailCodeUtils {

    /**
     * 生成6位随机验证码
     *
     * @return
     */
    public static String getRandomCode() {
        String str = "1234567890";
        String code = "";
        for (int i = 0; i < 4; i++) {
            int index = (int) (Math.random() * str.length());
            code += str.charAt(index);
        }
        return code;
    }
}