package com.acintyo.entitygenerator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.acintyo.entity.ErrorLog;
import com.acintyo.exceptions.InvalidDateFormatException;
import com.acintyo.exceptions.TableCreationException;
import com.acintyo.repository.IErrorLogRepository;
import com.acintyo.util.DateMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DynamicTableGenerator {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IErrorLogRepository errorLogRepository;

	private static final List<String> numericCoumns =
			/*"FREEQTY", "DISC1", "DISC2", "SCHEME1", "SCHEME2", "SCHEMEVALUE", "SGST", "CGST", 
			 *"SGSTAMOUNT", "CGST","CGSTAMOUNT", "TAXAMOUNT", "TCSAMOUNT","SUBTOTAL", ,"SUMPRODDIS" */
			List.of("RATE", "PTR", "MRP", "QTY", "FREE", "DIS", "DISC", "DISCOUNT",    "SCHEME", 
					"GST", "AMOUNT", "STOCK", "CASH", "PRICE", "TCS", "TOTAL", "AMT", "FREIGHT",
					"SUM", "ADJUST", "ROUNDING", "PROVALUE");

	private static final List<String> dateColumns = List.of("DATE", "EXPIRY");

	/*private static final List<String> nonNullDateColumns = 
			List.of("DATE", "EXPIRY");*/

	// equals
	private static final List<String> nonNullStringFields = List.of(
			"PRODNAME", "PRODUCTNAME", "COMNAME", "COMPANYNAME", "ITEMNAME", "NAME", "PACKING", 
			"BATCHNO", "MGRID", "HSNCODE");

	// equals
	private static final List<String> nonNullNumericFileds = List.of(
			"MRP", "RATE", "GST", "PTR", "QTY", "FREE", "SGST", "CGST", "FREEQTY",  "DISCOUNT", 
			"AMOUNT", "FREEQTY", "TOTALAMOUNT", "TOTALDISCOUNT");

	private static final Set<String> nonNegativeNumericFields = Set.of(
			"MRP", "RATE", "GST", "PTR", "QTY", "FREE", "STOCK", "SGST", "CGST", "SGSTAMOUNT",   "CGSTAMOUNT",
			"DISCOUNT", "AMOUNT", "FREEQTY", "TOTALAMOUNT", "TOTALDISCOUNT", "RETPRICE", "SGSTPER",
			"CGSTPER", "TOTALQTY", "SUMGST", "SUMCGST", "NETAMT", "SUBTOTAL", "TAXABLEAMT", "IGST", "TAXAMT"
			);;

	
	public List<String> checkingDuplicateColumnNames(String columnNames[]) {
		// convert columnNames[] into LinkedHashSet
		Set<String> cNames = new LinkedHashSet<>();
		int i = 1;
		for (String name : columnNames) {
			name = name.replaceAll("[\\.\\s]", "").replace("/", "").toUpperCase();
			if (!cNames.contains(name)) {
				cNames.add(name);
			} else {
				// If duplicate found then appending 1,2.. to name
				cNames.add(name+ i++);
			}
		}
		return new ArrayList<>(cNames);
		/*try {
			StringBuilder table = new StringBuilder("create table if not exists ");
			table.append(tableName).append(" (id bigint auto_increment primary key, ");
			for (int i = 0; i < columnNames.length; i++) {
				String columnName = columnNames[i].replaceAll("[\\.\\s]", "").replace("/", ""); // Remove any spaces in
				table.append(columnName).append(" varchar(255)");
				if (i < columnNames.length - 1) {
					table.append(", ");
				}
			}
			table.append(")");
			System.out.println("Manual: " + table.toString());
			jdbcTemplate.execute(table.toString());
		} catch (Exception e) {
			log.info("creating table Exception : " + e);
			throw new TableCreationException(e.getMessage());
		}*/
	}

	/*public void createTable(Set<String> columnNames, String tableName) {
		try {
			StringBuilder table = new StringBuilder(
					"create table if not exists " + tableName + " (id bigint auto_increment primary key, ");
			for (String name : columnNames) {
				String columnName = name.replaceAll("[\\.\\s]", "").replace("/", "");
				table.append(columnName).append(" varchar(255), ");
			}
			table.setCharAt(table.length() - 2, ')');
			System.out.println("Manual: " + table.toString());
			jdbcTemplate.execute(table.toString());
		} catch (Exception e) {
			log.info("creating table Exception : " + e);
			throw new TableCreationException(e.getMessage());
		}
	}*/

	/**
	 * Creates table in database with the given {@code table name with given List of attributes.}
	 * @param columnNames Column names to create table with
	 * @param tableName Name of the table to create in database
	 * @throws TableCreationException if the SQL Query execution is failed. 
	 */
	public void createTable(List<String> columnNames, String tableName) {
		/*final List<String> numericCoumns = List.of("RATE", "PTR", "MRP", "QTY", "FREE", "DISC1", "DISC2", "DISCOUNT",
				"SCHEME1", "SCHEME2", "SCHEMEVALUE", "AMOUNT", "SGST", "SGSTAMOUNT", "CGST", "CGSTAMOUNT", "CASH",
				"TAXAMOUNT", "TCS", "TCSAMOUNT");*/
		try {
			StringBuilder table = new StringBuilder(
					"create table if not exists " + tableName + " (RECORD_ID bigint auto_increment primary key, ");
			for (String name : columnNames) {
				if(isDateField(name)) {
					table.append(name + " varchar(20), ");
				}
				else if(isNumericField(name)) {
					table.append(name + " varchar(20), ");
				}
				else {
					table.append(name + " varchar(255), ");
				}
			}
			table.append(" FLAG varchar(10))");
			// table.setCharAt(table.length() - 2, ')');
			System.out.println("Manual: " + table.toString());
			jdbcTemplate.execute(table.toString());
		} catch (Exception e) {
			log.info("creating table Exception : " + e);
			throw new TableCreationException(e.getMessage());
		}
	}

	private boolean isDateField(String columnName) {
		for (String dateColumn : dateColumns) {
			if (columnName.contains(dateColumn)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNumericField(String columnName) {
		for (String numericCoumn : numericCoumns) {
			if (columnName.contains(numericCoumn)) {
				return true;
			}
		}
		return false;
	}

	/*public void insertData(String cNames[], String[] row, String tableName) {
		StringBuilder insertSql = new StringBuilder("insert into ");
		insertSql.append(tableName + "(");
		for (int i = 0; i < cNames.length; i++) {
			insertSql.append(cNames[i]);
			if (i < cNames.length - 1) {
				insertSql.append(", ");
			}
		}
		insertSql.append(") values (");
		for (int i = 0; i < cNames.length; i++) {
			insertSql.append("'").append(row[i]).append("'");
			if (i < cNames.length - 1) {
				insertSql.append(", ");
			}
		}
		insertSql.append(")");
		System.out.println("Manual: " + insertSql);
		jdbcTemplate.execute(insertSql.toString());
	}*/

	public void insertData(List<String> columnNames, String[] row, String tableName) {
		// finding Date column index and Numeric column index
		List<Integer> dateFieldIndex = new ArrayList<>();
		List<Integer> numericFieldIndex = new ArrayList<>();
		try {
			for (int i = 0; i < columnNames.size(); i++) {
				String columnName = columnNames.get(i);
				if (isDateField(columnName)) {
					dateFieldIndex.add(i);
				} else if (isNumericField(columnName)) {
					if (!columnName.equals("MRPINCL"))
						numericFieldIndex.add(i);
				}
			}

			String flag = "true";
			// INSERT QUERY STARTS HERE
			StringBuilder insertSql = new StringBuilder("insert into " + tableName + "(");
			for (String name : columnNames) {
				String columnName = name;
				insertSql.append(columnName + ", ");
			}
			insertSql.append(" flag )");
			// insertSql.setCharAt(insertSql.length() - 2, ')');
			insertSql.append(" values (");
			for (int i = 0; i < columnNames.size() && i<row.length ; i++) {
				// For date fields
				if (dateFieldIndex.contains(i)) {
					if (row[i] == null || row[i].isBlank()) {
						if (columnNames.get(i).equals("EXPIRY") || columnNames.get(i).equals("DATE")
								|| columnNames.get(i).equals("EXPIRYS")) {
							flag = "false";
							ErrorLog errorLog = ErrorLog.builder().tableName(tableName).filedName(columnNames.get(i))
									.recordId(getLastRecordId(tableName) + 1).reason("Invalid Date format : " + row[i]
											+ "date is NULL at Column : " + columnNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
							/*throw new InvalidDateFormatException
								("Invalid Date format : "+ row[i] + "date is null at Column : " + columnNames.get(i));*/
						}
						insertSql.append("null, ");
					} else {
						try {
							LocalDate date = DateMapper.standardizedDate(row[i]);
							insertSql.append("'").append(date.toString()).append("', ");
						} catch (InvalidDateFormatException e) {
							flag = "false";
							insertSql.append("'").append(row[i]).append("', ");
							ErrorLog errorLog = ErrorLog.builder().tableName(tableName).filedName(columnNames.get(i))
									.recordId(getLastRecordId(tableName) + 1)
									.reason("Invalid Date format : " + row[i] + ", at Column : " + columnNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
							/*throw new InvalidDateFormatException
							("Invalid Date format : "+ row[i] + ", at Column : " + columnNames.get(i));*/
						}

					}
				}
				// For numeric fields
				else if (numericFieldIndex.contains(i)) {
					if (row[i] == null || row[i].isBlank() || row[i].isEmpty()) {
						if (nonNullNumericFileds.contains(columnNames.get(i))) {
							flag = "false";
							ErrorLog errorLog = ErrorLog.builder().tableName(tableName).filedName(columnNames.get(i))
									.recordId(getLastRecordId(tableName) + 1)
									.reason("Excepted numberic value found : NULL at Column : " + columnNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
						}
						insertSql.append("null, ");
					} else {
						try {
							double numericData = Double.parseDouble(row[i]);
							insertSql.append("'" + numericData + "', ");
							if (nonNegativeNumericFields.contains(columnNames.get(i))  && numericData < 0) {
								flag = "false";
								ErrorLog errorLog = ErrorLog.builder()
										.tableName(tableName)
										.filedName(columnNames.get(i))
										.recordId(getLastRecordId(tableName) + 1).reason("Excepted a Positive numberic value found : "
												+ row[i] + " at Column : " + columnNames.get(i))
										.build();
								errorLogRepository.save(errorLog);
							}
						} catch (NumberFormatException e) {
							flag = "false";
							insertSql.append("'" + row[i] + "', ");
							ErrorLog errorLog = ErrorLog.builder().tableName(tableName).filedName(columnNames.get(i))
									.recordId(getLastRecordId(tableName) + 1).reason("Excepted numberic value found : "
											+ row[i] + " at Column : " + columnNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
							/*throw new InvalidNumericDataException(
									"Excepted Positive numberic data found : " + row[i] + " at Column : " + columnNames.get(i));*/
						}
					}
				}
				// For String fields
				else {
					if (row[i] == null || row[i].isBlank()) {
						if (nonNullStringFields.contains(columnNames.get(i))) {
							insertSql.append("null, ");
							ErrorLog errorLog = ErrorLog.builder().tableName(tableName).filedName(columnNames.get(i))
									.recordId(getLastRecordId(tableName) + 1)
									.reason("Excepted a text value, found NULL at Column : " + columnNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
						} else {
							insertSql.append("null, ");
						}
					} else {
						insertSql.append("'").append(row[i]).append("', ");
					}
				}
			}
			for(int i=0;i<(columnNames.size()-row.length);i++) {
				insertSql.append("NULL, ");
			}
			
			insertSql.append(" '" + flag + "' )");
			System.out.println("Manual: " + insertSql);
			jdbcTemplate.execute(insertSql.toString());
		} catch (Exception e) {
			throw e;
		}
	}
	
	public long getLastRecordId(String tableName) {
		String query = "SELECT RECORD_ID FROM " + tableName + " ORDER BY RECORD_ID DESC LIMIT 1";
		System.out.println("Manual: " + query);
		try {
			return jdbcTemplate.queryForObject(query, Long.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	/*public void addColumns(String[] cNames, String tableName) {
		StringBuilder alterQuery = new StringBuilder("alter table ").append(tableName).append(" add column(");
		for (int i = 0; i < cNames.length; i++) {
			alterQuery.append(cNames[i].replaceAll("[\\.\\s]", "").replace("/", "") + " varchar(255)");
			if (i < cNames.length - 1) {
				alterQuery.append(", ");
			}
		}
		alterQuery.append(")");
		System.out.println("Manual: " + alterQuery);
		jdbcTemplate.update(alterQuery.toString());
	}*/

	public void addColumn(String cName, String tableName) {
		cName = cName.toUpperCase();
		StringBuilder alterQuery = new StringBuilder("alter table " + tableName + " add column(" + cName);

		if (cName.contains("MRPINCL")) {
			alterQuery.append(" varchar(20) ) ");
		} else if (isDateField(cName)) {
			alterQuery.append(" VARCHAR(20) )");
		} else if (isNumericField(cName)) {
			alterQuery.append(" VARCHAR(20) )");
		} else {
			alterQuery.append(" varchar(255) ) ");
		}

		System.out.println("Manual: " + alterQuery);
		jdbcTemplate.update(alterQuery.toString());
	}
	public List<Map<String, Object>> findTableData(String tableName, Pageable pageable) {
		// Execute SQL query to retrieve data from the specified table
		StringBuilder selectQuery = new StringBuilder("select * from " + tableName + " order by ");
		// for sorting
		for (Sort.Order order : pageable.getSort()) {
			selectQuery.append(order.getProperty() + " " + order.getDirection() + ",");
		}
		// removing last ,
		selectQuery.setCharAt(selectQuery.length() - 1, ' ');
		// for pagination
		selectQuery.append(" Limit " + pageable.getPageSize() + " offset " + pageable.getOffset());
		System.out.println("Manual: " + selectQuery);
		return jdbcTemplate.queryForList(selectQuery.toString());
	}

	public void renameColumn(String tableName, String oldName, String newName) {
		String rename = "alter table " + tableName + " rename column "+ oldName + " to "+ newName;
		System.out.println("Manual: " + rename);
		jdbcTemplate.execute(rename);
	}

	public List<String> getColumNamesByTableName(String tableName) {
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";
		List<String> list = jdbcTemplate.queryForList(query, String.class, tableName);
		return list;
	}
}