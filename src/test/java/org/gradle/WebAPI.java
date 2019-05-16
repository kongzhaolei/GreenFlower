package org.gradle;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.client.HttpClientFactory;
import org.gradle.needle.engine.HttpReqGen;
import org.gradle.needle.util.ExcelUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class WebAPI {

	String testset0 = "main";
	private static Logger logger = Logger.getLogger(WebAPI.class.getName());

	@BeforeClass
	public static void init() throws Exception {
		logger.info("-----------------------所有接口请求开始-------------------------" + "\r\n");
	}

	@AfterClass
	public static void teardown() {
		logger.info("-----------------------所有接口请求结束-------------------------");
		// 清理外部接口数据
		// HttpResVer.teardown(testid, "Output");
		// HttpResVer.teardown(testid, "Comparision");
	}

	@Test(priority = 0, dataProvider = "test0", enabled = true)
	public void test0(String testid, String call_type, String url, Map<String, String> header,
			Map<String, String> body) {
		String response = HttpClientFactory.invokeServiceMethod(call_type, url, header, body);
		logger.info(response);
		try {
			ExcelUtils.saveResponse("Output", testid, response);
			//ExcelUtils.ParserAndCompare(testid, "Baseline", response, "Comparision");
			//assertThat(response).containsSequence("{","ModelData","140802016");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@DataProvider
	public Iterator<Object[]> test0() throws Exception {
		String filePath = System.getProperty("user.dir") + "/src/main/resources/webapi.xls";
		ExcelUtils edu = new ExcelUtils(filePath);
		ExcelUtils.setWorkSheet("Input");
		Iterator<Map<String, String>> datamap = edu.getCaseSet(testset0);
		Iterator<Object[]> requestfiles = HttpReqGen.preReqGen(datamap);
		return (requestfiles);
	}
}
