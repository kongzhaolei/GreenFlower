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
	private int wtid;
	private String rectime;
	private double windspeed;
	private double realpower;
	private double theorypower;
	private char wtstatus;
	private char limitstatus;
	
	public Integer getWfid() {
		return wfid;
	}
	
	public void setWfid(int wfid) {
		this.wfid = wfid;
	}
	
	public Integer getWtid() {
		return wtid;
	}
	
	public void setWtid(int wtid) {
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
	
	public Character getWtStatus() {
		return wtstatus;
	}
	
	public Character getlimitStatus() {
		return limitstatus;
	}

}
