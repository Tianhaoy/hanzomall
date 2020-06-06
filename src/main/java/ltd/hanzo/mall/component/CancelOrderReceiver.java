package ltd.hanzo.mall.component;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/6 17:25
 * @Description:取消订单消息的处理者
 */
@Component
@RabbitListener(queues = "mall.order.cancel")
@Slf4j
public class CancelOrderReceiver {
    @Autowired
    private HanZoMallOrderService hanZoMallOrderService;
    @Autowired
    private TaskService taskService;

    @RabbitHandler
    public void handle(String orderNo){
        log.info("receive delay message orderNo:{}",orderNo);
        hanZoMallOrderService.cancelOrder(orderNo);
        taskService.cancelOrderSendSimpleMail(orderNo);
    }
}
