package org.gradle;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

import org.gradle.needle.client.TCPDataClient;
import org.gradle.needle.config.GlobalSettings;
import org.gradle.needle.util.VTimer;
import org.gradle.needle.util.VerifyUtils;

public class DataDispGhost {

	private static String host = GlobalSettings.getProperty("host");
	private static int port = Integer.parseInt(GlobalSettings.getProperty("port"));

	public static void main(String[] args) {
		VTimer.keyWorkTimer();
		new TCPDataClient(host, port).GeneratorStart();
		
/*
		new TCPDataClient(host, port).TcpConnect();
		while (true) {
			 TCPDataClient.sendDevSedimentOneData();
			 TCPDataClient.sendDevSedimentRealData();
			 TCPDataClient.sendDevTenData();
			 TCPDataClient.sendDevFiveData();
			 await().atMost(120000, MILLISECONDS)	
			 .untilAsserted(() -> new VerifyUtils("10.1.3.152:5432", "v5",
			 "postgres", "postgres")
			 .assertTbaleChanges("public.statisticdata"));s
			 await().atMost(2000, MILLISECONDS).untilAsserted(() -> new VerifyUtils().assertTcpResponse());
		}
*/
	}
}
	