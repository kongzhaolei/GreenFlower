package org.gradle;

import java.io.File;
import java.io.IOException;

import org.gradle.needle.engine.AnemometerDataGenerator;

public class AnemometerMachine {

	public static void main(String[] args) {
		AnemometerDataGenerator anemometerDataGenerator = new AnemometerDataGenerator(
				new File("/opt/needle"));
		try {
			anemometerDataGenerator.setLongtitude("95.435");
			anemometerDataGenerator.setlatitude("37.1777");
			anemometerDataGenerator.genAnemometerFiles(5);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
