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
	// ����ű�test0Ҫִ�еĲ��Լ�
	String testset0 = "Statistic";
	//String testset0 = "Query";
	private static Logger logger = Logger.getLogger(HttpServiceTest.class
			.getName());

	@BeforeClass
	public static void init() throws Exception {
		logger.info("-----------------------���нӿ�����ʼ-------------------------"
				+ "\r\n");
	}

	@AfterClass
	public static void teardown() {
		logger.info("-----------------------���нӿ��������-------------------------");
		// �����ⲿ�ӿ�����
		// HttpResVer.teardown(testid, "Output");
		// HttpResVer.teardown(testid, "Comparision");
	}

	@Test(priority = 0, dataProvider = "test0", enabled = true)
	public void test0(String testid, String call_type, String url,
			Map<String, String> header, Map<String, String> body) {

      //String response = HttpServiceClient.invokeServiceMethod(call_type, url,header, body);
	    String response = HttpServiceNewClient.invokeServiceMethod(call_type,url,header,body);
		logger.info("�ӿڲ������� " + testid + "�ķ��ؽ��:" + "\r\n");
		logger.info(response + "\r\n");

		try {
			HttpResVer.saveResponse("Output", testid, response);
			logger.info("�ӿڲ������� " + testid + "�ķ��ؽ����д��Output" + "\r\n");

			HttpResVer.ParserAndCompare(testid, "Baseline", response,"Comparision");
			logger.info("�ӿڲ������� " + testid + "�ĶԱȽ����д��Comparision" + "\r\n\n");
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
