package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dao.AskHelper;
import com.google.gson.Gson;

@Controller
public class AskController {
	private Logger logger = Logger.getLogger(AddressController.class);

	@RequestMapping("getservice.do")
	public void getService(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		AskHelper ah = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			ah = new AskHelper();
			List<String> list = ah.getService();
			String json = gs.toJson(list);
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
