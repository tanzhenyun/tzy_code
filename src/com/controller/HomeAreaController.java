package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.HomeAreaDoc;
import com.dao.HomeAreaHelper;
import com.google.gson.Gson;

/**
 * 首页活动controller
 * 
 * @author KF_04
 * 
 */
@Controller
public class HomeAreaController {
	private Logger logger = Logger.getLogger(HomeAreaController.class);

	/**
	 * 获取所有活动
	 */
	@RequestMapping("allactivities.do")
	public void getAllActivities(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		HomeAreaHelper hah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			hah = new HomeAreaHelper();
			List<HomeAreaDoc> allActivities = hah.getAllActivities();
			String json = gs.toJson(allActivities);
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
