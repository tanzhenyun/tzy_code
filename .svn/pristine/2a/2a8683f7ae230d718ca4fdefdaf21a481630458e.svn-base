package com.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dao.OrderSettlementHelper;
import com.dao.PayHelper;
import com.google.gson.Gson;
import com.util.wx.WXPayUtil;

/**
 * @author 作者 :Gzy
 * @version 创建时间：2018年3月22日 上午8:57:50 类说明:
 */
@Controller
public class OrderSettlementController {
	// OrderSettlementController.java|上午8:57:50

	private Logger logger = Logger.getLogger(OrderSettlementController.class);

	// 获得结算单信息
	@RequestMapping("balanceaccounts.do")
	public void balanceAccounts(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		OrderSettlementHelper osh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			osh = new OrderSettlementHelper();
			writer = response.getWriter();
			Gson gs = new Gson();
			String[] arr = request.getParameterValues("array");
			String[] couponarr = request.getParameterValues("couponarr");
			String totalamount = request.getParameter("totalamount");
			String paymethod = request.getParameter("paymethod");
			// String openid = request.getParameter("openid");
			Map<String, String> map = osh.savePrescription(arr, couponarr, "", totalamount, paymethod);
			// 发送请求用来公众号给用户发模板消息
			// PayHelper ph = new PayHelper();
			// String prescriptionidstr = (String) map.get("prescriptionidstr");
			logger.info("货到付款开始微信公众号发消息----------------");
			// ph.sendMsg(prescriptionidstr, openid, "2", totalamount);
			String json = gs.toJson(map);
			// writer.print("{\"dataflag\":\"" + message + "\"}");
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 微信支付
	@RequestMapping("dopay.do")
	public void doPay(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			PayHelper ph = new PayHelper();
			Gson gs = new Gson();
			// String spbill_create_ip = request.getRemoteAddr();
			String openId = request.getParameter("openid");
			String total_fee = request.getParameter("total_fee");
			String totalamount = request.getParameter("totalamount");
			String[] arr = request.getParameterValues("array");
			String[] couponarr = request.getParameterValues("couponarr");
			Map<String, String> gopay = ph.doPay(openId, total_fee, totalamount, arr, couponarr);
			String json = gs.toJson(gopay);
			System.out.println("微信支付结果-----" + json);
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

	// 订单重新支付
	@RequestMapping("repay.do")
	public void rePay(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			Gson gs = new Gson();
			PayHelper ph = new PayHelper();
			String openid = request.getParameter("openid");
			String total_fee = request.getParameter("total_fee");
			String orderid = request.getParameter("orderid");
			Map<String, String> repay = ph.rePay(orderid, openid, total_fee);
			String json = gs.toJson(repay);
			System.out.println("微信重新支付结果-----" + json);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 成功支付
	@RequestMapping("successpay.do")
	public void success(HttpServletRequest request, HttpServletResponse response) {
		try {
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			logger.info("~~~~~~~~~~~~~~~~~付款成功~~~~~~~~~~~~~~~~~~~");
			outSteam.close();
			inStream.close();
			String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
			logger.info("支付完成打印微信返回信息----" + result);
			// Map<Object, Object> map = XMLUtil.doXMLParse(result);
			Map<String, String> map = WXPayUtil.xmlToMap(result);
			for (Object keyValue : map.keySet()) {
				logger.info(keyValue + "==========" + map.get(keyValue));
			}
			if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
				// TODO 对数据库的操作
				response.getWriter().write(setXML("SUCCESS", "OK")); // 告诉微信服务器，我收到信息了，不要在调用回调action了
				logger.info("微信成功支付-----" + setXML("SUCCESS", "OK") + "----" + map.get("out_trade_no"));
				PayHelper ph = new PayHelper();
				// 支付完成后将当前订单下的所有药方状态改为电商收款
				ph.updatePrescription(map.get("out_trade_no"));

				// 插入电子支付表
				ph.InsertWXResult(map.get("out_trade_no"), map.get("total_fee"), map.get("cash_fee"),
						map.get("transaction_id"), map.get("time_end"), map.get("appid"), map.get("mch_id"),
						map.get("sign"));

				// 执行赠券策略生成代金券流水
				ph.generateCouponLst(map.get("out_trade_no"));

				// 发送请求用来公众号给用户发模板消息
				logger.info("线上支付开始微信公众号发消息-----------------");
				// ph.sendMsg(map.get("out_trade_no"), map.get("openid"), "1",
				// map.get("total_fee"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}

	}

	public String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}

	// 微信退款
	@RequestMapping("refund.do")
	public void refund(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			PayHelper ph = new PayHelper();
			Gson gs = new Gson();
			String transaction_id = request.getParameter("transaction_id");
			String out_refund_no = request.getParameter("out_refund_no");
			String total_fee = request.getParameter("total_fee");
			String refund_fee = request.getParameter("refund_fee");
			String refund = ph.refund(transaction_id, out_refund_no, total_fee, refund_fee);
			Map<String, String> map = WXPayUtil.xmlToMap(refund);
			String json = gs.toJson(refund);
			System.out.println("微信退款结果-----" + json);
			for (String keyValue : map.keySet()) {
				System.out.println(keyValue + "=" + map.get(keyValue));
			}
			if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
				// TODO 对数据库的操作
				response.getWriter().write(setXML("SUCCESS", "OK")); //
				// 告诉微信服务器，我收到信息了，不要在调用回调action了
				System.out.println("微信退款成功-----" + setXML("SUCCESS", ""));
			}
			writer.print(json);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}

	@RequestMapping("refundsuccess.do")
	public void refundSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		InputStream inStream = request.getInputStream();
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		System.out.println("~~~~~~~~~~~~~~~~~退款成功~~~~~~~~~~~~~~~~~~~");
		outSteam.close();
		inStream.close();
		String result = new String(outSteam.toByteArray(), "utf-8");//
		// 获取微信调用我们notify_url的返回信息
		System.out.println("退款完成打印微信返回信息----" + result);
		// Map<Object, Object> map = XMLUtil.doXMLParse(result);
		Map<String, String> map = WXPayUtil.xmlToMap(result);
		for (Object keyValue : map.keySet()) {
			System.out.println(keyValue + "=" + map.get(keyValue));
		}
		if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
			// TODO 对数据库的操作
			response.getWriter().write(setXML("SUCCESS", "OK")); //
			// 告诉微信服务器，我收到信息了，不要在调用回调action了
			System.out.println("微信成功退款-----" + setXML("SUCCESS", ""));
		}

	}

}
