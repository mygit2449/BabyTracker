package com.BabyTracker.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class AgeCalculater {
	int ageYears, ageDays, ageMonths, day, month;

	public String calculateAge(String dob){
		try{
			
			SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			Date date_of_birth = mDateFormat.parse(dob);
			Calendar dobCal = Calendar.getInstance();
			dobCal.setTime(date_of_birth);
			
			long currentTime = System.currentTimeMillis();
			Calendar currentDay = Calendar.getInstance();
			currentDay.setTimeInMillis(currentTime);
			
			return getAge(dobCal, currentDay);			

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getAge(Calendar birthDay, Calendar currentDay)
	{
	
		Log.v(getClass().getSimpleName(), "birth day  "+birthDay.getTime());
		ageYears = currentDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
		
		int currMonth = currentDay.get(Calendar.MONTH)+1;
		int birthMonth = birthDay.get(Calendar.MONTH)+1;
		 
		//Get difference between months
		ageMonths = currMonth - birthMonth;
		 
		//if month difference is in negative then reduce years by one and calculate the number of months.
		if(ageMonths < 0)
		{
			ageYears--;
			ageMonths = 12 - birthMonth + currMonth;
		  
		 if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE))
			 ageMonths--;
		  
		}else if(ageMonths == 0 && currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
		{
			ageYears--;
			ageMonths = 11;
		}
		 
		//Calculate the days
		if(currentDay.get(Calendar.DATE)>birthDay.get(Calendar.DATE))
		{
			ageDays = currentDay.get(Calendar.DATE) -  birthDay.get(Calendar.DATE);
		}
		else if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE))
		{
			
		 int today = currentDay.get(Calendar.DAY_OF_MONTH);
		 currentDay.add(Calendar.MONTH, -1);
		 ageDays = currentDay.getActualMaximum(Calendar.DAY_OF_MONTH)-birthDay.get(Calendar.DAY_OF_MONTH)+today;
		 
		}else
		{
			ageDays=0;
		  
			 if(ageMonths == 12)
			 {
				 ageYears++;
				 ageMonths = 0;
			 }
		 
		}
		
		
		System.out.println("The age is : "+ageYears+" years, "+ageMonths+" months and "+ageDays+" days" );
		
		if(ageYears > 0)
		{
			if(ageMonths == 0)
			{
				
				return ((ageYears > 1) ? ageYears + " Y " : ageYears + " Y ") 
				   + ((ageDays > 1) ? ageDays + " Days " : ageDays + " Day ");	
				
			}else
			{
				
				return ((ageYears > 1) ? ageYears + " Ys " : ageYears + " Y ") 
					   + ((ageMonths > 1) ? ageMonths + " Ms " : ageMonths + " M ")
					   + ((ageDays > 1) ? ageDays + " Ds " : ageDays + " D ");
			}
			
		}else if(ageYears == 0)
		{
			if(ageMonths == 0)
			{
				return ((ageDays > 1) ? ageDays + " Days " : ageDays + " Day ");	
				
			}else
			{
				return  ((ageMonths > 1) ? ageMonths + " Ms " : ageMonths + " M ")
				   + ((ageDays > 1) ? ageDays + " Ds " : ageDays + " D ");
			}
		}
		
		return null;
	}

}
