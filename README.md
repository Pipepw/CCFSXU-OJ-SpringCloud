# 项目介绍

## 项目背景

算法能力作为计算机专业人员必备的能力，往往需要通过不断做题来提高，而在线测评系统（Online Judge）便是用于编程人员进行做题的网站，但是使用其他的在线测评系统，没有办法统计同学们的做题情况，不能发布定制化的符合大家需求以及能力的试题和竞赛，同时也没有办法激发大家的兴趣。

该系统可以发布定制化的试题和竞赛，可以更有针对性的帮助大家提高算法能力。同时还可以设置各种类型的榜单和奖励，以帮助大家提高算法能力。还可以帮助老师分析同学们的做题情况。

该项目为后端代码。

## Spring Cloud 相关

该项目是基于VOJ进行实现，并将其由 Spring MVC 项目重构为 Spring Boot 架构，然后再重构为 Spring Cloud 架构，该仓库为 Spring Cloud 架构版本仓库，目前已实现：服务注册中心：**Eureka**、消费者与生产者分离、使用 **Feign** 进行**声明式的服务调用**、实现 **Hystrix DashBoard** 对访问以及程序状况进行监控、因为服务器资源有限，所以没有创建多个相同的服务以通过 **Ribbon** 进行负载均衡。

# 项目架构

## 功能模块图

![image-20210228112807559](https://gitee.com/hzm_pwj/FigureBed/raw/master/giteeImg/20210228112943.png)

## E-R图

![image-20210228112859217](https://gitee.com/hzm_pwj/FigureBed/raw/master/giteeImg/20210228112859.png)

## 模块

**ccfsxu-oj-api**：项目的公共部分代码。

**ccfsxu-oj-consumer-hystrix-dashboard-9001**：项目 hystrix-dashboard 监控代码，端口号为9001。

**ccfsxu-oj-eureka-7001**：项目服务注册中心代码，端口号为7001。

**ccfsxu-oj-provider-8001**：项目服务提供者代码，端口号为8001。

**ccfsxu-oj-consumer-80**：项目消费者代码，端口号为80。

# 项目部署

## 环境

特别注意JDK、Spring Boot、Spring Cloud以及MyBtis的版本对应关系：参考文章[JDK、Mybatis、Mysql、Maven、Spring Boot以及Spring Cloud的版本对应关系](https://blog.csdn.net/weixin_42972730/article/details/112723830)。

| 环境               | 版本          |
| ------------------ | ------------- |
| JDK 版本           | 11            |
| Spring  Boot 版本  | 2.3.0.RELEASE |
| Spring  Cloud 版本 | Hoxton.SR5    |
| MySQL 版本         | 8.0.23        |
| MyBatis  版本      | 2.1.4         |
| Swagger  版本      | 2.9.2         |

## 创建数据库

将根目录下的sql文件导入到数据库中。

## 项目启动

直接将Eurek、Provider、Consumer以及Hystrix-DashBoard依次启动即可。

## 访问地址

Swagger：http://localhost/swagger-ui.html

Hystrix-DashBoard：http://localhost:9002/hystrix (因为我的9001端口被占用，所以项目中实际是使用的9002端口，可在对应代码中进行修改)

