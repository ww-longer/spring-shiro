package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.sys.commons.base.BaseController;


/**
 * <p>
 * 
 * </p>
 *
 * @author jiewai
 * @since 2018-05-23
 */
@TableName("outsource_allocation_amount")
public class OutsourceAllocationAmount extends BaseController {

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
	private String custId;
    /**
     * 手机号
     */
	private String telNumber;
    /**
     * 借据号
     */
	private String ious;
    /**
     * 最新催收金额
     */
	private Double nowCollectionAmount;
    /**
     * 最新账龄
     */
	private Integer nowAgecd;
	/**
	 * 移交账龄
	 */
	private Integer transferAgecd;
    /**
     * 移交日
     */
	private Date transfer;
    /**
     * 出催日期
     */
	private Date thePushDay;
    /**
     * 上次催收金额
     */
	private Double lastCollectionAmount;
    /**
     * 上次账龄
     */
	private Integer lastAgecd;
    /**
     * 最近更新时间或创建时间
     */
	private Date updateTime;
    /**
     * 公司
     */
	private String company;
    /**
     * 备注
     */
	private String remarks;
	/**
	 * 创建时间
	 */
	private Date creatDate;
	/**
	 *	是否留案
	 */
	private String isLeaveCase;
	/**
	 *	留案日期(操作日期)
	 */
	private Date leaveCaseDate;


	private String startTransferTime;
	private String endTransferTime;
	private String startThePushDayTime;
	private String endThePushDayTime;

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

	public Integer getTransferAgecd() {
		return transferAgecd;
	}

	public void setTransferAgecd(Integer transferAgecd) {
		this.transferAgecd = transferAgecd;
	}

	public Date getTransfer() {
		return transfer;
	}

	public void setTransfer(Date transfer) {
		this.transfer = transfer;
	}

	public Date getThePushDay() {
		return thePushDay;
	}

	public void setThePushDay(Date thePushDay) {
		this.thePushDay = thePushDay;
	}

	public Double getLastCollectionAmount() {
		return lastCollectionAmount;
	}

	public void setLastCollectionAmount(Double lastCollectionAmount) {
		this.lastCollectionAmount = lastCollectionAmount;
	}

	public Integer getLastAgecd() {
		return lastAgecd;
	}

	public void setLastAgecd(Integer lastAgecd) {
		this.lastAgecd = lastAgecd;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreatDate() {
		return creatDate;
	}

	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}

	public String getIsLeaveCase() {
		return isLeaveCase;
	}

	public void setIsLeaveCase(String isLeaveCase) {
		this.isLeaveCase = isLeaveCase;
	}

	public Date getLeaveCaseDate() {
		return leaveCaseDate;
	}

	public void setLeaveCaseDate(Date leaveCaseDate) {
		this.leaveCaseDate = leaveCaseDate;
	}

	public String getStartTransferTime() {
		return startTransferTime;
	}

	public void setStartTransferTime(String startTransferTime) {
		this.startTransferTime = startTransferTime;
	}

	public String getEndTransferTime() {
		return endTransferTime;
	}

	public void setEndTransferTime(String endTransferTime) {
		this.endTransferTime = endTransferTime;
	}

	public String getStartThePushDayTime() {
		return startThePushDayTime;
	}

	public void setStartThePushDayTime(String startThePushDayTime) {
		this.startThePushDayTime = startThePushDayTime;
	}

	public String getEndThePushDayTime() {
		return endThePushDayTime;
	}

	public void setEndThePushDayTime(String endThePushDayTime) {
		this.endThePushDayTime = endThePushDayTime;
	}
}
