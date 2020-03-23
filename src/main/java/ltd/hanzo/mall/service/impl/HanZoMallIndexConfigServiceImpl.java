package ltd.hanzo.mall.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.IndexConfigTypeEnum;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexCarouselVO;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexConfigGoodsVO;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.dao.IndexConfigMapper;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.IndexConfig;
import ltd.hanzo.mall.service.HanZoMallIndexConfigService;

import ltd.hanzo.mall.service.RedisService;
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
@Slf4j
public class HanZoMallIndexConfigServiceImpl implements HanZoMallIndexConfigService {

    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private HanZoMallGoodsMapper goodsMapper;

    @Resource
    private RedisService redisService;

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
        //热销商品校验
        if (IndexConfigTypeEnum.INDEX_GOODS_HOT.getType()==configType){
            String key = "redis:list:indexGoodsHot";
            boolean exists =redisService.hasKey(key);
            if (exists){
                //redis中存在key 不需要从数据库中读取
                log.debug("redis中存在key:"+key);
                String indexGoodsHot =redisService.get(key).toString();
                List<HanZoMallIndexConfigGoodsVO> ConfigGoods = new ArrayList<>(number);
                if(indexGoodsHot!=null){
                    log.debug("从redis中读取热销商品信息--");
                    ConfigGoods = JSON.parseArray(indexGoodsHot,HanZoMallIndexConfigGoodsVO.class);
                }
                return ConfigGoods;
            }
        }else if (IndexConfigTypeEnum.INDEX_GOODS_NEW.getType()==configType){
            String key = "redis:list:indexGoodsNew";
            boolean exists =redisService.hasKey(key);
            if (exists){
                //redis中存在key 不需要从数据库中读取
                log.debug("redis中存在key:"+key);
                String indexGoodsNew =redisService.get(key).toString();
                List<HanZoMallIndexConfigGoodsVO> ConfigGoods = new ArrayList<>(number);
                if(indexGoodsNew!=null){
                    log.debug("从redis中读取新品信息--");
                    ConfigGoods = JSON.parseArray(indexGoodsNew,HanZoMallIndexConfigGoodsVO.class);
                }
                return ConfigGoods;
            }
        }else if (IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType()==configType){
            String key = "redis:list:indexGoodsRecommond";
            boolean exists =redisService.hasKey(key);
            if (exists){
                //redis中存在key 不需要从数据库中读取
                log.debug("redis中存在key:"+key);
                String indexGoodsRecommond =redisService.get(key).toString();
                List<HanZoMallIndexConfigGoodsVO> ConfigGoods = new ArrayList<>(number);
                if(indexGoodsRecommond!=null){
                    log.debug("从redis中读取新品信息--");
                    ConfigGoods = JSON.parseArray(indexGoodsRecommond,HanZoMallIndexConfigGoodsVO.class);
                }
                return ConfigGoods;
            }
        }

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
        log.info("更新一遍redis缓存");
        if (IndexConfigTypeEnum.INDEX_GOODS_HOT.getType()==configType){
            String key = "redis:list:indexGoodsHot";
            String indexConfigGoods = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
            redisService.set(key,indexConfigGoods);
            redisService.expire(key,86400);
        }else if (IndexConfigTypeEnum.INDEX_GOODS_NEW.getType()==configType){
            String key = "redis:list:indexGoodsNew";
            String indexConfigNews = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
            redisService.set(key,indexConfigNews);
            redisService.expire(key,86400);
        }else if (IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType()==configType){
            String key = "redis:list:indexGoodsRecommond";
            String indexConfigRecommonds = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
            redisService.set(key,indexConfigRecommonds);
            redisService.expire(key,86400);
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
