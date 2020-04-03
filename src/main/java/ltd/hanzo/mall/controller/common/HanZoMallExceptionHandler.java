package ltd.hanzo.mall.controller.common;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.HanZoMallException;
import ltd.hanzo.mall.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Hanzo-mall全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class HanZoMallExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        Result result = new Result();
        result.setResultCode(500);
        //区分是否为自定义异常
        if (e instanceof HanZoMallException) {
            result.setMessage(e.getMessage());
        } else {
            e.printStackTrace();
            log.error("异常信息为："+e.getMessage());
            result.setMessage("未知异常，请联系管理员");
        }
        //检查请求是否为ajax, 如果是 ajax 请求则返回 Result json串, 如果不是 ajax 请求则返回 error 视图
        String contentTypeHeader = req.getHeader("Content-Type");
        String acceptHeader = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        if ((contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            return result;
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message", e.getMessage());
            modelAndView.addObject("url", req.getRequestURL());
            modelAndView.addObject("stackTrace", e.getStackTrace());
            modelAndView.addObject("author", "皓宇QAQ");
            modelAndView.addObject("ltd", "半藏商城");
            modelAndView.setViewName("error/error");
            return modelAndView;
        }
    }
}
