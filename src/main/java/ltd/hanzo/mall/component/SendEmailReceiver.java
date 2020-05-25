package ltd.hanzo.mall.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import ltd.hanzo.mall.service.MailSendService;
import ltd.hanzo.mall.service.SmsService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/23 21:55
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 监听MQ 发送邮件消息的接受者
 */
@Component
@RabbitListener(queues = "mall.email.send")
public class SendEmailReceiver {

    @Resource
    private MailSendService mailSendService;

    @RabbitHandler
    public void handle(JSONObject jsonObject) {
        Map<String, Object> map=  JSON.parseObject(jsonObject.toString());
        String to = map.get("to").toString();
        String subject = map.get("subject").toString();
        String content = map.get("content").toString();
        mailSendService.sendSimpleMail(to,subject,content);
    }

}
