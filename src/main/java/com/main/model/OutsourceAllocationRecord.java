package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.sys.commons.base.BaseController;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 委外分配日期记录表
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
@TableName("outsource_allocation_record")
public class OutsourceAllocationRecord extends BaseController {

    /**
     * 表id
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 姓名
     */
	private String name;
    /**
     * 省份证号
     */
	@TableField("cust_id")
	private String custId;
    /**
     * 电话号码
     */
	@TableField("tel_number")
	private String telNumber;
    /**
     * 借据号
     */
	private String ious;
    /**
     * 分期总金额
     */
	@TableField("total_amount")
	private String totalAmount;
    /**
     * 下一还款日
     */
	@TableField("next_refund_day")
	private String nextRefundDay;
    /**
     * 逾期金额
     */
	@TableField("amount_override")
	private String amountOverride;
    /**
     * 逾期本金
     */
	@TableField("overdue_principal")
	private String overduePrincipal;
    /**
     * 逾期利息
     */
	@TableField("overdue_accrual")
	private String overdueAccrual;
    /**
     * 罚息
     */
	@TableField("default_interest")
	private String defaultInterest;
    /**
     * 账龄
     */
	@TableField("age_cd")
	private String ageCd;
    /**
     * 逾期天数
     */
	private String overdue;
    /**
     * 借贷平台
     */
	@TableField("net_lending_platform")
	private String netLendingPlatform;
    /**
     * 是否独资
     */
	@TableField("is_sole_proprietorship")
	private String isSoleProprietorship;
    /**
     * 委外分配
     */
	@TableField("dca_distribution")
	private String dcaDistribution;
    /**
     * 案件类型
     */
	@TableField("the_case_distribution")
	private String theCaseDistribution;
	/**
	 * 移交日期
	 */
	@TableField("turn_over_day")
	private String turnOverDay;
    /**
     * 产品名称
     */
	@TableField("product_name")
	private String productName;
    /**
     * 总分期数
     */
	@TableField("total_aging")
	private String totalAging;
    /**
     * 合同生成日期
     */
	@TableField("contract_create_date")
	private String contractCreateDate;
    /**
     * 备注
     */
	private String remarks;
	/**
	 *  创建时间
	 */
	private Date createDate;
    /**
     * 创爱类型
     */
	private String createType;


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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getNextRefundDay() {
		return nextRefundDay;
	}

	public void setNextRefundDay(String nextRefundDay) {
		this.nextRefundDay = nextRefundDay;
	}

	public String getAmountOverride() {
		return amountOverride;
	}

	public void setAmountOverride(String amountOverride) {
		this.amountOverride = amountOverride;
	}

	public String getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(String overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public String getOverdueAccrual() {
		return overdueAccrual;
	}

	public void setOverdueAccrual(String overdueAccrual) {
		this.overdueAccrual = overdueAccrual;
	}

	public String getDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(String defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	public String getAgeCd() {
		return ageCd;
	}

	public void setAgeCd(String ageCd) {
		this.ageCd = ageCd;
	}

	public String getOverdue() {
		return overdue;
	}

	public void setOverdue(String overdue) {
		this.overdue = overdue;
	}

	public String getNetLendingPlatform() {
		return netLendingPlatform;
	}

	public void setNetLendingPlatform(String netLendingPlatform) {
		this.netLendingPlatform = netLendingPlatform;
	}

	public String getIsSoleProprietorship() {
		return isSoleProprietorship;
	}

	public void setIsSoleProprietorship(String isSoleProprietorship) {
		this.isSoleProprietorship = isSoleProprietorship;
	}

	public String getDcaDistribution() {
		return dcaDistribution;
	}

	public void setDcaDistribution(String dcaDistribution) {
		this.dcaDistribution = dcaDistribution;
	}

	public String getTheCaseDistribution() {
		return theCaseDistribution;
	}

	public void setTheCaseDistribution(String theCaseDistribution) {
		this.theCaseDistribution = theCaseDistribution;
	}

	public String getTurnOverDay() {
		return turnOverDay;
	}

	public void setTurnOverDay(String turnOverDay) {
		this.turnOverDay = turnOverDay;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTotalAging() {
		return totalAging;
	}

	public void setTotalAging(String totalAging) {
		this.totalAging = totalAging;
	}

	public String getContractCreateDate() {
		return contractCreateDate;
	}

	public void setContractCreateDate(String contractCreateDate) {
		this.contractCreateDate = contractCreateDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	protected Serializable pkVal() {
		return this.id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public static void main(String [] d){
        if ("zhzhehs这是怎么_SYS".indexOf("SYS") >= 0) {
            System.out.print("有");
        }else{
            System.out.print("没有");
        }
    }
}
