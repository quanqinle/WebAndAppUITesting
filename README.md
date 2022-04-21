# UI Automated Testing Framework for Web and App

[简体中文](./README.cn.md) | English

> Selenium 4 has come. I am modifying this project to fix with it. And almost done.
> If you use the old Selenium 3 still, go to the branch `backup-Selenium3`.

---

# Project Brief

+ This is a **UI test automation framework for Web/Android/iOS/H5**, and supports Android performance testing. The core
  of the project uses Selenium/Appium.
+ The project uses Maven for project management and TestNG for test case management.
+ The common Selenium APIs are encapsulated, such as `click()`, `type()`, etc. And they are added element availability
  verification, log recording, and taking screenshot for failed tests.
+ WebUI, AndroidUI and iOSUI are separate modules. According to OS features, special base class methods have been
  written,
  and the configuration file `config.properties` under the module root directory will not affect each other.
+ A testcase can test online or offline environment at the same time by different tests data driven.
+ Log and assertion have been encapsulated into LogUtil and AssertUtil, which is easy to replace or extend in the
  future.
+ WebDriverManager carries out the management (i.e., download, setup, and maintenance) of the web drivers in a fully
  automated manner.
+ Android: If `mapping.txt` the resource obfuscation file is provided, the framework can automatically replace the
  original element id with the obfuscated id, and then the positioning elements work fine.

# Environmental preparation

## Requirements

1. JDK >= 8 (The minimum Java version required is still 8.)
2. Maven >= 3
3. Selenium 4
4. Appium (java-client 8)
5. TestNG 6 -- TODO: to replace testNg with Junit
6. IDE (Eclipse or others, this tutorial takes Eclipse as an example) + TestNG plugin + Maven plugin

## Android

1. Install `Android SDK`  
   1.1 Configure environment variables  
   1.2 Verify `adb` in the console whether it run ok or not  
   1.3 If you are about to run the test cases on emulators, you need to create emulators using `AVD Manager`
2. Install the Appium server program, see [http://appium.io/](http://appium.io/). Recommend to install it in the way
   of `Appium Desktop`.

## iPhone

1. **macOS is required!**  
   1.1 Appium Server's operation, element recognition, and script execution all require the macOS  
   1.2 Install Xcode, verify the installation until the tested app can be compiled successfully and can be run in a
   simulator
2. If you want to run test case on a device, follow the
   manual [Deploying an iOS app to a real device](http://appium.io/slate/en/master/)
3. Install the Appium server program, after installation, you will find that it has more support for iOS testing than
   the Windows version
   ![Appium Server Mac](/docs/images/appium-server-mac.png)

# Project Structure

```
├─perftest_android <-- Android performance test module
│  ├─perfreport    <-- Performance test result
│  └─src           <-- test case
├─uitest_android   <-- Android ui test module
│  ├─app           <-- Store tested `apk` and `mapping` files
│  ├─testdata      <-- test data，csv format
│  │  ├─online     <-- test data for online
│  │  └─test       <-- test data for test
│  ├─screenshot    <-- Failure screenshot
│  └─src
│      └─main
│          └─java
│              └─com.quanql.test
│                  ├─androidui
│                  │   ├─base     <-- Android-specific base class, extend from the base of uitest_core
│                  │   ├─page     <-- page object
│                  │   └─testcase <-- test case
│                  └─perfutils    <-- Android performance test util
├─uitest_core
│  └─src
│      └─main
│          ├─java
│          │  └─com.quanql.test
│          │      └─core
│          │         ├─base 
│          │         │   ├─BaseOpt.java       <-- base class. such as click, type, findelement,screenShot etc.
│          │         │   ├─BasePage.java      <-- page object base class
│          │         │   ├─BaseTest.java      <-- test case base class. manage TestNG life cycle
│          │         │   └─DriverFactory.java <-- driver factory, web and android and other drivers are here
│          │         ├─listener 
│          │         └─utils
│          └─resources  <-- slf4j config file is here
└─uitest_web
    └─src
        └─main
            ├─java
            │  └─com.quanql.test
            │      └─webui
            │          ├─base     <-- web-specific base class, extend from the base of uitest_core
            │          ├─page
            │          └─testcase
            └─resources   <-- chromedriver is here
```

# Architecture Diagram

![proj frame](/docs/images/project-frame.png)

# Write test cases step by step

## 1. Web UI

### Find Elements

1. Open browser, i.e. Chrome, open the url
2. Click F12 to open up the `DevTools` window, find elements by it. Relative cssSelector expression is recommended, such
   as, `button[aria-label='Google Search']`

### Write test cases

+ page demo: See more details in `DemoBaiduPage.java` of uitest_web
+ case demo: See more details in `Demo163Test.java` of uitest_web

### Run test cases

1. configure `config.properties`
    + driver.type=chrome, firefox, iOSSafari
    + running.type=local (Standalone mode)、remote (hub-node mode)
    + remote.address=the hub server url, only used as `running.type=remote`

## 2. iPhone UI

### Compile the application under test

1. Premise: Ensure that xcode can compile the app under test successfully, and verify the app's basic functions in the
   simulator
2. Update the code
3. Compile the app

```
> cd $APP_PATH

> xcodebuild -workspace quanqlAPP.xcworkspace -scheme quanql -configuration Debug -sdk iphonesimulator -arch x86_64  
```

4. Generate compressed package, where **quanql.zip is the app under test**

```
> ditto -ck --sequesterRsrc --keepParent \`ls -1 -d -t ~/Library/Developer/Xcode/DerivedData/\*/Build/Products/Debug-iphonesimulator/*.app | head -n 1\`  ~/quanql.zip  
```

### Find Elements

1. On the `iOS settings` page of appium server, set `App Path` to the directory of the zip generated in the previous
   step, then set `Force Device` and `Platform Version`
2. Click `launch`
3. Click `inspector`. The operation of finding element is similar with Android
4. Relative XPath expression is recommended, such
   as, `Button("xpath=//UIAAlert[@name='prompt']//UIAButton[@name='OK']")`

### Write test cases

+ page demo: See more details in `LoginPage.java` of uitest_iphone
+ case demo: See more details in `DemoLoginTest.java` of uitest_iphone

### Run test cases

1. configure `config.properties`
    + remote.address=the path of appium server
    + app=the absolute path of appium server
2. appium client
    + on Mac, that means you can run client and appium server on the same Mac
    + OR, on PC, the client will send commands to the remote appium server, and then the cases are still run on appium
      server

## 3. Android UI

Similar with **iPhone**.

## 4. H5

Similar with **web**. In configure `config.properties`, set `driver.type=H5` so as to run test cases on mobile emulation
mode in Chrome.

# Two ways to write test cases

I think the people involved in writing UI automation can be divided into two groups (although there may be teams that
don't have two groups, all work together) :

1. The first group: the people who write the underlying or basic functions. They may be responsible for writing the
   common methods, identification and encapsulation elements in base. People in this group may not understand the
   business, but require high programming skills;
2. The second group: the people who write business test cases. They integrate pages and components according to business
   requirements to make them conform to a certain user story, and complete the transformation from manual business tests
   to automation. People in this group may be required to understand business, but need few programming skills. In
   addition, the pages and elements they use are provided by the first group of people.

The following describes the two ways provided in this project showing how to code. The first is more business-friendly,
less technical, and more readable. However, personally, I'm more comfortable with the second one because it supports
complex controls and scenarios better.

## The first way

More details in `./uitest_iphone/src/main/java/com/quanql/test/iphoneui/page/LoginPage.java` and
`./uitest_android/src/main/java/com/quanql/test/androidui/testcase/DemoLoginTest.java`

The Login page

```java
/**
 * Note that there are nothing in this class except elements location.
 *
 * This is the FIRST difference from the other way.
 */
public class LoginPage extends IphoneBasePage {

    public static Edit UserName = new Edit("xpath=//UIATextField[contains(@value,'type the phone number')]");
    public static Edit PassWord = new Edit("xpath=//UIASecureTextField");
    public static Button LoginBtn = new Button("xpath=//UIAButton[@name='Log in']");
    public static Text LoginFailAlert = new Text("xpath=//UIAAlert[@name='prompt']//UIAStaticText[@name='incorrect username or password']");
    public static Button AlertOKBtn = new Button("xpath=//UIAAlert[@name='prompt']//UIAButton[@name='OK']");

}
```

The Log in test case (using Log in page)

```java
/**
 * Explanation:
 * 1. firstly locate certain element, e.g. LoginPage.UserName
 * 2. secondly operate the element, e.g. LoginPage.UserName.sendkeys("111111"), type into something
 *
 * This is the SECOND difference from the other way.
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
        AssertUtil.assertTrue(LoginPage.LoginFailAlert.isDisplayed(), "login failure dialog expects");
    }
}
```

## The second way

More details in `./uitest_web/src/main/java/com/quanql/test/webui/testcase/Demo163Test.java`

Log in page

```java
/**
 * Note that not only elements' location but also elements' operation are in this class.
 */
public class DemoBaiduPage extends WebBasePage {
    private static String edtSearchId = "kw";
    private static String btnSearchXpath = "//input[@value='百度一下']";
    private static String txtNetEaseXpath = "//div[@id='content_left']/div//a[contains(.,'www.163.com')]";

    /**
     * open Baidu
     */
    public static void openBaidu() {
        LogUtil.info("open Baidu homepage by the url");
        baseOpt.open("https://www.baidu.com");
    }

    /**
     * type in search bar
     * @param name -
     */
    public static void typeInSearchEdt(String name) {
        LogUtil.info("Enter search content:" + name);
        baseOpt.sendkeys(By.id(edtSearchId), name);
    }

    /**
     * click search button
     */
    public static void clickSearchBtn() {
        LogUtil.info("click search button");
        baseOpt.click(By.xpath(btnSearchXpath));
    }

    public static boolean isNetEaseExisted() {
        LogUtil.info("verify the goal page is open");
        return baseOpt.isElementDisplayed(By.xpath(txtNetEaseXpath));
    }
}
```

The Log in test case (using Log in page)

```java
/**
 * Only the methods provided in the Page class can be called.
 * It restricts the developer from writing bad code because the elements are used incorrectly.
 */
public class Demo163Test extends WebBaseTest {

    @Test
    public void testSearchNetEase() {
        DemoBaiduPage.openBaidu();
        DemoBaiduPage.typeInSearchEdt("NetEase");
        DemoBaiduPage.clickSearchBtn();
        AssertUtil.assertTrue(DemoBaiduPage.isNetEaseExisted(), "NetEase isn't in the first place of the results！");
    }

}
```

# Data-Driven

See the example first

## Data-Driven example

### test case `NewUserRegisterTest.java`

```Java
/**
 * register
 */
public class NewUserRegisterTest extends AndroidBaseTest {

    @Test(dataProvider = "providerMethod")
    public void testNewUserRegister(String telephone, String passwd) {
        TabHomePage.TabMine.click();
        TabMyPage.MyRegister.click();
        RegisterPage.TelPhoneNum.sendkeys(telephone);
        RegisterPage.SetPassWord.sendkeys(passwd);
        RegisterPage.NowRegisterBtn.click();
        AssertUtil.assertTrue(RegisterPage.RegisterSucess.isDisplayed(), "fail to register");
    }
}
```

### config file `config.properties`

```
## Running environment: online, test
## It determines which data to use, and the parameter name is also the directory where the data is stored
running.env=test
```

### test data `NewPersonRegister.csv`

+ `testdata` and `src` at the same level
+ only .csv format supports currently

```
├─uitest_android
   ├─src 
   └─testdata  
      ├─online     <-- Online data
      │  └─ NewPersonRegister.csv   
      └─test       <-- Offline data
         └─ NewPersonRegister.csv
```

Use text editor to open `NewPersonRegister.csv`, you will see like the following, or you can open it in Excel:

```
telephone,passwd
18888888888,pwd123
```

## Data-Driven more detail

1. running.env in `config.properties` determines the data directory
2. the test method name annotated by `@Test` (i.e. testNewUserRegister in the above example) determines the data-driven
   filename NewPersonRegister.csv
    - the test method name **must start with test, but _ is optional**, e.g. testHelloWorld_01()
    - combined with the path in `1.`, the full file directory in this example
      is `submodule_name/testdata/test/NewPersonRegister.csv`
3. test case
    - `@Test` followed by `(dataProvider = "providerMethod")`
    - the number and sort of parameters in testNewUserRegister() are same with the stuff in the csv file, however it's
      not necessary to keep parameter name and cvs header the same

# Advertising

I have a video courses series on how to use Selenium for Automation testing. if you're interested, click
here [网易云课堂](http://study.163.com/course/introduction/1004926010.htm).

> Reminder: the course is in Mandarin only.

# Others

## Download chrome driver

+ [Chrome driver](https://sites.google.com/a/chromium.org/chromedriver/downloads)
