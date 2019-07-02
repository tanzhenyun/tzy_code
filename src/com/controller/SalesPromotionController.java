package com.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.AddressInfo;
import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.dao.AddressHelper;
import com.dao.SalesPromotionHelper;
import com.google.gson.Gson;

@Controller
public class SalesPromotionController {

	private Logger logger = Logger.getLogger(SalesPromotionController.class);

	@RequestMapping("getspinfo.do")
	public void getSingleSP(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		SalesPromotionHelper sph = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			sph = new SalesPromotionHelper();
			String shopid = request.getParameter("shopid");
			String goodsid = request.getParameter("goodsid");

			Map<String, Object> map = new HashMap<String, Object>();

			List<SingleSPInfo> singleSpList = sph.getSingleSpList(shopid, goodsid);
			List<WholeSPInfo> wholeSpList = sph.getWholeSpList(shopid);
			map.put("singlelist", singleSpList);
			map.put("wholelist", wholeSpList);
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
}
