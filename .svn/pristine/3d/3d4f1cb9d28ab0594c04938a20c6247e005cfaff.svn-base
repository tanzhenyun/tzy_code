package com.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.GoodsInfo;
import com.dao.SearchHelper;
import com.google.gson.Gson;

@Controller
public class SearchController {

	private Logger logger = Logger.getLogger(SearchController.class);

	// 搜索
	@RequestMapping("searchgoods.do")
	public void getSearchGoods(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		SearchHelper sh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			String word = request.getParameter("word");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			sh = new SearchHelper();
			word = word.toUpperCase();
			Map<String, Object> map = sh.queryGoods(word, skip, limit);
			// Map<String, Object> map = sh.getSearch(word, skip, limit);
			String json = gs.toJson(map);
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
	 * 点击商品查询门店信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("queryshop.do")
	public void queryShop(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		SearchHelper sh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			sh = new SearchHelper();
			String goodsid = request.getParameter("goodsid");
			List<Object> list = sh.queryShop(goodsid);
			writer.print(gs.toJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 搜索
	@RequestMapping("searchshop.do")
	public void getSearchShop(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		SearchHelper sh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			String word = request.getParameter("word");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			sh = new SearchHelper();
			word = word.toUpperCase();
			// List<GoodsInfo> goodslist =
			Map<String, Object> map = sh.getSearch(word, skip, limit);
			String json = gs.toJson(map);
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

	// 门店内搜索
	@RequestMapping("getshopsearch.do")
	public void getShopSearch(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		SearchHelper sh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			String shopid = request.getParameter("shopid");
			String word = request.getParameter("word");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			sh = new SearchHelper();
			word = word.toUpperCase();
			List<GoodsInfo> goodslist = sh.getShopSearch(shopid, word, skip, limit);
			String json = gs.toJson(goodslist);
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
