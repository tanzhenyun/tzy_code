package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.MemberInfo;
import com.bean.OrderInfo;
import com.dao.OrderHelper;
import com.google.gson.Gson;

@Controller
@RequestMapping("/")
public class OrderController {

	private Logger logger = Logger.getLogger(OrderController.class);

	// 提交订单前检查地址
	@RequestMapping("checkaddress.do")
	public void checkAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			oh = new OrderHelper();
			String userid = request.getParameter("userid");
			int dataflag = oh.checkAddress(userid);
			writer.print("{\"dataflag\":\"" + dataflag + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 获取订单页面信息
	@RequestMapping("getordergoods.do")
	public void getOrderGoods(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String userid = request.getParameter("userid");
			String str = request.getParameter("str");
			String[] arr = request.getParameterValues("array");
			OrderInfo order = oh.getOrderGoods(userid, str, arr);
			String json = gs.toJson(order);
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

	// 获取未完成订单信息
	@RequestMapping("waitreceiveorder.do")
	public void getWaitReceiveOrder(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String memberid = request.getParameter("memberid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List list = oh.getWaitReceiveOrder(memberid, skip, limit);
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

	// 获取已完成订单信息
	@RequestMapping("completedorder.do")
	public void getCompletedOrder(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String memberid = request.getParameter("memberid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List list = oh.getCompletedOrder(memberid, skip, limit);
			String json = gs.toJson(list);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 查询代金券
	@RequestMapping("querycoupon.do")
	public void queryCoupon(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String memberid = request.getParameter("memberid");
			List<MemberInfo> list = oh.queryCoupon(memberid);
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

	// 待支付订单
	@RequestMapping("pendingpayorder.do")
	public void getPendingPayOrder(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String memberid = request.getParameter("memberid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List list = oh.getPendingPayOrder(memberid, skip, limit);
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

	// 所有订单
	@RequestMapping("allorder.do")
	public void getAllOrder(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		OrderHelper oh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			oh = new OrderHelper();
			String memberid = request.getParameter("memberid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List list = oh.getAllOrder(memberid, skip, limit);
			String json = gs.toJson(list);
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
