package com.dao;

import org.apache.log4j.Logger;

import com.util.HttpUtil;
import com.util.Parameter;
import com.util.wx.CommonUtil;

public class WxHelper {

	private Logger logger = Logger.getLogger(WxHelper.class);

	/**
	 * 获取access_token
	 * 
	 * @param code
	 * @return
	 */
	public String getAccesstoken(String code) {
		String result = null;// 返回结果字符串
		try {
			String appid = Parameter.getParameter("appid");
			String secret = Parameter.getParameter("appsecret");
			String httpurl = "https://api.weixin.qq.com/sns/oauth2/access_token";
			String param = "appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
			// result = CommonUtil.httpsRequest(httpurl, "GET", param);
			result = HttpUtil.sendGet(httpurl, param);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
		return result;
	}

	public String getAccess() {
		String result = null;// 返回结果字符串
		try {
			String appid = Parameter.getParameter("appid");
			String secret = Parameter.getParameter("appsecret");
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
					+ "&secret=" + secret;
			// result = CommonUtil.httpsRequest(url, "GET", "");
			result = HttpUtil.sendGet(url, "");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据access_token,openid查询微信用户信息
	 * 
	 * @param access_token
	 * @param openid
	 * @return
	 */
	public String getWxUserInfo(String access_token, String openid) {
		// 通过access_token和openid请求获取用户信息https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
		String result = null;// 返回结果字符串
		try {
			String httpurl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid="
					+ openid + "&lang=zh_CN";
			// result = CommonUtil.httpsRequest(httpurl, "GET", "");
			result = HttpUtil.sendGet(httpurl, "");
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
		return result;
	}

}
