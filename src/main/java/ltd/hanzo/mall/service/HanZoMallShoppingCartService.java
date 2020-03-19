package ltd.hanzo.mall.service;


import ltd.hanzo.mall.controller.vo.HanZoMallShoppingCartItemVO;
import ltd.hanzo.mall.entity.HanZoMallShoppingCartItem;

import java.util.List;

public interface HanZoMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param hanZoMallShoppingCartItem
     * @return
     */
    String saveHanZoMallCartItem(HanZoMallShoppingCartItem hanZoMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param hanZoMallShoppingCartItem
     * @return
     */
    String updateHanZoMallCartItem(HanZoMallShoppingCartItem hanZoMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param hanZoMallShoppingCartItemId
     * @return
     */
    HanZoMallShoppingCartItem getHanZoMallCartItemById(Long hanZoMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param hanZoMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long hanZoMallShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param hanZoMallUserId
     * @return
     */
    List<HanZoMallShoppingCartItemVO> getMyShoppingCartItems(Long hanZoMallUserId);
}
