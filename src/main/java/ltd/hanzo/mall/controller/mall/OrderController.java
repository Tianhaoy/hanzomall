package ltd.hanzo.mall.controller.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import ltd.hanzo.mall.common.*;
import ltd.hanzo.mall.controller.vo.HanZoMallOrderDetailVO;
import ltd.hanzo.mall.controller.vo.HanZoMallOrderItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallShoppingCartItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.service.HanZoMallShoppingCartService;
import ltd.hanzo.mall.service.MailSendService;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
@Api(tags = "OrderController", description = "用户订单")
@Slf4j
@Controller
public class OrderController {

    @Resource
    private HanZoMallShoppingCartService hanZoMallShoppingCartService;
    @Resource
    private HanZoMallOrderService hanZoMallOrderService;
    @Resource
    private MailSendService mailSendService;

    @ApiOperation("某个订单信息")
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        HanZoMallOrderDetailVO orderDetailVO = hanZoMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @ApiOperation("我的订单信息")
    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", hanZoMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @ApiOperation("提交订单")
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<HanZoMallShoppingCartItemVO> myShoppingCartItems = hanZoMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            //无收货地址
            HanZoMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页 可能会出现快速双击 导致两个请求 第二个请求会抛出错误
            HanZoMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = hanZoMallOrderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @ApiOperation("取消订单")
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = hanZoMallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @ApiOperation("确认收货")
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = hanZoMallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @ApiOperation("支付前验证状态")
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderService.getHanZoMallOrderByOrderNo(orderNo);
        // 判断订单userId 验证是否是当前userId下的订单，否则报错 by thy 20200301 已添加验证是否同一用户
        // 判断订单状态 by thy 20200301 已添加验证状态为待支付 '0'
        if (user.getUserId()==hanZoMallOrder.getUserId()&&hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            log.debug("通过用户状态验证！");
            request.setAttribute("orderNo", orderNo);
            request.setAttribute("totalPrice", hanZoMallOrder.getTotalPrice());
            return "mall/pay-select";
        }
        return "error/error_5xx";
    }

    //各种支付
    @ApiOperation("跳转支付接口")
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType, RedirectAttributes attributes) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderService.getHanZoMallOrderByOrderNo(orderNo);
        List<HanZoMallOrderItemVO> orderItems = hanZoMallOrderService.getOrderItems(hanZoMallOrder.getOrderId());
        String itemString = "";
        if (!CollectionUtils.isEmpty(orderItems)) {
            for (int i = 0; i < orderItems.size(); i++) {
                itemString += orderItems.get(i).getGoodsName() + " x " + orderItems.get(i).getGoodsCount() +" ";
            }
        }
        log.debug("订单商品详情 "+itemString);
        // 判断订单userId 验证是否是当前userId下的订单，否则报错 by thy 20200301 已添加验证是否同一用户
        // 判断订单状态 by thy 20200301 已添加验证状态为待支付 '0'
        if (user.getUserId()==hanZoMallOrder.getUserId()&&hanZoMallOrder.getOrderStatus()== HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()&&hanZoMallOrder.getPayStatus()==HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            log.debug("通过用户状态验证！");
            request.setAttribute("orderNo", orderNo);
            request.setAttribute("totalPrice", hanZoMallOrder.getTotalPrice());
            if (payType == 1) {
                //payType == 1为支付宝支付 重定向到支付宝沙箱接口controller 进行支付
                //使用RedirectAttributes 拼key和value 自动帮拼到url上 不会出现中文乱码
                attributes.addAttribute("orderNo", orderNo);
                attributes.addAttribute("totalPrice", hanZoMallOrder.getTotalPrice());
                attributes.addAttribute("itemString", itemString);
                return "redirect:/alipay/goAlipay";
            } else {
                return "mall/wxpay";
            }
        }
        return "error/error_5xx";
    }

    @ApiOperation("微信支付成功确认死接口")
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String payResult = hanZoMallOrderService.paySuccess(orderNo, payType,user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            String emailAddress = user.getEmailAddress();
            if (!"".equals(emailAddress)){
                mailSendService.sendSimpleMail(emailAddress, "【半藏商城付款成功】", "您好，订单号为"+orderNo+"的订单通过微信付款成功！");
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

    //导出订单为Excel表格
    @ApiOperation("导出我的订单表格")
    @GetMapping("/orders/putExcel")
    public void ordersExcel(HttpServletResponse response,HttpSession httpSession){
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<HanZoMallOrder> orderListVOS = hanZoMallOrderService.getHanZoMallOrderByUserId(user.getUserId().toString());
        //创建poi导出数据对象
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        //创建sheet页
        SXSSFSheet sheet = sxssfWorkbook.createSheet("我的订单");
        //创建表头
        SXSSFRow headRow = sheet.createRow(0);
        //设置表头信息
        headRow.createCell(0).setCellValue("序号");
        headRow.createCell(1).setCellValue("订单号");
        headRow.createCell(2).setCellValue("商品名称");//可能有多个商品
        headRow.createCell(3).setCellValue("消费金额（元）");
        headRow.createCell(4).setCellValue("支付状态");
        headRow.createCell(5).setCellValue("支付类型");
        headRow.createCell(6).setCellValue("支付时间");
        headRow.createCell(7).setCellValue("订单状态");
        headRow.createCell(8).setCellValue("收货地址");
        headRow.createCell(9).setCellValue("创建时间");
        //序号
        int x= 1; //序号
        int j = 0;//从订单详情表中读取商品名称的标识
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//不转换Excel时间是数字
        // 遍历上面数据库查到的数据
        for (HanZoMallOrder order : orderListVOS) {
            //填充数据 从第二行开始
            String orderNo = order.getOrderNo();
            int TotalPrice = order.getTotalPrice();
            String payStatus = PayStatusEnum.getPayStatusEnumByStatus(order.getPayStatus()).getName();
            String PayType = PayTypeEnum.getPayTypeEnumByType(order.getPayType()).getName();
            String orderStatus = HanZoMallOrderStatusEnum.getHanZoMallOrderStatusEnumByStatus(order.getOrderStatus()).getName();
            String userAddress = order.getUserAddress();
            String patTime = "";
            String createTime = "";
            String goodsNames = "";
            if (order.getPayTime()!=null && !"".equals(order.getPayTime())){
                patTime = sdf.format(order.getPayTime());
            }else {
                patTime = "无";
            }
            if (order.getCreateTime()!=null && !"".equals(order.getCreateTime())){
                createTime = sdf.format(order.getCreateTime());
            }else {
                createTime = "无";
            }
            //还需要查tb_xxx_mall_order_item表读取订单所包含的商品名称 可能有多个
            List<HanZoMallOrderItemVO> orderItems = hanZoMallOrderService.getOrderItems(orderListVOS.get(j).getOrderId());
            if (orderItems!=null){
                StringBuffer sb = new StringBuffer();
                for (int k=0;k<orderItems.size();k++){
                    sb.append(orderItems.get(k).getGoodsName() + " x " +orderItems.get(k).getGoodsCount()+" ");
                }
                goodsNames = sb.toString();

            }
            SXSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            //开始填充
            dataRow.createCell(0).setCellValue(x);
            dataRow.createCell(1).setCellValue(orderNo);
            dataRow.createCell(2).setCellValue(goodsNames);
            dataRow.createCell(3).setCellValue(TotalPrice);
            dataRow.createCell(4).setCellValue(payStatus);
            dataRow.createCell(5).setCellValue(PayType);
            dataRow.createCell(6).setCellValue(patTime);
            dataRow.createCell(7).setCellValue(orderStatus);
            dataRow.createCell(8).setCellValue(userAddress);
            dataRow.createCell(9).setCellValue(createTime);
            x++;//序号自增
            j++;//查tb_xxx_mall_order_item 根据的orderId自增
        }
        try {
            // 下载导出
            String filename = "半藏商城订单明细";
            //filename = URLEncoder.encode(filename, "UTF-8");
            try {
                //避免文件名中文乱码，将UTF8打散重组成ISO-8859-1编码方式
                filename = new String (filename.getBytes("UTF8"),"ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 设置头信息
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            //让浏览器下载文件,name是上述默认文件下载名
            response.setHeader("Content-Disposition", "attachment;filename=" + filename+ ".xlsx");
            //创建一个输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //写入数据
            sxssfWorkbook.write(outputStream);
            // 关闭
            outputStream.close();
            sxssfWorkbook.close();
        }catch (IOException e){
            log.error("导出我的订单报表出错");
            e.printStackTrace();
        }
    }

}
