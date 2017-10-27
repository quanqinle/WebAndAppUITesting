# Web And App UI Testing

简述
---
+ 这是一个适用于Web/Android/iOS的UI自动化项目，以及支持Android性能测试。项目底层使用了Selenium/Appium。  
+ 项目用maven进行工程管理，通过testng进行用例管理。
+ 对常用的selenium api进行了封装，如click()、type()等，在其中增加了元素可用性判断、log记录、失败截图。
+ WebUI、AndroidUI、iOSUI分别单独成module，根据os特性可以编写专用的基类方法，并且module根目录下配置文件config.properties互不影响
+ 同一个testcase支持可配置的读取线上环境、线下环境2份测试驱动数据
+ 封装了日志LogUtil、断言AssertUtil，方便以后替换或扩展其他功能
+ Android：如提供资源混淆文件mapping.txt，框架可以自行将原始元素id替换成混淆后的id，然后实现正常的定位元素

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

架构图
---
![proj frame](/docs/images/project-frame.png)


使用方法
---
## iPhone UI

#### 编译被测应用
1. 前提：保证xcode已经可以编译通过被测app，且在模拟器上验证基本功能正常
2. 更新代码
3. 编译app  
> cd 工程目录  
> xcodebuild -workspace BeiBeiAPP.xcworkspace -scheme beibei -configuration Debug -sdk iphonesimulator -arch x86_64  

4. 生成压缩包，其中**beibei.zip就是被测包**
> ditto -ck --sequesterRsrc --keepParent \`ls -1 -d -t ~/Library/Developer/Xcode/DerivedData/\*/Build/Products/Debug-iphonesimulator/*.app | head -n 1\`  ~/beibei.zip  


#### 元素识别
1. 进入appium server的iOS settings界面，设置App Path=上一步生成的zip，设置Force Device和Platform Version
2. 点击launch
3. 点击inspector，后续元素识别操作同Android
4. 建议使用相对路径的xpath定位元素，例如：`Button("xpath=//UIAAlert[@name='提示']//UIAButton[@name='确定']")`

#### 编写用例
+ 页面demo：uitest_iphone下LoginPage.java
+ 用例demo：uitest_iphone下DemoLoginTest.java

#### 运行用例
1. 配置config.properties  
    + remote.address=appium server的地址  
    + app=appium server机器上的绝对路径
2. appium client，及UI工程代码  
    + 可以在Mac上，即，和appium   server在同一台机器上  
    + 也可以在PC上，执行指令会被发送到远端的appium server，执行时在appium server上



我是广告
---
推荐个最近在用的云服务提供商，质优价廉。好\*梯\*子，每月2.5刀，500G流量，网速够快。  
[点我！vultr](https://www.vultr.com/?ref=7159348)  
[![vultr](https://www.vultr.com/media/banner_1.png)](https://www.vultr.com/?ref=7159348)
