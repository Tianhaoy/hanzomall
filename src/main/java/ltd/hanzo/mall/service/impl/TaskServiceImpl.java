package ltd.hanzo.mall.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.HanZoMallOrderStatusEnum;
import ltd.hanzo.mall.component.SendEmailSender;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.service.HanZoMallUserService;
import ltd.hanzo.mall.service.MailSendService;
import ltd.hanzo.mall.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/5/19 23:45
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description:
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private HanZoMallOrderService hanZoMallOrderService;
    @Resource
    private HanZoMallUserService hanZoMallUserService;
    @Resource
    private MailSendService mailSendService;
    @Resource
    private SendEmailSender sendEmailSender;

    @Override
    public void callPayOrders() {
        //扫描订单 查询是否有订单状态为未支付的情况
        List<HanZoMallOrder> orderListVOS = hanZoMallOrderService.getHanZoMallOrderByOrderStatus(HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus());
        if (orderListVOS != null) {
            //遍历数据发送邮箱提醒用户尽快支付
            for (HanZoMallOrder order : orderListVOS) {
                Long userId = order.getUserId();
                String orderNo = order.getOrderNo();
                int totalPrice = order.getTotalPrice();
                Date createDate = order.getCreateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH分mm秒");
                String date = sdf.format(createDate);
                HanZoMallUserVO hanZoMallUserVO = hanZoMallUserService.getByPrimaryKey(userId);
                if (hanZoMallUserVO != null && !StringUtils.isEmpty(hanZoMallUserVO.getEmailAddress())) {
                    //用户邮箱不为空 拼接主题、内容给用户发送邮件
                    String subject = "【半藏商城未支付订单提醒】";
                    String content = "您好，你在" + date + "创建的订单号为" + orderNo + "的订单还尚未支付，待支付金额为" + totalPrice + "元,请尽快支付。";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("to",hanZoMallUserVO.getEmailAddress());
                    jsonObject.put("subject",subject);
                    jsonObject.put("content",content);
                    sendEmailSender.sendMessage(jsonObject);
                } else if (hanZoMallUserVO != null && hanZoMallUserVO.getLoginName() != null) {
                    //用户邮箱为空 可以给用户发送手机号
                    log.info("用户邮箱为空，可以给用户发送短信提醒");
                    log.info("阿里云不允许个人开通短信提醒的接口，暂时不发送。");
                } else if (hanZoMallUserVO == null) {
                    log.info("用户信息为空--无操作--");
                }
            }
        }
    }
}
