package ltd.hanzo.mall.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
 * @Description: 监听MQ 发送短信消息的接受者
 */
@Component
@RabbitListener(queues = "mall.sms.send")
public class SendSmsReceiver {

    @Resource
    private SmsService smsService;

    @RabbitHandler
    public void handle(JSONObject jsonObject) {
        Map<String, Object> map=  JSON.parseObject(jsonObject.toString());
        String phoneNumber = map.get("phoneNumber").toString();
        String randomCode = map.get("randomCode").toString();
        smsService.sendSms(phoneNumber,randomCode);
    }

}
