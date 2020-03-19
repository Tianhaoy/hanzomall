package ltd.hanzo.mall.dao;


import ltd.hanzo.mall.entity.HanZoMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HanZoMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(HanZoMallOrderItem record);

    int insertSelective(HanZoMallOrderItem record);

    HanZoMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<HanZoMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<HanZoMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<HanZoMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(HanZoMallOrderItem record);

    int updateByPrimaryKey(HanZoMallOrderItem record);
}