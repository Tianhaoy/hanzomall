package ltd.hanzo.mall.config;


/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/4/13 16:08
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description:shiro配置
 */
/*
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setLoginUrl("/oss-login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/oss-login");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/goods/detail/**", "authc");
        filterChainDefinitionMap.put("/shop-cart", "authc");
        filterChainDefinitionMap.put("/shop-cart/**", "authc");
        filterChainDefinitionMap.put("/saveOrder", "authc");
        filterChainDefinitionMap.put("/orders", "authc");
        filterChainDefinitionMap.put("/orders/**", "authc");
        filterChainDefinitionMap.put("/personal", "authc");
        filterChainDefinitionMap.put("/personal/updateInfo", "authc");
        filterChainDefinitionMap.put("/selectPayType", "authc");
        filterChainDefinitionMap.put("/payPage", "authc");
        filterChainDefinitionMap.put("/admin/**","anon");
        filterChainDefinitionMap.put("/register","anon");
        filterChainDefinitionMap.put("/login","anon");
        filterChainDefinitionMap.put("/logout","anon");
        filterChainDefinitionMap.put("/static","anon");
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        log.info("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;

    }

}
*/
