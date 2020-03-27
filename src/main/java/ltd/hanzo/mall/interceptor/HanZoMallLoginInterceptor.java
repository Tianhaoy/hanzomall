package ltd.hanzo.mall.interceptor;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hanzo-mall商城页面系统身份验证拦截器
 *
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 */
@Slf4j
@Component
public class HanZoMallLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        log.debug("进入商城系统身份验证拦截器");
        if (null == request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            response.sendRedirect(request.getContextPath() + "/oss-login");
            log.debug("未登录，拦截成功...");
            return false;
        } else {
            log.debug("已登录，放行...");
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
