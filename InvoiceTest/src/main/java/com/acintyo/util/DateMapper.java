package com.acintyo.util;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.acintyo.exceptions.InvalidDateFormatException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateMapper {
	
	//entity class datamapper code
	public static LocalDate standardizedDate(String dateString) {
		log.info("standardizedDate :" +dateString);
		
		if(dateString.isBlank() || dateString == null) {
			throw new InvalidDateFormatException(" Date format should not be null and blank");
		}
		if (isValidDatePattern(dateString)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MMM");
                YearMonth yearMonth = YearMonth.parse(dateString, formatter);
                return yearMonth.atDay(1);
            } 
            catch (DateTimeParseException e) {
            }
        }

		LocalDate date = null;
		DateTimeFormatter[] formatters = {
				//For MONTH values : JAN, FEB, MAR,....
				DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
				DateTimeFormatter.ofPattern("d-MMM-yyyy"),
				DateTimeFormatter.ofPattern("d-MMM-yy"),
				DateTimeFormatter.ofPattern("dd/MMM/yyyy"),
				DateTimeFormatter.ofPattern("d/MMM/yyyy"),
				DateTimeFormatter.ofPattern("dd-MMM-yy"),
				DateTimeFormatter.ofPattern("d-MMM-yy"),
				DateTimeFormatter.ofPattern("dd/MMM/yy"),
				DateTimeFormatter.ofPattern("d/MMM/yy"),
				DateTimeFormatter.ofPattern("M/yyyy"),
				
				//For YYYY-MM-DD
				DateTimeFormatter.ofPattern("yyyy-MM-dd"),
				DateTimeFormatter.ofPattern("yyyy-M-dd"),
				DateTimeFormatter.ofPattern("yyyy-MM-d"),
				DateTimeFormatter.ofPattern("yyyy-M-d"),
				DateTimeFormatter.ofPattern("yyyy/M/d"),
				DateTimeFormatter.ofPattern("yyyy/MM/dd"),
				DateTimeFormatter.ofPattern("yyyy/M/dd"),
				DateTimeFormatter.ofPattern("yyyy/MM/d"),
				
				
				//For only month and year
				/*
				 * DateTimeFormatter.ofPattern("MM/yy"),
				 * DateTimeFormatter.ofPattern("MM/yyyy"),
				 */
				
				DateTimeFormatter.ofPattern("dd-MM-yyyy"),
				DateTimeFormatter.ofPattern("dd-MM-yy"),
				DateTimeFormatter.ofPattern("dd-M-yyyy"),
				DateTimeFormatter.ofPattern("dd-M-yy"),
				DateTimeFormatter.ofPattern("d-MM-yyyy"),
				DateTimeFormatter.ofPattern("dd-MM-yy"),
				DateTimeFormatter.ofPattern("d-M-yyyy"),
				DateTimeFormatter.ofPattern("dd-MMM"),
				DateTimeFormatter.ofPattern("dd/MM/yyyy"),
				DateTimeFormatter.ofPattern("dd/MM/yy"),
				DateTimeFormatter.ofPattern("dd/M/yyyy"),
				DateTimeFormatter.ofPattern("dd/M/yy"),
				DateTimeFormatter.ofPattern("d/MM/yyyy"),
				DateTimeFormatter.ofPattern("dd/MM/yy"),
				DateTimeFormatter.ofPattern("d/M/yyyy"),
				DateTimeFormatter.ofPattern("M/dd/yyyy"),
				DateTimeFormatter.ofPattern("M/d/yyyy"),
				DateTimeFormatter.ofPattern("MM/d/yyyy"),
				DateTimeFormatter.ofPattern("MM/dd/yyyy"),
				DateTimeFormatter.ofPattern("ddMMyyyy"),
				DateTimeFormatter.ofPattern("ddMMyy"),
				DateTimeFormatter.ofPattern("dMMyyyy"),
				DateTimeFormatter.ofPattern("Mddyyyy"),
				DateTimeFormatter.ofPattern("MMddyyyy"),
				DateTimeFormatter.ofPattern("MMdyyyy")
		};
		
		for(DateTimeFormatter formatter : formatters) {
			try {
                date = LocalDate.parse(dateString, formatter);
                return date;
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
		}
		
		dateString = dateString.trim();
		StringBuilder sb = new StringBuilder();
		int seperator = 0;
		for (char ch : dateString.toCharArray()) {
			if (Character.isDigit(ch)) {
				sb.append(ch);
			} else {
				seperator++;
			}
		}
		
		//M yyyy
		if(sb.length()==5 && seperator==1) {
			int year = Integer.parseInt("" + sb.charAt(1) + sb.charAt(2) + sb.charAt(3) + sb.charAt(4));
			int month = Integer.parseInt("" + sb.charAt(0));
			date=LocalDate.of(year, month, 1);
			return date;
		}
		//M YYYY
		else if(sb.length()==5 && seperator==0) {
			int year = Integer.parseInt("" + sb.charAt(1) + sb.charAt(2) + sb.charAt(3) + sb.charAt(4));
			int month = Integer.parseInt("" + sb.charAt(0));
			date=LocalDate.of(year, month, 1);
			return date;
		}
		//MM YYYY
		else if(sb.length()==6 && seperator==0) {
			int year = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3) + sb.charAt(4) + sb.charAt(5));
			int month = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			date=LocalDate.of(year, month, 1);
			return date;
		}
		
		//dMMyyy5y
		else if(sb.length()==7 && seperator==0) {
			int year = Integer.parseInt("" + sb.charAt(3) + sb.charAt(4) + sb.charAt(5) + sb.charAt(6));
			int month = Integer.parseInt("" + sb.charAt(1) + sb.charAt(2));
			int day = Integer.parseInt("" + sb.charAt(0));
            date=LocalDate.of(year, month, day);
            return date;
		}
		
		// MM YY count : 4, spaces:1
		else if (sb.length() == 4 && seperator == 1) {
			int year = Integer.parseInt("20" + sb.charAt(2) + sb.charAt(3));
			int month = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
			return date;
		}
		// MM YYYY count : 6, spaces:1
		else if (sb.length() == 6 && seperator == 1) {
			int year = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3) + sb.charAt(4) + sb.charAt(5));
			int month = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
			return date;
		}
		// M YY count : 3, spaces:1
		if (sb.length() == 3 && seperator == 1) {
			int year = Integer.parseInt("20" + sb.charAt(1) + sb.charAt(2));
			int month = Integer.parseInt("" + sb.charAt(0));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
			return date;
		}
		
		throw new InvalidDateFormatException("Date format is invalid");
		
        
		/*stringDate = stringDate.trim();
		if (stringDate.isBlank()) {
			throw new InvalidDateFormatException("Date format is invalid");
		}
		 
		StringBuilder sb = new StringBuilder();
		int seperator = 0;
		for (char ch : stringDate.toCharArray()) {
			if (Character.isDigit(ch)) {
				sb.append(ch);
			} else {
				seperator++;
			}
		}
		
		LocalDate date = null;
		
		if (sb.length() < 3 || sb.length() > 8) {
			throw new InvalidDateFormatException();
		}
		// DD MM YYYY count : 8, spaces:2
		else if (sb.length() == 8 && seperator == 2) {
			int year = Integer.parseInt("" + sb.charAt(4) + sb.charAt(5) + sb.charAt(6) + sb.charAt(7));
			int month = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3));
			int day = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			date = LocalDate.of(year, month, day);
		}
		// DD M YYYY count : 7, spaces:2
		else if (sb.length() == 7 && seperator == 2) {
			int year = Integer.parseInt("" + sb.charAt(3) + sb.charAt(4) + sb.charAt(5) + sb.charAt(6));
			int month = Integer.parseInt("" + sb.charAt(2));
			int day = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			date = LocalDate.of(year, month, day);
		}
		// D MM YYYY count : 7, spaces:0
		else if (sb.length() == 7 && seperator == 0) {
			int year = Integer.parseInt(
					"" + stringDate.charAt(3) + stringDate.charAt(4) + stringDate.charAt(5) + stringDate.charAt(6));
			int month = Integer.parseInt("" + stringDate.charAt(1) + stringDate.charAt(2));
			int day = Integer.parseInt("" + stringDate.charAt(0));
			System.out.println(stringDate);
			System.out.println("day : " + day);
			return LocalDate.of(year, month, day);
		}
		// DD MM YY count : 6, spaces:2
		else if (sb.length() == 6 && seperator == 2) {
			int year = Integer.parseInt("" + sb.charAt(4) + sb.charAt(5));
			int month = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3));
			int day = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			date = LocalDate.of(year, month, day);
		}
		
		// D M YY count : 4, spaces:2
		else if (sb.length() == 4 && seperator == 2) {
			int year = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3));
			int month = Integer.parseInt("" + sb.charAt(1));
			int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, day);
		}
		// MM YYYY count : 6, spaces:1
		else if (sb.length() == 6 && seperator == 1) {
			int year = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3) + sb.charAt(4) + sb.charAt(5));
			int month = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
		}
		// M YYYY count : 5, spaces:1
		else if (sb.length() == 5 && seperator == 1) {
			int year = Integer.parseInt("" + sb.charAt(1) + sb.charAt(2) + sb.charAt(3) + sb.charAt(4));
			int month = Integer.parseInt("" + sb.charAt(0));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
		}
		// MM YY count : 4, spaces:1
		else if (sb.length() == 4 && seperator == 1) {
			int year = Integer.parseInt("" + sb.charAt(2) + sb.charAt(3));
			int month = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
		}
		// MM YY count : 3, spaces:1
		else if (sb.length() == 3 && seperator == 1) {
			int year = Integer.parseInt("" + sb.charAt(1) + sb.charAt(2));
			int month = Integer.parseInt("" + sb.charAt(0));
			// int day = Integer.parseInt("" + sb.charAt(0));
			date = LocalDate.of(year, month, 1);
		}
		if (date == null)
			throw new InvalidDateFormatException("Date format is invalid");
		return date;*/
	}
	
	private static boolean isValidDatePattern(String date) {
		String regex = "^[0-9]{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)$";
      Pattern compiledPattern = Pattern.compile(regex);
      Matcher matcher = compiledPattern.matcher(date);
      return matcher.matches();
	}
}