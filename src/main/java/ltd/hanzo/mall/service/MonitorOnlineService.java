package ltd.hanzo.mall.service;

import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/20 18:12
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 监控在线人数接口
 */
public interface MonitorOnlineService {
    //只是从redis中get在线人数
    String getMonitorOnlineNumber();

    //登录set当前的redis key中的hashKey
    String loginSetMonitorOnlineNumber(HanZoMallUserVO user);

    //退出删除当前redis key中的hashKey
    String logoutDelMonitorOnlineNumber(HanZoMallUserVO user);
}
