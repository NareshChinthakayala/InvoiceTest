package com.acintyo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acintyo.entity.ErrorLog;

public interface IErrorLogRepository extends JpaRepository<ErrorLog,Integer> {
	
	List<ErrorLog> findByTableNameIgnoreCase(String tableName);

}
