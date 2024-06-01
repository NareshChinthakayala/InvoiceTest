package com.acintyo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.acintyo.bindings.CsvColumnDto;
import com.acintyo.bindings.InvoiceResponse;
import com.acintyo.entity.CsvFileColumnHeader;
import com.acintyo.entity.CsvFileHeader;
import com.acintyo.entity.ErrorLog;
import com.acintyo.entity.LakshimiBalajiAgencies;
import com.opencsv.exceptions.CsvException;

public interface IInvoiceService {
	
	//Insert operations	
	public InvoiceResponse readFile(String distId, Character delimitor,String fileType, MultipartFile file) throws IOException, CsvException;
	public InvoiceResponse saveLakshmiBalajiAgencies(String distId, Character delimitor, String FileType, MultipartFile file) throws IOException;
	public InvoiceResponse saveKk(String distId, Character delimitor, String FileType, MultipartFile file) throws IOException, CsvException;

	//retrive
	public List<CsvFileHeader> getHeadersByDistributerId(String distId);
	public List<CsvFileColumnHeader> getColumnNamesByFileId(String fileId);
	public List<CsvColumnDto> getColumnDtosByFileId(String fileId);
	
	public List<Map<String, Object>> getTableData(String tableName, Pageable pageable);
	
	//update
	public InvoiceResponse updateColumnByColId(CsvFileColumnHeader fileHeader, String tableName);
	public InvoiceResponse updateColumnByColId(CsvColumnDto dto, String tableName);
	
	//get lakshmibalajiagencies
	public List<LakshimiBalajiAgencies> getLakshmiBalajiAgenciesData(Pageable pageable);
	
	//get errorLog data
	
	public List<ErrorLog> getErrorLogData(String distId,String fileType);
	
}
