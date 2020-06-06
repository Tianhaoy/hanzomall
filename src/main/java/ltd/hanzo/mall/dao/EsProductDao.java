package ltd.hanzo.mall.dao;

import ltd.hanzo.mall.entity.EsProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/4 21:03
 * @Description:搜索系统中的商品管理自定义Dao
 */
public interface EsProductDao {
    List<EsProduct> getAllEsProductList(@Param("goodsId") Long goodsId);

}

