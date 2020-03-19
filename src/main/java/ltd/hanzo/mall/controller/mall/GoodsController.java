package ltd.hanzo.mall.controller.mall;

import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallException;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallGoodsDetailVO;
import ltd.hanzo.mall.controller.vo.SearchPageCategoryVO;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.service.HanZoMallCategoryService;
import ltd.hanzo.mall.service.HanZoMallGoodsService;
import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private HanZoMallGoodsService hanZoMallGoodsService;
    @Resource
    private HanZoMallCategoryService hanZoMallCategoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据 params.containsKey是查看key是否有值 可以为null
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = hanZoMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", hanZoMallGoodsService.searchHanZoMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        HanZoMallGoods goods = hanZoMallGoodsService.getHanZoMallGoodsById(goodsId);
        if (goods == null) {
            HanZoMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()){
            HanZoMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        HanZoMallGoodsDetailVO goodsDetailVO = new HanZoMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}
