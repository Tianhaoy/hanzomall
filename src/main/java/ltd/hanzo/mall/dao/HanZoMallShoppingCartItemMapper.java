package ltd.hanzo.mall.dao;


import ltd.hanzo.mall.entity.HanZoMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HanZoMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(HanZoMallShoppingCartItem record);

    int insertSelective(HanZoMallShoppingCartItem record);

    HanZoMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    HanZoMallShoppingCartItem selectByUserIdAndGoodsId(@Param("hanZoMallUserId") Long hanZoMallUserId, @Param("goodsId") Long goodsId);

    List<HanZoMallShoppingCartItem> selectByUserId(@Param("hanZoMallUserId") Long hanZoMallUserId, @Param("number") int number);

    int selectCountByUserId(Long hanZoMallUserId);

    int updateByPrimaryKeySelective(HanZoMallShoppingCartItem record);

    int updateByPrimaryKey(HanZoMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}