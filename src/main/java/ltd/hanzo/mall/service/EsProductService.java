package ltd.hanzo.mall.service;

import ltd.hanzo.mall.entity.EsProduct;
import ltd.hanzo.mall.entity.EsProductRelatedInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/4 21:01
 * @Description:商品搜索管理Service
 * 只有接口可用 没有前端页面 之前的前端页面不是前后端分离的
 */
public interface EsProductService {
    /**
     * 从数据库中导入所有商品到ES
     */
    int importAll();

    /**
     * 根据id删除商品
     */
    void delete(Long id);

    /**
     * 根据id创建商品
     */
    EsProduct create(Long id);

    /**
     * 批量删除商品
     */
    void delete(List<Long> ids);

    /**
     * 根据关键字搜索名称、简介、标签
     */
    Page<EsProduct> search(String keyword,Integer pageNum, Integer pageSize);

    /**
     * 根据商品分类搜索
     */
    Page<EsProduct> fixedSearch(Long goodsCategoryId ,Integer pageNum, Integer pageSize);

    /**
     * 根据关键字搜索名称 0-->默认相关度；1->按新品；2->按库存；3->价格从低到高；4->价格从高到低
     */
    Page<EsProduct> sortSearch(String keyword,Integer pageNum, Integer pageSize,Integer sort);

    /**
     * 根据用户点击商品id 推荐相关商品
     */
    Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize);

    /**
     * 获取搜索词相关分类、属性
     */
    EsProductRelatedInfo searchRelatedInfo(String keyword);
}
