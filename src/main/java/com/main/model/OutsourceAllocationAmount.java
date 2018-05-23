package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.sys.commons.base.BaseEntity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@TableName("outsource_allocation_amount")
public class OutsourceAllocationAmount extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 移交日
     */
	private Date transfer;
    /**
     * 出催日期
     */
	@TableField("the_push_day")
	private Date thePushDay;
    /**
     * 上次催收金额
     */
	@TableField("last_collection_amount")
	private Double lastCollectionAmount;
    /**
     * 上次账龄
     */
	@TableField("last_agecd")
	private Integer lastAgecd;
    /**
     * 最近更新时间或创建时间
     */
	@TableField("update_time")
	private Date updateTime;
    /**
     * 备注
     */
	private String remarks;


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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
