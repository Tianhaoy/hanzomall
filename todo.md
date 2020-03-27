## 2020年3月已办事项
- 新增功能点 导出我的订单详情Execl表格 
- 新增页面 我的账单 统计消费记录 支持导出我的账单Execl表格
- 加入各种支付功能 整合支付接口 
   无能为力 微信没办法 银联maven不能导jar先不弄（暂时只支持支付宝沙箱支付..）
    >https://gitee.com/52itstyle/spring-boot-pay
- 用户表新增mail邮箱字段 保存用户的邮箱
- 个人信息中增加邮箱的显示和修改等功能
- 成功付款成功后会给给邮箱发送邮件
- 新增用户修改密码功能需要验证邮箱 才允许修改密码
- 新增手机验证码直接登录功能点
- 找回密码验证邮箱（发邮箱免费 发短信收费）
- 添加redis 缓存技术 主页显示内容存储到Redis中 过期时间为1天
- 添加quartz定时任务 扫描未支付订单 发送邮件提醒
- 新增后台修改主页配置，同步更新redis
## 2020年3月待办事项
- 增加定时秒杀某个商品的功能 支持高并发
- 增加优惠券领取页面 以及优惠券统计 支付时查询计算等功能
- 微服务Dubbo Springcloud
- spring Security.. Shiro的认证鉴权服务
-  zookeeper
- 消息队列RocketMQ、kafka、ActiveMQ、RabbitMQ
- API文档swagger
- 聊天WebSocket
- 新增客服功能 
- 新增消息联系功能 用户可以和管理员进行在消息框进行实时对话 
- 后台管理系统也需要增加一个对话列表 显示那些用户与管理员进行聊天 显示聊天记录 并可以实时回复
- 增加一个消息通知 监控订单流程变化 提醒给用户
- 深度学习Spring Boot 
    >http://www.springboot.wiki
- Spring Boot 的各种小demo 
    >https://github.com/ityouknow/spring-boot-examples
- 学习Spring Cloud 
    >http://www.springcloud.wiki
- 使用RabbitMQ作为消息中间件
- Spring AMQP操作消息中间件
- Spring-Cloud-Eureka作为微服务注册中心
- Spring-Cloud-Ribbon作为客户端负载均衡
- Spring-Cloud-Gateway作为微服务网关
- Spring-Cloud-Config作为微服务配置中心
- Spring-Cloud-Bus作为服务总线
- 使用Oauth2协议完成第三方认证
- 使用Spring Security Oauth2和Redis完成单点登录
- 使用JWT令牌校验用户权限
- 基于CAP理论和柔性事务补偿TCC实现分布式事务
- Seata完成分布式事务控制解决方案
- 秒杀分析和技术解决方案
- Redis集群、分布式解决方案、持久化策略、哨兵模式、击穿&雪崩解决方案
- 电商微服务表结构设计
- FastDFS分布式文件存储

