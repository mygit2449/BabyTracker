package com.BabyTracker.Model;

public class MedicalHistoryModel {

	String dateofvisit = "";
	String doctor_name = "";
	String purpose = "";
	String remarks = "";
	String note = "";
	int baby_id = 0;
	
	
	public int getBaby_id() {
		return baby_id;
	}
	public void setBaby_id(int baby_id) {
		this.baby_id = baby_id;
	}
	public String getDateofvisit() {
		return dateofvisit;
	}
	public void setDateofvisit(String dateofvisit) {
		this.dateofvisit = dateofvisit;
	}
	public String getDoctor_name() {
		return doctor_name;
	}
	public void setDoctor_name(String doctor_name) {
		this.doctor_name = doctor_name;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
