package ltd.hanzo.mall.task;


import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.HanZoMallOrderStatusEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.service.HanZoMallUserService;
import ltd.hanzo.mall.service.MailSendService;
import ltd.hanzo.mall.service.TaskService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 每天定时扫描订单 未支付状态的单子发送邮件提醒支付
 * @Author by 皓宇QAQ
 * @Date 2020/3/24 12:38
 */
@Slf4j
public class CallPayQuartzTask extends QuartzJobBean{

    @Resource
    private TaskService taskService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("通过Quart开始批量发送待支付订单邮件提醒");
        taskService.callPayOrders();
    }
}
