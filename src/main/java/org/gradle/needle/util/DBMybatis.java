/**
 *
 */
package org.gradle.needle.util;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * @author kongzhaolei
 *
 */
public class DBMybatis {
	
	protected static SqlSessionFactory configssf;
	protected static SqlSessionFactory datassf;
	protected static Reader reader;
	
	//一个静态方法
	static
	{
		try {
			reader = Resources.getResourceAsReader("SqlMapConfig.xml");
			configssf = new SqlSessionFactoryBuilder().build(reader, "configdb");
			datassf = new SqlSessionFactoryBuilder().build(reader, "datadb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
