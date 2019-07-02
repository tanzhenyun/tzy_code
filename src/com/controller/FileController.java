package com.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bean.FileInfo;
import com.bean.GoodsInfo;
import com.dao.FileHelper;
import com.google.gson.Gson;

@Controller
public class FileController {

	private Logger logger = Logger.getLogger(FileController.class);

	/**
	 * 轮播图
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("banner.do")
	public void getBanner(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			Gson gs = new Gson();
			fh = new FileHelper();
			FileInfo banner = fh.getBanner();
			String json = gs.toJson(banner);
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
	 * 读取图片文件夹并且返回所有文件名字
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getfileinfo.do")
	public void getFileInfo(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String goodsid = request.getParameter("goodsid");
			String shopid = request.getParameter("shopid");
			Gson gs = new Gson();
			fh = new FileHelper();
			FileInfo fileInfo = fh.getFileInfo(shopid, goodsid);
			String json = gs.toJson(fileInfo);
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
	 * 查询门店商品
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getshopgoods.do")
	public void getShop(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String shopid = request.getParameter("shopid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			Gson gs = new Gson();
			fh = new FileHelper();
			List<GoodsInfo> shop = fh.getShop(shopid, skip, limit);
			String json = gs.toJson(shop);
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
	 * 查询推荐商品
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getgoodsrecommendinfo.do")
	public void getGoodsRecommend(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String goodsid = request.getParameter("goodsid");
			String importflag = request.getParameter("importflag");
			String assistflag = request.getParameter("assistflag");
			Gson gs = new Gson();
			fh = new FileHelper();
			List<GoodsInfo> shop = fh.getgoodsrecommendinfo(goodsid, importflag, assistflag);
			String json = gs.toJson(shop);
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
	 * 首页查询推荐
	 * 
	 * @return
	 */
	@RequestMapping("getrecommend.do")
	public void getRecommend(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		Gson gs = new Gson();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			fh = new FileHelper();
			Map<String, List> map = fh.getRecommend(skip, limit);
			String json = gs.toJson(map);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 首页活动用户领取代金券
	 */
	@RequestMapping("usergetcoupon.do")
	public void receiveCoupon(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		FileHelper fh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			String memberid = request.getParameter("memberid");
			String spruleid = request.getParameter("spruleid");
			String couponamount = request.getParameter("couponamount");
			String validdate = request.getParameter("validdate");
			String userange = request.getParameter("userange");
			fh = new FileHelper();
			int flag = fh.generateCoupon(memberid, spruleid, couponamount, validdate, userange);
			writer.print("{\"dataflag\":\"" + flag + "\"}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
