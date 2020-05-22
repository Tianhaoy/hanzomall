package ltd.hanzo.mall.config;


import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class HanZoMallWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;
    @Autowired
    private HanZoMallLoginInterceptor hanZoMallLoginInterceptor;
    @Autowired
    private HanZoMallCartNumberInterceptor hanZoMallCartNumberInterceptor;
    @Autowired
    private HanZoMallMonitorOnlineInterceptor hanZoMallMonitorOnlineInterceptor;
    @Autowired
    private HanZoMallVisitNumberInterceptor hanZoMallVisitNumberInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径（后台登陆拦截）
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**");
        // 购物车中的数量统一处理
        registry.addInterceptor(hanZoMallCartNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/oss-login")
                .excludePathPatterns("/logout");
        // 商城页面登陆拦截
        registry.addInterceptor(hanZoMallLoginInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/oss-login")
                .excludePathPatterns("/logout")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")            
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage")
                .addPathPatterns("/bill");
        //用户进行的操作统计在线人数处理
        registry.addInterceptor(hanZoMallMonitorOnlineInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/oss-login")
                .excludePathPatterns("/logout");
        //访问次数统计
        registry.addInterceptor(hanZoMallVisitNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/oss-login")
                .excludePathPatterns("/logout");
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations(Constants.IMAGES_ADDRESS + "/" + Constants.FILEDIR);
        registry.addResourceHandler("/goods-img/**").addResourceLocations(Constants.IMAGES_ADDRESS + "/" + Constants.FILEDIR);
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
//        registry.addResourceHandler("/goods-img/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}
