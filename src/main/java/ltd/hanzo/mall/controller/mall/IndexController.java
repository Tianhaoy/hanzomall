package ltd.hanzo.mall.controller.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.IndexConfigTypeEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexCarouselVO;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexCategoryVO;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexConfigGoodsVO;
import ltd.hanzo.mall.service.HanZoMallCarouselService;
import ltd.hanzo.mall.service.HanZoMallCategoryService;
import ltd.hanzo.mall.service.HanZoMallIndexConfigService;
import ltd.hanzo.mall.service.MonitorOnlineService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Api(tags = "IndexController", description = "用户首页")
@Controller
public class IndexController {

    @Resource
    private HanZoMallCarouselService hanZoMallCarouselService;

    @Resource
    private HanZoMallIndexConfigService hanZoMallIndexConfigService;

    @Resource
    private HanZoMallCategoryService hanZoMallCategoryService;

    @Resource
    private MonitorOnlineService monitorOnlineService;

    @ApiOperation("首页路由")
    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<HanZoMallIndexCategoryVO> categories = hanZoMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        List<HanZoMallIndexCarouselVO> carousels = hanZoMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<HanZoMallIndexConfigGoodsVO> hotGoodses = hanZoMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<HanZoMallIndexConfigGoodsVO> newGoodses = hanZoMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<HanZoMallIndexConfigGoodsVO> recommendGoodses = hanZoMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
