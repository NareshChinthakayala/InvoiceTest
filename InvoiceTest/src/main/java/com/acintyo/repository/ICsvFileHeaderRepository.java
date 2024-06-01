package com.acintyo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acintyo.entity.CsvFileHeader;

public interface ICsvFileHeaderRepository extends JpaRepository<CsvFileHeader, String>{
	public Optional<CsvFileHeader> findByDistributerIdIgnoreCaseAndFileTypeIgnoreCase(String distId, String fileType);
	public List<CsvFileHeader> findByDistributerId(String distId);
}
