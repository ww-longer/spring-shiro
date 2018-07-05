package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.sys.commons.base.BaseEntity;

/**
 * <p>
 * 
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@TableName("outsource_allocation_repayment")
public class OutsourceAllocationRepayment extends BaseEntity {

    /**
     * 表ID
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 姓名
     */
	private String name;
    /**
     * 身份证号
     */
	@TableField("cust_id")
	private String custId;
    /**
     * 手机号
     */
	@TableField("tel_number")
	private String telNumber;
    /**
     * 借据号
     */
	private String ious;
    /**
     * 还款金额
     */
	@TableField("cur_amount")
	private Double curAmount;
    /**
     * 还款日期
     */
	@TableField("repayment_date")
	private Date repaymentDate;
    /**
     * 移交日期
     */
	private Date transfer;
    /**
     * 移交账龄
     */
	@TableField("hand_over_agecd")
	private Integer handOverAgecd;
    /**
     * 移交金额
     */
	@TableField("hand_over_amount")
	private Double handOverAmount;
    /**
     * 公司
     */
	private String company;
	/**
	 * 创建日期
	 */
	private Date creatDate;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 是否全额还款(0-全额,1-部分)
	 */
	private Integer isSumRefund;
    /**
     * 备注
     */
	private String remarks;

	private String startRepaymentDateTime;
	private String endRepaymentDateTime;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getIous() {
		return ious;
	}

	public void setIous(String ious) {
		this.ious = ious;
	}

	public Double getCurAmount() {
		return curAmount;
	}

	public void setCurAmount(Double curAmount) {
		this.curAmount = curAmount;
	}

	public Date getRepaymentDate() {
		return repaymentDate;
	}

	public void setRepaymentDate(Date repaymentDate) {
		this.repaymentDate = repaymentDate;
	}

	public Date getTransfer() {
		return transfer;
	}

	public void setTransfer(Date transfer) {
		this.transfer = transfer;
	}

	public Integer getHandOverAgecd() {
		return handOverAgecd;
	}

	public void setHandOverAgecd(Integer handOverAgecd) {
		this.handOverAgecd = handOverAgecd;
	}

	public Double getHandOverAmount() {
		return handOverAmount;
	}

	public void setHandOverAmount(Double handOverAmount) {
		this.handOverAmount = handOverAmount;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getCreatDate() {
		return creatDate;
	}

	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getIsSumRefund() {
		return isSumRefund;
	}

	public void setIsSumRefund(Integer isSumRefund) {
		this.isSumRefund = isSumRefund;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStartRepaymentDateTime() {
		return startRepaymentDateTime;
	}

	public void setStartRepaymentDateTime(String startRepaymentDateTime) {
		this.startRepaymentDateTime = startRepaymentDateTime;
	}

	public String getEndRepaymentDateTime() {
		return endRepaymentDateTime;
	}

	public void setEndRepaymentDateTime(String endRepaymentDateTime) {
		this.endRepaymentDateTime = endRepaymentDateTime;
	}
}
