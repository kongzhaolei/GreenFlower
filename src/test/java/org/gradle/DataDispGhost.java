package org.gradle;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;
import org.gradle.needle.util.VTimer;
import org.gradle.needle.util.VerifyUtils;

public class DataDispGhost {

	private static String host = "10.1.3.152";
	private static int port = 8804;
	// private static Logger logger =
	// Logger.getLogger(DataDispGhost.class.getName());

	public static void main(String[] args) {
		VTimer.timerStart();
		new TCPDataClient(host, port).TcpConnect();
		while (true) {
			TCPDataClient.sendDevSedimentOneData();
			//TCPDataClient.sendDevSedimentRealData();
			// TCPDataClient.sendDevOneData();
			// TCPDataClient.sendDevTenData();
			
			// await().atMost(120000, MILLISECONDS)
			// .untilAsserted(() -> new VerifyUtils("10.1.3.152:5432", "v5",
			// "postgres", "postgres")
			// .assertTbaleChanges("public.statisticdata"));

			await().atMost(2000, MILLISECONDS).untilAsserted(() -> new VerifyUtils().assertTcpResponse());
		}

		// new TCPDataClient(host, port).GeneratorStart();

	}
}
