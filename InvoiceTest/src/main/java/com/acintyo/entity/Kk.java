package com.acintyo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kk_invoice")
public class Kk {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recordId;
	
	//private String panNo;//12
	
	//private String phoneNo;//17
	
	private String billNo;//19
	
	private String date;//29
	
	private String mfr;//58
	
	private String hsnCode;//59
	
	private String productName;//60
	
	private String pack;//61
	
	private String batchNo;//62
	
	private String expiry;//63
	
	private String mrp;//64
	
	private String qty;//65
	
	private String free;//66
	
	private String rate;//67
	
	private String amount;//68
	
	private String discount;//69
	
	private String taxableAmt;//70
	
	private String igst;//71
	
	private String taxAmt;//72
	
	private String total;//73
	
	private String flag;
	
}
