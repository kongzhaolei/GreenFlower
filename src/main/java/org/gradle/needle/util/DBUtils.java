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
	private Connection conn=null;   //���ݿ����Ӷ���
	private Statement ps=null;  //���ݿ�Ԥ�������
	private ResultSet rs=null;   //��ѯ�����
	private static Logger logger = Logger.getLogger(DBUtils.class
			.getName());
	String url;
	String instance;
	String username;
	String password;
	String connurl;
	String databasetype;
	
	/*
	 * ���췽��1
	 * databasetype ���ݿ�����
	 * url ���ӵ�ַ��·����������Ĭ�϶˿�
	 * instance ���ݿ�ʵ��
	 * username �û���
	 * password ����
	 */
	public DBUtils(String databasetype, String url, String instance, String username,
			String password) {
		this.databasetype = databasetype;
		this.url = url;
		this.instance = instance;
		this.username = username;
		this.password = password;
		conn = GetConn();
	}
	
	/*
	 * ���췽��2
	 * ��ʵ����������û������룬���ACCESS�������ݿ�
	 */
	public DBUtils(String databasetype, String url) {
		this.databasetype = databasetype;
		this.url = url;
	}
	
	/*
	 * ��ȡ���ݿ����Ӷ���
	 * �������ݿ����ͼ�������
	 * ֧��sqlserver��postgresql, oracle, access
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
				logger.info("�ݲ�֧�ָ��������ݿ⣺ " + databasetype);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try{
			if ("access".equals(databasetype)) {
				conn = DriverManager.getConnection(connurl);
			}else {
				conn = DriverManager.getConnection(connurl, username, password);
			}		
			 if(conn != null && !conn.isClosed())
				 logger.info(conn.getSchema() + "���ݿ����ӳɹ���");
		}catch(SQLException e){  
			e.printStackTrace();
		}
		return conn;
	}
	
	//�ر���������
	public void ConnClose(){
//		if (rs!=null)
//			try{
//				rs.close();
//			}catch(SQLException e){
//				e.printStackTrace();  
//			}
		
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
		logger.info("���ݿ������ѹر�");
	}
	
	//��ȡ������ѯ���
	public ResultSet Query(String sql){
		try{
			ps = GetConn().createStatement();
			rs = ps.executeQuery(sql);
			if (!rs.wasNull()) {
				logger.info("��ѯ�ɹ�");
			}
		}catch (SQLException e){
			e.printStackTrace(); 
		}
		return rs;
	}
	
	// ��ȡĳһ�еĲ�ѯ���
	public String[] Query(String sql,String column) {
		List<String> list = new ArrayList<String>();
	       String[] farms = null;
		try {
			ps = GetConn().createStatement();
			rs = ps.executeQuery(sql);
			while (rs.next()) {
				String farm = (rs.getString(column));
				list.add(farm);
			}
			farms = (String[]) list.toArray(new String[list.size()]);
			System.out.println("��ѯ�ɹ���");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return farms;
	}
	
	 // ����ִ��sql���޷��ؽ��������insert
	public void excutesql(String sql) {
		try{
			ps = GetConn().createStatement();
			ps.executeQuery(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
   
	/*
	 * DataAssert()
	 * ��֤�������ֶεĲ�ѯ�����������ֵ�Աȣ�������֤���
	 * ��ȣ�true ������ȣ�false
	 */
	public boolean DataAssert(String sql, String qycolumn, String etresult){
		String qyresult = null;
		boolean a;
		try{
			ps = GetConn().createStatement();
			rs = ps.executeQuery(sql);
			while (rs.next()){
				qyresult = rs.getString(qycolumn);
			}
			logger.info("���ݲ�ѯ�ɹ���");
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
	 * ��֤�������������ݵĹ����Ա�
	 */
	public void DataAssert(String key, String zbsql,String zbcolumn,String bbsql,String bbcolumn){
		 Map<String,Float> zbmap = new HashMap<String,Float>();
		 Map<String,Float> bbmap = new HashMap<String,Float>();
		 try{
				ps = GetConn().createStatement();
				ResultSet zbrs = ps.executeQuery(zbsql);
				 while (zbrs.next()){
							zbmap.put(zbrs.getString(key),zbrs.getFloat(zbcolumn));
				 }
			}catch (SQLException e){
				e.printStackTrace(); 
			}
		 
		 try{
				ps = GetConn().createStatement();
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
