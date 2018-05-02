package com.sys.commons.sms;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Properties;

public class SendMsgUtil {

	private static String key="";
	private static String possword="";
	
	static{
		Properties p=new Properties();
		try {
			p.load(new FileInputStream(SendMsgUtil.class.getResource("/config.properties").getPath()));
			key=p.getProperty("mdkey");
			possword=p.getProperty("mdpwd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void send(String mobile,String context) throws Exception{
		// 输入软件序列号和密码
		String sn = key;
		String pwd = possword;
		String mobiles = mobile;
		String content = URLEncoder.encode(context, "utf8");
		Client client = new Client(sn, pwd);
		String result_mt = client.mdSmsSend_u(mobiles, content, "2", "", "");
		if (result_mt.startsWith("-") || result_mt.equals("")){//发送短信，如果是以负号开头就是发送失败。
			System.out.print("发送失败！返回值为：" + result_mt + "请查看webservice返回值对照表");
			return;
		}else {// 输出返回标识，为小于19位的正数，String类型的。记录您发送的批次。
			System.out.print("发送成功，返回值为：" + result_mt);
		}
	}
	
	public static void getBalance() throws Exception{
		// 输入软件序列号和密码
		String sn = key;
		String pwd = possword;
		Client client = new Client(sn, pwd);
		String result_mt = client.getBalance();
		if (result_mt.startsWith("-") || result_mt.equals("")){//发送短信，如果是以负号开头就是发送失败。
			System.out.print("发送失败！返回值为：" + result_mt + "请查看webservice返回值对照表");
			return;
		}else {// 输出返回标识，为小于19位的正数，String类型的。记录您发送的批次。
			System.out.print("发送成功，返回值为：" + result_mt);
		}
	}
	
}
