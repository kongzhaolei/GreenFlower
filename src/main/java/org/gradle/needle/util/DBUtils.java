package org.gradle.needle.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DBUtils {
	private Connection conn=null;   //数据库连接对象
	private Statement ps=null;  //数据库预编译对象
	private ResultSet rs=null;   //查询结果集
	private static Logger logger = Logger.getLogger(DBUtils.class
			.getName());
	String url;
	String instance;
	String username;
	String password;
	String connurl;
	String databasetype;
	
	/*
	 * 构造方法1
	 * databasetype 数据库类型
	 * url 连接地址或路径，不包括默认端口
	 * instance 数据库实例
	 * username 用户名
	 * password 密码
	 */
	public DBUtils(String databasetype, String url, String instance, String username,
			String password) {
		this.databasetype = databasetype;
		this.url = url;
		this.instance = instance;
		this.username = username;
		this.password = password;
	}
	
	/*
	 * 构造方法2
	 * 无实例概念，针对ACCESS类型数据库
	 */
	public DBUtils(String databasetype, String url, String username,
			String password) {
		this.databasetype = databasetype;
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/*
	 * 获取数据库连接对象
	 * 根据数据库类型加载驱动
	 * 支持sqlserver，postgresql, oracle, access
	 * 
	 */
	public Connection GetConn() {
		try {
			if ("sqlserver".equals(databasetype)) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connurl = "jdbc:sqlserver://" + url + ";databaseName="
						+ instance;
			}
				
			if ("postgres".equals(databasetype)) {
				Class.forName("org.postgresql.Driver");
				connurl = "jdbc:postgresql://" + url + ":5432/" + instance;
			} 
			
			if("oracle".equals(databasetype)){
				Class.forName("oracle.jdbc.driver.OracleDriver");
				connurl = "jdbc:oracle:thin:@" + url + ":1521:" + instance;
			}
			
			if ("access".equals(databasetype)) {
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				connurl = "jdbc:ucanaccess://" + url ;
			}
			
			else {
				logger.info("暂不支持该类型数据库： " + databasetype);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try{
			conn = DriverManager.getConnection(connurl, username, password);
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
	
	// 获取某一列的查询结果
	public String[] Query(String sql,String column) {
		List<String> list = new ArrayList<String>();
	       String[] farms = null;
		try {
			ps = conn.createStatement();
			rs = ps.executeQuery(sql);
			while (rs.next()) {
				String farm = (rs.getString(column));
				list.add(farm);
			}
			farms = (String[]) list.toArray(new String[list.size()]);
			System.out.println("查询成功！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return farms;
	}
	
	 // 单纯执行sql，无返回结果，例如insert
	public void excutesql(String sql) {
		try{
			ps = conn.createStatement();
			ps.executeQuery(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
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
