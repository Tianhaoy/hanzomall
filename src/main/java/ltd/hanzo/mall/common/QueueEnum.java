package ltd.hanzo.mall.common;

import com.rabbitmq.client.AMQP;
import lombok.Getter;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/23 21:00
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 消息队列枚举配置
 */
@Getter
public enum  QueueEnum {
    /**
     * 发送短信消息通知队列
     */
    QUEUE_SMS_SEND("mall.sms.direct", "mall.sms.send", "mall.sms.send"),

    /**
     * 发送邮件消息通知队列
     */
    QUEUE_EMAIL_SEND("mall.email.direct", "mall.email.send", "mall.email.send"),

    /**
     * 消息通知队列
     * mall.order.direct（取消订单消息队列所绑定的交换机）:绑定的队列为mall.order.cancel，一旦有消息以mall.order.cancel为路由键发过来，会发送到此队列。
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),

    /**
     * 消息通知ttl队列
     * mall.order.direct.ttl（订单延迟消息队列所绑定的交换机）:绑定的队列为mall.order.cancel.ttl，一旦有消息以mall.order.cancel.ttl为路由键发送过来，会转发到此队列，并在此队列保存一定时间，等到超时后会自动将消息发送到mall.order.cancel（取消订单消息消费队列）。
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl");

    /**
     * 交换机名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
