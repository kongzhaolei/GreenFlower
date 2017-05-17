package org.gradle.needle.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 
 * @author kongzhaolei
 * 
 */
public class DBUtils {
	private static Logger logger = Logger.getLogger(DBUtils.class.getName());
	String url;
	String instance;
	String username;
	String password;
	String connurl;
	String databasetype;

	/*
	 * ���췽��1 databasetype ���ݿ����� url ���ӵ�ַ��·����������Ĭ�϶˿� instance ���ݿ�ʵ�� username �û���
	 * password ����
	 */
	public DBUtils(String databasetype, String url, String instance, String username, String password) {
		this.databasetype = databasetype;
		this.url = url;
		this.instance = instance;
		this.username = username;
		this.password = password;
	}

	/*
	 * ���췽��2 ��ʵ����������û������룬���ACCESS�������ݿ�
	 */
	public DBUtils(String databasetype, String url) {
		this.databasetype = databasetype;
		this.url = url;
	}

	/*
	 * ��ȡ���ݿ����Ӷ��� �������ݿ����ͼ������� ֧��sqlserver��postgresql, oracle, access
	 * 
	 */
	public Statement GetConn() {
		Statement conn = null; // ���ݿ�Ԥ�������
		try {
			if ("sqlserver".equals(databasetype)) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connurl = "jdbc:sqlserver://" + url + ";databaseName=" + instance;
			}

			if ("postgres".equals(databasetype)) {
				Class.forName("org.postgresql.Driver");
				connurl = "jdbc:postgresql://" + url + ":5432/" + instance;
			}

			if ("oracle".equals(databasetype)) {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				connurl = "jdbc:oracle:thin:@" + url + ":1521:" + instance;
			}

			if ("access".equals(databasetype)) {
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				connurl = "jdbc:ucanaccess://" + url;
			}

			else {
				logger.info("�ݲ�֧�ָ��������ݿ⣺ " + databasetype);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if ("access".equals(databasetype)) {
				conn = DriverManager.getConnection(connurl).createStatement();

			} else {
				conn = DriverManager.getConnection(connurl, username, password).createStatement();
			}
			if (conn != null && !conn.isClosed())
				logger.info(" ���ݿ����ӳɹ���");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	// �ر���������
	public void ConnClose() {
		if (GetConn() != null)
			try {
				GetConn().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		logger.info("���ݿ������ѹر�");
	}

	// ��ȡ������ѯ���
	public ResultSet Query(String sql) {
		ResultSet rs = null;
		try {
			rs = GetConn().executeQuery(sql);
			if (!rs.wasNull()) {
				logger.info("��ѯ�ɹ�");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	// ��ȡĳһ�еĲ�ѯ���,���б�洢
	public List<String> Query(String sql, String column) {
		List<String> lists = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = GetConn().executeQuery(sql);
			while (rs.next()) {
				lists.add(rs.getString(column));
			}
			System.out.println("��ѯ�ɹ���");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lists;
	}

	// ����ִ��sql���޷��ؽ��������insert
	public void excutesql(String sql) {
		try {
			GetConn().executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
