package com.acintyo.validator;

import com.acintyo.exceptions.InvalidNumericDataException;

public class CsvFileValidator {

	public static void Validator(String numericValue, String columnNames, String Mrp) {
		if (numericValue.isBlank() || numericValue.isEmpty()) {
			throw new InvalidNumericDataException("' " + numericValue + " ' should not be null and blank");
		}
		try {
			Double value = Double.parseDouble(numericValue.replaceAll(",", ""));
			Double MrpValue = Double.parseDouble(Mrp.replace(",", ""));
			if (value < 0) {
				throw new InvalidNumericDataException("' " + numericValue + " ' should not be negative");

			} 
			else if (columnNames.equalsIgnoreCase("gst") || columnNames.equalsIgnoreCase("rate")
					|| columnNames.equalsIgnoreCase("Discount") || columnNames.equalsIgnoreCase("ptr")) {
				if (value > MrpValue)
					throw new InvalidNumericDataException("' " + numericValue + " ' should not be greater than MRP");
			}
		} catch (InvalidNumericDataException e) {
			throw new InvalidNumericDataException(e.getMessage());
		} catch (Exception e) {
			throw new NumberFormatException(
					"' " + numericValue + " ' do not pass charactors, value must be in numeric");
		}
	}
}
