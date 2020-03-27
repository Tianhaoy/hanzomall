package ltd.hanzo.mall.controller.admin;

import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.entity.Carousel;
import ltd.hanzo.mall.service.HanZoMallCarouselService;
import ltd.hanzo.mall.service.UpdateRedisService;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy
 * 轮播图管理
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class HanZoMallCarouselController {

    @Resource
    HanZoMallCarouselService hanZoMallCarouselService;
    @Resource
    UpdateRedisService updateRedisService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request) {
        request.setAttribute("path", "hanzo_mall_carousel");
        return "admin/hanzo_mall_carousel";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/carousels/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(hanZoMallCarouselService.getCarouselPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/carousels/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody Carousel carousel) {
        if (StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常，请输入正确的轮播图信息！");
        }
        String result = hanZoMallCarouselService.saveCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            boolean sign =updateRedisService.updateIndexCarouselRedis();
            if (sign){
                log.info("更新轮播图缓存成功");
            }else {
                log.error("更新轮播图缓存失败--不报错--不影响流程--");
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/carousels/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Carousel carousel) {
        if (Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hanZoMallCarouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            boolean sign =updateRedisService.updateIndexCarouselRedis();
            if (sign){
                log.info("更新轮播图缓存成功");
            }else {
                log.error("更新轮播图缓存失败--不报错--不影响流程--");
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        Carousel carousel = hanZoMallCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/carousels/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (hanZoMallCarouselService.deleteBatch(ids)) {
            boolean sign =updateRedisService.updateIndexCarouselRedis();
            if (sign){
                log.info("更新轮播图缓存成功");
            }else {
                log.error("更新轮播图缓存失败--不报错--不影响流程--");
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}