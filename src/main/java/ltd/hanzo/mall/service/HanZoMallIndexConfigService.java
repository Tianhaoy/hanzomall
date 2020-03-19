package ltd.hanzo.mall.service;


import ltd.hanzo.mall.controller.vo.HanZoMallIndexConfigGoodsVO;
import ltd.hanzo.mall.entity.IndexConfig;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;

import java.util.List;

public interface HanZoMallIndexConfigService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<HanZoMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}
