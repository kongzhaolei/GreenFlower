package org.gradle;

import java.io.File;
import java.io.IOException;

import org.gradle.needle.engine.AnemometerDataGenerator;

public class AnemometerMachine {

	public static void main(String[] args) {
		AnemometerDataGenerator anemometerDataGenerator = new AnemometerDataGenerator(
				new File("/opt/needle"));
		try {
			anemometerDataGenerator.genAnemometerFiles(5);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
