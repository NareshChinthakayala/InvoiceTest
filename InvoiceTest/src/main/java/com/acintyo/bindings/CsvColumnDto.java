package com.acintyo.bindings;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvColumnDto {
	@NotBlank
	private String colId;
	@NotBlank
	private String columnName;
}
