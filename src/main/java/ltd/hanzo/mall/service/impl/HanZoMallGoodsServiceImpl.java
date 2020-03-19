package ltd.hanzo.mall.service.impl;

import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallSearchGoodsVO;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.service.HanZoMallGoodsService;

import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HanZoMallGoodsServiceImpl implements HanZoMallGoodsService {

    @Resource
    private HanZoMallGoodsMapper goodsMapper;

    @Override
    public PageResult getHanZoMallGoodsPage(PageQueryUtil pageUtil) {
        //当前页码中的数据列表
        List<HanZoMallGoods> goodsList = goodsMapper.findHanZoMallGoodsList(pageUtil);
        //数据总条数 用于计算分页数据
        int total = goodsMapper.getTotalHanZoMallGoods(pageUtil);
        //分页信息封装
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveHanZoMallGoods(HanZoMallGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveHanZoMallGoods(List<HanZoMallGoods> hanZoMallGoodsList) {
        if (!CollectionUtils.isEmpty(hanZoMallGoodsList)) {
            goodsMapper.batchInsert(hanZoMallGoodsList);
        }
    }

    @Override
    public String updateHanZoMallGoods(HanZoMallGoods goods) {
        HanZoMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public HanZoMallGoods getHanZoMallGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchHanZoMallGoods(PageQueryUtil pageUtil) {
        List<HanZoMallGoods> goodsList = goodsMapper.findHanZoMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalHanZoMallGoodsBySearch(pageUtil);
        List<HanZoMallSearchGoodsVO> hanZoMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            hanZoMallSearchGoodsVOS = BeanUtil.copyList(goodsList, HanZoMallSearchGoodsVO.class);
            for (HanZoMallSearchGoodsVO hanZoMallSearchGoodsVO : hanZoMallSearchGoodsVOS) {
                String goodsName = hanZoMallSearchGoodsVO.getGoodsName();
                String goodsIntro = hanZoMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    hanZoMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    hanZoMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(hanZoMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
