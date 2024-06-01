package com.acintyo.controller;

import java.sql.SQLSyntaxErrorException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.acintyo.bindings.InvoiceResponse;
import com.acintyo.exceptions.ColumnNotFoundException;
import com.acintyo.exceptions.DistributerAndFileTyepNotFoundException;
import com.acintyo.exceptions.DistributerNotFoundException;
import com.acintyo.exceptions.DuplicateColumnNameException;
import com.acintyo.exceptions.InvalidDateFormatException;
import com.acintyo.exceptions.TableCreationException;
import com.acintyo.exceptions.TableNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class InvoiceTemplateExceptionHandler {
	
	@ExceptionHandler(ColumnNotFoundException.class)
	public ResponseEntity<?> handlerColumnNotFoundException(ColumnNotFoundException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
			.status(false)
			.message(ex.getMessage())
			.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	
	@ExceptionHandler(DistributerNotFoundException.class)
	public ResponseEntity<?> handlerDistributerNotFoundException(DistributerNotFoundException ex) {
		log.warn("Resolved ["+ex.getClass().getName()+"");
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
			.status(false)
			.message(ex.getMessage())
			.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	
	@ExceptionHandler(TableNotFoundException.class)
	public ResponseEntity<?> handlerTableNotFoundException(TableNotFoundException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
			.status(false)
			.message(ex.getMessage())
			.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	
	@ExceptionHandler(DuplicateColumnNameException.class)
	public ResponseEntity<?> handlerDuplicateColumnNameException(DuplicateColumnNameException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
			.status(false)
			.message(ex.getMessage())
			.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public ResponseEntity<?> handlerSQLSyntaxErrorException(SQLSyntaxErrorException ex) throws Exception {
		if(ex.getErrorCode()==1146) {
			InvoiceResponse invoiceResponse = InvoiceResponse.builder()
					.status(false)
					.message("Distributer Data not found")
					.error(ex.getClass().getSimpleName()).build();
			return ResponseEntity.ok(invoiceResponse);
			//throw  new TableNotFoundException("Table not found");
		}
		else {
			throw ex;
		}
		
	}
	
	public ResponseEntity<?> handlerInvalidDateFormatException(InvalidDateFormatException ex){
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
				.status(false)
				.message(ex.getMessage())
				.error(ex.getClass().getSimpleName()).build();
			return ResponseEntity.ok(invoiceResponse);
	}
	
	@ExceptionHandler(TableCreationException.class)
	public ResponseEntity<?> handlerTableCreationException(TableCreationException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
			.status(false)
			.message(ex.getMessage())
			.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	@ExceptionHandler(InvalidDateFormatException.class)
	public ResponseEntity<?> InvalidDateFormatException123(InvalidDateFormatException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
				.status(false)
				.message(ex.getMessage())
				.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	@ExceptionHandler(DistributerAndFileTyepNotFoundException.class)
	public ResponseEntity<?> DistributerAndFileTyepNotFoundException(DistributerAndFileTyepNotFoundException ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
				.status(false)
				.message(ex.getMessage())
				.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> IOAndCsvFileException(Exception ex) {
		InvoiceResponse invoiceResponse = InvoiceResponse.builder()
				.status(false)
				.message(ex.getMessage())
				.error(ex.getClass().getSimpleName()).build();
		return ResponseEntity.ok(invoiceResponse);
	}
}
