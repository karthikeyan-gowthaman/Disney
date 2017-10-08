package com.ibm.rds.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * LoggerUtil Instance.
	 */
	private static LoggerUtil logger = LoggerUtil.getLogger(DateUtil.class.getName());

	/**
	 * This method gets current datetime
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String getCurrentDateTime()
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        Calendar cal = Calendar.getInstance();  
			cal.setTime(currentDateTime);  
			currentDateTime = cal.getTime();
	        //Apply formatting
	        return formatDate(currentDateTime, "yyyy-MM-dd'T'HH:mm:ss");
	    }
	// public static void main( String[] args ) throws Exception 	    {
	public static String getCurrentDateTimePlus4Days()
	  throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        // current date + 4 days
		 	Date FourthDaysDate = new Date(currentDateTime.getTime() + 4 *(1000 * 60 * 60 * 24));

	        Calendar cal = Calendar.getInstance();  
			cal.setTime(FourthDaysDate);  
			FourthDaysDate = cal.getTime();
			//String strda =formatDate(FourthDaysDate, "yyyy-MM-dd'T'HH:mm:ss");
	        //Apply formatting
			//System.out.println("FourthDaysDate **********" +strda);
	        return formatDate(FourthDaysDate, "yyyy-MM-dd'T'HH:mm:ss");
	    }
	/**
	 * This method gets the future dateTime i.e currentdatetime + 1 day
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String getFutureDateTime()
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        Calendar cal = Calendar.getInstance();  
			cal.setTime(currentDateTime);  
			cal.add(Calendar.DATE, 1);
			currentDateTime = cal.getTime();
	        //Apply formatting
	        return formatDate(currentDateTime, "yyyy-MM-dd'T'HH:mm:ss");
	    }

	/**
	 * This method gets the future dateTime i.e currentdatetime + noOfHour
	 * @param noOfHour
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String getCurrenteDateTime(int noOfHour)
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        Calendar cal = Calendar.getInstance();  
			cal.setTime(currentDateTime);  
			cal.add(Calendar.HOUR_OF_DAY, noOfHour);
			currentDateTime = cal.getTime();
	        //Apply formatting
	        return formatDate(currentDateTime, "yyyy-MM-dd'T'HH:mm:ss");
	    }

	/**
	 * This method gets the future dateTime i.e currentdatetime + noOfMins
	 * @param noOfMins
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String getFutureDateTime(int noOfMins)
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        Calendar cal = Calendar.getInstance();  
			cal.setTime(currentDateTime);  
			cal.add(Calendar.MINUTE, noOfMins);
			currentDateTime = cal.getTime();
	        //Apply formatting
	        return formatDate(currentDateTime, "yyyy-MM-dd'T'HH:mm:ss");
	    }

	/**
	 * This method format dates according to sterling standard
	 * @param inputDate
	 * @param outputFormat
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String formatDate(
         java.util.Date inputDate,
         String outputFormat)
         throws IllegalArgumentException, Exception
     {
         //Validate input date value
         if (inputDate == null)
         {
             throw new IllegalArgumentException("Input date cannot "
                     + " be null in DateUtils.formatDate method");
         }

         //Validate output date format
         if (outputFormat == null)
         {
             throw new IllegalArgumentException("Output format cannot"
                     + " be null in DateUtils.formatDate method");
         }

         //Apply formatting
         SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
         return formatter.format(inputDate);
     }

	/**
	 * This method retuen future dtae based on the noOfMonth passed in input
	 * by adding the noOfMothsto curent dateTime
	 * 
	 * @param noOfMonths
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String getCurrentPlusFutureDateTime(int noOfMonths)
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();
	        Calendar cal = Calendar.getInstance();  
			cal.setTime(currentDateTime);  
			cal.add(Calendar.MONTH, noOfMonths);
			currentDateTime = cal.getTime();
	        //Apply formatting
	        return formatDate(currentDateTime, "yyyy-MM-dd'T'HH:mm:ss");
	    }

}