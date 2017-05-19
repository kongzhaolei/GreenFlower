package org.gradle.needle.util;

import org.apache.log4j.Logger;
import org.assertj.db.api.ChangeAssert;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Table;
import org.postgresql.ds.PGPoolingDataSource;

import static org.assertj.db.api.Assertions.assertThat;

import javax.sql.DataSource;

public class VerifyUtils {

	private static Logger logger = Logger.getLogger(VerifyUtils.class.getName());
	private static String url;
	private static String instance;
	private static String username;
	private static String password;

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

	public static void assertDbChanges() {
		DataSource dataSource = new DBUtils(url, instance, username, password).getPGDataSource();
		Changes changes = new Changes(dataSource);
		assertThat(changes).hasNumberOfChanges(3).ofCreation().hasNumberOfChanges(1).ofDeletion().hasNumberOfChanges(1)
				.ofModification().hasNumberOfChanges(1).ofAll().hasNumberOfChanges(3);

	}

	/*
	 * verify changes on a table
	 */
	public static void assertTbaleChanges(String tablenane) {
		DataSource dataSource = new DBUtils(url, instance, username, password).getPGDataSource();
		Table table = new Table(dataSource, tablenane);
		Changes changes = new Changes(table);
		changes.setStartPointNow();
		ChangeAssert a = assertThat(changes).change().isCreation();

	}

}
