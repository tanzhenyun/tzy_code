package com.controller;

import java.io.PrintWriter;
/**
 * 分类
 */
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.DescriotorDoc;
import com.bean.GoodsInfo;
import com.dao.ClassifyHelper;
import com.google.gson.Gson;

@Controller
public class ClassifyController {
	private Logger logger = Logger.getLogger(ClassifyController.class);

	// 获取分类列表左边的数据
	@RequestMapping("midclass.do")
	public void classify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			List classify = ch.getMidClass();
			String json = gs.toJson(classify);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 获取选中分类列表的详细数据
	@RequestMapping("minclass.do")
	public void classifydtl(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			String midclassid = request.getParameter("midclassid");
			List classifydtl = ch.getMinClass(midclassid);
			String json = gs.toJson(classifydtl);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 获取药品小类描述及推荐信息
	@RequestMapping("getdescriptor.do")
	public void getDescriptor(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			String minclassid = request.getParameter("minclassid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			DescriotorDoc minDescriptor = ch.getMinDescriptor(minclassid, skip, limit);
			String json = gs.toJson(minDescriptor);
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

	// 获取非药品小类描述及推荐信息
	@RequestMapping("getgoodslist.do")
	public void getGoodsList(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			String minclassid = request.getParameter("minclassid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List<GoodsInfo> goodsList = ch.getGoodsList(minclassid, skip, limit);
			String json = gs.toJson(goodsList);
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

	// 获取药品小类商品列表
	@RequestMapping("getgoods.do")
	public void getGoods(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			String goodsid = request.getParameter("goodsid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List<GoodsInfo> goodsList = ch.getGoods(goodsid, skip, limit);
			String json = gs.toJson(goodsList);
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

	// 药品商品列表
	@RequestMapping("druggoodslist.do")
	public void getDrugGoodsList(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		ClassifyHelper ch = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ch = new ClassifyHelper();
			String minclassid = request.getParameter("minclassid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List<GoodsInfo> goodsList = ch.getDrugGoodsList(minclassid, skip, limit);
			String json = gs.toJson(goodsList);
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
