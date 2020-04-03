package ltd.hanzo.mall.service;

import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;

import java.util.List;

public interface HanZoMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getHanZoMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveHanZoMallGoods(HanZoMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param hanZoMallGoodsList
     * @return
     */
    void batchSaveHanZoMallGoods(List<HanZoMallGoods> hanZoMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateHanZoMallGoods(HanZoMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    HanZoMallGoods getHanZoMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchHanZoMallGoods(PageQueryUtil pageUtil);

}
