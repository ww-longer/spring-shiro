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
	 * company 公司
	 */
	private String company;
	/**
	 * 逾期天数
	 */
	private Integer overdue;
	/**
	 * 网络借贷平台
	 */
	private String netLendingPlatform;
	/**
	 * 产品名称
	 */
	private String productName;
    /**
     * 逾期本金
     */
	private Double overduePrincipal;
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
