package ltd.hanzo.mall.service;

import ltd.hanzo.mall.controller.vo.HanZoMallOrderDetailVO;
import ltd.hanzo.mall.controller.vo.HanZoMallOrderItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallShoppingCartItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.entity.MallUser;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HanZoMallOrderService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getHanZoMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param hanZoMallOrder
     * @return
     */
    String updateOrderInfo(HanZoMallOrder hanZoMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(HanZoMallUserVO user, List<HanZoMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    HanZoMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    HanZoMallOrder getHanZoMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType, Long userId);

    List<HanZoMallOrderItemVO> getOrderItems(Long id);

    /**
     * 通过userId获取订单详情
     *
     * @param userId
     * @return
     */
    List<HanZoMallOrder> getHanZoMallOrderByUserId(String userId);

    /**
     * 通过userId获取交易完成订单详情
     *
     * @param userId
     * @return
     */
    List<HanZoMallOrder> getHanZoMallFinishOrderByUserId(String userId);

    /**
     * 通过订单状态获取订单详情
     *
     * @param orderStatus
     * @return
     */
    List<HanZoMallOrder> getHanZoMallOrderByOrderStatus(int orderStatus);

    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(String orderNo);


}
