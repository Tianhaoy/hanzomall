package ltd.hanzo.mall.controller.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import ltd.hanzo.mall.entity.EsProduct;
import ltd.hanzo.mall.entity.EsProductRelatedInfo;
import ltd.hanzo.mall.service.EsProductService;
import ltd.hanzo.mall.util.CommonPage;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/4 21:45
 * @Description:搜索商品管理Controller
 */
@Controller
@Api(tags = "EsProductController", description = "Elasticsearch搜索模块管理")
@RequestMapping("/esProduct")
public class EsProductController {
    @Autowired
    private EsProductService esProductService;

    @ApiOperation(value = "导入所有数据库中商品到ES")
    @RequestMapping(value = "/importAll", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> importAllList() {
        int count = esProductService.importAll();
        return ResultGenerator.genSuccessResult(count);
    }

    @ApiOperation(value = "根据id删除ES中商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Object> delete(@PathVariable Long id) {
        esProductService.delete(id);
        return ResultGenerator.genSuccessResult(null);
    }

    @ApiOperation(value = "根据id批量删除ES中商品")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> delete(@RequestParam("ids") List<Long> ids) {
        esProductService.delete(ids);
        return ResultGenerator.genSuccessResult(null);
    }

    @ApiOperation(value = "根据id创建商品到ES")
    @RequestMapping(value = "/create/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Result<EsProduct> create(@PathVariable Long id) {
        EsProduct esProduct = esProductService.create(id);
        if (esProduct != null) {
            return ResultGenerator.genSuccessResult(esProduct);
        } else {
            return ResultGenerator.genFailResult("error");
        }
    }

    @ApiOperation(value = "Elasticsearch-简单or搜索")
    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public Result<CommonPage<EsProduct>> search(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.search(keyword, pageNum, pageSize);
        return ResultGenerator.genSuccessResult(CommonPage.restPage(esProductPage));
    }

    @ApiOperation(value = "Elasticsearch-商品分类is搜索")
    @RequestMapping(value = "/search/fixed", method = RequestMethod.GET)
    @ResponseBody
    public Result<CommonPage<EsProduct>> fixedSearch(@RequestParam(required = false) Long goodsCategoryId,
                                                @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.fixedSearch(goodsCategoryId, pageNum, pageSize);
        return ResultGenerator.genSuccessResult(CommonPage.restPage(esProductPage));
    }

    @ApiOperation(value = "Elasticsearch-综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按库存；3->价格从低到高；4->价格从高到低",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/search/sort", method = RequestMethod.GET)
    @ResponseBody
    public Result<CommonPage<EsProduct>> sortSearch(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                      @RequestParam(required = false, defaultValue = "0") Integer sort) {
        Page<EsProduct> esProductPage = esProductService.sortSearch(keyword,pageNum, pageSize, sort);
        return ResultGenerator.genSuccessResult(CommonPage.restPage(esProductPage));
    }

    @ApiOperation(value = "Elasticsearch-根据用户点击商品id推荐相关商品")
    @RequestMapping(value = "/recommend/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<CommonPage<EsProduct>> recommend(@PathVariable Long id,
                                                   @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.recommend(id, pageNum, pageSize);
        return ResultGenerator.genSuccessResult(CommonPage.restPage(esProductPage));
    }


    @ApiOperation(value = "Elasticsearch-聚合搜索商品相关信息筛选属性--暂时不可用")
    @RequestMapping(value = "/search/relate", method = RequestMethod.GET)
    @ResponseBody
    public Result<EsProductRelatedInfo> searchRelatedInfo(@RequestParam(required = false) String keyword) {
        EsProductRelatedInfo productRelatedInfo = esProductService.searchRelatedInfo(keyword);
        return ResultGenerator.genSuccessResult(productRelatedInfo);
    }

}
