package ltd.hanzo.mall.controller.mall;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallOrderStatusEnum;
import ltd.hanzo.mall.common.PayStatusEnum;
import ltd.hanzo.mall.common.PayTypeEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallOrderItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.util.PageQueryUtil;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class BillController {
    @Resource
    private HanZoMallOrderService hanZoMallOrderService;

    @GetMapping("/bill")
    public String billListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        params.put("orderStatus", "4");//交易成功的订单状态为4
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.Bill_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", hanZoMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "bill");
        return "mall/bill";
    }

    //导出订单为Excel表格
    @GetMapping("/bill/putExcel")
    public void ordersExcel(HttpServletResponse response, HttpSession httpSession){
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<HanZoMallOrder> orderListVOS = hanZoMallOrderService.getHanZoMallFinishOrderByUserId(user.getUserId().toString());
        //创建poi导出数据对象
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        //创建sheet页
        SXSSFSheet sheet = sxssfWorkbook.createSheet("我的账单");
        //创建表头one
        SXSSFRow headRowOne = sheet.createRow(0);
        SXSSFRow headRowTwo = sheet.createRow(1);
        SXSSFRow headRowThree = sheet.createRow(2);
        //设置表头oneTwo信息
        headRowOne.createCell(0).setCellValue("半藏商城账单记录明细查询");
        headRowTwo.createCell(0).setCellValue("账号:["+user.getLoginName()+"]");
        headRowThree.createCell(0).setCellValue("---------------------------------账单交易记录明细列表------------------------------------");

        //创建表头
        SXSSFRow headRow = sheet.createRow(3);
        //设置表头信息
        headRow.createCell(0).setCellValue("订单号");
        headRow.createCell(1).setCellValue("交易号");
        headRow.createCell(2).setCellValue("交易创建时间");//可能有多个商品
        headRow.createCell(3).setCellValue("付款时间");
        headRow.createCell(4).setCellValue("最近修改时间");
        headRow.createCell(5).setCellValue("交易来源地");
        headRow.createCell(6).setCellValue("类型");
        headRow.createCell(7).setCellValue("商品名称");
        headRow.createCell(8).setCellValue("金额（元）");
        headRow.createCell(9).setCellValue("交易状态");
        headRow.createCell(10).setCellValue("服务费（元）");
        headRow.createCell(11).setCellValue("成功退款（元）");
        headRow.createCell(12).setCellValue("备注");

        int x= 0; //标识有交易有几行
        int j = 0;//从订单详情表中读取商品名称的标识
        int priceSum =0;//总共支出
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//不转换Excel时间是数字
        // 遍历上面数据库查到的数据
        for (HanZoMallOrder order : orderListVOS) {
            //填充数据 从第二行开始
            String orderNo = order.getOrderNo();
            int TotalPrice = order.getTotalPrice();
            priceSum +=TotalPrice;
            String payStatus = PayStatusEnum.getPayStatusEnumByStatus(order.getPayStatus()).getName();
            String PayType = PayTypeEnum.getPayTypeEnumByType(order.getPayType()).getName();
            String orderStatus = HanZoMallOrderStatusEnum.getHanZoMallOrderStatusEnumByStatus(order.getOrderStatus()).getName();
            String extraInfo = order.getExtraInfo();
            String patTime = "";
            String createTime = "";
            String goodsNames = "";
            String updateTime = "";
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
            if (order.getUpdateTime()!=null && !"".equals(order.getUpdateTime())){
                updateTime = sdf.format(order.getUpdateTime());
            }else {
                updateTime = "无";
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
            dataRow.createCell(0).setCellValue(orderNo);
            dataRow.createCell(1).setCellValue(extraInfo);
            dataRow.createCell(2).setCellValue(createTime);
            dataRow.createCell(3).setCellValue(patTime);
            dataRow.createCell(4).setCellValue(updateTime);
            dataRow.createCell(5).setCellValue(PayType);
            dataRow.createCell(6).setCellValue(payStatus);
            dataRow.createCell(7).setCellValue(goodsNames);
            dataRow.createCell(8).setCellValue(TotalPrice);
            dataRow.createCell(9).setCellValue(orderStatus);
            dataRow.createCell(10).setCellValue(0);
            dataRow.createCell(11).setCellValue(0);
            dataRow.createCell(12).setCellValue("");
            x++;//序号自增
            j++;//查tb_xxx_mall_order_item 根据的orderId自增
        }

        //创建表头four five six
        SXSSFRow headRowFour = sheet.createRow(sheet.getLastRowNum() + 1);
        SXSSFRow headRowFive = sheet.createRow(sheet.getLastRowNum() + 1);
        SXSSFRow headRowSix = sheet.createRow(sheet.getLastRowNum() + 1);
        SXSSFRow headRowSeven = sheet.createRow(sheet.getLastRowNum() + 1);
        //设置表尾信息
        headRowFour.createCell(0).setCellValue("------------------------------------------------------------------------------------");
        headRowFive.createCell(0).setCellValue("共"+x+"笔交易");
        headRowSix.createCell(0).setCellValue("已支出"+priceSum+"元");
        headRowSeven.createCell(0).setCellValue("导出时间["+sdf.format(new Date().getTime())+"]   用户:"+user.getNickName());
        try {
            // 下载导出
            String filename = "半藏商城账单交易明细";
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
            log.error("导出我的账单报表出错");
            e.printStackTrace();
        }
    }
}
