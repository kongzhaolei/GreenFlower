package org.gradle.needle.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.dbo.GlobalSettings;

public class OracleConnAndDataAssert {
	private Connection conn=null;   //数据库连接对象
	private Statement ps=null;  //数据库预编译对象
	private ResultSet rs=null;   //查询结果集
	private static Logger logger = Logger.getLogger(OracleConnAndDataAssert.class
			.getName());
	
	//获取数据库连接
	public Connection GetConn() {
		try{
			Class.forName(GlobalSettings.driver);
		}catch(ClassNotFoundException e){
			logger.info("加载驱动失败："+ e.getMessage());
		}
		
		try{
			conn = DriverManager.getConnection(GlobalSettings.connurl, GlobalSettings.userName, GlobalSettings.password);
			 if(conn != null && !conn.isClosed())
				 logger.info("数据库连接成功！");
		}catch(SQLException e){  
			e.printStackTrace();
		}
		return conn;
	}
	
	//关闭所有连接
	public void ConnClose(){
		if (rs!=null)
			try{
				rs.close();
			}catch(SQLException e){
				e.printStackTrace();  
			}
		
		if (ps!=null)
			try{
				ps.close();
			}catch(SQLException e){
				e.printStackTrace(); 
			}
		
		if (conn!=null)
			try{
				conn.close();
			}catch(SQLException e){
				e.printStackTrace(); 
			}
		logger.info("数据库连接已关闭");
	}
	
	//获取整个查询结果
	public ResultSet Query(String sql){
		conn = GetConn();
		try{
			ps = conn.createStatement();
			rs = ps.executeQuery(sql);
			System.out.println("查询成功！");
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		return rs;
	}
   
	//DataAssert()重载1，获取单表单个字段的查询结果，和期望值对比，返回验证结果
	public void DataAssert(String sql, String qycolumn, String etresult){
		String qyresult = null;
		try{
			ps = conn.createStatement();
			rs = ps.executeQuery(sql);
			while (rs.next()){
				qyresult = rs.getString(qycolumn);
			}
			logger.info("数据查询成功！");
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		if(qyresult.equals(etresult))
			logger.info("数据验证通过");
		else
			logger.info("数据验证不通过");
	}
	
	//DataAssert()重载2，获取两个表单个字段的结果进行对比
	public void DataAssert(String zbsql,String zbcolumn,String bbsql,String bbcolumn){
		double zbresultf = 0;
		double bbresultf = 0;
		
		try{
			ps = conn.createStatement();
			ResultSet zbrs = ps.executeQuery(zbsql);
			while (zbrs.next()){
				double zbresult = zbrs.getFloat(zbcolumn);
				BigDecimal  bd1 = new BigDecimal(zbresult);  
				zbresultf = bd1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			}	
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		
		try{
			ps = conn.createStatement();
			ResultSet bbrs = ps.executeQuery(bbsql);
				while (bbrs.next()){
					double bbresult = bbrs.getFloat(bbcolumn);
					BigDecimal  bd2 = new BigDecimal(bbresult);  
					bbresultf = bd2.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
				}
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		
		if(bbresultf == zbresultf)
			logger.info(bbcolumn + "----------" + "passed");
		else
			logger.info(bbcolumn + "---------- " + "failed");
	}
	
	//DataAssert()重载3,获取两个表两组数据的关联对比
	public void DataAssert(String key, String zbsql,String zbcolumn,String bbsql,String bbcolumn){
		 Map<String,Float> zbmap = new HashMap<String,Float>();
		 Map<String,Float> bbmap = new HashMap<String,Float>();
		 try{
				ps = conn.createStatement();
				ResultSet zbrs = ps.executeQuery(zbsql);
				 while (zbrs.next()){
							zbmap.put(zbrs.getString(key),zbrs.getFloat(zbcolumn));
				 }
			}catch (SQLException e){
				e.printStackTrace(); 
			}
		 
		 try{
				ps = conn.createStatement();
				ResultSet bbrs = ps.executeQuery(bbsql);
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
