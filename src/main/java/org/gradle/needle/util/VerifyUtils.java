package org.gradle.needle.util;

import org.apache.log4j.Logger;
import org.assertj.db.type.Changes;

import static org.assertj.db.api.Assertions.assertThat;

public class VerifyUtils {

	private static Logger logger = Logger.getLogger(VerifyUtils.class.getName());
	Changes changes = new Changes();

	public VerifyUtils() {

	}

	/**
	 * verify database object
	 */
	
	public static boolean assertDbCreation() {
		return false;
		
		
	}
	
	

}
