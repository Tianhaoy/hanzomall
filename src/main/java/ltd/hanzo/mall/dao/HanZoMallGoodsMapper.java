package ltd.hanzo.mall.dao;


import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.StockNumDTO;
import ltd.hanzo.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HanZoMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(HanZoMallGoods record);

    int insertSelective(HanZoMallGoods record);

    HanZoMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(HanZoMallGoods record);

    int updateByPrimaryKeyWithBLOBs(HanZoMallGoods record);

    int updateByPrimaryKey(HanZoMallGoods record);

    List<HanZoMallGoods> findHanZoMallGoodsList(PageQueryUtil pageUtil);

    int getTotalHanZoMallGoods(PageQueryUtil pageUtil);

    List<HanZoMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<HanZoMallGoods> findHanZoMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalHanZoMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("hanZoMallGoodsList") List<HanZoMallGoods> hanZoMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds") Long[] orderIds, @Param("sellStatus") int sellStatus);

}