package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;

import java.io.OutputStream;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 外包公司
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
public class OutsourceCompany extends Model<OutsourceCompany> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 公司编号
     */
	@TableField("company_number")
	private String companyNumber;
    /**
     * 公司名称
     */
	private String company;
    /**
     * 公司地址
     */
	private String address;
    /**
     * 创建时间
     */
	@TableField("create_date")
	private Date createDate;
    /**
     * 创建人
     */
	@TableField("create_user")
	private String createUser;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
