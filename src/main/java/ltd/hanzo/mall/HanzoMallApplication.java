package ltd.hanzo.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 */
@MapperScan("ltd.hanzo.mall.dao")
@SpringBootApplication
public class HanzoMallApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(HanzoMallApplication.class, args);
    }

}
