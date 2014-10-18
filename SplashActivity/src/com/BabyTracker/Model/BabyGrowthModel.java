package com.BabyTracker.Model;

public class BabyGrowthModel {

	String baby_name = "";
	String baby_height = "";
	String baby_weight = "";
	String baby_dateofbirth = "";
	String date_of_update = "";
	int growth_id;
	
	public int getGrowth_id() {
		return growth_id;
	}
	public void setGrowth_id(int growth_id) {
		this.growth_id = growth_id;
	}
	public String getDate_of_update() {
		return date_of_update;
	}
	public void setDate_of_update(String date_of_update) {
		this.date_of_update = date_of_update;
	}
	public String getBaby_dateofbirth() {
		return baby_dateofbirth;
	}
	public void setBaby_dateofbirth(String baby_dateofbirth) {
		this.baby_dateofbirth = baby_dateofbirth;
	}
	long baby_age = 0;
	
	public long getBaby_age() {
		return baby_age;
	}
	public void setBaby_age(long baby_age) {
		this.baby_age = baby_age;
	}
	String baby_circumference = "";
	public String getBaby_circumference() {
		return baby_circumference;
	}
	public void setBaby_circumference(String baby_circumference) {
		this.baby_circumference = baby_circumference;
	}
	public String getBaby_name() {
		return baby_name;
	}
	public void setBaby_name(String baby_name) {
		this.baby_name = baby_name;
	}
	public String getBaby_height() {
		return baby_height;
	}
	public void setBaby_height(String baby_height) {
		this.baby_height = baby_height;
	}
	public String getBaby_weight() {
		return baby_weight;
	}
	public void setBaby_weight(String baby_weight) {
		this.baby_weight = baby_weight;
	}
}
