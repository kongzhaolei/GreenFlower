/**
 * 
 */
package org.gradle.needle.model;

/**
 * @author needle
 *
 */
public class WtOneData {
	private int wfid;
	private String wtid;
	private String rectime;
	private double windspeed;
	private double realpower;
	private double theorypower;
	private int wtstatus;
	private int limitstatus;
	private double envitemp;
	private double endelec;
	
	public Integer getWfid() {
		return wfid;
	}
	
	public void setWfid(int wfid) {
		this.wfid = wfid;
	}
	
	public String getWtid() {
		return wtid;
	}
	
	public void setWtid(String wtid) {
		this.wtid = wtid;
	}
	
	public String getRectime() {
		return rectime;
	}
	
	public void setRectime(String time) {
		this.rectime = time;
	}
	
	public Double getWindSpeed() {
		return windspeed;
	}
	
	public Double getRealPower() {
		return realpower;
	}
	
	public Double getTheoryPower() {
		return theorypower;
	}
	
	public Integer getWtStatus() {
		return wtstatus;
	}
	
	public Integer getlimitStatus() {
		return limitstatus;
	}
	
	public Double getEnvitemp() {
		return envitemp;
	}
	
	public Double getEndelec() {
		return endelec;
	}

}
