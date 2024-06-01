<p align="center">
 	<a href="https://gitee.com/Linriqiang/springboot-insc">
 	    <img src="https://img.shields.io/badge/RuoYi-v4.7.8-brightgreen.svg">
 	</a>
      <img src="https://img.shields.io/badge/Mysql-8.0-green.svg" alt="Downloads">
      <img src="https://img.shields.io/badge/JDK-1.8-green.svg" alt="Build Status">
      <img src="https://img.shields.io/badge/license-Apache%202-blue.svg" alt="Build Status">
      <img src="https://img.shields.io/badge/Spring%20Boot-3.2-blue.svg" alt="Downloads">
 </p>
 
```
🕙 格局打开，分享是一种美德，右上随手点个 🌟 Star，谢谢
```

**风险提示**

 _本项目只用作学习，不做任何投资建议，一切风险自行承担，敬请广大开发者注意_ 


**温馨提醒**

1. 亲，动动您发财的手为我们点一颗star，是对我们最好的鼓励和支持，也是我们前进的动力<br/>
2. 格局打开，全部开源
3. 演示环境<br/>
        
　　　　铭文诊断管理在线体验：<a href='http://insc.wechatqun.cn:8088' target="_blank" >http://insc.wechatqun.cn </a>

## 项目简介

​       本项目使用ruoyi 单体框架通过定时拉取机制自动获取Brc20的铭文数据，并随后运用算法进行筛选和诊断，从而识别出那些具有潜在价值的铭文。
       点一颗<a href="https://gitee.com/Linriqiang/springboot-insc" target="_blank"> Star </a>鼓励一下作者吧
       请关注我们了解后续的情况


| 后续完善功能                  |          |
|----------------------|------------|
| 1.每天同步日报到推特                 |        |
| 2.每天自动布道到推特               |       |
| 3.异动铭文及时通过微信公众号，短信，Telegram通知用户t          |      |
| 4.团队管理,发送通知及时通知团队成员         |       |
| 5.一建生成精美带有铭文符号的图片，为铭文赋能         |       |
| 6.历史数据分析等         |       |
| 7.成本统计以及追踪         |       |

| 项目                                                              | Star                                                                                                                                                                                                                                                                                             | 简介                          |
|-----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| [SpringBoot-Brc20-ordinals](https://gitee.com/Linriqiang/springboot-insc)  | [![Gitee star](https://gitee.com/Linriqiang/springboot-insc/badge/star.svg)](https://gitee.com/Linriqiang/springboot-insc)       | Gitee版        |
| [SpringBoot-Brc20-ordinals](https://github.com/gdweixiao001/SpringBoot-Brc20-ordinals)      | [![GitHub stars](https://img.shields.io/github/stars/gdweixiao001/SpringBoot-Brc20-ordinals.svg?style=social&label=Stars)](https://github.com/gdweixiao001/SpringBoot-Brc20-ordinals)               | GitHub版       |


## 核心技术栈

| 技术栈                  | 版本         |
|----------------------|------------|
| Java                 | 1.8        |
| Mysql               | 8.0      |
| Spring Boot          | 2.5.15     |
| Mybatis Plus         | 3.5.1      |
| Shiro         | 1.13.0      |


## 工程结构
``` 
mwzg
├── ruoyi-admin 
├── ruoyi-coin 
├── ruoyi-common 
├── ruoyi-framework
├── ruoyi-generator
├── ruoyi-quartz 
├── ruoyi-system 
```
## 项目启动步骤
    1.把sql文件导入到数据库
    2.修改ruoyi-admin下的 application-druid.yml
        把里面的Reids和Mysql换为自己的即可
    3.启动RuoYiApplication即可
    4。登录账号密码
        账号：admin
        密码：123456

### 后端界面展示

##### 登录页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/1.png" />

##### 首页页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/2.png" />

##### 铭文详情页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/3.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/4.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/5.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/6.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/7.png" />

##### 铭文管理页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/8.png" />

##### 铭文排行榜统计页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/9.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/10.png" />

##### 统计分析页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/11.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/12.png" />

##### 当日数据页面

<img src="https://ai.oss.mj.ink/chatgpt/insc/13.png" />
<img src="https://ai.oss.mj.ink/chatgpt/insc/14.png" />


## 合作（广告勿扰）：
    
 <div align=center >
    <td ><img height="350" width="250" src="https://ai.oss.mj.ink/chatgpt/insc/wx.jpg"/></td>
 </div>
 
 ## 开源协议
 Apache Licence 2.0 （[英文原文](http://www.apache.org/licenses/LICENSE-2.0.html)）
 Apache Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）。
 需要满足的条件如下：
 * 需要给代码的用户一份Apache Licence
 * 如果你修改了代码，需要在被修改的文件中说明。
 * 在延伸的代码中（修改和有源代码衍生的代码中）需要带有原来代码中的协议，商标，专利声明和其他原来作者规定需要包含的说明。
 * 如果再发布的产品中包含一个Notice文件，则在Notice文件中需要带有Apache Licence。你可以在Notice中增加自己的许可，但不可以表现为对Apache Licence构成更改。
   Apache Licence也是对商业应用友好的许可。使用者也可以在需要的时候修改代码来满足需要并作为开源或商业产品发布/销售。
 
 ## 用户权益
 * 允许免费用于学习等，但请保留源码作者信息。
 * 对未经过授权和不遵循 Apache 2.0 协议二次开源或者商业化我们将追究到底。
 * 参考请注明：参考自 Springboot-insc：https://gitee.com/Linriqiang/springboot-insc
