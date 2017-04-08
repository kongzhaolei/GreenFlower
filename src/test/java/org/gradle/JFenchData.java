package org.gradle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.gradle.needle.util.ExcelDataUtils;
import org.gradle.needle.util.XMLUtils;

public class JFenchData {
	public static void main(String[] args) {
		String url = "http://odata.juhe.cn/netdata/NetdataServlet.cn/Stocks";// url为请求的api接口地址
		String appkey = "625905f46f14642c489bb5bce5928e6b";// 聚合数据申请的对应key
		String urlAll = new StringBuffer(url).append("?key=").append(appkey)
				.append("&$format=xml").toString();
		String charset = "UTF-8";
		String Result = get(urlAll, charset);// 得到xml字符串
		Document xmldoc = XMLUtils.string2xmldoc(Result);  //字符串转换为XML DOC
		int row = 1;  //定义写入数据的第一行
		String sheet = "S0204";  // 定义写入的sheet
		
		//初始化excel sheet
		try {
			ExcelDataUtils.setExcelWorkSheet(sheet);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// 解析xmldoc
		Element root = xmldoc.getRootElement();
		for (Iterator i = root.elementIterator("entry"); i.hasNext();) {
			Element entry = (Element) i.next();
			Element content = entry.element("content");
			Element properties = content.element("properties");
			String Code = properties.elementText("Code");
			String Title = properties.elementText("Title");
			String Newnet = properties.elementText("Newnet");
			String Totalnet = properties.elementText("Totalnet");
			String Date = properties.elementText("Changetime");
			String Dayincrease = properties.elementText("Dayincrease");
			String Daygrowrate = properties.elementText("Daygrowrate");

			// 将解析后的数据逐行写入excel文件中
			try {
				ExcelDataUtils.setCellData(Code, row, 0);
				ExcelDataUtils.setCellData(Title, row, 1);
				ExcelDataUtils.setCellData(Date, row, 2);
				ExcelDataUtils.setCellData(Newnet, row, 3);
				ExcelDataUtils.setCellData(Totalnet, row, 4);
				ExcelDataUtils.setCellData(Daygrowrate, row, 5);
				ExcelDataUtils.setCellData(Dayincrease, row, 6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			row++;
		}
		System.out.println("合计：" + row + " 行 数据已写入");
	}

	/**
	 * 
	 * @param urlAll
	 *            :请求接口
	 * @param charset
	 *            :字符编码
	 * @return 返回xml结果
	 */
	public static String get(String urlAll, String charset) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(60000);
			connection.setConnectTimeout(60000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, charset));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
