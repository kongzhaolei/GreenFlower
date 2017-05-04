package org.gradle.needle.verify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.dto.GlobalSettings;
import org.gradle.needle.util.ExcelDataUtils;

public class HttpResVer {
	private static Logger logger = Logger.getLogger(HttpResVer.class.getName());

	// ���弸���г������������ݶ�ȡ��д��
	static int responsecol = Integer.parseInt(GlobalSettings.getProperty("response"));
	static int expectcol = Integer.parseInt(GlobalSettings.getProperty("expect"));
	static int E_keycol = Integer.parseInt(GlobalSettings.getProperty("E_key"));
	static int E_valuecol = Integer.parseInt(GlobalSettings.getProperty("E_value"));
	static int A_keycol = Integer.parseInt(GlobalSettings.getProperty("A_key"));
	static int A_valuecol = Integer.parseInt(GlobalSettings.getProperty("A_value"));
	static int Resultcol = Integer.parseInt(GlobalSettings.getProperty("Result"));

	/**
	 * �Ի���expect�ͷ���response��ѭ���Աȣ����ԱȽ��д��comparison�����
	 * 
	 * @param testid
	 * @param bsheet
	 * @param response
	 * @param csheet
	 */
	public static void ParserAndCompare(String testid, String bsheet,
			String response, String csheet) {

		Map<String, String> actualmap = parser(response);
		try {
			// ��bsheet�ж�ȡexpect�ַ���
			ExcelDataUtils.setWorkSheet(bsheet);
			int brownum = ExcelDataUtils.getRowNumberOnTestid(testid);
			String expectstr = (String) ExcelDataUtils.getCellData(brownum,
					expectcol);

			// �л���csheet,д��ԱȺ���
			ExcelDataUtils.setWorkSheet(csheet);
			int crownum = ExcelDataUtils.getRowNumberOnTestid(testid);
			Map<String, String> expectmap = parser(expectstr);
			Iterator<String> iter = expectmap.keySet().iterator();

			while (iter.hasNext()) {
				String ekey = iter.next();
				String evalue = expectmap.get(ekey);
				ExcelDataUtils.setCellData(ekey, crownum, E_keycol);
				ExcelDataUtils.setBorder(crownum, E_keycol);
				ExcelDataUtils.setCellData(evalue, crownum, E_valuecol);
				ExcelDataUtils.setBorder(crownum, E_valuecol);

				String avalue = actualmap.get(ekey);
				if (avalue != null) {
					ExcelDataUtils.setCellData(ekey, crownum, A_keycol);
					ExcelDataUtils.setBorder(crownum, A_keycol);
					ExcelDataUtils.setCellData(avalue, crownum, A_valuecol);
					ExcelDataUtils.setBorder(crownum, A_valuecol);
					if (avalue.equals(evalue)) {
						ExcelDataUtils
								.setCellData("passed", crownum, Resultcol);
						ExcelDataUtils.setBorder(crownum, Resultcol);
						ExcelDataUtils
								.setCellColor("GREEN", crownum, Resultcol);
					} else {
						ExcelDataUtils
								.setCellData("failed", crownum, Resultcol);
						ExcelDataUtils.setBorder(crownum, Resultcol);
						ExcelDataUtils.setCellColor("YELLOW", crownum,
								Resultcol);
					}
				} else {
					ExcelDataUtils.setCellData("--", crownum, A_keycol);
					ExcelDataUtils.setCellData("--", crownum, A_valuecol);
					ExcelDataUtils.setCellData("failed", crownum, Resultcol);
					ExcelDataUtils.setBorder(crownum, Resultcol);
					ExcelDataUtils.setCellColor("YELLOW", crownum, Resultcol);
					logger.info(testid + ":  " + ekey + "��ͳ�����ڷ��ؽ���в�����");
				}

				// ������testid���������һ�����У�׼��д����һ��key-value
				int totalrows = ExcelDataUtils.getWorkSheet().getLastRowNum(); // ��ȡ������
				ExcelDataUtils.insertRow(crownum, totalrows, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ͳ�Ʋ�ѯ�ӿڷ��ص��ַ�������������ֵ����map
	 * 
	 * @param response
	 * @return
	 */
	private static Map<String, String> parser(String response) {
		Map<String, String> m = new HashMap<String, String>();

		// �����������ַ�����У�飬�ų����ؽ��Ϊ�ջ򷵻ط������Ľ��
		if (response != null) {
			if (response.indexOf("OK") >= 0) {
				String[] tokens = response.split("},");
				for (int i = 0; i < tokens.length; i++) {
					int firstindex = tokens[i].indexOf("\"");
					int lastindex = tokens[i].lastIndexOf("\"");
					String key = tokens[i].substring(firstindex + 1,
							tokens[i].indexOf("\"", firstindex + 1) - 1);
					String value = tokens[i].substring(
							tokens[i].lastIndexOf("\"", lastindex - 1) + 1,
							lastindex - 1);
					m.put(key, value);
				}
			} else {
				logger.info("���������ؽ���������Խ���");
			}
		} else {
			logger.info("���ؽ��Ϊ�գ������Խ���");
		}
		return m;
	}

	/**
	 * ���ӿڵķ��ؽ��д��output��
	 * 
	 * @param osheet
	 * @param testid
	 * @param response
	 */
	public static void saveResponse(String osheet, String testid,
			String response) {
		try {
			ExcelDataUtils.setWorkSheet(osheet);
			int rownumber = ExcelDataUtils.getRowNumberOnTestid(testid);
			ExcelDataUtils.setCellData(response, rownumber, responsecol);
			ExcelDataUtils.setBorder(rownumber, responsecol);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * ���ⲿ���Լ�EXCEL��������
	 * 
	 * @param testid
	 * @param sheet
	 */
	public static void teardown(String testid, String sheet) {

	}
}
