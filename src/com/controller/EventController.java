package com.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.EventInfo;
import com.dao.EventHelper;
import com.google.gson.Gson;

@Controller
public class EventController {

	private Logger logger = Logger.getLogger(EventController.class);

	@RequestMapping("event.do")
	public void getEvent(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		EventHelper eh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			eh = new EventHelper();
			String eventid = request.getParameter("eventid");
			EventInfo eventinfo = eh.getEventInfo(eventid);
			String json = gs.toJson(eventinfo);
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
