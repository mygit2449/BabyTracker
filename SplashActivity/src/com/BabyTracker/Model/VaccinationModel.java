package com.BabyTracker.Model;


public class VaccinationModel {

	String vaccination_name;
	int vaccination_starttime;
	int vaccination_endtime;
	String vaccination_description;
	int vaccination_id;
	int background_resource;
	String vaccination_status;
	String vaccination_time;
	
	
	public String getVaccination_time() {
		return vaccination_time;
	}
	public void setVaccination_time(String vaccination_time) {
		this.vaccination_time = vaccination_time;
	}
	public String getVaccination_status() {
		return vaccination_status;
	}
	public void setVaccination_status(String vaccination_status) {
		this.vaccination_status = vaccination_status;
	}
	public int getBackground_resource() {
		return background_resource;
	}
	public void setBackground_resource(int background_resource) {
		this.background_resource = background_resource;
	}
	public String getVaccination_name() {
		return vaccination_name;
	}
	public void setVaccination_name(String vaccination_name) {
		this.vaccination_name = vaccination_name;
	}
	public int getVaccination_starttime() {
		return vaccination_starttime;
	}
	public void setVaccination_starttime(int vaccination_starttime) {
		this.vaccination_starttime = vaccination_starttime;
	}
	public int getVaccination_endtime() {
		return vaccination_endtime;
	}
	public void setVaccination_endtime(int vaccination_endtime) {
		this.vaccination_endtime = vaccination_endtime;
	}
	public String getVaccination_description() {
		return vaccination_description;
	}
	public void setVaccination_description(String vaccination_description) {
		this.vaccination_description = vaccination_description;
	}
	public int getVaccination_id() {
		return vaccination_id;
	}
	public void setVaccination_id(int vaccination_id) {
		this.vaccination_id = vaccination_id;
	}
	
}
