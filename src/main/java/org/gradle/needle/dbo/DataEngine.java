package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DataEngine {

	int protocolid;

	// 构造方法,初始化协议号
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * 根据GWSOCKET命令获取cachevalue
	 */
	public String getCacheValue(String cmdname) {
		String sReturn = null;
		DataDefined df = new DataDefined(protocolid);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			ResultSet rSet1 = df.getConfigSet(cmdname);
			ResultSet rSet2 = df.getDataSet(cmdname);
			while (rSet2.next()) {
				varpathMap.put(rSet2.getString("varpath"),
						rSet2.getString("cachevalue"));
			}

			while (rSet1.next()) {
				if (varpathMap.containsKey(rSet1.getString("iecpath"))) {
					sReturn = sReturn
							+ varpathMap.get(rSet1.getString("iecpath")) + ";";
				} else {
					System.err.println("DataSet不存在此IEC量： "
							+ rSet1.getString("iecpath"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;

	}

}
