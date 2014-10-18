package com.BabyTracker.Model;

import java.util.ArrayList;

public class DailyActivityModel {

	int diperstatus;
	int totalMilkCount;
	String activityDate;
	int milk_quantity;
	int diper_status_change;
	int baby_id;
	String dailyactivity_date;
	int feedCount;
	int sleepingHours;
	
	public int getSleepingHours() {
		return sleepingHours;
	}
	public void setSleepingHours(int sleepingHours) {
		this.sleepingHours = sleepingHours;
	}
	public ArrayList<DailyChildModel> mChildItems;
	
	public int getFeedCount() {
		return feedCount;
	}
	public void setFeedCount(int feedCount) {
		this.feedCount = feedCount;
	}
	public String getDailyactivity_date() {
		return dailyactivity_date;
	}
	public void setDailyactivity_date(String dailyactivity_date) {
		this.dailyactivity_date = dailyactivity_date;
	}
	public int getBaby_id() {
		return baby_id;
	}
	public void setBaby_id(int baby_id) {
		this.baby_id = baby_id;
	}
	public int getDiper_status_change() {
		return diper_status_change;
	}
	public void setDiper_status_change(int diper_status_change) {
		this.diper_status_change = diper_status_change;
	}
	public int getMilk_quantity() {
		return milk_quantity;
	}
	public void setMilk_quantity(int milk_quantity) {
		this.milk_quantity = milk_quantity;
	}
	public String getActivityDate() {
		return activityDate;
	}
	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}
	public int getDiperstatus() {
		return diperstatus;
	}
	public void setDiperstatus(int diperstatus) {
		this.diperstatus = diperstatus;
	}
	public int getTotalMilkCount() {
		return totalMilkCount;
	}
	public void setTotalMilkCount(int totalMilkCount) {
		this.totalMilkCount = totalMilkCount;
	}
}
