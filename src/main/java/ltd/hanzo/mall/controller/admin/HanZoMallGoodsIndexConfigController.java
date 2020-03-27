package ltd.hanzo.mall.controller.admin;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.IndexConfigTypeEnum;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.entity.IndexConfig;
import ltd.hanzo.mall.service.HanZoMallIndexConfigService;
import ltd.hanzo.mall.service.UpdateRedisService;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class HanZoMallGoodsIndexConfigController {

    @Resource
    private HanZoMallIndexConfigService hanZoMallIndexConfigService;
    @Resource
    UpdateRedisService updateRedisService;

    @GetMapping("/indexConfigs")
    public String indexConfigsPage(HttpServletRequest request, @RequestParam("configType") int configType) {
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)) {
            return "error/error_5xx";
        }

        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/hanzo_mall_index_config";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/indexConfigs/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(hanZoMallIndexConfigService.getConfigsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/indexConfigs/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig) {
        if (Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hanZoMallIndexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/indexConfigs/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig) {
        if (Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigId())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hanZoMallIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            boolean sign =false;
            if (IndexConfigTypeEnum.INDEX_GOODS_HOT.getType()==indexConfig.getConfigType()){
                 sign =updateRedisService.updateIndexConfig(indexConfig.getConfigType(), Constants.INDEX_GOODS_HOT_NUMBER);
            }else if (IndexConfigTypeEnum.INDEX_GOODS_NEW.getType()==indexConfig.getConfigType()){
                 sign =updateRedisService.updateIndexConfig(indexConfig.getConfigType(),Constants.INDEX_GOODS_NEW_NUMBER);
            }else if (IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType()==indexConfig.getConfigType()){
                 sign =updateRedisService.updateIndexConfig(indexConfig.getConfigType(),Constants.INDEX_GOODS_RECOMMOND_NUMBER);
            }
            if (sign){
                log.info("更新缓存成功");
            }else {
                log.error("更新分类缓存失败--不报错--不影响流程--");
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        IndexConfig config = hanZoMallIndexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(config);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/indexConfigs/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (hanZoMallIndexConfigService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }


}