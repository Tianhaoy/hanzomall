package ltd.hanzo.mall.service;



import ltd.hanzo.mall.controller.vo.HanZoMallIndexCarouselVO;
import ltd.hanzo.mall.entity.Carousel;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;

import java.util.List;


public interface HanZoMallCarouselService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<HanZoMallIndexCarouselVO> getCarouselsForIndex(int number);
}
