package ltd.hanzo.mall.controller.common;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.service.MailSendService;
import ltd.hanzo.mall.util.EmailCodeUtils;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * @发送邮箱
 */
@Slf4j
@RestController
public class MailSendController {

    @Resource
    private MailSendService mailSendService;

    @RequestMapping("/sendCodeMail")
    public Result mailSendKaptcha(HttpSession httpSession) {
        //生成6位随机码
        try {
            String randomCode = EmailCodeUtils.getRandomCode();
            HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
            String emailAddress = user.getEmailAddress();
            mailSendService.sendSimpleMail(emailAddress, "【半藏商城修改密码】", "您好，你本次的验证码为:"+randomCode+"有效期为60秒！");
            httpSession.setAttribute(Constants.RANDOM_CODE,randomCode);//将随机验证码放到session中
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResultGenerator.genFailResult("服务怠机,请稍后重试");
        }

        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping("/sendHtmlMail")
    public String SendHtmlMail() {
        String content = "<html><body><h3>hello world ! --->Html邮件</h3></body></html>";
        mailSendService.sendHtmlMail("2469653218@qq.com","test", content);
        return "success";
    }

    @RequestMapping("/sendAttachmentsMail")
    public String sendAttachmentsMail() {
        String filePath = "";//文件的路径
        mailSendService.sendAttachmentsMail("2469653218@qq.com","test","testfile",filePath);
        return "success";
    }
}
