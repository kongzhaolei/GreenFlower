package org.gradle.needle.util;

import org.apache.log4j.Logger;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Table;
import org.gradle.needle.client.TCPDataClientHandler;

import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

public class VerifyUtils {
	private static String url;
	private static String instance;
	private static String username;
	private static String password;
	private static Logger logger = Logger.getLogger(VerifyUtils.class.getName());
	

	/*
	 * database
	 */
	public VerifyUtils(String url, String instance, String username, String password) {
		VerifyUtils.url = url;
		VerifyUtils.instance = instance;
		VerifyUtils.username = username;
		VerifyUtils.password = password;
	}

	/*
	 * verify changes on database
	 */

	public VerifyUtils() {
		
	}

	public void assertDbChanges() {
		DataSource dataSource = new DBUtils(url, instance, username, password).getPGDataSource();
		Changes changes = new Changes(dataSource);
		assertThat(changes).hasNumberOfChanges(3).ofCreation().hasNumberOfChanges(1).ofDeletion().hasNumberOfChanges(1)
				.ofModification().hasNumberOfChanges(1).ofAll().hasNumberOfChanges(3);

	}

	/*
	 * verify changes on a table
	 */
	public void assertTbaleChanges(String tablename) {
		DataSource dataSource = new DBUtils(url, instance, username, password).getPGDataSource();
		Table table = new Table(dataSource, tablename);
		Changes changes = new Changes(table);
		changes.setStartPointNow();
		assertThat(changes).change().isCreation();
	}
	
	/*
	 * verify string
	 */
	public void assertTcpResponse() {
		assertThat(TCPDataClientHandler.getchannelRead() == "(ok)");
		logger.info("assert success");
	}
}
