package ltd.hanzo.mall.service;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * 2020年3月17日17:18:31
 * @发送短信接口
 */
public interface SmsService {
    /**
     * 发送短信接口
     * @param phoneNumber
     * @param randomCode
     * @return
     */
    boolean sendSms(String phoneNumber, String randomCode);
}
