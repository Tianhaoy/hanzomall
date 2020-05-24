package ltd.hanzo.mall.component;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.QueueEnum;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/23 21:39
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 发送短信消息的发出者
 */
@Slf4j
@Component
public class SendSmsSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(JSONObject object){
        //给发送短信消息通知队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_SMS_SEND.getExchange(), QueueEnum.QUEUE_SMS_SEND.getRouteKey(),object);
    }
}
