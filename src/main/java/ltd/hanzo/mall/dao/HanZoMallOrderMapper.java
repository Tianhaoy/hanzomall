package ltd.hanzo.mall.dao;

import ltd.hanzo.mall.entity.HanZoMallOrder;
import ltd.hanzo.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HanZoMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(HanZoMallOrder record);

    int insertSelective(HanZoMallOrder record);

    HanZoMallOrder selectByPrimaryKey(Long orderId);

    HanZoMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(HanZoMallOrder record);

    int updateByPrimaryKey(HanZoMallOrder record);

    List<HanZoMallOrder> findHanZoMallOrderList(PageQueryUtil pageUtil);

    int getTotalHanZoMallOrders(PageQueryUtil pageUtil);

    List<HanZoMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);

    List<HanZoMallOrder> selectByUserId(String userId);

    List<HanZoMallOrder> selectFinishOrdersByUserId(String userId);
}