/**
 *�����������ݿ�ĻỰ����������mybatis�����ļ���Ϣ
 */
package org.gradle.needle.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * @author kongzhaolei
 * 
 */
public final class DBFactory {

	private static final String configpath = "SqlMapConfig.xml";

	/*
	 * ����environment id ��ȡ��Ӧ��SqlSessionFactory
	 */
	public static SqlSessionFactory getSqlSessionFactory(DBEnvironment env) {
		SqlSessionFactory sqlSessionFactory = null;
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(configpath);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,
					env.name());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return sqlSessionFactory;
	}

	public static enum DBEnvironment {
		configdb, 
		datadb;
	}
}
