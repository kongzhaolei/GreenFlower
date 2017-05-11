package org.gradle.needle.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class VerifyUtils {
	
DBUtils dbUtils = new DBUtils(databasetype, url, instance, username, password)
private static Logger logger = Logger.getLogger(VerifyUtils.class.getName());
	public VerifyUtils() {
		
	}
	
	/**
	 * verify database object 
	 */
	/*
	 * DataAssert()
	 * 验证单表单个字段的查询结果，和期望值对比，返回验证结果
	 * 相等：true ，不相等：false
	 */
	public boolean DataAssert(String sql, String qycolumn, String etresult){
		String qyresult = null;
		boolean a;
		ResultSet rs = null;
		try{
			rs = GetConn().executeQuery(sql);
			while (rs.next()){
				qyresult = rs.getString(qycolumn);
			}
			logger.info("数据查询成功！");
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		if(qyresult.equals(etresult))
			a = true;
		else
			a = false;
		return a;
	}
	
	/*
	 * DataAssert()
	 * 验证两个表两组数据的关联对比
	 */
	public void DataAssert(String key, String zbsql,String zbcolumn,String bbsql,String bbcolumn){
		 Map<String,Float> zbmap = new HashMap<String,Float>();
		 Map<String,Float> bbmap = new HashMap<String,Float>();
		 ResultSet zbrs = null;
		 ResultSet bbrs = null;
			try{
				zbrs = GetConn().executeQuery(zbsql);
				 while (zbrs.next()){
							zbmap.put(zbrs.getString(key),zbrs.getFloat(zbcolumn));
				 }
			}catch (SQLException e){
				e.printStackTrace(); 
			}
		 
		 try{
				bbrs =  GetConn().executeQuery(bbsql);
				 while (bbrs.next()){
							bbmap.put(bbrs.getString(key),bbrs.getFloat(bbcolumn));
				 }
			}catch (SQLException e){
				e.printStackTrace(); 
			}
		 
		 Iterator<String> i =zbmap.keySet().iterator();
		 while(i.hasNext()){
			 String j = i.next();
			 double one = zbmap.get(j);
			 BigDecimal  bd1 = new BigDecimal(one);  
			 double onef = bd1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			 double two = bbmap.get(j);
			 BigDecimal  bd2 = new BigDecimal(two);  
			 double twof = bd2.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
			 if (onef == twof)
				 logger.info(j + "----------" + bbcolumn + "----------passed");
			 else
				 logger.info(j + "----------" + bbcolumn + "----------failed"); 
		 }
	}

}
