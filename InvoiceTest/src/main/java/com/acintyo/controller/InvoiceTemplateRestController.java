package com.acintyo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.acintyo.bindings.CsvColumnDto;
import com.acintyo.bindings.InvoiceResponse;
import com.acintyo.entity.CsvFileColumnHeader;
import com.acintyo.entity.CsvFileHeader;
import com.acintyo.entity.ErrorLog;
import com.acintyo.entity.LakshimiBalajiAgencies;
import com.acintyo.service.IInvoiceService;
import com.opencsv.exceptions.CsvException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/distributers")
public class InvoiceTemplateRestController {
	
	@Autowired
	IInvoiceService invoiceService;
	
	@PostMapping(value="/upload/invoice-file", consumes = "multipart/form-data")
	public ResponseEntity<?> readFileData(
			@RequestParam String distId,
			@RequestParam Character isFirstRowHeader,
			@RequestParam Character delimiter,
			@RequestParam String fileType,
			@RequestParam("file") MultipartFile file) throws IOException, CsvException {
		
		if(file.isEmpty()) {
			return ResponseEntity.badRequest().body("Please upload a file.");
		}
			InvoiceResponse response = invoiceService.readFile(distId, delimiter,fileType, file);
			return new ResponseEntity<InvoiceResponse>(response,HttpStatus.OK);
	}
	
	@GetMapping(value = "/get/distributer-header")
	public ResponseEntity<?> getDistributer(@RequestParam String distId) {
		List<CsvFileHeader> list = invoiceService.getHeadersByDistributerId(distId);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@GetMapping(value = "/get/distributer-columns")
	public ResponseEntity<?> getCloumnByFieldId(String fieldId) {
		List<CsvFileColumnHeader> list = invoiceService.getColumnNamesByFileId(fieldId);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	@GetMapping(value = "/get/distributer-columnDtos")
	public ResponseEntity<?> getCloumnDtosByFieldId(String fieldId) {
		List<CsvColumnDto> list = invoiceService.getColumnDtosByFileId(fieldId);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@GetMapping("/get/table-data")
	public ResponseEntity<?> retrivingTableData(
			@RequestParam String distId,
			@RequestParam String filetype,
			@PageableDefault Pageable pageable){
		List<Map<String, Object>> tableData = invoiceService.getTableData((distId+"_"+filetype).replaceAll("-", ""),pageable);
		return ResponseEntity.status(HttpStatus.OK).body(tableData);
	}
	
	@PutMapping("/update/column")
	public ResponseEntity<?> updateColumn(
			@Valid @RequestBody CsvFileColumnHeader columnHeader,
			@RequestParam String distId,
			@RequestParam String type) {                                            //tableName = distId_type
		InvoiceResponse response = invoiceService.updateColumnByColId(columnHeader,(distId+"_"+type).replaceAll("-", ""));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping("/update/column-dto")
	public ResponseEntity<?> updateColumn(
			@Valid @RequestBody CsvColumnDto dto,
			@RequestParam String distId,
			@RequestParam String type) {                                   //tableName = distId_type
		InvoiceResponse response = invoiceService.updateColumnByColId(dto,(distId+"_"+type).replaceAll("-", ""));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@GetMapping("/get-lakshmiBalajiAgencies-data")
	public ResponseEntity<?> getLakshmiBalajiAgenciesDaga(@PageableDefault Pageable pageable){
		List<LakshimiBalajiAgencies> agencies=invoiceService.getLakshmiBalajiAgenciesData(pageable);
		return new ResponseEntity<List<LakshimiBalajiAgencies>>(agencies,HttpStatus.OK);
	}
	
	
	@GetMapping("/get-ErrorLogData")
	public ResponseEntity<?> getErrorLogData(@RequestParam String distId,@RequestParam String fileTyep){
		List<ErrorLog> errorLogs=invoiceService.getErrorLogData(distId, fileTyep);
		return new ResponseEntity<List<ErrorLog>>(errorLogs,HttpStatus.OK);
	}
	}
