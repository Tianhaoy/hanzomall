package ltd.hanzo.mall.controller.common;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallOrderStatusEnum;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.service.MailSendService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * @阿里支付接口
 */
@Slf4j
@Controller
@RequestMapping("/alipay")
public class AlipayController {

    @Resource
    private HanZoMallOrderService hanZoMallOrderService;
    @Resource
    private MailSendService mailSendService;

    //前往支付宝沙箱网关进行支付
    @RequestMapping(value = "/goAlipay", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String goAlipay(@RequestParam("orderNo") String orderNo,@RequestParam("totalPrice") String totalPrice,
                           @RequestParam("itemString") String itemString,HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse) throws Exception {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(Constants.GATEWAY_URL, Constants.APP_ID, Constants.APP_PRIVATE_KEY, Constants.FORMAT, Constants.CHARSET, Constants.ALIPAY_PUBLIC_KEY, Constants.SIGN_TYPE);
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(Constants.RETURN_URL);//同步
        alipayRequest.setNotifyUrl(Constants.NOTIFY_URL);//异步

        String out_trade_no = orderNo; //订单号
        String total_amount = totalPrice;//付款金额，必填
        String subject = "【半藏商城】"+itemString;  //订单名称，必填
        String body = null;//商品描述，可空
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "1c";
        if(null!=total_amount) { //支付金额不等于空
            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                    + "\"total_amount\":\""+ total_amount +"\","
                    + "\"subject\":\""+ subject +"\","
                    + "\"body\":\""+ body +"\","
                    + "\"timeout_express\":\""+ timeout_express +"\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
            //请求
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            return result;
        }
        return "error/error_5xx";
    }

    //支付宝同步通知页面,成功返回
    @RequestMapping(value = "/alipayReturnNotice")
    public String alipayReturnNotice(HttpServletRequest request, HttpServletRequest response, HttpSession httpSession) throws Exception {
        log.info("支付成功, 进入同步通知接口...");
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
           // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        log.info("支付宝返回参数："+params);
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, Constants.ALIPAY_PUBLIC_KEY, Constants.CHARSET, Constants.SIGN_TYPE);
        if(signVerified) {
            //商户订单号
            String orderNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
            HanZoMallOrder hanZoMallOrder = hanZoMallOrderService.getHanZoMallOrderByOrderNo(orderNo);
            String emailAddress = user.getEmailAddress();
            if (!"".equals(emailAddress)){
                mailSendService.sendSimpleMail(emailAddress, "【半藏商城付款成功】", "您好，订单号为"+orderNo+"的订单通过支付宝付款"+total_amount+"元！");
            }
            if (hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.OREDER_PAID.getOrderStatus()){
                //有可能异步回调比同步回调块，已经更改支付状态了 不做任何处理
                log.info("订单同步回调时已更新支付状态为已支付");
            }else if (hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                //支付成功 并且订单支付状态为待支付 更新状态为已支付
                String payResult = hanZoMallOrderService.paySuccess(orderNo, Constants.ALIPAY_TYPE,user.getUserId());
                if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
                    log.info("修改订单状态成功");
                    return "redirect:/orders/"+orderNo;
                }else{
                    //更新订单状态失败
                    log.error("修改订单状态失败");
                    return "error/error_5xx";
                }
            }
            //String payResult = hanZoMallOrderService.paySuccess(orderNo, Constants.ALIPAY_TYPE,user.getUserId());
            log.info("******************** 支付成功(支付宝同步通知) ********************");
            log.info("* 订单号: {}", orderNo);
            log.info("* 支付宝交易号: {}", trade_no);
            log.info("* 实付金额: {}", total_amount);
            log.info("***************************************************************");

        }else{
            log.error("同步回调签名验证失败");
        }
        return "redirect:/orders";
    }

    //支付宝异步 通知页面
    @RequestMapping(value = "/alipayNotifyNotice")
    @ResponseBody
    public String alipayNotifyNotice(HttpServletRequest request, HttpServletRequest response) throws Exception {
        log.info("支付成功, 进入异步通知接口...");
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        // 调用SDK验证签名
        log.info("支付宝返回参数："+params);
        boolean signVerified = AlipaySignature.rsaCheckV1(params, Constants.ALIPAY_PUBLIC_KEY, Constants.CHARSET, Constants.SIGN_TYPE);
        if(signVerified) {
            //商户订单号
            String orderNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            if(trade_status.equals("TRADE_FINISHED")){
                //订单没有退款功能, 这个条件判断是进不来的, 所以此处不必写代码
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                log.info("******************* 支付成功(支付宝异步通知) *******************");
                log.info("* 订单号: {}", orderNo);
                log.info("* 支付宝交易号: {}", trade_no);
                log.info("* 实付金额: {}", total_amount);
                log.info("*************************************************************");
                //付款完成后，支付宝系统发送该交易状态通知
                //验证支付成功后 需要验证是否更新过支付状态了
                HanZoMallOrder hanZoMallOrder = hanZoMallOrderService.getHanZoMallOrderByOrderNo(orderNo);
                if (hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.OREDER_PAID.getOrderStatus()){
                    //并且同步回调时已经更改支付状态了 不做任何处理
                    log.info("订单同步回调时已更新支付状态为已支付");
                }else if (hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                    //支付成功 并且订单支付状态为待支付 更新状态为已支付
                    String payResult = hanZoMallOrderService.paySuccess(orderNo, Constants.ALIPAY_TYPE,hanZoMallOrder.getUserId());
                    if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
                        log.info("修改订单状态成功");
                    }else{
                        //更新订单状态失败
                        log.error("修改订单状态失败");
                    }
                }
            }
        }else {
            log.error("异步回调签名验证失败");
        }
        return "success";
    }
}
