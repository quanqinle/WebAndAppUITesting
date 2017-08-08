# Web And App UI Testing

简述
---
+ 这是一个适用于Web/Android/iOS的UI自动化项目，以及支持Android性能测试。项目底层使用了Selenium/Appium。  
+ 项目用maven进行工程管理，通过testng进行用例管理。
+ 对常用的selenium api进行了封装，如click()、type()等，在其中增加了元素可用性判断、log记录、失败截图。


环境准备
---
### 公共
1. jdk>=1.8 
2. maven>=3
3. IDE(eclipse或其他，以下以eclipse为例) + testng插件 + maven插件

### Android
1. 安装Android SDK  
1.1 配置环境变量  
1.2 控制台下验证`adb`运行正常  
1.3 如果使用模拟器运行用例，需要先用`AVD Manager`创建模拟器
2. 安装Appium服务端程序，参见[http://appium.io/](http://appium.io/)。避免踩坑，建议使用现成的安装包，界面操作更省心

### iPhone
1. **Mac OS X是必须的！**   
1.1 Appium Server运行、元素识别、脚本执行都需要用到Mac  
1.2 安装Xcode，验证安装，直至被测app可以编译成功，并在模拟器中打开  
2. 在真机上测试，按手册操作[Deploying an iOS app to a real device](http://appium.io/slate/en/master/)
3. 安装Appium服务端程序，安装后会发现它比Windows版多了对iOS测试的支持
![Appium Server Mac](/docs/images/appium-server-mac.png)

工程结构
---

```
├─perftest_android  <--Android性能测试工程
│  ├─perfreport 性能结果
│  └─src 用例
├─uitest_android  <--Android ui测试工程
│  ├─app 存放被测apk和mapping文件
│  ├─testdata  测试数据，csv格式
│  │  ├─online 线上数据
│  │  └─test   线下数据
│  ├─screenshot 失败截图
│  └─src
│      └─main
│          └─java
│              └─com
│                  └─beibei
│                      └─test
│                          ├─androidui
│                          │   ├─base Android专用的基础类，集成自uitest_core的base
│                          │   ├─page 页面对象
│                          │   └─testcase 测试用例
│                          └─perfutils Android性能测试工具
├─uitest_core
│  └─src
│      └─main
│          ├─java
│          │  └─com
│          │      └─beibei
│          │          └─test
│          │              └─core
│          │                 ├─base 
│          │                 │   ├─BaseOpt.java  事件基类。click、type、findelement、截图等
│          │                 │   ├─BasePage.java 页面基类。
│          │                 │   ├─BaseTest.java 测试用例基类。管理testng生命周期
│          │                 │   └─DriverFactory.java driver构造类。web、android的driver都在这里
│          │                 ├─listener 
│          │                 └─utils 工具箱
│          └─resources 存放log4j配置
└─uitest_web
    └─src
        └─main
            ├─java
            │  └─com
            │      └─beibei
            │          └─test
            │              └─webui
            │                  ├─base web专用的基础类，集成自uitest_core的base
            │                  ├─page
            │                  └─testcase
            └─resources 存放chromedriver
```
我是广告
---
推荐个最近在用的云服务提供商，质优价廉。好\*梯\*子，每月2.5刀，500G流量，网速够快。  
[点我！vultr](https://www.vultr.com/?ref=7159348)  
[![vultr](https://www.vultr.com/media/banner_1.png)](https://www.vultr.com/?ref=7159348)