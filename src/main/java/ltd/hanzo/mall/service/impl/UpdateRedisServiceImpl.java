package ltd.hanzo.mall.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallCategoryLevelEnum;
import ltd.hanzo.mall.common.IndexConfigTypeEnum;
import ltd.hanzo.mall.controller.vo.*;
import ltd.hanzo.mall.dao.CarouselMapper;
import ltd.hanzo.mall.dao.GoodsCategoryMapper;
import ltd.hanzo.mall.dao.HanZoMallGoodsMapper;
import ltd.hanzo.mall.dao.IndexConfigMapper;
import ltd.hanzo.mall.entity.Carousel;
import ltd.hanzo.mall.entity.GoodsCategory;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.entity.IndexConfig;
import ltd.hanzo.mall.service.RedisService;
import ltd.hanzo.mall.service.UpdateRedisService;
import ltd.hanzo.mall.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @Description:
 * @Date 2020/3/27 22:29
 */
@Slf4j
@Service
public class UpdateRedisServiceImpl implements UpdateRedisService {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private CarouselMapper carouselMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private HanZoMallGoodsMapper goodsMapper;

    @Override
    public boolean updateIndexCategoryRedis() {
        List<HanZoMallIndexCategoryVO> hanZoMallIndexCategoryVOS = new ArrayList<>();
        String key = "redis:list:indexCategory";
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HanZoMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        try {
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取二级分类的数据
                List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, HanZoMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                    //获取三级分类的数据
                    List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                    if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                        List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                        //处理二级分类
                        for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                            SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                            BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                            //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                            if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                                //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                                List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                                secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                                secondLevelCategoryVOS.add(secondLevelCategoryVO);
                            }
                        }
                        //处理一级分类
                        if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                            //根据 parentId 将 thirdLevelCategories 分组
                            Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                            for (GoodsCategory firstCategory : firstLevelCategories) {
                                HanZoMallIndexCategoryVO hanZoMallIndexCategoryVO = new HanZoMallIndexCategoryVO();
                                BeanUtil.copyProperties(firstCategory, hanZoMallIndexCategoryVO);
                                //如果该一级分类下有数据则放入 hanZoMallIndexCategoryVOS 对象中
                                if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                    //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                    List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                    hanZoMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                    hanZoMallIndexCategoryVOS.add(hanZoMallIndexCategoryVO);
                                }
                            }
                        }
                    }
                }
                log.info("修改后更新一遍redis缓存");
                String indexCategory = JSON.toJSONString(hanZoMallIndexCategoryVOS);
                redisService.set(key, indexCategory);
                redisService.expire(key, 86400);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新缓存失败给上一级返回false"+e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateIndexCarouselRedis() {
        List<HanZoMallIndexCarouselVO> hanZoMallIndexCarouselVOS = new ArrayList<>(Constants.INDEX_CAROUSEL_NUMBER);
        try {
            String key = "redis:list:indexCarousel";
            List<Carousel> carousels = carouselMapper.findCarouselsByNum(Constants.INDEX_CAROUSEL_NUMBER);
            if (!CollectionUtils.isEmpty(carousels)) {
                hanZoMallIndexCarouselVOS = BeanUtil.copyList(carousels, HanZoMallIndexCarouselVO.class);
            }
            log.info("修改后更新一遍redis缓存");
            String indexCarousel = JSON.toJSONString(hanZoMallIndexCarouselVOS);
            redisService.set(key,indexCarousel);
            redisService.expire(key,86400);
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新缓存失败给上一级返回false"+e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateIndexConfig(int indexConfig,int number) {
        List<HanZoMallIndexConfigGoodsVO> hanZoMallIndexConfigGoodsVOS = new ArrayList<>(number);
        try {
            List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(indexConfig, number);
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
            log.info("修改后更新更新一遍redis缓存");
            if (IndexConfigTypeEnum.INDEX_GOODS_HOT.getType()==indexConfig){
                String key = "redis:list:indexGoodsHot";
                String indexConfigGoods = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
                redisService.set(key,indexConfigGoods);
                redisService.expire(key,86400);
            }else if (IndexConfigTypeEnum.INDEX_GOODS_NEW.getType()==indexConfig){
                String key = "redis:list:indexGoodsNew";
                String indexConfigNews = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
                redisService.set(key,indexConfigNews);
                redisService.expire(key,86400);
            }else if (IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType()==indexConfig){
                String key = "redis:list:indexGoodsRecommond";
                String indexConfigRecommonds = JSON.toJSONString(hanZoMallIndexConfigGoodsVOS);
                redisService.set(key,indexConfigRecommonds);
                redisService.expire(key,86400);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新缓存失败给上一级返回false"+e.getMessage());
            return false;
        }
        return true;
    }
}
