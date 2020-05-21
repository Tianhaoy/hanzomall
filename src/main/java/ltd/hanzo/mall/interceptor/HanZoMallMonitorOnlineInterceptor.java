package ltd.hanzo.mall.interceptor;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/20 17:46
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description: 监听在线人数拦截器
 */
@Slf4j
@Component
public class HanZoMallMonitorOnlineInterceptor implements HandlerInterceptor {

    @Resource
    private RedisService redisService;
    @Value("${constants.online_list.key}")
    private String key;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        log.debug("进入监听在线情况拦截器");
        if (null != request.getSession() && null != request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            HanZoMallUserVO user = (HanZoMallUserVO) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
            //当user不等于空的时候 不管在redis中是否能查看这个hashKey都重新更新过期时间
            //重新更新当前用户的hashKey的过期时间
            redisService.hSet(key, user.getUserId().toString(), user.getLoginName(), 1800);
        } else {
            //session中的user为null，给上一层返回true，不影响其他业务
        }
        Map<Object, Object> map = redisService.hGetAll(key);
        String onlineNumber = map.size()+"";
        request.setAttribute("onlineNumber", onlineNumber);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
