package ltd.hanzo.mall.service.impl;

import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallShoppingCartItemVO;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.dao.HanZoMallShoppingCartItemMapper;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.HanZoMallShoppingCartItem;
import ltd.hanzo.mall.service.HanZoMallShoppingCartService;
import ltd.hanzo.mall.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HanZoMallShoppingCartServiceImpl implements HanZoMallShoppingCartService {

    @Resource
    private HanZoMallShoppingCartItemMapper hanZoMallShoppingCartItemMapper;

    @Resource
    private HanZoMallGoodsMapper hanZoMallGoodsMapper;

    @Override
    public String saveHanZoMallCartItem(HanZoMallShoppingCartItem hanZoMallShoppingCartItem) {
        HanZoMallShoppingCartItem temp = hanZoMallShoppingCartItemMapper.selectByUserIdAndGoodsId(hanZoMallShoppingCartItem.getUserId(), hanZoMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //购物车中已存在这个物品则修改该记录
            temp.setGoodsCount(temp.getGoodsCount()+hanZoMallShoppingCartItem.getGoodsCount());
            return updateHanZoMallCartItem(temp);
        }
        HanZoMallGoods hanZoMallGoods = hanZoMallGoodsMapper.selectByPrimaryKey(hanZoMallShoppingCartItem.getGoodsId());
        //商品为空
        if (hanZoMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        if (hanZoMallGoods.getStockNum() == 0) {
            return ServiceResultEnum.GOODS_NUM_NULL.getResult();
        }
        int totalItem = hanZoMallShoppingCartItemMapper.selectCountByUserId(hanZoMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (hanZoMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (hanZoMallShoppingCartItemMapper.insertSelective(hanZoMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateHanZoMallCartItem(HanZoMallShoppingCartItem hanZoMallShoppingCartItem) {
        HanZoMallShoppingCartItem hanZoMallShoppingCartItemUpdate = hanZoMallShoppingCartItemMapper.selectByPrimaryKey(hanZoMallShoppingCartItem.getCartItemId());
        if (hanZoMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (hanZoMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        hanZoMallShoppingCartItemUpdate.setGoodsCount(hanZoMallShoppingCartItem.getGoodsCount());
        hanZoMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (hanZoMallShoppingCartItemMapper.updateByPrimaryKeySelective(hanZoMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public HanZoMallShoppingCartItem getHanZoMallCartItemById(Long hanZoMallShoppingCartItemId) {
        return hanZoMallShoppingCartItemMapper.selectByPrimaryKey(hanZoMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long hanZoMallShoppingCartItemId) {
        //todo userId不同不能删除
        return hanZoMallShoppingCartItemMapper.deleteByPrimaryKey(hanZoMallShoppingCartItemId) > 0;
    }

    @Override
    public List<HanZoMallShoppingCartItemVO> getMyShoppingCartItems(Long hanZoMallUserId) {
        List<HanZoMallShoppingCartItemVO> hanZoMallShoppingCartItemVOS = new ArrayList<>();
        List<HanZoMallShoppingCartItem> hanZoMallShoppingCartItems = hanZoMallShoppingCartItemMapper.selectByUserId(hanZoMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(hanZoMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> hanZoMallGoodsIds = hanZoMallShoppingCartItems.stream().map(HanZoMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<HanZoMallGoods> hanZoMallGoods = hanZoMallGoodsMapper.selectByPrimaryKeys(hanZoMallGoodsIds);
            Map<Long, HanZoMallGoods> hanZoMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(hanZoMallGoods)) {
                hanZoMallGoodsMap = hanZoMallGoods.stream().collect(Collectors.toMap(HanZoMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (HanZoMallShoppingCartItem hanZoMallShoppingCartItem : hanZoMallShoppingCartItems) {
                HanZoMallShoppingCartItemVO hanZoMallShoppingCartItemVO = new HanZoMallShoppingCartItemVO();
                BeanUtil.copyProperties(hanZoMallShoppingCartItem, hanZoMallShoppingCartItemVO);
                if (hanZoMallGoodsMap.containsKey(hanZoMallShoppingCartItem.getGoodsId())) {
                    HanZoMallGoods hanZoMallGoodsTemp = hanZoMallGoodsMap.get(hanZoMallShoppingCartItem.getGoodsId());
                    hanZoMallShoppingCartItemVO.setGoodsCoverImg(hanZoMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = hanZoMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    hanZoMallShoppingCartItemVO.setGoodsName(goodsName);
                    hanZoMallShoppingCartItemVO.setSellingPrice(hanZoMallGoodsTemp.getSellingPrice());
                    hanZoMallShoppingCartItemVOS.add(hanZoMallShoppingCartItemVO);
                }
            }
        }
        return hanZoMallShoppingCartItemVOS;
    }
}
