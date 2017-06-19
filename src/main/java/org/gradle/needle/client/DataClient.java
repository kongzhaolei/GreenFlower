package org.gradle.needle.client;

public interface DataClient {
	
	public void sendDevWarnLog();
	
	public void sendDevFaultData();
	
	public void sendDevAlarmData();
	
	public void sendDevStateData();

}
