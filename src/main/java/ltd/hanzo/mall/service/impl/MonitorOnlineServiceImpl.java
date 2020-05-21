package ltd.hanzo.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.service.MonitorOnlineService;
import ltd.hanzo.mall.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${constants.online_list.key}")
    private String key;

    @Override
    public String getMonitorOnlineNumber() {
        log.info("从redis中查询当前在线人数--");
        Map<Object, Object> map = redisService.hGetAll(key);
        String onlineNumber = map.size()+"";
        log.info("当前在线人数为 "+onlineNumber);
        return onlineNumber;
    }

    @Override
    public String loginSetMonitorOnlineNumber(HanZoMallUserVO user) {
        //boolean exists =redisService.hHasKey(key,user.getUserId().toString());
        //不需要从redis中判断当前用户的userId为hashKey是否存在
        //不管存不存在 直接更新就可以 hashKey是唯一的
        redisService.hSet(key, user.getUserId().toString(), user.getLoginName(), 1800);
        Map<Object, Object> map = redisService.hGetAll(key);
        String onlineNumber = map.size() + "";
        return onlineNumber;
    }

    @Override
    public String logoutDelMonitorOnlineNumber(HanZoMallUserVO user) {
        boolean exists = redisService.hHasKey(key, user.getUserId().toString());
        //退出登录同时判断redis中是否含有这个userId的hashKey
        if (exists) {
            log.info("redis中存在当前用户的hashKey,删除这个hashKey");
            //如果在hash中存在这个属性 删除这个属性
            redisService.hDel(key, user.getUserId().toString());
        }
        Map<Object, Object> map = redisService.hGetAll(key);
        String onlineNumber = map.size() + "";
        return onlineNumber;
    }
}
