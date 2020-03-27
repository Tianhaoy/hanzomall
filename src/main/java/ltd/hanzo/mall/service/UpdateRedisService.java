package ltd.hanzo.mall.service;

import org.springframework.stereotype.Service;

/**
 * @Description: 修改主页配置信息会同步更新缓存接口
 * @Author ASUS
 * @Date 2020/3/27 22:28
 */
public interface UpdateRedisService {

    boolean updateIndexCategoryRedis();

    boolean updateIndexCarouselRedis();

    boolean updateIndexConfig(int indexConfig,int number);

}
