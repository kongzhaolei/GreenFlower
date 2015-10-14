/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.gradle.needle.selenium;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.gradle.needle.dbo.GlobalSettings;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

public class CaptureScreenshot {

	public static void log(String logText) {
		System.out.println("["
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
						System.currentTimeMillis())) + "] " + logText);
	}

	public static String screenShot(BrowserFactory bf) {
		String dir = "screenshot"; // TODO
		String time = new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(new Date());
		String screenShotPath = dir + File.separator + time + ".png";
		WebDriver augmentedDriver = null;
		if (GlobalSettings.browserCoreType == 1
				|| GlobalSettings.browserCoreType == 3) {
			augmentedDriver = bf.getBrowserCore();
			augmentedDriver.manage().window().setPosition(new Point(0, 0));
			augmentedDriver.manage().window()
					.setSize(new Dimension(9999, 9999));
		} else if (GlobalSettings.browserCoreType == 2) {
			augmentedDriver = new Augmenter().augment(bf.getBrowserCore());
		} else {
			return "无法识别的浏览器";
		}

		try {
			File sourceFile = ((TakesScreenshot) augmentedDriver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(sourceFile, new File(screenShotPath));

		} catch (Exception e) {
			e.printStackTrace();
			return "截图失败";
		}

		return screenShotPath.replace("\\", "/");
	}

}
