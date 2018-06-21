package com.main.model;

import com.baomidou.mybatisplus.enums.IdType;
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
@TableName("data_on_stock")
public class DataOnStock extends BaseController {

    /**
     * 表Id
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

}
