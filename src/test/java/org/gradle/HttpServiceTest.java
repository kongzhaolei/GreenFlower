package org.gradle;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.client.HttpServiceNewClient;
import org.gradle.needle.util.ExcelDataUtils;
import org.gradle.needle.util.HttpReqGen;
import org.gradle.needle.util.HttpResVer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HttpServiceTest {
	// 定义脚本test0要执行的测试集
	String testset0 = "Statistic";
	//String testset0 = "Query";
	private static Logger logger = Logger.getLogger(HttpServiceTest.class
			.getName());

	@BeforeClass
	public static void init() throws Exception {
		logger.info("-----------------------所有接口请求开始-------------------------"
				+ "\r\n");
	}

	@AfterClass
	public static void teardown() {
		logger.info("-----------------------所有接口请求结束-------------------------");
		// 清理外部接口数据
		// HttpResVer.teardown(testid, "Output");
		// HttpResVer.teardown(testid, "Comparision");
	}

	@Test(priority = 0, dataProvider = "test0", enabled = true)
	public void test0(String testid, String call_type, String url,
			Map<String, String> header, Map<String, String> body) {

      //String response = HttpServiceClient.invokeServiceMethod(call_type, url,header, body);
	    String response = HttpServiceNewClient.invokeServiceMethod(call_type,url,header,body);
		logger.info("接口测试用例 " + testid + "的返回结果:" + "\r\n");
		logger.info(response + "\r\n");

		try {
			HttpResVer.saveResponse("Output", testid, response);
			logger.info("接口测试用例 " + testid + "的返回结果已写入Output" + "\r\n");

			HttpResVer.ParserAndCompare(testid, "Baseline", response,"Comparision");
			logger.info("接口测试用例 " + testid + "的对比结果已写入Comparision" + "\r\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@DataProvider
	public Iterator<Object[]> test0() throws Exception {
		ExcelDataUtils.setExcelWorkSheet("Input");
		Iterator<Map<String, String>> datamap = ExcelDataUtils
				.getRowDataMap(testset0);
		Iterator<Object[]> requestfiles = HttpReqGen.preReqGen(datamap);
		return (requestfiles);
	}
}
