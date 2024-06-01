package com.acintyo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorLog {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String tableName;
	
	private String filedName;
	
	private String reason;
	
	private Long recordId;
	
	@CreationTimestamp
	private LocalDateTime insertedAt;

}
