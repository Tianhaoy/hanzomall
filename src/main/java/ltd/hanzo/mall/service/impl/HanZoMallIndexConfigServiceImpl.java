package ltd.hanzo.mall.service.impl;

import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexConfigGoodsVO;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.dao.IndexConfigMapper;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.IndexConfig;
import ltd.hanzo.mall.service.HanZoMallIndexConfigService;

import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HanZoMallIndexConfigServiceImpl implements HanZoMallIndexConfigService {

    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private HanZoMallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<HanZoMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<HanZoMallIndexConfigGoodsVO> hanZoMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<HanZoMallGoods> hanZoMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            hanZoMallIndexConfigGoodsVOS = BeanUtil.copyList(hanZoMallGoods, HanZoMallIndexConfigGoodsVO.class);
            for (HanZoMallIndexConfigGoodsVO hanZoMallIndexConfigGoodsVO : hanZoMallIndexConfigGoodsVOS) {
                String goodsName = hanZoMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = hanZoMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    hanZoMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    hanZoMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return hanZoMallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
