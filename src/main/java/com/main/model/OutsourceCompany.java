package com.main.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.*;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.sys.commons.base.BaseController;

/**
 * <p>
 * 外包公司
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
@TableName("outsource_company")
public class OutsourceCompany extends BaseController {

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

	public static void main(String[] args) throws IOException {
		String path ="D:";   // 这边文件目录需改成相对路径
		File file = new File(path,"examination.txt");
		FileReader fr = new FileReader(file);  //字符输入流
		BufferedReader br = new BufferedReader(fr);  //使文件可按行读取并具有缓冲功能
		StringBuffer strB = new StringBuffer();   //strB用来存储jsp.txt文件里的内容
		String str = br.readLine();
		strB.append("<table border='1'>");
		while(str!=null){
			strB.append("<tr>");
			String[] strArray = str.split(",");
			for(int i = 0; i< strArray.length; i ++){
				strB.append("<td>"+ strArray[i] + "</td>");   //将读取的内容放入strB
			}
			strB.append("</tr>");
			str = br.readLine();
		}
		strB.append("</table>");

		br.close();    //关闭输入流

		System.out.print(strB);
	}

}
