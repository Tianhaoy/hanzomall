package ltd.hanzo.mall.controller.common;

import lombok.extern.slf4j.Slf4j;;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.service.SmsService;
import ltd.hanzo.mall.util.EmailCodeUtils;
import ltd.hanzo.mall.util.PhoneUtil;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy
 * @发送短信
 */
@Slf4j
@RestController
public class SmsSendController {

    @Resource
    private SmsService smsService;

    @RequestMapping("/sendCodeSms/{phoneNumber}")
    public Result SmsSendKaptcha(@PathVariable("phoneNumber") String phoneNumber, HttpSession httpSession) {
        //先验证手机号是否符合规则
        if (PhoneUtil.confPhone(phoneNumber)){
            try {
                //生成6位随机码
                String randomCode = EmailCodeUtils.getRandomCode();
                boolean sign = smsService.sendSms(phoneNumber,randomCode);
                if (sign){
                    //发送成功 将随机验证码放到session中
                    log.info("发送短信成功");
                    httpSession.setAttribute(Constants.RANDOM_CODE,randomCode);
                }else {
                    return ResultGenerator.genFailResult("服务怠机,请稍后重试");
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error("发送短信失败"+e.getMessage());
                return ResultGenerator.genFailResult("服务怠机,请稍后重试");
            }
        }else {
            return ResultGenerator.genFailResult("手机号不符合规则，请重新输入");
        }
        return ResultGenerator.genSuccessResult();
    }
}
