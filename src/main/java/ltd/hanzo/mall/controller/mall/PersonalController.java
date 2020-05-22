package ltd.hanzo.mall.controller.mall;

import cn.hutool.extra.servlet.ServletUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.MallUser;
import ltd.hanzo.mall.service.HanZoMallUserService;
import ltd.hanzo.mall.service.MonitorOnlineService;
import ltd.hanzo.mall.service.RedisService;
import ltd.hanzo.mall.util.MD5Util;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Api(tags = "PersonalController", description = "个人信息")
@Slf4j
@Controller
public class PersonalController {

    @Resource
    private HanZoMallUserService hanZoMallUserService;
    @Resource
    private RedisService redisService;
    @Resource
    private MonitorOnlineService monitorOnlineService;

    @ApiOperation("个人信息路由")
    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        request.setAttribute("path", "personal");
        return "mall/personal";
    }

    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public String logout(HttpSession httpSession, HttpServletRequest request) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String onlineNumber = monitorOnlineService.logoutDelMonitorOnlineNumber(user);
        request.setAttribute("onlineNumber", onlineNumber);
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        httpSession.removeAttribute("ipAddress");
        return "redirect:/oss-login";
    }

    @ApiOperation("账号登录路由")
    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "mall/login";
    }

    @ApiOperation("用户注册路由")
    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "mall/register";
    }

    @ApiOperation("手机登录路由")
    @GetMapping({"/oss-login", "oss-login.html"})
    public String ossLoginPage() {
        return "mall/oss-login";
    }

    @ApiOperation("用户账号登录")
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession,HttpServletRequest request) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equalsIgnoreCase(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = hanZoMallUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        //登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
            String onlineNumber = monitorOnlineService.loginSetMonitorOnlineNumber(user);
            request.setAttribute("onlineNumber", onlineNumber);
            String ipAddress = ServletUtil.getClientIP(request);
            log.info(ipAddress);
            httpSession.setAttribute("ipAddress",ipAddress);
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @ApiOperation("用户手机登录")
    @PostMapping("/ossLogin")
    @ResponseBody
    public Result ossLogin(@RequestParam("loginName") String loginName,
                           @RequestParam("kaptchaCode") String kaptchaCode,
                        HttpSession httpSession,HttpServletRequest request) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String randomCode = httpSession.getAttribute(Constants.RANDOM_CODE) + "";
        if (StringUtils.isEmpty(randomCode) || !kaptchaCode.equalsIgnoreCase(randomCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        //先验证用户是否注册过
        if (hanZoMallUserService.getByLoginName(loginName) == null) {
            //用户没有注册过 先自动帮用户注册账号 再登录
            log.info("用户没有注册过--");
            String password = "123456";//通过手机号注册的用户密码默认为123456
            String registerResult = hanZoMallUserService.register(loginName, password);
            //注册成功
            if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
                String loginResult = hanZoMallUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
                //登录成功
                if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
                    HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                    String onlineNumber = monitorOnlineService.loginSetMonitorOnlineNumber(user);
                    request.setAttribute("onlineNumber", onlineNumber);
                    String ipAddress = ServletUtil.getClientIP(request);
                    log.info(ipAddress);
                    httpSession.setAttribute("ipAddress",ipAddress);
                    return ResultGenerator.genSuccessResult("登录成功。检测到此手机号是第一次登录，系统将您自动创建账号，默认密码为",password);
                } else {
                    //登录失败
                    return ResultGenerator.genFailResult(loginResult);
                }
            } else {
                //注册失败
                return ResultGenerator.genFailResult(registerResult);
            }
        }else {
            //用户已经注册过 直接跳过密码登录 新写一个登录
            log.info("用户注册过，走特殊登录--");
            String loginResult = hanZoMallUserService.loginByLoginName(loginName, httpSession);
            //登录成功
            if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
                HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                String onlineNumber = monitorOnlineService.loginSetMonitorOnlineNumber(user);
                request.setAttribute("onlineNumber", onlineNumber);
                String ipAddress = ServletUtil.getClientIP(request);
                log.info(ipAddress);
                httpSession.setAttribute("ipAddress",ipAddress);
                return ResultGenerator.genSuccessResult();
            } else {
                //登录失败
                return ResultGenerator.genFailResult(loginResult);
            }
        }
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equalsIgnoreCase(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String registerResult = hanZoMallUserService.register(loginName, password);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @ApiOperation("修改个人信息")
    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody MallUser mallUser, HttpSession httpSession) {
        HanZoMallUserVO mallUserTemp = hanZoMallUserService.updateUserInfo(mallUser,httpSession);
        if (mallUserTemp == null) {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        } else {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        }
    }

    @ApiOperation("修改密码")
    @PostMapping("/personal/updatePassword/{kaptchaCode}")
    @ResponseBody
    public Result updatePassword(@RequestBody MallUser mallUser, @PathVariable("kaptchaCode") String kaptchaCode,HttpSession httpSession) {
        String randomCode = httpSession.getAttribute(Constants.RANDOM_CODE)+"";
        if (!kaptchaCode.equalsIgnoreCase(randomCode)){
            Result result = ResultGenerator.genFailResult("验证码不一致");
            return result;
        }else{
            HanZoMallUserVO mallUserTemp = hanZoMallUserService.updatePassword(mallUser,httpSession);
            if (mallUserTemp == null) {
                Result result = ResultGenerator.genFailResult("修改失败");
                return result;
            } else {
                //返回成功
                Result result = ResultGenerator.genSuccessResult();
                return result;
            }
        }
    }
}
