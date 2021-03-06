package org.gradle.needle.util;

import java.math.BigDecimal;
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

/**
 * 
 * @author kongzhaolei
 * 
 */
public class DBUtils {
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
	 * 无实例概念，不设用户和密码，针对ACCESS类型数据库
	 */
	public DBUtils(String databasetype, String url) {
		this.databasetype = databasetype;
		this.url = url;
	}
	
	/*
	 * 获取数据库连接对象
	 * 根据数据库类型加载驱动
	 * 支持sqlserver，postgresql, oracle, access
	 * 
	 */
	public Statement GetConn() {
		Statement conn = null;   //数据库预编译对象
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
			if ("access".equals(databasetype)) {
				conn = DriverManager.getConnection(connurl).createStatement();
				
			}else {
				conn = DriverManager.getConnection(connurl, username, password).createStatement();
			}		
			 if(conn != null && !conn.isClosed())
				 logger.info(" 数据库连接成功！");
		}catch(SQLException e){  
			e.printStackTrace();
		}
		return conn;
	}
	
	//关闭所有连接
	public void ConnClose(){	
		if (GetConn()!=null)
			try{
				GetConn().close();
			}catch(SQLException e){
				e.printStackTrace(); 
			}
		logger.info("数据库连接已关闭");
	}
	
	//获取整个查询结果
	public ResultSet Query(String sql){
		ResultSet rs = null;
		try{
			rs = GetConn().executeQuery(sql);
			if (!rs.wasNull()) {
				logger.info("查询成功");
			}
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		return rs;
	}
	
	// 获取某一列的查询结果,以列表存储
	public List<String> Query(String sql,String column) {
		List<String> lists = new ArrayList<String>();
	       ResultSet rs = null;
			try{
				rs = GetConn().executeQuery(sql);
			while (rs.next()) {
				lists.add(rs.getString(column));
			}
			System.out.println("查询成功！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lists;
	}
	
	 // 单纯执行sql，无返回结果，例如insert
	public void excutesql(String sql) {
		try{
			GetConn().executeQuery(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
   
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
