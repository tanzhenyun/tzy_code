package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.bean.User;
import com.dao.LoginHelper;
import com.google.gson.Gson;

@Controller
public class LoginController {

	private Logger logger = Logger.getLogger(LoginController.class);

	// 注册
	@RequestMapping("register.do")
	public void register(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		LoginHelper lh = null;
		Gson gs = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			gs = new Gson();
			String phonenumber = request.getParameter("phonenumber");
			List<User> list = lh.register(phonenumber);
			String json = gs.toJson(list);
			writer.print(json);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 绑定会员卡
	@RequestMapping("bindmember.do")
	public void bindMember(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		LoginHelper lh = null;
		Gson gs = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			gs = new Gson();
			String memberid = request.getParameter("memberid");
			String phonenumber = request.getParameter("phonenumber");
			String access_tokeninfo = (String) request.getSession().getAttribute("accessinfo");

			JSONObject jsonobject = JSONObject.parseObject(access_tokeninfo);// 转成Json格式
			String access_token = jsonobject.getString("access_token");// 获取access_token
			String openid = jsonobject.getString("openid");// 获取openId
			logger.info("绑定会员卡==openid==" + openid + "access_token==" + access_token);

			User user = lh.bindMember(memberid, phonenumber, openid, access_token);
			String json = gs.toJson(user);
			logger.info("绑定微信信息: " + json);
			writer.print(json);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 检查用户是否存在（手机号码）
	@RequestMapping("checkphonenumber.do")
	public void checkPhonenumber(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		LoginHelper lh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			String phonenumber = request.getParameter("phone");
			int dataflag = lh.checkPhonenumber(phonenumber);
			writer.print("{\"dataflag\":\"" + dataflag + "\"}");
			System.out.println("用户是否存在" + dataflag);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 首页获取用户信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getuser.do")
	public void getUser(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		LoginHelper lh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			gs = new Gson();
			String accessinfo = (String) request.getSession().getAttribute("accessinfo");

			User user = lh.getUser(accessinfo);
			if (user.getWxnickname() == null || "".equals(user.getWxnickname())) {
				logger.info("没有微信信息,清除access_token有效时间");
				System.out.println("没有微信信息,清除access_token有效时间");
				request.getSession().setAttribute("lasttime", null);
			}
			String json = gs.toJson(user);
			System.out.println("获取用户信息-----" + json);
			logger.info("登录用户信息: " + json);
			writer.print(json);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 修改手机号码
	@RequestMapping("updatephonenumner.do")
	public void updatePhoneNumber(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		LoginHelper lh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			String userid = request.getParameter("userid");
			String memberid = request.getParameter("memberid");
			String phonenumber = request.getParameter("phonenumber");
			int dataflag = lh.updatePhoneNumber(userid, phonenumber, memberid);
			writer.print("{\"dataflag\":\"" + dataflag + "\"}");
			// if (dataflag == 1) {
			// String access_token = (String)
			// request.getSession().getAttribute("access_token");
			// User user = lh.getUserInfo(userid, access_token);
			// String json = gs.toJson(user);
			// System.out.println("用户信息" + json);
			// writer.print(json);
			// }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 客服登录
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("login.do")
	public void login(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		LoginHelper lh = null;
		Gson gs = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			lh = new LoginHelper();
			gs = new Gson();

			String userid = request.getParameter("userid");
			String password = request.getParameter("password");
			User userinfo = lh.login(userid, password);
			String json = gs.toJson(userinfo);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
