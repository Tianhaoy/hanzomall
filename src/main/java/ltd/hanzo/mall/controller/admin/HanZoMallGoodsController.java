package ltd.hanzo.mall.controller.admin;


import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.HanZoMallCategoryLevelEnum;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.entity.GoodsCategory;
import ltd.hanzo.mall.entity.HanZoMallGoods;
import ltd.hanzo.mall.service.HanZoMallCategoryService;
import ltd.hanzo.mall.service.HanZoMallGoodsService;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy
 * 商品管理
 */
@Controller
@RequestMapping("/admin")
public class HanZoMallGoodsController {

    @Resource
    private HanZoMallGoodsService hanZoMallGoodsService;
    @Resource
    private HanZoMallCategoryService hanZoMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "hanzo_mall_goods");
        return "admin/hanzo_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HanZoMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), HanZoMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/hanzo_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        HanZoMallGoods hanZoMallGoods = hanZoMallGoodsService.getHanZoMallGoodsById(goodsId);
        if (hanZoMallGoods == null) {
            return "error/error_400";
        }
        if (hanZoMallGoods.getGoodsCategoryId() > 0) {
            if (hanZoMallGoods.getGoodsCategoryId() != null || hanZoMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = hanZoMallCategoryService.getGoodsCategoryById(hanZoMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HanZoMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = hanZoMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), HanZoMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = hanZoMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (hanZoMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HanZoMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), HanZoMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = hanZoMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), HanZoMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", hanZoMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/hanzo_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(hanZoMallGoodsService.getHanZoMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody HanZoMallGoods hanZoMallGoods) {
        if (StringUtils.isEmpty(hanZoMallGoods.getGoodsName())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(hanZoMallGoods.getTag())
                || Objects.isNull(hanZoMallGoods.getOriginalPrice())
                || Objects.isNull(hanZoMallGoods.getGoodsCategoryId())
                || Objects.isNull(hanZoMallGoods.getSellingPrice())
                || Objects.isNull(hanZoMallGoods.getStockNum())
                || Objects.isNull(hanZoMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hanZoMallGoodsService.saveHanZoMallGoods(hanZoMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody HanZoMallGoods hanZoMallGoods) {
        if (Objects.isNull(hanZoMallGoods.getGoodsId())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsName())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(hanZoMallGoods.getTag())
                || Objects.isNull(hanZoMallGoods.getOriginalPrice())
                || Objects.isNull(hanZoMallGoods.getSellingPrice())
                || Objects.isNull(hanZoMallGoods.getGoodsCategoryId())
                || Objects.isNull(hanZoMallGoods.getStockNum())
                || Objects.isNull(hanZoMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(hanZoMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hanZoMallGoodsService.updateHanZoMallGoods(hanZoMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        HanZoMallGoods goods = hanZoMallGoodsService.getHanZoMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (hanZoMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}