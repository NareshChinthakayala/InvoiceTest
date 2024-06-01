package com.acintyo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acintyo.entity.CsvFileColumnHeader;
import java.util.List;
import java.util.Optional;


public interface ICsvFileColumnHeaderRepository extends JpaRepository<CsvFileColumnHeader, String>{

	public List<CsvFileColumnHeader> findByFileId(String fileId);
	//public Optional<CsvFileColumnHeader> findByFileId(String fileId);
	
}
