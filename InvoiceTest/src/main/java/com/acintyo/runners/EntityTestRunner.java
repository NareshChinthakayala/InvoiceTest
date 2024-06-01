package com.acintyo.runners;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.acintyo.entity.CsvFileColumnHeader;
import com.acintyo.entitygenerator.DynamicEntityGenerator;
import com.acintyo.repository.ICsvFileColumnHeaderRepository;

//@Component
public class EntityTestRunner implements CommandLineRunner {

	@Autowired
	private ICsvFileColumnHeaderRepository columnHeaderRepository;
	
	@Override
	public void run(String... args) throws Exception {
	
		List<CsvFileColumnHeader> list = columnHeaderRepository.findByFileId("FILE-25");
		String[] columnNames = new String[12];
		int i=0;
//		list.forEach(ch->{columnNames[i]=ch.getColName();i++;});
		for(CsvFileColumnHeader ch : list) {
			columnNames[i++]=ch.getColName();
		}
		String packageName = "com.acintyo.entity";
//		"com.acintyo.entity";
				
		String className = "Dist1";
		
		DynamicEntityGenerator.generateAndCompileDynamicEntity(packageName, className, columnNames);
		
	}
}
