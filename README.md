# Web And App UI Testing

简述
---
+ 这是一个适用于Web/Android/iOS/H5的UI自动化项目，以及支持Android性能测试。项目底层使用了Selenium/Appium。  
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
│                  └─quanql
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
│          │      └─quanql
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
            │      └─quanql
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


用例编写步骤
---
### 1、iPhone UI

#### 编译被测应用
1. 前提：保证xcode已经可以编译通过被测app，且在模拟器上验证基本功能正常
2. 更新代码
3. 编译app  
> cd 工程目录  
> xcodebuild -workspace quanqlAPP.xcworkspace -scheme quanql -configuration Debug -sdk iphonesimulator -arch x86_64  

4. 生成压缩包，其中**quanql.zip就是被测包**
> ditto -ck --sequesterRsrc --keepParent \`ls -1 -d -t ~/Library/Developer/Xcode/DerivedData/\*/Build/Products/Debug-iphonesimulator/*.app | head -n 1\`  ~/quanql.zip  


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

两种用例编写方式
---
我将参与UI自动化用例编写的人员分为两类，（当然，也可能有的团队不会分的这么细，一个人齐活）
1. 第一类：编写底层/基础功能的人员，他们可能负责编写base中公共方法、识别和封装元素，这类人可以不了解业务，但对编程技能要求高；
2. 第二类：业务用例编写人员，将页面、组件按照一定的业务要求进行整合，使之符合一定的story，完成业务测试向自动化的转化，这类人要求熟悉业务，对编程技能要求不高。而他们使用的页面和元素就是第一类人员提供的。

工程中提供两了种用例编写的方式，第一种对用例编写人员更友好、技术要求不高、代码可读性强。而我个人更习惯第二种，因为它对复杂控件和场景支持更好。

### 方式一

例如，`./uitest_iphone/src/main/java/com/quanql/test/iphoneui/page/LoginPage.java`和  
`./uitest_android/src/main/java/com/quanql/test/androidui/testcase/DemoLoginTest.java`

封装登录page
```java
/**
 * 说明：
 * 注意这里只需要识别页面元素。
 *
 * 这是和另一个方式的第一处区别。
 */
public class LoginPage extends IphoneBasePage {

	public static Edit UserName = new Edit("xpath=//UIATextField[contains(@value,'请输入手机号码')]");
	public static Edit PassWord = new Edit("xpath=//UIASecureTextField");
	public static Button LoginBtn = new Button("xpath=//UIAButton[@name='立即登录']");
	public static Text LoginFailAlert = new Text("xpath=//UIAAlert[@name='提示']//UIAStaticText[@name='用户名或密码错误']");
	public static Button AlertOKBtn = new Button("xpath=//UIAAlert[@name='提示']//UIAButton[@name='确定']");

}
```

“登录”测试用例（使用登录page）
```java
/**
 * 说明：
 * 1. 先指定需要的元素，比如，LoginPage.UserName
 * 2. 再对元素进行操作，比如，LoginPage.UserName.sendkeys("111111") 输入内容，
 * sendkeys()是公共方法，并不是在page封装时提供的。
 * 但是这里需要注意！用例编写人员也可以对这个编辑框进行.click()，而另一种编写方式是不允许这样做的。
 *
 * 这是和另一个方式的第二处区别。
 */
public class DemoLoginTest extends IphoneBaseTest {
	
	@Test
	public void testLogin() {
		TabHomePage.wait(20000);
		TabHomePage.TabMine.click();
		TabMyPage.LoginNowBtn.click();
		LoginPage.UserName.sendkeys("111111");
		LoginPage.PassWord.sendkeys("2222");
		LoginPage.LoginBtn.click();
		AssertUtil.assertTrue(LoginPage.LoginFailAlert.isDisplayed(), "期望登录失败提示框出现");
	}
}
```

### 方式二

例如，`./uitest_web/src/main/java/com/quanql/test/webui/testcase/Demo163Test.java`

封装登录page
```java
/**
 * 说明：
 * 注意这里不仅需要识别页面元素，还需要完成元素的事件封装，比如点击按钮。
 */
public class DemoBaiduPage extends WebBasePage {
	private static String edtSearchId = "kw";
	private static String btnSearchXpath = "//input[@value='百度一下']";
	private static String txtNeteaseXpath = "//div[@id='content_left']/div//a[contains(.,'www.163.com')]";
	/**
	 * 通过url打开登录页面
	 */
	public static void openBaidu() {
		LogUtil.info("通过url打开百度首页");
		baseOpt.open("https://www.baidu.com");
	}
	
	/**
	 * 搜索框输入内容
	 * @param name
	 */
	public static void typeInSearchEdt(String name) {
		LogUtil.info("输入搜索内容:" + name);
		baseOpt.sendkeys(By.id(edtSearchId), name);
	}
	
	/**
	 * 点击搜索按钮
	 */
	public static void clickSearchBtn() {
		LogUtil.info("点击搜索按钮");
		baseOpt.click(By.xpath(btnSearchXpath));
	}
	
	public static boolean isNeteaseExisted() {
		LogUtil.info("检查163官网是否存在");
		return baseOpt.isElementDisplayed(By.xpath(txtNeteaseXpath));
	}
}
```

“登录”测试用例（使用登录page）
```java
/**
 * 说明：
 * 只能调用page分装中提供的方法，限制了用例编写人员随意的对控件使用不合适的操作。
 */
public class Demo163Test extends WebBaseTest {

	@Test
	public void testSearchNetease() {
		DemoBaiduPage.openBaidu();
		DemoBaiduPage.typeInSearchEdt("网易");
		DemoBaiduPage.clickSearchBtn();
		AssertUtil.assertTrue(DemoBaiduPage.isNeteaseExisted(), "网易没有出现在结果第1位！");
	}
	
}
```


其他
---
+ [chrome driver下载地址](https://sites.google.com/a/chromium.org/chromedriver/downloads)

