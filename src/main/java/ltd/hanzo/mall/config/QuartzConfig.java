package ltd.hanzo.mall.config;

import ltd.hanzo.mall.QuartzTask.CallPayQuartzTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: quartz定时任务配置
 * @Author by 皓宇QAQ
 * @Date 2020/3/23 19:51
 * 我的理解 @Configuration是随容器启动开始加载的,始终存在的单例模式。 @Component是使用一次即实例化一次
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail PayQuartz() {
        return JobBuilder.newJob(CallPayQuartzTask.class).withIdentity("CallPayQuartzTask").storeDurably().build();
    }

    @Bean
    public Trigger CallPayQuartzTaskTrigger() {
        //5秒执行一次
        //创建触发器
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInSeconds(5)
//                .repeatForever();
        //cron方式，每天定时执行一次
        //这些星号由左到右按顺序代表 ：*    *    *    *    *    *   *
        //格式：                 [秒] [分] [小时] [日] [月] [周] [年]
        return TriggerBuilder.newTrigger().forJob(PayQuartz())
                .withIdentity("CallPayQuartzTask")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 17 * * ?"))
                .build();
    }
}
