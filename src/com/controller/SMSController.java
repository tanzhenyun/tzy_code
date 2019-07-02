package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xml.sax.InputSource;

import com.util.Parameter;

/**
 * 发送短信
 * 
 * @author KF_04
 * 
 */
@Controller
public class SMSController {

	final String GET_URL = "http://120.77.14.55:8888";
	private static Logger logger = Logger.getLogger(SMSController.class);

	@RequestMapping("sendSMS.do")
	public void main(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			String userid = Parameter.getParameter("smsuserid");
			String account = Parameter.getParameter("smsaccount");
			String password = Parameter.getParameter("smspassword");
			String mobile = request.getParameter("phone");
			String verificationcode = "";
			for (int j = 0; j < 6; j++) {
				int b = (int) (Math.random() * 10);
				verificationcode += b;
			}
			String content = "【舍神科技】验证码为：" + verificationcode + "，请勿将验证码告知他人或在其他平台使用！(15分钟内有效)";
			System.out.println("验证码：" + verificationcode);
			logger.info("验证码：" + verificationcode);
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
			String dateStr = sf.format(date);// 时间戳，年月日时分秒
			String str = account + password + dateStr;// MD5加密，账号+密码+时间戳
			String sign = MD5(str);
			String send = Send(dateStr, sign, userid, mobile, content, GET_URL);
			getXml(send);
			writer = response.getWriter();
			writer.print("{\"dataflag\":\"" + verificationcode + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}

	}

	// MD5加密,账号+密码+时间戳
	public static String MD5(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(str.getBytes("utf-8"));
			StringBuffer zm = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				zm.append(hexString);
			}
			return zm.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
			return e.toString();
		}
	}

	// 发送短信
	public static String Send(String dateStr, String sign, String userid, String mobile, String content,
			String GET_URL) {
		StringBuffer sb = null;
		try {
			String requestUrl = GET_URL + "/v2sms.aspx?action=send&userid=" + userid + "&timestamp=" + dateStr
					+ "&sign=" + sign + "&mobile=" + mobile + "&content=" + URLEncoder.encode(content, "utf-8")
					+ "&sendTime=&extno=";
			URL getUrl = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
		return sb.toString();
	}

	// 解析xml
	public static void getXml(String xmlDoc) {
		try {
			StringReader read = new StringReader(xmlDoc);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(read);
			// 创建一个新的SAXBuilder
			SAXBuilder sb = new SAXBuilder();
			// 通过输入源构造一个Document
			Document doc = sb.build(source);
			// 取的根元素
			Element root = doc.getRootElement();
			// 得到根元素所有子元素的集合
			List jiedian = root.getChildren();
			// 获得XML中的命名空间（XML中未定义可不写）
			Namespace ns = root.getNamespace();
			String text = root.getChild("message", ns).getText();
			logger.info(text);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
	}
}
