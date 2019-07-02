package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.AddressInfo;
import com.dao.AddressHelper;
import com.google.gson.Gson;

@Controller
public class AddressController {

	private Logger logger = Logger.getLogger(AddressController.class);

	/**
	 * 获取所有收货地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getaddress.do")
	public void getAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ah = new AddressHelper();
			String userid = request.getParameter("userid");
			List<AddressInfo> addresslist = ah.getAddress(userid);
			String json = gs.toJson(addresslist);
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

	/**
	 * 删除收货地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("deleteaddress.do")
	public void deleteAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			ah = new AddressHelper();
			String userid = request.getParameter("userid");
			String id = request.getParameter("id");
			int dataflag = ah.deleteAddress(userid, id);
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

	/**
	 * 新增收货地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("insertaddress.do")
	public void insertAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			ah = new AddressHelper();
			String userid = request.getParameter("userid");
			String memberid = request.getParameter("memberid");
			String receivename = request.getParameter("receivename");
			String phonenumber = request.getParameter("phonenumber");
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String area = request.getParameter("area");
			String address = request.getParameter("address");
			String orderflag = request.getParameter("orderflag");
			int dataflag = ah.insertAddress(userid, memberid, receivename, phonenumber, province, city, area, address,orderflag);
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

	/**
	 * 获取修改收货地址信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getupdateaddressinfo.do")
	public void getUpdateAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ah = new AddressHelper();
			String userid = request.getParameter("userid");
			String id = request.getParameter("id");
			String flag = request.getParameter("flag");
			if ("1".equals(flag)) {
				AddressInfo adressinfo = ah.getUpdateAdressInfo(id);
				String json = gs.toJson(adressinfo);
				writer.print(json);
			} else {
				int dataflag = ah.setDefAddress(userid, id);
				writer.print("{\"dataflag\":\"" + dataflag + "\"}");
			}
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
	 * 保存修改后的收货地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("saveupdateaddress.do")
	public void savaUpdateAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			ah = new AddressHelper();
			String id = request.getParameter("id");
			String receivename = request.getParameter("receivename");
			String phonenumber = request.getParameter("phonenumber");
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String area = request.getParameter("area");
			String address = request.getParameter("address");
			int dataflag = ah.saveUpdateAddress(id, receivename, phonenumber, province, city, area, address);
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

	/**
	 * 切换默认地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("setdefaddress.do")
	public void setDefAddress(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		AddressHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			ah = new AddressHelper();
			String userid = request.getParameter("userid");
			String id = request.getParameter("id");
			int dataflag = ah.setDefAddress(userid, id);
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
}
