package ltd.hanzo.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.service.MonitorOnlineService;
import ltd.hanzo.mall.service.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/20 18:14
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description:
 */
@Slf4j
@Service
public class MonitorOnlineServiceImpl implements MonitorOnlineService {
    @Resource
    private RedisService redisService;

    @Override
    public String getMonitorOnlineNumber() {
        log.info("从redis中查询当前在线人数--");
        String key = "online:list";
        Map<Object, Object> map = redisService.hGetAll(key);
        String onlineNumber = map.size()+"";
        log.info("当前在线人数为 "+onlineNumber);
        return onlineNumber;
    }
}
