package org.gradle;

import java.io.File;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.gradle.needle.mapper.GlobalSettings;
import org.gradle.needle.selenium.BrowserFactory;
import org.gradle.needle.util.ExcelDataUtils;
import org.gradle.needle.util.DBUtils;
import org.gradle.needle.util.XMLParser;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class SeleniumDemo {
	static BrowserFactory bf;
	private static String XmlDataFile1 = GlobalSettings.XmlDataFile1;
	static String sql = "SELECT * FROM RLYW_RuChangMeiCY_zb WHERE YICIBIANMA IN ('140807251')";
	static File xmlfile = new File("//10.31.0.2/test/1.xml");

	@BeforeTest
	@Given("登录系统")
	public static void loginCicssystem() {
		// web页面操作
		bf = new BrowserFactory();
		bf.open("FP");
		bf.click("SY");
		bf.type("YHID", "admin");
		bf.type("YHSN", "123456");
		//bf.click("oBtn1");
		bf.LocatorWithjQuery(".login_right").click();
		bf.pause(6000);
		Reporter.log("CICS系统登录成功 | ");
		bf.click("JLGLXT");
		bf.MenuListDisplay("MenuList", "timewait");
		//bf.click("HCRCJL");
		bf.LocatorWithjQuery("[title|='火车入厂记录']").click();
		bf.pause(1000);
		bf.enterFramexpath("iframe1");
		bf.enterFrameid("iframe2");
		bf.enterFrameid("iframe3");
		bf.click("addbutton");
		//bf.LocatorWithjQuery(".ToolbarButton").click();
		bf.leaveFrame();
		bf.enterFramexpath("iframe1");
		bf.enterFrameid("iframe4");
		bf.click("IMPOPEN");
		bf.type("BEIZHU", "啦啦啦啦啦啦啦啦");
		bf.type("ADDSHULIANG","1");
		bf.click("ADDROWBUTTON");
	}
	
	@AfterTest
	@Then(value = "退出登录")
	public static void tearDown() {
		bf.click("SAVEBUTTON");
	    bf.click("RETURNBUTTON");
		bf.leaveFrame();
		bf.quit();
	}

	@Test(priority = 0, dataProvider = "HCRCJLDATA")
	@When("新增数据")
	public static void addTrainrecord(String chehao,
			String piaozhong, String fahuoriqi, String kuangdian) {
		bf.type("CHEHAO", chehao);
		bf.type("PIAOZHONG", piaozhong);
		bf.TypeJsElement(fahuoriqi, "FAHUORIQI");
		bf.TypeJsElement(kuangdian, "KUANGDIAN");
	}
	
	@DataProvider
	public Iterator<Object[]> HCRCJLDATA() throws Exception {
		ExcelDataUtils.setExcelWorkSheet("sheet1");
		String sTestCaseName = this.toString();
		Iterator<Object[]> testObjSet = ExcelDataUtils
				.getRowDataSet(sTestCaseName);
		return (testObjSet);
	}

	@Test(priority = 1, enabled = false)
	public static void SendXMLFile() {
		// 发送XML文件
		XMLParser.SendXMLFile(XmlDataFile1);
	}

/*	@Test(priority = 2, enabled = false)
	public static void DataBaseAssert() {
		// 数据库验证
		DBUtils oa = new DBUtils(");
		oa.DataAssert(sql, "ISCAIYANG", "1");
		oa.ConnClose();
	}
*/
	@Test(priority = 3, enabled = false)
	public static void DealWithExcelFile() {
		// 读取EXCEL数据
		try {
			ExcelDataUtils.setExcelWorkSheet("sheet1");
			ExcelDataUtils.getCellData(2, 1);
			ExcelDataUtils.setCellData("kongzhaolei", 2, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
