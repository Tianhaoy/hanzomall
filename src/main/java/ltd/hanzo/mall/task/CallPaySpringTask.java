package ltd.hanzo.mall.task;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/19 23:33
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 每天定时扫描订单 未支付状态的单子发送邮件提醒支付
 */
@Slf4j
@Component
public class CallPaySpringTask {

    @Resource
    private TaskService taskService;

    /**
     * cron表达式：Seconds Minutes Hours DayOfMonth Month DayOfWeek [Year]
     */
    @Scheduled(cron = "0 0 5 * * ?")
    private void callPay() {
        log.info("通过SpringTask开始批量发送待支付订单邮件提醒");
        taskService.callPayOrders();
    }


}
