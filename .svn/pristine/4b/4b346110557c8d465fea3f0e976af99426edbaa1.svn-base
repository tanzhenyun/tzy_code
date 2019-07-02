package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.CartInfo;
import com.bean.ShopInfo;
import com.dao.ShopHelper;
import com.dao.ShoppingCartHelper;
import com.google.gson.Gson;

@Controller
@RequestMapping("/")
public class controller {

	private Logger logger = Logger.getLogger(controller.class);

	/**
	 * 购物车数量修改
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("updateshoppingcart.do")
	public void updateShoppingCart(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		ShoppingCartHelper sch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String shopid = request.getParameter("shopid");
			String goodsid = request.getParameter("goodsid");
			String wareCount = request.getParameter("wareCount");
			String userid = request.getParameter("userid");
			sch = new ShoppingCartHelper();
			int dataflag = sch.updateShoppingCart(shopid, goodsid, wareCount, userid);
			writer.print("{\"dataflag\":\"" + dataflag + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			writer.print("{\"dataflag\":\"" + 0 + "\"}");
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 购物车商品
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getwarelist.do")
	public void getWarelist(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ShoppingCartHelper sch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			String userid = request.getParameter("userid");
			sch = new ShoppingCartHelper();
			List<CartInfo> cartinfolist = sch.getWareList(userid);
			String json = gs.toJson(cartinfolist);
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
	 * 购物车商品删除
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("deletegoods.do")
	public void deleteWare(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		ShoppingCartHelper sch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String userid = request.getParameter("userid");
			String str = request.getParameter("str");
			sch = new ShoppingCartHelper();
			int influence = sch.deleteGoods(userid, str);
			writer.print("{\"dataflag\":\"" + influence + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			writer.print("{\"dataflag\":\"" + 0 + "\"}");
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 获取周围门店信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getallshop.do")
	public void getAllShop(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ShopHelper sel = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			sel = new ShopHelper();
			gs = new Gson();
			String[] arr = request.getParameterValues("arr");
			// List<ShopInfo> list = sel.getAllShop();
			List<ShopInfo> list = sel.getSkipShop(arr);
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

	/**
	 * 门店经纬度信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getshopposition.do")
	public void getShopPosition(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ShopHelper sel = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			sel = new ShopHelper();
			List shopPosition = sel.getShopPosition();
			String json = gs.toJson(shopPosition);
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
	 * 获取所有位置信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getallshopposition.do")
	public void getAllShopPosition(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ShopHelper sel = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			sel = new ShopHelper();
			List shopPosition = sel.getAllShopPosition();
			String json = gs.toJson(shopPosition);
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
}
