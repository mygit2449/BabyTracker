package com.BabyTracker.Model;


public class AppointmentModel{

	String doctor_name = "";
	String dateofappointment = "";
	String timeofappointment = "";
	String purpose = "";
	String note = "";
	String datetime = "";
	int appointment_id;
	int reminderStatus;
	int appointment_babyid;
	
	
	public int getAppointment_babyid() {
		return appointment_babyid;
	}
	public void setAppointment_babyid(int appointment_babyid) {
		this.appointment_babyid = appointment_babyid;
	}
	public int getReminderStatus() {
		return reminderStatus;
	}
	public int isReminderStatus() {
		return reminderStatus;
	}
	public void setReminderStatus(int reminderStatus) {
		this.reminderStatus = reminderStatus;
	}
	public int getAppointment_id() {
		return appointment_id;
	}
	public void setAppointment_id(int appointment_id) {
		this.appointment_id = appointment_id;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getDoctor_name() {
		return doctor_name;
	}
	public void setDoctor_name(String doctor_name) {
		this.doctor_name = doctor_name;
	}
	public String getDateofappointment() {
		return dateofappointment;
	}
	public void setDateofappointment(String dateofappointment) {
		this.dateofappointment = dateofappointment;
	}
	public String getTimeofappointment() {
		return timeofappointment;
	}
	public void setTimeofappointment(String timeofappointment) {
		this.timeofappointment = timeofappointment;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
