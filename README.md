![loginlogo2.png](https://img.hacpai.com/file/2020/03/loginlogo2-29dc7fca.png)
![Version 1.0.0](https://img.shields.io/badge/version-1.0.0-yellow.svg)

`项目介绍`

hanzo-mall 项目（半藏商城）是一套Maven的电商系统，包括 商城系统及商城后台管理系统，基于 Spring Boot 2.1.0 及相关技术栈开发。前台商城系统包含首页、登录短信验证、商品分类、首页轮播、推荐商品、商品搜索、商品展示、新品展示、购物车、订单结算、订单流程、个人订单管理、个人账单管理、会员中心、导出Execl表格等模块。后台管理系统包含项目介绍、轮播图管理、商品管理、订单管理、会员管理、分类管理、修改密码、设置等模块。项目的初始模板来源于掘金大佬`13`的一个开源项目。

`项目技术栈`

- `Spring Boot 整合 MyBatis 操作数据库`
- `Spring Boot 整合 Redis缓存主页信息以及统计在线人数`
- `Spring Boot 整合 Quartz定时扫描订单发送提醒邮件`
- `Spring Boot 整合 Thymeleaf 模板引擎`
- `Spring Boot 整合 支付宝沙箱支付接口 `
- `Spring Boot 整合 阿里云的短信服务发送短信 `
- `Spring Boot 整合 mail发送邮件 `
- `Spring Boot 整合 OSS对象存储保存图片 `
- `Spring Boot 整合 poi 导出Execl表格 `
- `Spring Boot 整合 kaptcha 验证码 `
- `Spring Boot 整合 logback 打印日志`
- `Spring Boot 整合 swagger-ui 接口文档`
- `HTML5 搭载 JqGrid 分页插件`
- `KindEditor 富文本编译器`

`项目开发工具`

- IDEA+Git

`Linux部署工具`

- SecureCRT+Fz+FlashFXP

`数据和缓存图形化工具`

- Navicat+RedisDesktopManager

`接口测试工具`

- Postman

`项目演示`

- [商城首页](http://mall.babehome.com:28089/index)
- [商城后台管理系统](http://mall.babehome.com:28089/admin)

`开发及部署`

- 部署在阿里云服务器
- 域名备案以及域名解析

> 问题或者建议都可以在`issues`中反馈给我，依旧在继续开发完善新功能。

- 我的邮箱：`2469653218@qq.com`

- [GitHub](https://github.com/Tianhaoy/hanzomall)
- [个人博客](http://blog.babehome.com:8090/)

`半藏的名字解释`

~~我从2017年开始喜欢一个网红叫`半藏森林`，后来她做小三了，我太难过了(*^__^*)，所以就叫`半藏商城`~~

`下一步会开发的功能`
- 秒杀系统 打算在boot的基础上搞微服务架构，引入Eureka、Ribbon、Hystrix、Zuul、消息队列等东西。
- 秒杀的话比较复杂，可能会单独部署，需要进行接口的限流等等，秒杀完我的打算是发送短信或者邮件提醒
- 但是在并发情况下，短信服务或者邮件服务可能扛不住，所以需要搞消息队列，我之前的代码就有一些短信，邮件的逻辑，需要重构。
- 如果我这个秒杀单独部署的话，那还需要解决单点登录的问题。

`秒杀系统之后会开发的功能`
- 优惠券
- 用户与客服（后台管理员）在网页端就可以实时聊天的功能（前端代码打算使用layUI的layIm）


