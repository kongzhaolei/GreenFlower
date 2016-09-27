/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.gradle.needle.selenium;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.gradle.needle.dao.GlobalSettings;
import org.gradle.needle.dao.UIMapSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Wait;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;

public class BrowserFactory {

	RemoteWebDriver browserCore;
	WebDriverBackedSelenium browser;
	ChromeDriverService chromeServer;
	JavascriptExecutor JSExecutor;

	int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
	int timeout = Integer.parseInt(GlobalSettings.timeout);
	String projectUrl;

	private static Logger logger = Logger.getLogger(BrowserFactory.class
			.getName());

	public BrowserFactory() {
		setupBrowserCoreType(GlobalSettings.browserCoreType);
		JSExecutor = (JavascriptExecutor) browserCore;
		logger.info("启动浏览器");
	}

	// 选择浏览器
	private void setupBrowserCoreType(int type) {
		if (type == 1) {
			browserCore = new FirefoxDriver();
			logger.info("使用 Firefox浏览器");
			return;
		}
		if (type == 2) {
			chromeServer = new ChromeDriverService.Builder()
					.usingDriverExecutable(
							new File(GlobalSettings.chromeDriverPath))
					.usingAnyFreePort().build();
			try {
				chromeServer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches",
					Arrays.asList("--start-maximized"));
			browserCore = new RemoteWebDriver(chromeServer.getUrl(),
					capabilities);
			logger.info("使用Chrome浏览器");
			return;
		}
		if (type == 3) {
			System.setProperty("webdriver.ie.driver",
					GlobalSettings.ieDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities
					.internetExplorer();
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							true);
			browserCore = new InternetExplorerDriver(capabilities);
			logger.info("使用 IE 浏览器");
			return;
		}
		if (type == 4) {
			browserCore = new SafariDriver();
			logger.info("使用 Safari 浏览器");
			return;
		}

		Assert.fail("Incorrect browser type");
	}

	public RemoteWebDriver getBrowserCore() {
		return browserCore;
	}

	public WebDriverBackedSelenium getBrowser() {
		return browser;
	}

	public JavascriptExecutor getJavaScriptExecutor() {
		return JSExecutor;
	}
	

	// 加载测试页面
	public void open(String projectSName){
		projectUrl = GlobalSettings.getProperties().getProperty(projectSName);
		browser = new WebDriverBackedSelenium(browserCore, projectUrl);
		pause(stepInterval);
		try {
			browser.open(projectUrl);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("加载页面失败" + projectUrl);
		}
		logger.info("加载页面 " + projectUrl);
	}

	// 退出浏览器
	public void quit() {
		pause(stepInterval);
		browserCore.quit();
		if (GlobalSettings.browserCoreType == 2) {
			chromeServer.stop();
		}
		logger.info("退出浏览器");
	}

	// 单击页面元素
	public void click(String xpath) {
		xpath = UIMapSetting.getUIMap().getProperty(xpath);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickcheck(xpath, System.currentTimeMillis(), 2500);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("单击失败 " + xpath);
		}
		logger.info("单击 " + xpath);
	}

	// 检查页面元素是否存在
	public void expectElementExistOrNot(boolean expectExist,
			final String xpath, int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isElementPresent(xpath, -1);
					}
				}.wait("查找页面元素超时 " + xpath, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("无法找到页面元素 " + xpath);
			}
			logger.info("页面元素已找到 " + xpath);
		}
	}

	public boolean isElementPresent(String xpath, int time) {
		pause(time);
		boolean isPresent = browser.isElementPresent(xpath)
				&& browserCore.findElementByXPath(xpath).isDisplayed();
		if (isPresent) {
			return true;
		} else {
			return false;
		}
	}

	// 查找文本是否存在
	public boolean isTextPresent(String text, int time) {
		pause(time);
		boolean isPresent = browser.isTextPresent(text);
		if (isPresent) {
			logger.info("Found text " + text);
			return true;
		} else {
			logger.info("Not found text " + text);
			return false;
		}
	}

	// 判断页面元素是否可以单击，控制超时
	private void clickcheck(String xpath, long startTime, int timeout)
			throws Exception {
		try {
			browserCore.findElementByXPath(xpath).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.info("Element " + xpath + " 不可单击");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.info("Element " + xpath + " 不可单击, 重新尝试");
				clickcheck(xpath, startTime, timeout);
			}
		}
	}

	// 页面录入文本
	public void type(String xpath, String text) {
		xpath = UIMapSetting.getUIMap().getProperty(xpath);
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		WebElement we = browserCore.findElement(By.xpath(xpath));
		try {
			we.clear();
		} catch (Exception e) {
			logger.warn("清空失败 " + xpath);
		}
		try {
			we.sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("录入失败 " + text + " at " + xpath);
		}

		logger.info(" 在" + xpath + "录入 " + text);
	}

	// 显示层级菜单并等待
	public void MenuListDisplay(String menuxpath, String timewait) {
		final String menuxpath_p = UIMapSetting.getUIMap().getProperty(
				menuxpath);
		timewait = UIMapSetting.getUIMap().getProperty(timewait);
		(new WebDriverWait(browserCore, Integer.parseInt(timewait)))
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						return d.findElement(By.xpath(menuxpath_p))
								.isDisplayed();
					}
				});
	}

	// 鼠标指向页面元素
	public void mouseOver(String xpath) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.mouseMove(0, 0);

		WebElement we = browserCore.findElement(By.xpath(xpath));
		if (GlobalSettings.browserCoreType == 2) {
			try {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("鼠标指向失败 " + xpath);
			}

			logger.info("鼠标指向 " + xpath);
			return;
		}

		if (GlobalSettings.browserCoreType == 1
				|| GlobalSettings.browserCoreType == 3) {
			for (int i = 0; i < 5; i++) {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			}
			logger.info("鼠标指向  " + xpath);
			return;
		}

		if (GlobalSettings.browserCoreType == 4) {
			Assert.fail("不支持Safari");
		}
		Assert.fail("请重新选择浏览器");
	}

	// 选择浏览器窗口
	public void selectmainWindow() {
		pause(stepInterval);
		// browser.selectWindow(windowTitle);
		// 当最后只有一个窗口剩下的时候，应该可以直接使用
		browserCore.switchTo().window(browserCore.getWindowHandle());
		logger.info("切换窗口至 " + browserCore.getTitle());
	}

	/**
	 * 通过循环比较所有窗口的handle 排除其他窗口，定位到当前弹出窗口
	 */
	public void selectWindow() {
		pause(stepInterval);
		String handle_0 = browserCore.getWindowHandle();
		String Title_0 = browserCore.getTitle();
		for (String handler : browserCore.getWindowHandles()) {
			if (handler.equals(handle_0))
				continue;
			browserCore.switchTo().window(handler);
			// System.out.println(browserCore.getTitle());
			logger.info(Title_0 + "  切换窗口至  " + browserCore.getTitle());
		}
	}
    
	/**
	 * switchFrame()方法是
	 * 在多个iframe反复切换，不再需要回到最外层frame
	 
	
	//
	 private List<String> asList(String path){
	     List<String> list = new ArrayList<String>();
	     path.split("->").each{list.add(it)};
	     return list;
	  }
	 
	 private void switchFrame(String framePath) {
		 browserCore.switchTo().defaultContent();
	     asList(framePath).each { 
	    	 browserCore.switchTo().frame(framePath);
	     }
	 }
*/
	
	// 通过ID定位iframe
	public void enterFrameid(String Id) {
		Id = UIMapSetting.getUIMap().getProperty(Id);
		pause(stepInterval);
		browserCore.switchTo().frame(browserCore.findElementById(Id));
		logger.info("进入 iframe " + Id);
	}

	// 通过xpath定位iframe
	public void enterFramexpath(String xpath) {
		xpath = UIMapSetting.getUIMap().getProperty(xpath);
		pause(stepInterval);
		browserCore.switchTo().frame(browserCore.findElementByXPath(xpath));
		logger.info("进入 iframe " + xpath);
	}

	// 跳出iframe
	public void leaveFrame() {
		pause(stepInterval);
		browserCore.switchTo().defaultContent();
		logger.info("跳出iframe");
	}

	// 刷新浏览器
	public void refresh() {
		pause(stepInterval);
		browserCore.navigate().refresh();
		logger.info("已刷新");
	}

	// 键盘事件
	public void pressKeyboard(int keyCode) {
		pause(stepInterval);
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.keyPress(keyCode);
		rb.delay(100);
		rb.keyRelease(keyCode);
		logger.info("Pressed key with code " + keyCode);
	}

	// 键盘输入文本
	public void inputKeyboard(String text) {
		String cmd = System.getProperty("user.dir")
				+ "\\res\\SeleniumCommand.exe" + " sendKeys " + text;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		logger.info("Pressed key with string " + text);
	}

	// 线程等待
	public void pause(int time) {
		if (time <= 0) {
			return;
		}
		try {
			Thread.sleep(time);
			logger.info("等待 " + time + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 异常处理
	private void handleFailure(String notice) {
		String png = CaptureScreenshot.screenShot(this);
		String log = notice + "  已经保存失败截图 " + png;
		logger.error(log);
		if (projectUrl.lastIndexOf("/") == projectUrl.length()) {
			projectUrl = projectUrl.substring(0, projectUrl.length() - 1);
		}
		Reporter.log(log + "<br/><img src=\"" + projectUrl + "/" + png
				+ "\" />");
		Assert.fail(log);
	}

	// 获取文本
	public String getText(String xpath) {
		WebElement element = this.getBrowserCore().findElement(By.xpath(xpath));
		return element.getText();
	}

	// 选择标签
	public void select(String xpath, String option) {
		WebElement element = this.browserCore.findElement(By.xpath(xpath));
		Select select = new Select(element);
		select.selectByVisibleText(option);
	}

	// DOM 处理时间控件或模态窗口,赋值
	public void TypeJsElement(String value, String id) {
		id = UIMapSetting.getUIMap().getProperty(id);
		WebElement DatePicker = browserCore.findElement(By.id(id));
		JSExecutor.executeScript(
				"arguments[0].value=arguments[1]", DatePicker, value);
		logger.info("在 " + id + "  录入  " + value);
	};
	

	
	/**
	 * 判断页面是否加载了jquery库
	 * 加载jquery库并初始化
	 * 利用jquery定位页面元素
	 */
	
	//初始化jQuery库
	public void injectjQueryCheck() {
		if (!jQueryLoaded()){
		    System.out.println("Jquery未加载");
		    injectjQuery(); 
		}
		else
			System.out.println("Jquery已加载");
	}
    
	//加载Jquery
	private void injectjQuery() {
	   try{
		   JSExecutor.executeScript("var headID = document.getElementsByTagName(\"head\")[0];" 
				   + "var newScript = document.createElement('script');" 
				   + "newScript.type = 'text/javascript';" 
				   + "newScript.src = '/greenflower/src/test/resources/jquery-2.1.1.js';" 
				   + "headID.appendChild(newScript);"); 
		   System.out.println("Jquery 加载成功"); 
		   Thread.sleep(1000);
	   }catch(InterruptedException e){
		   e.printStackTrace(); 
	   }
	}
    
	//判断是否加载了jQuery库
	private boolean jQueryLoaded() {
		Boolean loaded;
		String s = (String)(JSExecutor.executeScript("return typeof jQuery")); 
		 if(s.equals("function"))
			 loaded=true;
		 else 
			 loaded=false;
		 return loaded;
	}
	
	//利用jQuery定位
	public WebElement LocatorWithjQuery(String selector){ 
		injectjQueryCheck();
		String js="return jQuery(\""+selector+"\")[0]";
		WebElement ele = (WebElement)(JSExecutor).executeScript(js);
		return ele;  
	}
}
