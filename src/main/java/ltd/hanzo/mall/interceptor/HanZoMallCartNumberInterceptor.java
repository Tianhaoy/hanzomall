package ltd.hanzo.mall.interceptor;

import lombok.extern.slf4j.Slf4j;

import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.dao.HanZoMallShoppingCartItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hanzo-mall购物车数量处理
 *
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 */
@Slf4j
@Component
public class HanZoMallCartNumberInterceptor implements HandlerInterceptor {

    @Autowired
    private HanZoMallShoppingCartItemMapper hanZoMallShoppingCartItemMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //购物车中的数量会更改，但是在这些接口中并没有对session中的数据做修改，这里统一处理一下
        log.debug("进入同步购物车数量拦截器");
        if (null != request.getSession() && null != request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            //如果当前为登陆状态，就查询数据库并设置购物车中的数量值
            HanZoMallUserVO hanZoMallUserVO = (HanZoMallUserVO) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
            //设置购物车中的数量
            hanZoMallUserVO.setShopCartItemCount(hanZoMallShoppingCartItemMapper.selectCountByUserId(hanZoMallUserVO.getUserId()));
            request.getSession().setAttribute(Constants.MALL_USER_SESSION_KEY, hanZoMallUserVO);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
