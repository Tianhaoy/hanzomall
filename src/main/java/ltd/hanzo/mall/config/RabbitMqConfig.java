package ltd.hanzo.mall.config;

import ltd.hanzo.mall.common.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/23 21:04
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 消息队列配置
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 发送短信消息通知队列所绑定的->交换机
     */
    @Bean
    DirectExchange sendSmsDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_SMS_SEND.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 发送短信的->消费队列
     */
    @Bean
    public Queue sendSmsQueue() {
        return new Queue(QueueEnum.QUEUE_SMS_SEND.getName());
    }

    /**
     * 将发送短信 队列绑定到->交换机
     */
    @Bean
    Binding sendSmsBinding(DirectExchange sendSmsDirect, Queue sendSmsQueue){
        return BindingBuilder
                .bind(sendSmsQueue)
                .to(sendSmsDirect)
                .with(QueueEnum.QUEUE_SMS_SEND.getRouteKey());
    }
}
