package ltd.hanzo.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.*;
import ltd.hanzo.mall.controller.vo.*;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.dao.HanZoMallOrderItemMapper;
import ltd.hanzo.mall.dao.HanZoMallOrderMapper;
import ltd.hanzo.mall.dao.HanZoMallShoppingCartItemMapper;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.entity.HanZoMallOrderItem;
import ltd.hanzo.mall.entity.StockNumDTO;
import ltd.hanzo.mall.service.HanZoMallOrderService;
import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.NumberUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class HanZoMallOrderServiceImpl implements HanZoMallOrderService {

    @Resource
    private HanZoMallOrderMapper hanZoMallOrderMapper;
    @Resource
    private HanZoMallOrderItemMapper hanZoMallOrderItemMapper;
    @Resource
    private HanZoMallShoppingCartItemMapper hanZoMallShoppingCartItemMapper;
    @Resource
    private HanZoMallGoodsMapper hanZoMallGoodsMapper;

    @Override
    public PageResult getHanZoMallOrdersPage(PageQueryUtil pageUtil) {
        List<HanZoMallOrder> hanZoMallOrders = hanZoMallOrderMapper.findHanZoMallOrderList(pageUtil);
        int total = hanZoMallOrderMapper.getTotalHanZoMallOrders(pageUtil);
        PageResult pageResult = new PageResult(hanZoMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(HanZoMallOrder hanZoMallOrder) {
        HanZoMallOrder temp = hanZoMallOrderMapper.selectByPrimaryKey(hanZoMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp!=null && temp.getOrderStatus() != 0 && !temp.getTotalPrice().equals(hanZoMallOrder.getTotalPrice()) && temp.getOrderStatus() < 3){
            return ServiceResultEnum.UPDATE_ORDER_PRICE_ERROR.getResult();
        }
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(hanZoMallOrder.getTotalPrice());
            temp.setUserName(hanZoMallOrder.getUserName());
            temp.setUserPhone(hanZoMallOrder.getUserPhone());
            temp.setUserAddress(hanZoMallOrder.getUserAddress());
            temp.setUserAddress(hanZoMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (hanZoMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HanZoMallOrder> orders = hanZoMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HanZoMallOrder hanZoMallOrder : orders) {
                if (hanZoMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (hanZoMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (hanZoMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HanZoMallOrder> orders = hanZoMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HanZoMallOrder hanZoMallOrder : orders) {
                if (hanZoMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                    continue;
                }
//              if (hanZoMallOrder.getOrderStatus() != 1 && hanZoMallOrder.getOrderStatus() != 2) {
                if (hanZoMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (hanZoMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                //  return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                    return errorOrderNos + "订单的状态不是配货完成无法执行出库操作";
                } else {
                //  return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                    return "你选择了太多状态不是配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HanZoMallOrder> orders = hanZoMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HanZoMallOrder hanZoMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (hanZoMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (hanZoMallOrder.getOrderStatus() == 4 || hanZoMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += hanZoMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (hanZoMallOrderMapper.closeOrder(Arrays.asList(ids), HanZoMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(HanZoMallUserVO user, List<HanZoMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(HanZoMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(HanZoMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<HanZoMallGoods> hanZoMallGoods = hanZoMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<HanZoMallGoods> goodsListNotSelling = hanZoMallGoods.stream()
                .filter(hanZoMallGoodsTemp -> hanZoMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            HanZoMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, HanZoMallGoods> hanZoMallGoodsMap = hanZoMallGoods.stream().collect(Collectors.toMap(HanZoMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (HanZoMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!hanZoMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                HanZoMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > hanZoMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                HanZoMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(hanZoMallGoods)) {
            if (hanZoMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = hanZoMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    HanZoMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                HanZoMallOrder hanZoMallOrder = new HanZoMallOrder();
                hanZoMallOrder.setOrderNo(orderNo);
                hanZoMallOrder.setUserId(user.getUserId());
                hanZoMallOrder.setUserAddress(user.getAddress());
                //总价
                for (HanZoMallShoppingCartItemVO hanZoMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += hanZoMallShoppingCartItemVO.getGoodsCount() * hanZoMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    HanZoMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                hanZoMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                hanZoMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (hanZoMallOrderMapper.insertSelective(hanZoMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<HanZoMallOrderItem> hanZoMallOrderItems = new ArrayList<>();
                    for (HanZoMallShoppingCartItemVO hanZoMallShoppingCartItemVO : myShoppingCartItems) {
                        HanZoMallOrderItem hanZoMallOrderItem = new HanZoMallOrderItem();
                        //使用BeanUtil工具类将hanZoMallShoppingCartItemVO中的属性复制到hanZoMallOrderItem对象中
                        BeanUtil.copyProperties(hanZoMallShoppingCartItemVO, hanZoMallOrderItem);
                        //HanZoMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        hanZoMallOrderItem.setOrderId(hanZoMallOrder.getOrderId());
                        hanZoMallOrderItems.add(hanZoMallOrderItem);
                    }
                    //保存至数据库
                    if (hanZoMallOrderItemMapper.insertBatch(hanZoMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    HanZoMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                HanZoMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            HanZoMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        HanZoMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public HanZoMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderMapper.selectByOrderNo(orderNo);
        if (hanZoMallOrder != null && userId==hanZoMallOrder.getUserId()) {
            //todo 验证是否是当前userId下的订单，否则报错 by thy 20200301 已添加验证是否同一用户
            log.debug("通过用户状态验证！");
            List<HanZoMallOrderItem> orderItems = hanZoMallOrderItemMapper.selectByOrderId(hanZoMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<HanZoMallOrderItemVO> hanZoMallOrderItemVOS = BeanUtil.copyList(orderItems, HanZoMallOrderItemVO.class);
                HanZoMallOrderDetailVO hanZoMallOrderDetailVO = new HanZoMallOrderDetailVO();
                BeanUtil.copyProperties(hanZoMallOrder, hanZoMallOrderDetailVO);
                hanZoMallOrderDetailVO.setOrderStatusString(HanZoMallOrderStatusEnum.getHanZoMallOrderStatusEnumByStatus(hanZoMallOrderDetailVO.getOrderStatus()).getName());
                hanZoMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(hanZoMallOrderDetailVO.getPayType()).getName());
                hanZoMallOrderDetailVO.setHanZoMallOrderItemVOS(hanZoMallOrderItemVOS);
                return hanZoMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public HanZoMallOrder getHanZoMallOrderByOrderNo(String orderNo) {
        return hanZoMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = hanZoMallOrderMapper.getTotalHanZoMallOrders(pageUtil);
        List<HanZoMallOrder> hanZoMallOrders = hanZoMallOrderMapper.findHanZoMallOrderList(pageUtil);
        List<HanZoMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(hanZoMallOrders, HanZoMallOrderListVO.class);
            //设置订单状态中文显示值
            for (HanZoMallOrderListVO hanZoMallOrderListVO : orderListVOS) {
                hanZoMallOrderListVO.setOrderStatusString(HanZoMallOrderStatusEnum.getHanZoMallOrderStatusEnumByStatus(hanZoMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = hanZoMallOrders.stream().map(HanZoMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<HanZoMallOrderItem> orderItems = hanZoMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<HanZoMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(HanZoMallOrderItem::getOrderId));
                for (HanZoMallOrderListVO hanZoMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(hanZoMallOrderListVO.getOrderId())) {
                        List<HanZoMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(hanZoMallOrderListVO.getOrderId());
                        //将HanZoMallOrderItem对象列表转换成HanZoMallOrderItemVO对象列表
                        List<HanZoMallOrderItemVO> hanZoMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, HanZoMallOrderItemVO.class);
                        hanZoMallOrderListVO.setHanZoMallOrderItemVOS(hanZoMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderMapper.selectByOrderNo(orderNo);
        if (hanZoMallOrder != null && userId==hanZoMallOrder.getUserId()) {
            //todo 验证是否是当前userId下的订单，否则报错 by thy 20200301 已添加验证是否同一用户
            //todo 订单状态判断
            log.debug("通过用户状态验证！");
            if (hanZoMallOrderMapper.closeOrder(Collections.singletonList(hanZoMallOrder.getOrderId()), HanZoMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderMapper.selectByOrderNo(orderNo);
        if (hanZoMallOrder != null && userId==hanZoMallOrder.getUserId()) {
            //todo 验证是否是当前userId下的订单，否则报错 by thy 20200301 已添加验证是否同一用户
            //todo 订单状态判断
            log.debug("通过用户状态验证！");
            hanZoMallOrder.setOrderStatus((byte) HanZoMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            hanZoMallOrder.setUpdateTime(new Date());
            if (hanZoMallOrderMapper.updateByPrimaryKeySelective(hanZoMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType, Long userId) {
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderMapper.selectByOrderNo(orderNo);
        if (hanZoMallOrder != null&& userId==hanZoMallOrder.getUserId()&&hanZoMallOrder.getPayStatus()==HanZoMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            // 订单状态判断 非待支付状态下不进行修改操作 by thy 20200301 已添加验证是否同一用户和限制状态才能进行修改
            log.debug("通过用户状态验证！");
            hanZoMallOrder.setOrderStatus((byte) HanZoMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            hanZoMallOrder.setPayType((byte) payType);
            hanZoMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            hanZoMallOrder.setPayTime(new Date());
            hanZoMallOrder.setUpdateTime(new Date());
            if (hanZoMallOrderMapper.updateByPrimaryKeySelective(hanZoMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<HanZoMallOrderItemVO> getOrderItems(Long id) {
        HanZoMallOrder hanZoMallOrder = hanZoMallOrderMapper.selectByPrimaryKey(id);
        if (hanZoMallOrder != null) {
            List<HanZoMallOrderItem> orderItems = hanZoMallOrderItemMapper.selectByOrderId(hanZoMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<HanZoMallOrderItemVO> hanZoMallOrderItemVOS = BeanUtil.copyList(orderItems, HanZoMallOrderItemVO.class);
                return hanZoMallOrderItemVOS;
            }
        }
        return null;
    }

    @Override
    public List<HanZoMallOrder> getHanZoMallOrderByUserId(String userId) {
        List<HanZoMallOrder> hanZoMallOrders = hanZoMallOrderMapper.selectByUserId(userId);
        if (hanZoMallOrders != null){
            //获取订单数据
            //数据转换 将实体类转成vo
            List<HanZoMallOrder> orderListS = new ArrayList<>();
            orderListS = BeanUtil.copyList(hanZoMallOrders, HanZoMallOrder.class);
            return orderListS;
        }
        return null;
    }

    @Override
    public List<HanZoMallOrder> getHanZoMallFinishOrderByUserId(String userId) {
        List<HanZoMallOrder> hanZoMallOrders = hanZoMallOrderMapper.selectFinishOrdersByUserId(userId);
        if (hanZoMallOrders != null){
            //获取订单数据
            //数据转换 将实体类转成vo
            List<HanZoMallOrder> orderListS = new ArrayList<>();
            orderListS = BeanUtil.copyList(hanZoMallOrders, HanZoMallOrder.class);
            return orderListS;
        }
        return null;
    }


}