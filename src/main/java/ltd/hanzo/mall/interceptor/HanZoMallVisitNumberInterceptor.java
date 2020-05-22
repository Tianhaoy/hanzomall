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
import java.util.Map;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/22 9:01
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description:
 */
@Slf4j
@Component
public class HanZoMallVisitNumberInterceptor implements HandlerInterceptor {
    @Resource
    private RedisService redisService;
    @Value("${constants.visit_hash.key}")
    private String key;
    @Value("${constants.visit_hash.hashKey}")
    private String hashKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();
        boolean exists =redisService.hHasKey(key,hashKey);
        //先判断这个key是否为空 为空的话先set
        if (exists){
            //这个key不为空 访问了index ++1
            if (uri.startsWith("/index") || uri.startsWith("/") || uri.startsWith("/index.html")) {
                log.info("访问了主页，访问量+1");
                Map<Object, Object> map = redisService.hGetAll(key);
                int visitNumber = (int) map.get(hashKey);
                log.info("之前"+visitNumber+"");
                redisService.hSet(key, hashKey, visitNumber+1);
            }else{
                //这个key不为空 没访问主页 不set
            }
        }else {
            //如果为空并且还访问了index 直接set一个1
            if (uri.startsWith("/index") || uri.startsWith("/") || uri.startsWith("/index.html")) {
                log.info("访问了主页，访问量+1");
                redisService.hSet(key,hashKey,1);
            }else{
                //如果为空但是访问的不是index set一个0
                redisService.hSet(key,hashKey,0);
            }
        }
        Map<Object, Object> map = redisService.hGetAll(key);
        int visitNumber = (int) map.get(hashKey);
        String visitNumberString = visitNumber+"";
        log.info("之后"+visitNumberString);
        request.setAttribute("visitNumber",visitNumberString);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
