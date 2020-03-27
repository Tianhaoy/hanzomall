package ltd.hanzo.mall.service.impl;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallCategoryLevelEnum;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexCategoryVO;
import ltd.hanzo.mall.controller.vo.SearchPageCategoryVO;
import ltd.hanzo.mall.controller.vo.SecondLevelCategoryVO;
import ltd.hanzo.mall.controller.vo.ThirdLevelCategoryVO;
import ltd.hanzo.mall.dao.GoodsCategoryMapper;
import ltd.hanzo.mall.entity.GoodsCategory;
import ltd.hanzo.mall.service.HanZoMallCategoryService;
import ltd.hanzo.mall.service.RedisService;
import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class HanZoMallCategoryServiceImpl implements HanZoMallCategoryService {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private RedisService redisService;


    @Override
    public PageResult getCategorisPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<HanZoMallIndexCategoryVO> getCategoriesForIndex() {
        List<HanZoMallIndexCategoryVO> hanZoMallIndexCategoryVOS = new ArrayList<>();
        String key = "redis:list:indexCategory";
        boolean exists =redisService.hasKey(key);
        if (exists){
            //redis中存在key 不需要从数据库中读取
            log.debug("redis中存在key:"+key);
            String indexCategory =redisService.get(key).toString();
            List<HanZoMallIndexCategoryVO> categories = new ArrayList<>();
            if(indexCategory!=null){
                log.debug("从redis中读取商品分类--");
                categories = JSON.parseArray(indexCategory,HanZoMallIndexCategoryVO.class);
            }
            return categories;
        }
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HanZoMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
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
            log.info("更新一遍redis缓存");
            String indexCategory = JSON.toJSONString(hanZoMallIndexCategoryVOS);
            redisService.set(key,indexCategory);
            redisService.expire(key,86400);
            return hanZoMallIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            //获取当前三级分类的二级分类
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == HanZoMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                //获取当前二级分类下的三级分类List
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }
}
