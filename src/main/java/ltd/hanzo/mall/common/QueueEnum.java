package ltd.hanzo.mall.common;

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
    QUEUE_SMS_SEND("mall.sms.direct", "mall.sms.send", "mall.sms.send");

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
