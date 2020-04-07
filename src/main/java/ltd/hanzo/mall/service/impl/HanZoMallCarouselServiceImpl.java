package ltd.hanzo.mall.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallIndexCarouselVO;
import ltd.hanzo.mall.dao.CarouselMapper;
import ltd.hanzo.mall.entity.Carousel;
import ltd.hanzo.mall.service.HanZoMallCarouselService;
import ltd.hanzo.mall.service.RedisService;
import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HanZoMallCarouselServiceImpl implements HanZoMallCarouselService {

    @Resource
    private CarouselMapper carouselMapper;
    @Resource
    private RedisService redisService;

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        List<Carousel> carousels = carouselMapper.findCarouselList(pageUtil);
        int total = carouselMapper.getTotalCarousels(pageUtil);
        PageResult pageResult = new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (carouselMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return carouselMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<HanZoMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<HanZoMallIndexCarouselVO> hanZoMallIndexCarouselVOS = new ArrayList<>(number);
        String key = "redis:list:indexCarousel";
        boolean exists =redisService.hasKey(key);
        if (exists){
            //redis中存在key 不需要从数据库中读取
            log.debug("redis中存在key:"+key);
            String indexCarousel =redisService.get(key).toString();
            List<HanZoMallIndexCarouselVO> carousels = new ArrayList<>(number);
            if(indexCarousel!=null){
                log.debug("从redis中读取轮播图信息--");
                carousels = JSON.parseArray(indexCarousel,HanZoMallIndexCarouselVO.class);
            }
            return carousels;
        }
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carousels)) {
            hanZoMallIndexCarouselVOS = BeanUtil.copyList(carousels, HanZoMallIndexCarouselVO.class);
        }
        log.info("更新一遍redis缓存");
        String indexCarousel = JSON.toJSONString(hanZoMallIndexCarouselVOS);
        redisService.set(key,indexCarousel);
        redisService.expire(key,86400);
        return hanZoMallIndexCarouselVOS;
    }
}
