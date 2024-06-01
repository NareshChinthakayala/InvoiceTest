package com.acintyo.entity;

import org.hibernate.annotations.GenericGenerator;

import com.acintyo.generators.ColumnIdGenerator;

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
@Table(name = "CSVFILE_COLUMN_HEADER")
public class CsvFileColumnHeader {
	
	@Id
	@GenericGenerator(name = "column_id",type = ColumnIdGenerator.class)
	@GeneratedValue(generator = "column_id")
	private String colId;
	
	private String recordId;//should be combination of colId and fileId
	
	private String fileId;
	
	private String colName;
	
}
