package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.sys.commons.base.BaseController;


/**
 * <p>
 * 
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@TableName("new_add_ious")
public class NewAddIous extends BaseController {

	/**
	 * 表ID
	 */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	/**
	 * 身份证号
	 */
	@TableField("cust_id")
	private String custId;
	/**
	 * 借据号
	 */
	private String ious;
	/**
	 * 最新催收金额
	 */
	@TableField("now_collection_amount")
	private Double nowCollectionAmount;
	/**
	 * 最新账龄
	 */
	@TableField("now_agecd")
	private Integer nowAgecd;
	/**
	 * 公司预分配
	 */
	private String company;
	/**
	 * 逾期天数
	 */
	private Integer overdue;
	/**
	 * 网络借贷平台
	 */
	@TableField("net_lending_platform")
	private String netLendingPlatform;
	/**
	 * 产品名称
	 */
	@TableField("product_name")
	private String productName;
	/**
	 * 逾期欠款本金
	 */
	@TableField("overdue_principal")
	private Double overduePrincipal;
	/**
	 * 分期总金额(当前余额本金)
	 */
	@TableField("total_amount")
	private Double totalAmount;
	/**
	 * 下一还款日(还款日)
	 */
	@TableField("next_refund_day")
	private String nextRefundDay;
	/**
	 * 逾期利息(逾期欠款利息)
	 */
	@TableField("overdue_accrual")
	private Double overdueAccrual;
	/**
	 * 罚息(逾期欠款费用(罚息))
	 */
	@TableField("default_interest")
	private Double defaultInterest;
	/**
	 * 是否独资(出资比例)
	 */
	@TableField("is_sole_proprietorship")
	private String isSoleProprietorship;
	/**
	 * 合同生成日期
	 */
	@TableField("contract_create_date")
	private String contractCreateDate;
	/**
	 * 总分期数(分期数)
	 */
	private Integer totalAging;
	/**
	 * 最近更新时间或创建时间
	 */
	@TableField("update_time")
	private Date updateTime;
	/**
	 * 创建时间
	 */
	@TableField("creat_date")
	private Date creatDate;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getIous() {
		return ious;
	}

	public void setIous(String ious) {
		this.ious = ious;
	}

	public Double getNowCollectionAmount() {
		return nowCollectionAmount;
	}

	public void setNowCollectionAmount(Double nowCollectionAmount) {
		this.nowCollectionAmount = nowCollectionAmount;
	}

	public Integer getNowAgecd() {
		return nowAgecd;
	}

	public void setNowAgecd(Integer nowAgecd) {
		this.nowAgecd = nowAgecd;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getOverdue() {
		return overdue;
	}

	public void setOverdue(Integer overdue) {
		this.overdue = overdue;
	}

	public String getNetLendingPlatform() {
		return netLendingPlatform;
	}

	public void setNetLendingPlatform(String netLendingPlatform) {
		this.netLendingPlatform = netLendingPlatform;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(Double overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getNextRefundDay() {
		return nextRefundDay;
	}

	public void setNextRefundDay(String nextRefundDay) {
		this.nextRefundDay = nextRefundDay;
	}

	public Double getOverdueAccrual() {
		return overdueAccrual;
	}

	public void setOverdueAccrual(Double overdueAccrual) {
		this.overdueAccrual = overdueAccrual;
	}

	public Double getDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(Double defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	public String getIsSoleProprietorship() {
		return isSoleProprietorship;
	}

	public void setIsSoleProprietorship(String isSoleProprietorship) {
		this.isSoleProprietorship = isSoleProprietorship;
	}

	public String getContractCreateDate() {
		return contractCreateDate;
	}

	public void setContractCreateDate(String contractCreateDate) {
		this.contractCreateDate = contractCreateDate;
	}

	public Integer getTotalAging() {
		return totalAging;
	}

	public void setTotalAging(Integer totalAging) {
		this.totalAging = totalAging;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreatDate() {
		return creatDate;
	}

	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}

}
