
package com.acintyo.service;

import java.io.BufferedReader;import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acintyo.bindings.CsvColumnDto;
import com.acintyo.bindings.InvoiceResponse;
import com.acintyo.entity.CsvFileColumnHeader;
import com.acintyo.entity.CsvFileHeader;
import com.acintyo.entity.ErrorLog;
import com.acintyo.entity.Kk;
import com.acintyo.entity.LakshimiBalajiAgencies;
import com.acintyo.entitygenerator.DynamicTableGenerator;
import com.acintyo.exceptions.ColumnNotFoundException;
import com.acintyo.exceptions.DistributerAndFileTyepNotFoundException;
import com.acintyo.exceptions.DistributerNotFoundException;
import com.acintyo.repository.ICsvFileColumnHeaderRepository;
import com.acintyo.repository.ICsvFileHeaderRepository;
import com.acintyo.repository.IErrorLogRepository;
import com.acintyo.repository.IKkRepository;
import com.acintyo.repository.ILakshmiBalajiAgenciesRepository;
import com.acintyo.util.DateMapper;
import com.acintyo.validator.CsvFileValidator;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceServiceImpl implements IInvoiceService {

	@Autowired
	private ICsvFileHeaderRepository csvFileHeaderRepository;

	@Autowired
	private ICsvFileColumnHeaderRepository csvFileColumnHeaderRepository;

	@Autowired
	private ILakshmiBalajiAgenciesRepository lakshmiBalajiAgenciesRepository;
	
	@Autowired
	private IKkRepository kkRepository;

	@Autowired
	private DynamicTableGenerator dynamicTableGenerator;
	
	@Autowired
	private IErrorLogRepository errorLogRepository;

	@Autowired
	private Validator validator;

	@Transactional
	@Override
	public InvoiceResponse readFile(String distId, Character delimitor, String fileType, MultipartFile file)
			throws IOException, CsvException {
		log.info("readFile method started");
		List<String> distIdNames=List.of("");
		// Checking whether the distributer is LaxmiBalajiAgencies or not. (No headers for this distributer)
		
		if (distId.equalsIgnoreCase("laxmi")) {
			log.debug("distId is laxmi");
			CsvFileHeader csvFileHeader=new CsvFileHeader();
			Optional<CsvFileHeader> optional=csvFileHeaderRepository.findByDistributerIdIgnoreCaseAndFileTypeIgnoreCase(distId, fileType);
			log.debug("checking distId and fileType is available in csvFileHeaer table");
			if(optional.isEmpty())  {
				csvFileHeader.setDistributerId(distId);
				csvFileHeader.setDelimitor(delimitor);
				csvFileHeader.setFileType(fileType);
				csvFileHeader.setNoOfColumns(17);
				log.info("saving csvFileHeader");
			csvFileHeaderRepository.save(csvFileHeader);
			
			String[] columnNames= {"manfactureName","manfactureShortName","productName","packing",
					"packing","expiry","gst","rate","ptr","mrp","qty","freeQty","discount","totalDiscount",
					"totalAmount","mgrId","hsnCode"};
			for (String str : columnNames) {
				CsvFileColumnHeader fileColumnHeader = new CsvFileColumnHeader();
				fileColumnHeader.setFileId(csvFileHeader.getFileId());
				fileColumnHeader.setColName(str);
				CsvFileColumnHeader savedfileColumnHeader = csvFileColumnHeaderRepository.save(fileColumnHeader);
				savedfileColumnHeader
						.setRecordId(savedfileColumnHeader.getColId() + ":" + savedfileColumnHeader.getFileId());
				csvFileColumnHeaderRepository.save(savedfileColumnHeader);
			}
			}
			log.info("calling saveLakshmiBalajiAgencies method");
			return saveLakshmiBalajiAgencies(distId, delimitor, fileType, file);
		}else if(distId.equalsIgnoreCase("kk")) {
			log.info("distId is KK");
			CsvFileHeader csvFileHeader=new CsvFileHeader();
			Optional<CsvFileHeader> optional=csvFileHeaderRepository.findByDistributerIdIgnoreCaseAndFileTypeIgnoreCase(distId, fileType);
			if(optional.isEmpty()) {
				csvFileHeader.setDistributerId(distId);
				csvFileHeader.setDelimitor(delimitor);
				csvFileHeader.setFileType(fileType);
				csvFileHeader.setNoOfColumns(20);
				log.info("saving csvFileHeader data");
			csvFileHeaderRepository.save(csvFileHeader);
			
			String[] columnNames= {"billNo","date","mfr","hsnCode","productName","pack","batchNo",
					"expiry","mrp","qty","free","rate","amount","discount","taxableAmt","igst","taxAmt","total"};
			for (String str : columnNames) {
				CsvFileColumnHeader fileColumnHeader = new CsvFileColumnHeader();
				fileColumnHeader.setFileId(csvFileHeader.getFileId());
				fileColumnHeader.setColName(str);
				log.info("saving fileColumnHeader data");
				CsvFileColumnHeader savedfileColumnHeader = csvFileColumnHeaderRepository.save(fileColumnHeader);
				savedfileColumnHeader
						.setRecordId(savedfileColumnHeader.getColId() + ":" + savedfileColumnHeader.getFileId());
				log.info("combinning both coloumnId and FileId");
				csvFileColumnHeaderRepository.save(savedfileColumnHeader);
			}
			}
			log.info("calling saveKk method");
			return saveKk(distId, delimitor, fileType, file);
		}
		
		// If Distributer not present in our database
		else if (csvFileHeaderRepository.findByDistributerIdIgnoreCaseAndFileTypeIgnoreCase(distId, fileType)
				.isEmpty()) {
			log.info("distributor and fileType not present in out database");
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			log.info("reading file");
			String line = "";
			line = reader.readLine();
			List<String> dataHeders = dynamicTableGenerator
					.checkingDuplicateColumnNames(line.split("" + delimitor));
			log.info("New Headers : " + dataHeders);

			/* if (dataHeders.length != List.of(new HashSet<>(Arrays.asList(dataHeders))).size()) {
				throw new DuplicateColumnNameException("Column names are duplicate in csv file");
			} */
			
			// create CsvFileHeader
			CsvFileHeader fileHeader = new CsvFileHeader();
			fileHeader.setDistributerId(distId);
			fileHeader.setDelimitor(delimitor);
			fileHeader.setFileType(fileType);
			fileHeader.setNoOfColumns(dataHeders.size());

			// Save the CsvFileHeader
			log.info("saving csvFileHeader data");
			CsvFileHeader savedHeader = csvFileHeaderRepository.save(fileHeader);

			// Saving column Names into CsvFileColumnHeader
			for (String str : dataHeders) {
				CsvFileColumnHeader fileColumnHeader = new CsvFileColumnHeader();
				fileColumnHeader.setFileId(savedHeader.getFileId());
				fileColumnHeader.setColName(str);
				log.info("saving csvFileColumnHeader data");
				CsvFileColumnHeader savedfileColumnHeader = csvFileColumnHeaderRepository.save(fileColumnHeader);
				savedfileColumnHeader
						.setRecordId(savedfileColumnHeader.getColId() + ":" + savedfileColumnHeader.getFileId());
				csvFileColumnHeaderRepository.save(savedfileColumnHeader);
			}

			// Table name
			String tableName = (distId + "_" + fileType).replaceAll("-", "");
			
			// create table with Name DistId and columns with dataHeders
			log.info("calling createTable method in DynamicTableGenerator");
			dynamicTableGenerator.createTable(dataHeders, tableName);
			
			// Insert data into these table
			while ((line = reader.readLine()) != null) {
				String rows[] = line.split("" + delimitor);
				log.info("calling insertData method in DynamicTableGenerator");
				dynamicTableGenerator.insertData(dataHeders, rows, tableName);
			}
			// Response
			return InvoiceResponse.builder().status(true).message("File Data saved Successfully").build();

		}

		// If Distributer is alredy present
		else {
			log.info("distributor and file type present in our dataBase");
			return readExistingDistributerFile(distId, delimitor, fileType, file);
		}
	}
	/**
	 * Reads the CSV file data and inserts that data into database. Only if the
	 * distributer is already present in our database
	 * 
	 * @param distId    unique id of the distributer
	 * @param delimiter the character that is used for separating data in file
	 * @param fileType  specify whether the file is {@code Invoice or Inventory}
	 * @param file      file that contains data to be inserted into database
	 */
	@Transactional
	private InvoiceResponse readExistingDistributerFile(String distId, Character delimitor, String fileType,
			MultipartFile file) throws IOException {

		log.info("Method readExistingDistributerFile() called");
		CsvFileHeader savedHeader = csvFileHeaderRepository
				.findByDistributerIdIgnoreCaseAndFileTypeIgnoreCase(distId, fileType).get();

		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String line = "";
		line = reader.readLine();
		List<String> dataHeders = dynamicTableGenerator.checkingDuplicateColumnNames(line.split("" + delimitor));
		log.info("Headers : " + dataHeders);

		// table name
		String tableName = (distId + "_" + fileType).replaceAll("-", "");

		// getting all the previous columns
		List<String> prevCols = dynamicTableGenerator.getColumNamesByTableName(tableName);
		log.info("Old column names : " + prevCols);
		// taking new col headers into set
		Set<String> newCols = new HashSet<>(dataHeders);

		// removing all the matching columns from prevCols and newCols
		prevCols.forEach(col -> newCols.remove(col));
		log.info("New Column names : " + newCols);

		// inserting newCols into database table CsvFileColumnHeader
		for (String str : newCols) {
			CsvFileColumnHeader fileColumnHeader = new CsvFileColumnHeader();
			fileColumnHeader.setFileId(savedHeader.getFileId());
			fileColumnHeader.setColName(str);
			CsvFileColumnHeader savedfileColumnHeader = csvFileColumnHeaderRepository.save(fileColumnHeader);
			savedfileColumnHeader
					.setRecordId(savedfileColumnHeader.getColId() + "-" + savedfileColumnHeader.getFileId());
			csvFileColumnHeaderRepository.save(savedfileColumnHeader);

			// alter table (adding new columns to table)
			dynamicTableGenerator.addColumn(str, tableName);
		}
		// updating no of columns
		savedHeader.setNoOfColumns(savedHeader.getNoOfColumns() + newCols.size());
		csvFileHeaderRepository.save(savedHeader);
		// insert the records into database
		while ((line = reader.readLine()) != null) {
			String rows[] = line.split("" + delimitor);
			dynamicTableGenerator.insertData(dataHeders, rows, tableName);
		}
		// response
		return InvoiceResponse.builder().status(true).message("Distributer already present").build();
	}

	@Transactional
	@Override
	public InvoiceResponse saveLakshmiBalajiAgencies(String distId, Character delimitor, String FileType,
			MultipartFile file) throws IOException {

		// create CsvFileHeader
		/*CsvFileHeader fileHeader = new CsvFileHeader();
		fileHeader.setDistributerId(distId);
		fileHeader.setDelimitor(delimiter);
		fileHeader.setFileType(FileType);
		fileHeader.setNoOfColumns(17);
		
		// save the CsvFileHeader
		csvFileHeaderRepository.save(fileHeader);*/

		// "3,4,6,7,9,10,13,14,15,16,20,21,22,23,25,37,38";
		log.info("saveLakshmiBalajiAgencies() method  invoked");
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String line = "";

		//List<LakshimiBalajiAgencies> lakshimiBalajiAgencies = new ArrayList<>();
		List<String> columnNames = dynamicTableGenerator.getColumNamesByTableName("lakshimi_Balaji_Invoice");
		columnNames.forEach(cn->cn=cn.toUpperCase());
		while ((line = reader.readLine()) != null) {
			String data[] = line.split("" + delimitor);
			if (data[0].trim().equals("T") || data[0].trim().equals("t")) {
				
				/*String[] rows = { data[2],data[3],data[5],data[6],data[8],data[9],data[12],data[14],data[15],data[16],data[20],
							data[21],data[22],data[23],data[25],data[37],data[38]};
				
				dynamicTableGenerator.insertData(columnNames, rows, "Lakshimi_Balaji_Invoice");*/
				
				String flag = "true";
				LakshimiBalajiAgencies laxmiBalaji = new LakshimiBalajiAgencies();
				laxmiBalaji.setFlag(flag);
				laxmiBalaji.setManfactureName(data[2]);
				laxmiBalaji.setManfactureShortName(data[3]);
				laxmiBalaji.setProductName(data[5]);
				laxmiBalaji.setPacking(data[6]);
				laxmiBalaji.setBatchNO(data[8]);
				laxmiBalaji.setExpiry(/*DateMapper.standardizedDate*/(data[9]));
				laxmiBalaji.setGst(/*Double.parseDouble*/(data[12].trim()));
				laxmiBalaji.setRate(/*Double.parseDouble*/(data[14].trim()));
				laxmiBalaji.setPtr(/*Double.parseDouble*/(data[15].trim()));
				laxmiBalaji.setMrp(/*Double.parseDouble*/(data[16].trim()));
				laxmiBalaji.setQty(/*Double.parseDouble*/(data[20].trim()));
				laxmiBalaji.setFreeQty(/*Double.parseDouble*/(data[21].trim()));
				laxmiBalaji.setDiscount(/*Double.parseDouble*/(data[22].trim()));
				laxmiBalaji.setTotalDiscount(/*Double.parseDouble*/(data[23].trim()));
				laxmiBalaji.setTotalAmount(/*Double.parseDouble*/(data[25].trim()));
				laxmiBalaji.setMgrId(data[37]);
				laxmiBalaji.setHsnCode(data[38]);
				
				//adding numeric values to list
				List<String> numericValus=new ArrayList<>();
				numericValus.add(data[12]);
				numericValus.add(data[14]);
				numericValus.add(data[15]);
				numericValus.add(data[16]);
				numericValus.add(data[20]);
				numericValus.add(data[21]);
				numericValus.add(data[22]);
				numericValus.add(data[23]);
				numericValus.add(data[25]);
				List<String> cNames=List.of("GST","RATE","PTR","MRP","QTY","FREE QYT","Discount","Total Discount","Total Amount");
				
				LakshimiBalajiAgencies savedLaxmiBalaji = lakshmiBalajiAgenciesRepository.save(laxmiBalaji);
				List<LakshimiBalajiAgencies> balajiAgencies=List.of(savedLaxmiBalaji);
				try {
					System.out.println(data[9]);
					DateMapper.standardizedDate(data[9]);
				}catch(Exception e) {
					flag = "false";
					ErrorLog errorLog = ErrorLog.builder()
							.tableName("Lakshimi_Balaji_Invoice")
							.filedName("EXPIRY")
							.recordId(savedLaxmiBalaji.getRecordId())
							.reason("Invalid Date format : "+"' "+ data[9]+" ' "+ e.getMessage() + ", at Column : EXPIRY")
							.build();
					errorLogRepository.save(errorLog);
				}
				// checking validations
					//Set<ConstraintViolation<LakshimiBalajiAgencies>> violations = validator.validate(laxmiBalaji);
				for(int i=0;i<numericValus.size()-1;i++) {
				try {	
					}catch (Exception e) {
						flag = "false";
							ErrorLog errorLog = ErrorLog.builder()
									.tableName("laxmi_balaji_invoice")
									.filedName(cNames.get(i).toUpperCase())
									.recordId(savedLaxmiBalaji.getRecordId())
									.reason("Invalid numeric format :"+ e.getMessage()+ ", at Column : "+cNames.get(i))
									.build();
							errorLogRepository.save(errorLog);
						}
				}
				if(flag.equals("false")) {
					savedLaxmiBalaji.setFlag(flag);
					lakshmiBalajiAgenciesRepository.save(savedLaxmiBalaji);
				}
			}
		}
		//lakshmiBalajiAgenciesRepository.saveAll(lakshimiBalajiAgencies);
		log.info("lakshmiBalajiAgencies Data save successfully into database");
		return InvoiceResponse.builder().status(true).message("Data saved successfully").build();
	}
	@Transactional
	@Override
	public InvoiceResponse saveKk(String distId, Character delimitor, String FileType,
			MultipartFile file) throws IOException, CsvException {

		// "3,4,6,7,9,10,13,14,15,16,20,21,22,23,25,37,38";
		log.info("saveKk() method  invoked");
		//List<LakshimiBalajiAgencies> lakshimiBalajiAgencies = new ArrayList<>();
		//List<String> columnNames = dynamicTableGenerator.getColumNamesByTableName("Kk_Invoice");
		//columnNames.forEach(cn->cn=cn.toUpperCase());
		
		CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> records=csvReader.readAll();
            for (String[] record : records) {
            	String flag="true";
            	Kk kk = new Kk();
            	kk.setFlag(flag);
    			//kk.setPanNo(record[12]);
    			//kk.setPhoneNo(record[17]);
    			kk.setBillNo(record[19]);
    			kk.setDate(record[29]);
    			kk.setMfr(record[58]);
    			kk.setHsnCode(record[59]);
    			kk.setProductName(record[60]);
    			kk.setPack(record[61]);
    			kk.setBatchNo(record[62]);
    			kk.setExpiry(record[63]);
    			kk.setMrp(record[64]);
    			kk.setQty(record[65]);
    			kk.setFree(record[66]);
    			kk.setRate(record[67]);
    			kk.setAmount(record[68]);
    			kk.setDiscount(record[69]);
    			kk.setTaxableAmt(record[70]);
    			kk.setIgst(record[71]);
    			kk.setTaxAmt(record[72]);
    			kk.setTotal(record[73]);
				System.out.println(kk);
					System.out.println(kk.getDate());
					System.out.println(kk.getExpiry());		
				//adding numeric values to array
				String[] numericValus= {kk.getMrp(),kk.getQty(),
						kk.getFree(),kk.getRate(),kk.getAmount(),kk.getDiscount(),kk.getTaxableAmt(),
						kk.getIgst(),kk.getTaxAmt(),kk.getTotal()};
				
				String[] cNames= {"mrp","qty","free","rate","amount","discount",
						"taxableAmt","igst","taxAmt","total"};
				//adding date values to array
				String[] dateValue={kk.getDate(),kk.getExpiry()};
				
				String[] dateFieldName= {"date","expiry"};
				
				Kk savedKk = kkRepository.save(kk);
				
				//List<Kk> balajiAgencies=List.of(savedKk);
				
				for(int i=0;i<dateValue.length;i++)
				{
				try {
					//System.out.println(dateValue[i]);
					DateMapper.standardizedDate(dateValue[i]);
				}catch(Exception e) {
					flag = "false";
					ErrorLog errorLog = ErrorLog.builder()
							.tableName("Kk_Invoice")
							.filedName(dateFieldName[i].toUpperCase())
							.recordId(savedKk.getRecordId())
							.reason("Invalid Date format : "+"' "+ dateValue[i]+" ' "+ e.getMessage() + ", at Column : "+dateFieldName[i])
							.build();
					errorLogRepository.save(errorLog);
				}
				}
				// checking validations
					//Set<ConstraintViolation<LakshimiBalajiAgencies>> violations = validator.validate(laxmiBalaji);
				for(int i=0;i<numericValus.length;i++) {
				try {	
					//System.out.println(numericValus[i]);
					CsvFileValidator.Validator(numericValus[i],cNames[i],kk.getMrp());
					}catch (Exception e) {
						flag = "false";
							ErrorLog errorLog = ErrorLog.builder()
									.tableName("Kk_Invoice")
									.filedName(cNames[i].toUpperCase())
									.recordId(savedKk.getRecordId())
									.reason("Invalid numeric format :"+ e.getMessage()+ ", at Column : "+cNames[i])
									.build();
							errorLogRepository.save(errorLog);
						}
				}
				if(flag.equals("false")) {
					savedKk.setFlag(flag);
					kkRepository.save(savedKk);
				}
            }
		//lakshmiBalajiAgenciesRepository.saveAll(lakshimiBalajiAgencies);
		log.info("Kk Data save successfully into database");
		return InvoiceResponse.builder().status(true).message("Data saved successfully").build();
	}

	@Override
	public List<CsvFileHeader> getHeadersByDistributerId(String distId) {
		List<CsvFileHeader> list = csvFileHeaderRepository.findByDistributerId(distId);
		if (list.isEmpty()) {
			throw new DistributerNotFoundException("Distributer not found with given id ");
		}
		return list;
	}

	@Override
	public List<CsvFileColumnHeader> getColumnNamesByFileId(String fileId) {
		List<CsvFileColumnHeader> list = csvFileColumnHeaderRepository.findByFileId(fileId);
		return list;
	}

	@Override
	public List<CsvColumnDto> getColumnDtosByFileId(String fileId) {
		List<CsvFileColumnHeader> list = csvFileColumnHeaderRepository.findByFileId(fileId);
		List<CsvColumnDto> listOfDto = new ArrayList<>();
		for (CsvFileColumnHeader col : list) {
			CsvColumnDto dto = new CsvColumnDto();
			dto.setColId(col.getColId());
			dto.setColumnName(col.getColName());
			listOfDto.add(dto);
		}
		return listOfDto;
	}

	@Override
	public List<Map<String, Object>> getTableData(String tableName, Pageable pageable) {
		return dynamicTableGenerator.findTableData(tableName, pageable);
	}

	@Override
	@Transactional
	public InvoiceResponse updateColumnByColId(CsvFileColumnHeader fileHeader, String tableName) {
		Optional<CsvFileColumnHeader> optional = csvFileColumnHeaderRepository.findById(fileHeader.getColId());
		if (optional.isPresent()) {
			CsvFileColumnHeader fileColumnHeader = optional.get();
			String oldName = fileColumnHeader.getColName();
			fileColumnHeader.setColName(fileHeader.getColName());
			csvFileColumnHeaderRepository.save(fileColumnHeader);

			// rename the column name in table(invoice/inventry)
			renameColumn(tableName, oldName, fileHeader.getColName());

			return InvoiceResponse.builder().status(true).message("Column updated Successfully").build();

		} else {
			throw new ColumnNotFoundException("Column Id not found");
		}
	}

	@Override
	@Transactional
	public InvoiceResponse updateColumnByColId(CsvColumnDto dto, String tableName) {
		CsvFileColumnHeader fileColumnHeader = new CsvFileColumnHeader();
		fileColumnHeader.setColId(dto.getColId());
		fileColumnHeader.setColName(dto.getColumnName());
		return updateColumnByColId(fileColumnHeader, tableName);
	}

	private void renameColumn(String tableName, String oldName, String newName) {
		dynamicTableGenerator.renameColumn(tableName, oldName, newName);
	}

	@Override
	public List<LakshimiBalajiAgencies> getLakshmiBalajiAgenciesData(Pageable pageable) {

		return lakshmiBalajiAgenciesRepository.findAll();
	}
	
	@Override
	public List<ErrorLog> getErrorLogData(String distId, String fileType) {
		String tableName=(distId+"_"+fileType).replaceAll("-","");
		List<ErrorLog> errorLogs=errorLogRepository.findByTableNameIgnoreCase(tableName);
		if(errorLogs.isEmpty()) {
			throw new DistributerAndFileTyepNotFoundException("distributer and file type not found");
		}
		return errorLogs;
	}
	
	
}