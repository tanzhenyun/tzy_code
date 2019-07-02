package com.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.LoginHelper;
import com.dao.WxHelper;
import com.shinesend.helper.SSDecimalHelper;

@Controller
public class WXController {

	private Logger logger = Logger.getLogger(WXController.class);

	@RequestMapping("wx.do")
	public String getAccess_tokec(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = null;
		WxHelper wh = new WxHelper();
		int checkUserByOpenid = 0;
		String memberflag = request.getParameter("memberflag");
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			// 当前时间
			String curtime = new Date().getTime() + "";
			String access_tokeninfo = "";
			String openid = "";
			// access_tokec上次启用时间
			String lasttime = (String) request.getSession().getAttribute("lasttime");
			int compare = SSDecimalHelper.compare(SSDecimalHelper.sub(curtime, lasttime, 0), "7100000");
			System.out.println("上次access时间" + lasttime + "-----当前时间" + curtime);
			String code = request.getParameter("code");// 通过code获取access_token
			// access_token有效时间为两个小时,计算时间是否需要重新获取,大于0 为过期
			if (compare > 0) {
				System.out.println("access_token过期");
				request.getSession().setAttribute("lasttime", curtime);
				access_tokeninfo = wh.getAccesstoken(code);
				json = JSON.parseObject(access_tokeninfo);
				request.getSession().setAttribute("accessinfo", access_tokeninfo);
			} else {
				System.out.println("access_token能用");
				access_tokeninfo = (String) request.getSession().getAttribute("accessinfo");
				json = JSON.parseObject(access_tokeninfo);
				if (json == null || access_tokeninfo.indexOf("errcode") >= 0) {
					access_tokeninfo = wh.getAccesstoken(code);
					request.getSession().setAttribute("accessinfo", access_tokeninfo);
					json = JSON.parseObject(access_tokeninfo);
				}
			}
			openid = (String) json.get("openid");
			logger.info("获取微信token: " + access_tokeninfo);
			System.out.println("获取access_tokeninfo-----" + access_tokeninfo);

			// 判断跳转页面
			checkUserByOpenid = new LoginHelper().checkUserByOpenid(openid);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}

		if (memberflag == null) {
			if (checkUserByOpenid > 0) {
				return "redirect:/index.html";
			} else {
				return "redirect:/index.html";
			}
		} else {
			if (checkUserByOpenid > 0) {
				return "redirect:/html/membercenter1.html";
			} else {
				return "redirect:/html/register.html?memberflag=1";
			}
		}
	}
}
