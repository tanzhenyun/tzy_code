package com.dao;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.bean.MemberCenterDocInfo;
import com.bean.MemberCenterDtlInfo;
import com.bean.MemberDtl;
import com.bean.MemberIdcardTypeDoc;
import com.bean.MemberInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

public class MemberHelper {

	private Logger logger = Logger.getLogger(MemberHelper.class);

	// 获取会员信息
	public MemberIdcardTypeDoc memberinfo(String memberid) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSSelectHelper sh1 = null;
		List<MemberDtl> idcardtypelist = null;
		List<MemberDtl> sexlist = null;
		MemberIdcardTypeDoc memberinfo = null;
		MemberDtl idcardtypeinfo = null;
		MemberDtl sexinfo = null;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			// 查询会员信息
			String sql = "select * from  v_rtl_member a where memberid = ?  ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			// 查询一条数据
			SSTableModel memberinfomodel = sh.executeSelect(con, 0, 1);
			String birthdate = "";
			if (memberinfomodel.getRowCount() > 0) {
				memberinfo = new MemberIdcardTypeDoc();
				idcardtypelist = new ArrayList<MemberDtl>();
				sexlist = new ArrayList<MemberDtl>();
				// bean类设置定义变量的值，存在设置的list里面，返回前台
				memberinfo.setMembername(memberinfomodel.getItemValue(0, "membername"));
				memberinfo.setIdcardtype(memberinfomodel.getItemValue(0, "idcardtype"));
				memberinfo.setIdcardno(memberinfomodel.getItemValue(0, "idcardno"));
				memberinfo.setSex(memberinfomodel.getItemValue(0, "sex"));
				memberinfo.setMobile(memberinfomodel.getItemValue(0, "mobile"));
				memberinfo.setMailaddress(memberinfomodel.getItemValue(0, "mailaddress"));

				birthdate = memberinfomodel.getItemValue(0, "birthdate");
				if (!"".equals(birthdate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = formatter.parse(birthdate);
					birthdate = formatter.format(parse);
				}
				memberinfo.setBirthdate(birthdate);

				// 查询会员证件类型
				String sql1 = "select b.* " + "from SYS_DICTIONARY a,SYS_DICTIONARY_DTL b "
						+ "where keyword = 'RTL_MEMBERINFO_IDCARDTYPE' " + "and a.ddlid = b.ddlid";
				sh1 = new SSSelectHelper(sql1);
				SSTableModel idcardtypemodel = sh1.executeSelect(con, 0, 999);

				if (idcardtypemodel.getRowCount() > 0) {
					for (int i = 0; i < idcardtypemodel.getRowCount(); i++) {
						idcardtypeinfo = new MemberDtl();
						idcardtypeinfo.setKeyname(idcardtypemodel.getItemValue(i, "keyname"));
						idcardtypeinfo.setKeyid(idcardtypemodel.getItemValue(i, "keyid"));
						idcardtypelist.add(idcardtypeinfo);
					}
					memberinfo.setIdcardlist(idcardtypelist);
				}
				// 性别
				sql1 = "select b.* " + "from SYS_DICTIONARY a,SYS_DICTIONARY_DTL b "
						+ "where keyword = 'RTL_MEMBERINFO_SEX' " + "and a.ddlid = b.ddlid";
				sh1 = new SSSelectHelper(sql1);
				SSTableModel sexmodel = sh1.executeSelect(con, 0, 999);

				if (sexmodel.getRowCount() > 0) {
					for (int i = 0; i < sexmodel.getRowCount(); i++) {
						sexinfo = new MemberDtl();
						sexinfo.setKeyname(sexmodel.getItemValue(i, "keyname"));
						sexinfo.setKeyid(sexmodel.getItemValue(i, "keyid"));
						sexlist.add(sexinfo);
					}
					memberinfo.setSexlist(sexlist);
				}

			}
			return memberinfo;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return memberinfo;
	}

	// 修改会员信息
	public int updatememberinfo(String memberid, String memsex, String birthdate, String idcardno, String mailaddress,
			String idcardtype, String username) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int executeUpdate = 0;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			String sql = "update rtl_member "
					+ "set sex =?, birthdate = to_date(?,'yyyy-mm-dd hh24:mi:ss'),idcardno = ?,"
					+ "mailaddress = ?,idcardtype=?, membername=?" + "where memberid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(memsex);
			uh.bindParam(birthdate);
			uh.bindParam(idcardno);
			uh.bindParam(mailaddress);
			uh.bindParam(idcardtype);
			uh.bindParam(username);
			uh.bindParam(memberid);
			executeUpdate = uh.executeUpdate(con);
			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			DataConnection.rollback(con);
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return executeUpdate;
	}

	// 获取我的票券信息
	public List<MemberInfo> getmembercoupods(String memberid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<MemberInfo> list = null;
		MemberInfo membercoupods = null;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			list = new ArrayList<MemberInfo>();
			String sql = "select couponid,couponno,usestatus,publishamount,validdate,credate,userange,memo "
					+ " from RTL_CASHCOUPON a where userange in(0, 2, 3) and memberid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			// 查询多条数据
			SSTableModel couponomodel = sh.executeSelect(con, 0, 9999);
			String validdate = "";
			String credate = "";
			for (int i = 0; i < couponomodel.getRowCount(); i++) {
				membercoupods = new MemberInfo();
				membercoupods.setCouponid(couponomodel.getItemValue(i, "couponid"));
				membercoupods.setUsestatus(couponomodel.getItemValue(i, "usestatus"));
				membercoupods.setCouponno(couponomodel.getItemValue(i, "couponno"));
				membercoupods.setPublishamount(couponomodel.getItemValue(i, "publishamount"));
				membercoupods.setUserange(couponomodel.getItemValue(i, "userange"));
				membercoupods.setMemo(couponomodel.getItemValue(i, "memo"));
				membercoupods.setNewvaliddate(couponomodel.getItemValue(i, "validdate"));
				credate = couponomodel.getItemValue(i, "credate");
				if (!"".equals(credate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = formatter.parse(credate);
					credate = formatter.format(parse);
				}
				membercoupods.setCredate(credate);
				validdate = couponomodel.getItemValue(i, "validdate");
				if (!"".equals(validdate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = formatter.parse(validdate);
					validdate = formatter.format(parse);
				}
				membercoupods.setValiddate(validdate);
				list.add(membercoupods);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return list;
	}

	// 获取我的特权信息
	public MemberInfo getmyselfprivilege(String membertypeid) {
		Connection con = null;
		SSSelectHelper sh = null;
		MemberInfo myselfprivilege = new MemberInfo();
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			String sql = "select * from app_membercenter_dtl where membertypeid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(membertypeid);
			// 查询多条数据
			SSTableModel memberinfomodel = sh.executeSelect(con, 0, 1);
			if (memberinfomodel.getRowCount() > 0) {
				// bean类设置定义变量的值，存在设置的list里面，返回前台
				myselfprivilege.setMemberprivilege(memberinfomodel.getItemValue(0, "memberprivilege"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return myselfprivilege;
	}

	// 获取会员信息特权等全部信息
	public List<MemberCenterDocInfo> getprivilegeinfo() {
		Connection con = null;
		SSSelectHelper sh = null;
		List<MemberCenterDocInfo> doclist = null;
		List<MemberCenterDtlInfo> dtllist = null;
		MemberCenterDocInfo docinfo = null;
		MemberCenterDtlInfo dtlinfo = null;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			doclist = new ArrayList<MemberCenterDocInfo>();
			String sqldoc = "select * from app_membercenter_doc";
			sh = new SSSelectHelper(sqldoc);
			// 查询多条数据
			SSTableModel docmodel = sh.executeSelect(con, 0, 99999);
			if (docmodel.getRowCount() <= 0) {
				return doclist;
			}
			for (int j = 0; j < docmodel.getRowCount(); j++) {
				dtllist = new ArrayList<MemberCenterDtlInfo>();
				docinfo = new MemberCenterDocInfo();
				String memcenterdocid = docmodel.getItemValue(j, "memcenterdocid");
				if (docmodel.getRowCount() > 0) {
					// bean类设置定义变量的值，存在设置的list里面，返回前台
					docinfo = new MemberCenterDocInfo();
					docinfo.setMembermechanism(docmodel.getItemValue(j, "membermechanism"));
					docinfo.setMemberpointdesc(docmodel.getItemValue(j, "memberpointdesc"));
				}

				String sql = "select a.memberprivilege,a.membertypeid,b.membertypename "
						+ "from app_membercenter_dtl a,rtl_membertype b where memcenterdocid = ?"
						+ "and a.membertypeid = b.membertypeid";
				sh = new SSSelectHelper(sql);
				sh.bindParam(memcenterdocid);
				// 查询多条数据
				SSTableModel dtlmodel = sh.executeSelect(con, 0, 9999);
				if (dtlmodel.getRowCount() <= 0) {
				}
				for (int i = 0; i < dtlmodel.getRowCount(); i++) {
					// bean类设置定义变量的值，存在设置的list里面，返回前台
					dtlinfo = new MemberCenterDtlInfo();
					dtlinfo.setMemberprivilege(dtlmodel.getItemValue(i, "memberprivilege"));
					dtlinfo.setMembertypeid(dtlmodel.getItemValue(i, "membertypeid"));
					dtlinfo.setMembertypename(dtlmodel.getItemValue(i, "membertypename"));
					dtllist.add(dtlinfo);
				}
				docinfo.setDtllist(dtllist);
				doclist.add(docinfo);
			}
			return doclist;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}

		return doclist;
	}

	// 获取可以兑换订单代金券
	public List<MemberInfo> getIntegralInfo() {
		Connection con = null;
		SSSelectHelper sh = null;
		List<MemberInfo> list = null;
		MemberInfo integralinfo = null;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			list = new ArrayList<MemberInfo>();
			String sql = "select b.keyname, a.cashcouponsubid, a.cashcouponsub, a.publishamount, a.coupondeductpoint,"
					+ " a.coupontype, a.validdate, a.userange "
					+ " from rtl_cashcouponsub a, v_sys_dic b where b.keyid(+) = a.coupontype "
					+ " and b.keyword(+) = 'RTL_CASHCOUPON_TYPE' and mallflag = 1";
			sh = new SSSelectHelper(sql);
			// 查询多条数据
			SSTableModel integralinfomodel = sh.executeSelect(con, 0, 9999);
			for (int i = 0; i < integralinfomodel.getRowCount(); i++) {
				// bean类设置定义变量的值，存在设置的list里面，返回前台
				integralinfo = new MemberInfo();
				integralinfo.setCashcouponsubid(integralinfomodel.getItemValue(i, "cashcouponsubid"));
				integralinfo.setCashcouponsub(integralinfomodel.getItemValue(i, "cashcouponsub"));
				integralinfo.setPublishamount(integralinfomodel.getItemValue(i, "publishamount"));
				integralinfo.setCoupondeductpoint(integralinfomodel.getItemValue(i, "coupondeductpoint"));
				integralinfo.setCoupontype(integralinfomodel.getItemValue(i, "coupontype"));
				integralinfo.setValiddate(integralinfomodel.getItemValue(i, "validdate"));
				integralinfo.setKeyname(integralinfomodel.getItemValue(i, "keyname"));
				integralinfo.setUserange(integralinfomodel.getItemValue(i, "userange"));
				list.add(integralinfo);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return list;
	}

	// 点击兑换代金券
	public int exchangeIntegral(String cashcouponsubid, String memberid, String userange) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSUpdateHelper uh = null;
		SSTableModel shmodel = null;
		String couponno = null;
		int executeUpdate = 0;
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			// 根据前台传过来的兑换代金券id查询所要生成的代金券的面额、类型、有效期、使用积分
			String sql = "select * from rtl_cashcouponsub where cashcouponsubid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(cashcouponsubid);
			// 查询一条数据
			SSTableModel cashinfomodel = sh.executeSelect(con, 0, 1);
			// 查询有效期
			String validdate = cashinfomodel.getItemValue(0, "validdate");
			// 代金券面额
			String publishamount = cashinfomodel.getItemValue(0, "publishamount");
			// 兑换代金券使用的积分
			String coupondeductpoint = cashinfomodel.getItemValue(0, "coupondeductpoint");
			// 兑换的代金券类型
			String coupontype = cashinfomodel.getItemValue(0, "coupontype");
			// 兑换的代金券策略名称
			String cashcouponsub = cashinfomodel.getItemValue(0, "cashcouponsub");

			sql = "select * from v_rtl_member where memberid = ? ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			sh.executeSelect(con, 0, 1);
			SSTableModel m = sh.executeSelect(con, 0, 1);
			String mobile = m.getItemValue(0, "mobile");

			for (int i = 1; i > 0; i++) {
				// 构造代金券号
				StringBuffer coupon = new StringBuffer();
				String d = new SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime());
				// 取六位随机数
				String genRandomNum = genRandomNum();
				coupon.append(d);
				coupon.append(genRandomNum);
				couponno = getCouponno(coupon.toString());
				String quersql = "select couponno from rtl_cashcoupon where couponno=? for update ";
				sh = new SSSelectHelper(quersql);
				sh.bindParam(couponno);
				shmodel = sh.executeSelect(con, 0, 1);
				if (shmodel == null || shmodel.getRowCount() <= 0) {
					break;
				}
			}

			// 计算有效日期
			int days = Integer.parseInt(validdate);
			validdate = plusDay(days);
			// 生成代金券记录
			SSInsertHelper iih = new SSInsertHelper("rtl_cashcoupon");
			iih.bindSequence("couponid", "seq_rtl_cashcoupon");
			iih.bindParam("couponno", couponno);
			iih.bindParam("amount", publishamount);
			iih.bindParam("publishamount", publishamount);
			iih.bindDate("validdate", validdate);
			iih.bindParam("usestatus", "1");
			iih.bindParam("memberid", memberid);
			iih.bindParam("mobile", mobile);
			iih.bindSysdate("credate");
			iih.bindParam("coupontype", coupontype);
			iih.bindParam("inputmanid", "9");
			iih.bindParam("issuemode", "4");

			// 使用范围(检查使用范围的值是否符合)
			if ("".equals(userange)) {
				iih.bindParam("userange", "2");
			} else {
				iih.bindParam("userange", userange);
			}
			// 生效日期
			iih.bindSysdate("startdate");
			iih.executeInsert(con);

			String lstsql = "select * from rtl_cashcoupon where couponno=?";
			SSSelectHelper lstssh = new SSSelectHelper(lstsql);
			lstssh.bindParam(couponno);
			SSTableModel couponlstModel = lstssh.executeSelect(con, 0, 1);
			SSInsertHelper ssih = new SSInsertHelper("rtl_cashcoupon_lst");
			ssih.bindSequence("lstid", "seq_rtl_cashcoupon_lst");
			ssih.bindParam("couponid", couponlstModel.getItemValue(0, "couponid"));
			ssih.bindParam("amount", publishamount);
			ssih.bindParam("comefrom", "54");
			ssih.bindSysdate("credate");
			ssih.bindParam("inputmanid", "9");
			ssih.executeInsert(con);
			// 积分消费记录SEQ_RTL_MEMBERPOINT_CONSUME
			String pointconsumeid = SSModelHelper.getSequenceValue(con, "SEQ_RTL_MEMBERPOINT_CONSUME");
			SSInsertHelper ihh = new SSInsertHelper("rtl_memberpoint_consume");
			ihh.bindParam("pointconsumeid", pointconsumeid);
			ihh.bindParam("memberid", memberid);
			ihh.bindParam("coupontype", "0");
			ihh.bindParam("consumepoint", coupondeductpoint);// 消费积分
			ihh.bindParam("usestatus", "1");
			ihh.bindParam("inputmanid", "9");
			ihh.bindSysdate("credate");
			ihh.bindParam("cashcouponsubid", cashcouponsubid);// 代金券策略id
			ihh.bindParam("cashcouponsub", cashcouponsub);// 策略名称
			ihh.bindParam("coupondeductpoint", coupondeductpoint);// 兑换积分
			ihh.bindParam("couponamount", publishamount);// 代金券金额
			ihh.bindParam("couponvaliddate", days + "");// 代金券有效天数
			ihh.executeInsert(con);

			// 生成积分流水
			SSInsertHelper ih = new SSInsertHelper("rtl_memberpoint_lst");
			ih.bindSequence("memberpointlstid", "seq_rtl_memberpoint_lst");
			ih.bindParam("memberid", memberid);
			ih.bindParam("sourcetable", "2");
			ih.bindParam("comefrom", "3");
			ih.bindParam("sourceid", pointconsumeid);
			ih.bindParam("sourcedtlid", pointconsumeid);
			ih.bindParam("ioflag", "1");
			ih.bindParam("memberpoint", coupondeductpoint);
			ih.bindSysdate("pointdate");
			ih.bindParam("usestatus", "1");
			ih.bindParam("deductstatus", "0");
			ih.bindParam("inputmanid", "9");
			ih.bindSysdate("credate");
			ih.executeInsert(con);

			// 第一步 获得会员总积分
			String sqls = "select * from V_RTL_MEMBER where memberid = ?";
			sh = new SSSelectHelper(sqls);
			sh.bindParam(memberid);
			// 查询一条数据
			SSTableModel pointmodel = sh.executeSelect(con, 0, 1);
			String totalpoint = pointmodel.getItemValue(0, "curpoint");
			// 兑换后的积分
			String surpluspoint = SSDecimalHelper.sub(totalpoint, coupondeductpoint, 2);
			// 更新兑换后的积分
			String sqlu = "update rtl_member set curpoint =?  where memberid = ?";
			uh = new SSUpdateHelper(sqlu);
			uh.bindParam(surpluspoint);
			uh.bindParam(memberid);
			executeUpdate = uh.executeUpdate(con);
			con.commit();
			return executeUpdate;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
			return executeUpdate;
		} finally {
			DataConnection.close(con);
		}
	}

	// private SSTableModel genRandom(Connection con) {
	// SSTableModel shmodel = null;
	// String couponno = null;
	// // 构造代金券号
	// StringBuffer coupon = new StringBuffer();
	// String d = new
	// SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime());
	// // 取六位随机数
	// String genRandomNum = genRandomNum();
	// coupon.append(d);
	// coupon.append(genRandomNum);
	// couponno = getCouponno(coupon.toString());
	// String quersql =
	// "select couponno from rtl_cashcoupon where couponno=? for update ";
	// SSSelectHelper sh = new SSSelectHelper(quersql);
	// sh.bindParam(couponno);
	// try {
	// shmodel = sh.executeSelect(con, 0, 1);
	// return shmodel;
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("error", e);
	// }
	// return shmodel;
	// }

	// 生成随机代金卷号码
	private static String genRandomNum() {
		int i; // 生成的随机数
		int count = 0; // 生成数的长度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < 6) {
			// 生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(6)); // 生成的数最大为5
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	// 计算一个规则
	private static String getCouponno(String couponno) {
		int s1 = 0;
		int s2 = 0;
		int s3 = 0;
		int s4 = 0;
		int s5 = 0;
		StringBuffer aa = new StringBuffer();
		for (int i = 0; i < couponno.length(); i++) {
			if (i % 2 == 0) {
				s1 += Integer.parseInt(couponno.substring(i, i + 1));

			} else {
				s2 += Integer.parseInt(couponno.substring(i, i + 1));
			}
		}
		s3 = s1 + s2 * 3;
		s4 = s3 % 10;

		if ((10 - s4) == 10) {
			s5 = 0;
		} else {
			s5 = 10 - s4;
		}
		aa.append(couponno);
		aa.append(s5);
		return aa.toString();
	}

	/**
	 * 当前日期加上天数后的日期
	 * 
	 * @param num
	 *            为增加的天数
	 * @return
	 */
	public static String plusDay(int num) {
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currdate = format.format(d);

		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, num);// num为增加的天数，可以改变的
		d = ca.getTime();
		String enddate = format.format(d);
		return enddate;
	}

	// 分页加载兑换礼品
	public List<MemberInfo> getIntegralGoodsInfo(String memberid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<MemberInfo> list = null;
		MemberInfo integralgoodsinfo = null;
		Pic pic = new Pic();
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();

			String sqls = "select * from V_RTL_MEMBER where memberid = ?";
			sh = new SSSelectHelper(sqls);
			sh.bindParam(memberid);
			// 查询一条数据
			SSTableModel membertypeidmodel = sh.executeSelect(con, 0, 1);
			String membertypeid = membertypeidmodel.getItemValue(0, "membertypeid");

			list = new ArrayList<MemberInfo>();
			String sql = "select a.membertypeid,a.membertypename,b.* from V_RTL_GOODS_DTL a,V_RTL_GOODS b "
					+ " where a.goodsid = b.goodsid and usestatus =1  and a.membertypeid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(membertypeid);
			// 查询多条数据
			SSTableModel integralgoodsinfomodel = sh.executeSelect(con, Integer.parseInt(skip),
					Integer.parseInt(limit));
			for (int i = 0; i < integralgoodsinfomodel.getRowCount(); i++) {
				// bean类设置定义变量的值，存在设置的list里面，返回前台
				integralgoodsinfo = new MemberInfo();
				integralgoodsinfo.setGoodsid(integralgoodsinfomodel.getItemValue(i, "goodsid"));
				integralgoodsinfo.setGoodsname(integralgoodsinfomodel.getItemValue(i, "goodsname"));
				integralgoodsinfo.setGoodsspecs(integralgoodsinfomodel.getItemValue(i, "goodsspecs"));
				integralgoodsinfo.setBasepackname(integralgoodsinfomodel.getItemValue(i, "basepackname"));
				integralgoodsinfo.setFactoryname(integralgoodsinfomodel.getItemValue(i, "factoryname"));
				integralgoodsinfo.setGoodsdeductpoint(integralgoodsinfomodel.getItemValue(i, "goodsdeductpoint"));
				String goodsPic = pic.getThumbnailGoodsPic(integralgoodsinfomodel.getItemValue(i, "goodsid"));
				integralgoodsinfo.setPicname(goodsPic);
				integralgoodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				list.add(integralgoodsinfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return list;
	}

	/**
	 * 检查核销人员是否符合
	 * 
	 * @param couponid
	 * @param value
	 * @return
	 */
	public int checkUseRange(String couponid, String value) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSUpdateHelper uh = null;
		int dataflag = 0;
		try {
			con = DataConnection.getConnection();
			String userid = value.substring(1);
			String sql = "select userid from sys_user where usestatus = 1 and userid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() > 0) {
				sql = "select couponid from rtl_cashcoupon where userange in(0, 2, 3) and usestatus = 1 and couponid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(couponid);
				model = sh.executeSelect(con, 0, 1);
				if (model.getRowCount() > 0) {
					String sqlupdate = "update rtl_cashcoupon set usestatus = 3, invaliddate = sysdate, invalidmanid = ?, "
							+ "modimanid=?,modidate=sysdate,operationdate = sysdate, operationmanid = ? where couponid = ? ";
					uh = new SSUpdateHelper(sqlupdate);
					uh.bindParam(userid);
					uh.bindParam(userid);
					uh.bindParam(userid);
					uh.bindParam(couponid);
					dataflag = uh.executeUpdate(con);
					con.commit();
				}
			} else {
				dataflag = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return dataflag;

	}
}
