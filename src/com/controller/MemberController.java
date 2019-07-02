package com.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.MemberCenterDocInfo;
import com.bean.MemberIdcardTypeDoc;
import com.bean.MemberInfo;
import com.dao.MemberHelper;
import com.google.gson.Gson;

@Controller
public class MemberController {
	private Logger logger = Logger.getLogger(MemberController.class);

	// 获取会员信息
	@RequestMapping("getmemberinfo.do")
	public void getMember(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			String memberid = request.getParameter("memberid");
			MemberIdcardTypeDoc memberinfo = mh.memberinfo(memberid);
			String json = gs.toJson(memberinfo);
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

	// 修改会员信息
	@RequestMapping("updatememberinfo.do")
	public void updateMemberInfo(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			mh = new MemberHelper();
			String memberid = request.getParameter("memberid");
			String memsex = request.getParameter("sex");
			String birthdate = request.getParameter("birthdate");
			String idcardno = request.getParameter("idcardno");
			String mailaddress = request.getParameter("mailaddress");
			String idcardtype = request.getParameter("idcardtype");
			String username = request.getParameter("username");
			int resultflag = mh.updatememberinfo(memberid, memsex, birthdate, idcardno, mailaddress, idcardtype,
					username);
			writer.print("{\"resultflag\":\"" + resultflag + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 获取我的票券信息
	@RequestMapping("getmembercoupods.do")
	public void getMemberCoupods(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			String memberid = request.getParameter("memberid");
			List<MemberInfo> membercoupods = mh.getmembercoupods(memberid);
			String json = gs.toJson(membercoupods);
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

	// 获取我的特权信息
	@RequestMapping("getmyselfprivilege.do")
	public void getMyselfPrivilege(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			String membertypeid = request.getParameter("membertypeid");
			MemberInfo myselfprivilege = mh.getmyselfprivilege(membertypeid);
			String json = gs.toJson(myselfprivilege);
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

	// 获取会员信息特权等全部信息
	@RequestMapping("getprivilegeinfo.do")
	public void getPrivilegeInfo(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			List<MemberCenterDocInfo> doclist = mh.getprivilegeinfo();
			String json = gs.toJson(doclist);
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

	// 获取可以兑换订单代金券
	@RequestMapping("getintegralinfo.do")
	public void getIntegralInfo(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			List<MemberInfo> integralinfo = mh.getIntegralInfo();
			String json = gs.toJson(integralinfo);
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

	// 兑换代金券
	@RequestMapping("exchangeintegral.do")
	public void exchangeIntegral(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			mh = new MemberHelper();
			String cashcouponsubid = request.getParameter("cashcouponsubid");
			String memberid = request.getParameter("memberid");
			String userange = request.getParameter("userange");
			int resultflag = mh.exchangeIntegral(cashcouponsubid, memberid, userange);
			writer.print("{\"resultflag\":\"" + resultflag + "\"}");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 获取可以兑换的礼品信息
	@RequestMapping("getintegralgoodsinfo.do")
	public void getIntegralGoodsInfo(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			String memberid = request.getParameter("memberid");
			String skip = request.getParameter("skip");
			String limit = request.getParameter("limit");
			List<MemberInfo> integralinfo = mh.getIntegralGoodsInfo(memberid, skip, limit);
			String json = gs.toJson(integralinfo);
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

	// 我的票钱界面核销代金券检查人员ID
	@RequestMapping("checkuserange.do")
	public void checkUseRange(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		Gson gs = null;
		MemberHelper mh = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			gs = new Gson();
			mh = new MemberHelper();
			String couponid = request.getParameter("couponid");
			String value = request.getParameter("value");
			int dataflag = mh.checkUseRange(couponid, value);
			writer.print("{\"resultflag\":\"" + dataflag + "\"}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
