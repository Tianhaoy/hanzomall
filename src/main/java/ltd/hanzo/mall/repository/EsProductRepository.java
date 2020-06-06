package ltd.hanzo.mall.repository;

import ltd.hanzo.mall.entity.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/4 20:57
 * @Description:商品ES操作类
 */
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Long> {
    /**
     * 衍生搜索关键字查询
     *
     * @param goodsName          商品名称
     * @param goodsIntro         商品简介
     * @param tag               商品标签
     * @param page              分页信息
     * @return
     */
    Page<EsProduct> findByGoodsNameOrGoodsIntroOrTag(String goodsName, String goodsIntro, String tag,Pageable page);

    /**
     * 衍生搜索商品分类定位查询
     *
     * @param goodsCategoryId   商品分类
     * @param page              分页信息
     * @return
     */
    Page<EsProduct> findByGoodsCategoryId(Long goodsCategoryId, Pageable page);

}

