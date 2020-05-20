package ltd.hanzo.mall.interceptor;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.service.RedisService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        log.debug("进入监听在线情况拦截器");
        if (null != request.getSession() && null == request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {

            return false;
        } else {
            HanZoMallUserVO user = (HanZoMallUserVO) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
            String key = "online:list";
            boolean exists =redisService.hHasKey(key,user.getUserId().toString());
            log.info("从redis中判断当前用户的userId为hashKey是否存在");
            if (exists){
                //如果在hash中存在这个属性 重新更新过期时间
                log.info("监听在线情况拦截器重新更新当前用户的hashKey的过期时间");
                redisService.hSet(key,user.getUserId().toString(),user.getLoginName(),1800);
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
