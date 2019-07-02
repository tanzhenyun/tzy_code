package com.controller;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dao.LifeServiceHelper;
import com.google.gson.Gson;

@Controller
@RequestMapping(method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
public class LifeServiceController {

	private Logger logger = Logger.getLogger(LifeServiceController.class);

	/**
	 * 查询分类
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryclass.do")
	public String getClassInfo() {
		Gson gs = null;
		List<Object> list = null;
		try {
			gs = new Gson();
			LifeServiceHelper lh = new LifeServiceHelper();
			list = lh.getClassInfo();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
		return gs.toJson(list);
	}

	/**
	 * 查询商户信息
	 * 
	 * @param searchval
	 * @return
	 */
	@ResponseBody
	@RequestMapping("querybusiness.do")
	public String getBusiness(String searchval, String text, String skip, String limit) {
		Gson gs = null;
		List<Object> list = null;
		try {
			gs = new Gson();
			LifeServiceHelper lh = new LifeServiceHelper();
			list = lh.getAll(searchval, text, skip, limit);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
		return gs.toJson(list);
	}

	@ResponseBody
	@RequestMapping("savebusiness.do")
	public String saveBusiness(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "files", required = false) MultipartFile multipartFile, String companyname,
			String companyshortname, String servicetypeid, String username, String tel, String tel1, String address,
			String memo,String provinceid,String cityid,String countyid,String wxnumber) {
		// 这里一定要写required=false
		// 不然前端不传文件一定报错。到不了后台。
		String flag = "";
		try {
			companyname = URLDecoder.decode(companyname, "UTF-8");
			companyshortname = URLDecoder.decode(companyshortname, "UTF-8");
			username = URLDecoder.decode(username, "UTF-8");
			address = URLDecoder.decode(address, "UTF-8");
			memo = URLDecoder.decode(memo, "UTF-8");
			wxnumber = URLDecoder.decode(wxnumber, "UTF-8");
			LifeServiceHelper lh = new LifeServiceHelper();
			flag = lh.saveBusiness(multipartFile, companyname, companyshortname, servicetypeid, username, tel, tel1,
					provinceid, cityid, countyid, address,wxnumber, memo);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
			flag = e.getMessage();
		}
		return "{\"flag\":\"" + flag + "\"}";
	}

	@ResponseBody
	@RequestMapping("area.do")
	public String getArea() {
		Map<String, Object> area = null;
		try {
			LifeServiceHelper lh = new LifeServiceHelper();
			area = lh.getArea();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
		return new Gson().toJson(area);
	}

}
