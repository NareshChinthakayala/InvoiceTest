package com.acintyo.entity;

import org.hibernate.annotations.GenericGenerator;

import com.acintyo.generators.FileIdGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CSVFile_HEADER")
public class CsvFileHeader {
	
	@Id
	@GenericGenerator(name = "file_id",type = FileIdGenerator.class)
	@GeneratedValue(generator = "file_id")
	private String fileId;
	
	private String distributerId;
	
	private Integer noOfColumns;
	
	private Character delimitor;
	
	private String fileType;
}
