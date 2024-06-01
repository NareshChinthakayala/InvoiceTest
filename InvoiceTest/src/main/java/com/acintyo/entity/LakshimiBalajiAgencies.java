package com.acintyo.entity;
import jakarta.persistence.Column;
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
@Table(name = "LakshimiBalajiInvoice")
public class LakshimiBalajiAgencies {
	@Id
	//@GenericGenerator(name = "laksmi_id",type = LakshmiBalajiAgenciesIdGenerator.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recordId;
	
	@Column(name = "manfacture_name")
	private String manfactureName;
	
	@Column(name = "manfacture_short_name")
	private String manfactureShortName;
	
	@Column(name = "product_name")
	private String productName;

	@Column(name = "packing", length = 20)
	private String packing;
	
	@Column(name = "batch_no", length = 30)
	private String batchNO;
	
	@Column(name = "expiry")
	private String expiry;
	
	private String gst;
	
	private String rate;
	
	private String ptr;
	
	private String mrp;
	
	private String qty;
	
	@Column(name = "free_qty")
	private String freeQty;
	
	private String discount;
	
	@Column(name = "total_discount")
	private String totalDiscount;
	
	@Column(name = "total_amount")
	private String totalAmount;
	
	@Column(name = "mgr_id", length = 20)
	private String mgrId;
	
	@Column(name = "hsn_code",length = 20)
	private String hsnCode;
	
	@Column(length = 10)
	private String flag;
	
}
