package com.wangzhixuan.test;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.sys.commons.sms.SendMsgUtil;
import com.wangzhixuan.test.base.BaseTest;

/**
 * 首页测试
 */
public class IndexTest extends BaseTest {

	/**
	 * 参考链接：Spring MVC测试框架详解——服务端测试
	 * <URL>http://jinnianshilongnian.iteye.com/blog/2004660</URL>
	 */
	public void index() throws Exception {
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
				.get("/")
				.accept(MediaType.TEXT_HTML))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}
	
	/**
	 * 短信发送测试
	 * @throws Exception
	 */
	public void MsgSend() throws Exception {
		SendMsgUtil.send("13370001213", "发送成功!");
	}
	
	@Test
	public void getBalance() throws Exception {
		SendMsgUtil.getBalance();
	}
	
}
