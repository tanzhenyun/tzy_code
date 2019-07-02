package com.dao;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bean.EventInfo;
import com.bean.FileInfo;
import com.bean.GoodsInfo;
import com.bean.HomeAreaDtl;
import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 读取文件帮助类
 * 
 * @author KF_04
 * 
 */
public class FileHelper {

	private Logger logger = Logger.getLogger(FileHelper.class);

	/**
	 * 轮播图
	 * 
	 * @return
	 */
	public FileInfo getBanner() {
		File file = null;
		FileInfo fileinfo = new FileInfo();
		try {
			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\echomepic\\lb";
			file = new File(pathname);
			List<String> filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int i = 0; i < fa.length; i++) {
					File fs = fa[i];
					if (fs.isDirectory()) {
						return fileinfo;
					} else {
						filenamelist.add(fs.getName());
					}
				}
				Collections.sort(filenamelist);
				fileinfo.setFilenamelist(filenamelist);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		}
		return fileinfo;

	}

	/**
	 * 获取商品详情信息
	 * 
	 * @param pathname
	 * @param shopid
	 * @param goodsid
	 * @return
	 */
	public FileInfo getFileInfo(String shopid, String goodsid) {
		Connection con = null;
		File file = null;
		FileInfo fileinfo = new FileInfo();
		GoodsInfo goodsinfo = null;
		SSSelectHelper sh = null;
		try {
			con = DataConnection.getConnection();
			// 检查此门店是否可用
			String shopsql = "select mallflag from glb_shop where shopid = ?";
			sh = new SSSelectHelper(shopsql);
			sh.bindParam(shopid);
			SSTableModel shopmodel = sh.executeSelect(con, 0, 1);
			if ("".equals(shopmodel.getItemValue(0, "mallflag")) || "0".equals(shopmodel.getItemValue(0, "mallflag"))) {
				return fileinfo;
			}

			goodsinfo = new GoodsInfo();
			Pic pic = new Pic();
			List<String> filenamelist = pic.getGoodsPicList(goodsid);

			String sql = "select  nvl(a.mallpriceid,1) mallpriceid from st_area a, ST_POS b, ST_GOODS_DEFPOS c "
					+ " where a.stareaid = b.stareaid and b.stposid = c.partstposid and a.stdeptid = ? and c.goodsid= ?";

			sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			sh.bindParam(goodsid);
			SSTableModel pricemodel = sh.executeSelect(con, 0, 1);
			String mallpriceid = null;
			if (pricemodel.getRowCount() > 0) {
				mallpriceid = pricemodel.getItemValue(0, "mallpriceid");
			} else {
				return fileinfo;
			}
			String sql1 = "select a.goodsid,g.goodsname,g.goodsspecs,i.companyname factoryname,h.packname basepackname,g.goodsbarcode,"
					+ " g.approvalno,sum(a.goodsqty) goodsqty,e.mallpriceid,k.companyid shopid,k.companyname shopname,l.price,g.otcflag,"
					+ " m.gooddesc ,n.importflag,n.assistflag"
					+ " from v_st_qty_saleable a,st_acc_def b,st_goods_defpos c,st_pos d,st_area e,glb_goods_lot f,glb_goods g,"
					+ " glb_goodspack h,sys_company i,glb_goodsstatus j,sys_company k,pr_goods_price l,glb_ecgoods m,app_sort_descriptor_dtl n"
					+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid and c.partstposid = d.stposid"
					+ " and d.stareaid = e.stareaid and a.lotid = f.lotid and f.validdate - sysdate > nvl(e.salesvaliddays, 0)"
					+ " and a.goodsid = g.goodsid and g.goodsid = h.goodsid and h.basepackflag = 1 and a.goodsstatusid = j.goodsstatusid"
					+ " and j.cansalesflag = 1 and g.factoryid = i.companyid(+) and g.mallflag = 1 and b.stdeptid = k.companyid"
					+ " and g.goodsid = l.goodsid and e.mallpriceid = l.priceid " + " and l.priceid = ? "
					+ " and a.goodsid = m.goodsid(+) " + " and a.goodsid  = n.goodsid(+)"
					+ " and b.stdeptid = ? and a.goodsid = ?"
					+ " group by a.goodsid,g.goodsname,g.goodsspecs,i.companyname,h.packname,g.goodsbarcode,g.approvalno,e.mallpriceid,"
					+ " b.stdeptid, k.companyid, k.companyname, l.price,g.otcflag,m.gooddesc, n.importflag,n.assistflag ";
			sh = new SSSelectHelper(sql1);
			sh.bindParam(mallpriceid);
			sh.bindParam(shopid);
			sh.bindParam(goodsid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				return fileinfo;
			} else {
				goodsinfo.setGoodsid(goodsid);
				goodsinfo.setGoodsname(model.getItemValue(0, "goodsname"));
				goodsinfo.setGoodsspecs(model.getItemValue(0, "goodsspecs"));
				goodsinfo.setBasepackname(model.getItemValue(0, "basepackname"));
				goodsinfo.setInventoryqty(model.getItemValue(0, "goodsqty"));
				goodsinfo.setFactoryname(model.getItemValue(0, "factoryname"));
				goodsinfo.setPrice(model.getItemValue(0, "price"));
				goodsinfo.setShopid(shopid);
				goodsinfo.setShopname(ShopHelper.getShopName(con, shopid));
				goodsinfo.setOtcflag(model.getItemValue(0, "otcflag"));
				goodsinfo.setImportflag(model.getItemValue(0, "importflag"));
				goodsinfo.setAssistflag(model.getItemValue(0, "assistflag"));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				fileinfo.setGoodsinfo(goodsinfo);
				fileinfo.setTxt(model.getItemValue(0, "gooddesc"));
				fileinfo.setFilenamelist(filenamelist);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return fileinfo;
	}

	/**
	 * 获取门店内商品
	 * 
	 * @param shopid
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<GoodsInfo> getShop(String shopid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<GoodsInfo> list = new ArrayList<GoodsInfo>();
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();

			// 检查此门店是否可用
			String shopsql = "select mallflag from glb_shop where shopid = ?";
			sh = new SSSelectHelper(shopsql);
			sh.bindParam(shopid);
			SSTableModel shopmodel = sh.executeSelect(con, 0, 1);
			if ("".equals(shopmodel.getItemValue(0, "mallflag")) || "0".equals(shopmodel.getItemValue(0, "mallflag"))) {
				return list;
			}

			String sql = "select a.goodsid,g.goodsname,g.goodsspecs,i.companyname factoryname,h.packname basepackname,"
					+ " g.goodsbarcode,g.approvalno,sum(a.goodsqty) goodsqty,e.mallpriceid,k.price"
					+ " from st_qty_now a,st_acc_def b,st_goods_defpos c,st_pos d,st_area e,glb_goods_lot f,"
					+ " glb_goods g,glb_goodspack h,sys_company i,glb_goodsstatus j,pr_goods_price k"
					+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid"
					+ " and c.partstposid = d.stposid and d.stareaid = e.stareaid and a.goodsid = k.goodsid(+)"
					+ " and e.mallpriceid = k.priceid and a.lotid = f.lotid "
					+ " and f.validdate - sysdate > nvl(e.salesvaliddays, 0)"
					+ " and a.goodsid = g.goodsid and g.goodsid = h.goodsid and h.basepackflag = 1 "
					+ " and a.goodsstatusid = j.goodsstatusid and j.cansalesflag = 1 "
					+ " and g.factoryid = i.companyid(+) and g.mallflag = 1 and b.stdeptid = ?"
					+ " group by a.goodsid,g.goodsname,g.goodsspecs,i.companyname,h.packname,"
					+ " g.goodsbarcode,g.approvalno,e.mallpriceid,k.price";
			sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			for (int i = 0; i < model.getRowCount(); i++) {
				goodsinfo = new GoodsInfo();

				String goodsPic = pic.getThumbnailGoodsPic(model.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);

				goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				goodsinfo.setGoodsspecs(model.getItemValue(i, "goodsspecs"));
				goodsinfo.setBasepackname(model.getItemValue(i, "basepackname"));
				goodsinfo.setFactoryname(model.getItemValue(i, "factoryname"));
				goodsinfo.setGoodsqty(model.getItemValue(i, "goodsqty"));
				goodsinfo.setPrice(model.getItemValue(i, "price"));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				SalesPromotionHelper sph = new SalesPromotionHelper();
				// 单品促销信息
				List<SingleSPInfo> singleSpList = sph.getSingleSpList(shopid, model.getItemValue(i, "goodsid"));
				goodsinfo.setSinglelist(singleSpList);
				// 整单促销信息
				List<WholeSPInfo> wholeSpList = sph.getWholeSpList(shopid);
				goodsinfo.setWholelist(wholeSpList);

				list.add(goodsinfo);
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

	/**
	 * 获取推荐商品
	 * 
	 * @param goodsid
	 * @param importflag
	 *            主药标志
	 * @param assistflag
	 *            辅药标志
	 * @return
	 */
	public List<GoodsInfo> getgoodsrecommendinfo(String goodsid, String importflag, String assistflag) {
		Connection con = null;
		List<GoodsInfo> list = new ArrayList<GoodsInfo>();
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select mallclassid from glb_goods where goodsid = ?";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(goodsid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			String mallclassid = model.getItemValue(0, "mallclassid");
			// 检查是主药或辅药
			SSTableModel modelmin = null;
			if ("1".equals(importflag)) {
				String sqlmin = "select c.goodsname,a.goodsid "
						+ " from app_sort_descriptor_dtl a, app_sort_descriptor_doc b,glb_goods c"
						+ " where b.minclassid = ? and a.sortdescdocid = b.sortdescdocid"
						+ " and a.assistflag = 1 and a.goodsid = c.goodsid";
				SSSelectHelper shmin = new SSSelectHelper(sqlmin);
				shmin.bindParam(mallclassid);
				modelmin = shmin.executeSelect(con, 0, 9999);
			} else if ("1".equals(assistflag)) {
				String sqlmin = "select c.goodsname,a.goodsid "
						+ " from app_sort_descriptor_dtl a, app_sort_descriptor_doc b,glb_goods c"
						+ " where b.minclassid = ? and a.sortdescdocid = b.sortdescdocid"
						+ " and a.importflag = 1 and a.goodsid = c.goodsid";
				SSSelectHelper shmin = new SSSelectHelper(sqlmin);
				shmin.bindParam(mallclassid);
				modelmin = shmin.executeSelect(con, 0, 9999);
			}
			for (int i = 0; i < modelmin.getRowCount(); i++) {
				goodsinfo = new GoodsInfo();

				String goodsPic = pic.getThumbnailGoodsPic(modelmin.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);

				goodsinfo.setGoodsid(modelmin.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(modelmin.getItemValue(i, "goodsname"));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				list.add(goodsinfo);
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

	/**
	 * 首页查询推荐
	 * 
	 * @return
	 */
	public Map<String, List> getRecommend(String skip, String limit) {
		Connection con = null;
		Map<String, List> map = new HashMap<String, List>();
		List<GoodsInfo> goodslist = new ArrayList<GoodsInfo>();
		List<EventInfo> eventlist = new ArrayList<EventInfo>();
		List<Object> adlist = new ArrayList<Object>();
		GoodsInfo goodsinfo = null;
		EventInfo eventinfo = null;
		File file = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			// 活动
			String sql = "select eventid,eventname,linktype,goodsid,linkeventid,deflink,goodsname "
					+ " from V_APP_EVENT where usestatus = 1 order by eventid";
			SSSelectHelper sh = new SSSelectHelper(sql);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			String picpath = Parameter.getParameter("picpath");
			String folderpath = Parameter.getParameter("folderpath");
			for (int i = 0, c = model.getRowCount(); i < c; i++) {
				eventinfo = new EventInfo();
				eventinfo.setEventid(model.getItemValue(i, "eventid"));
				eventinfo.setEventname(model.getItemValue(i, "eventname"));
				eventinfo.setLinktype(model.getItemValue(i, "linktype"));
				eventinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				eventinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				eventinfo.setLinkeventid(model.getItemValue(i, "linkeventid"));
				eventinfo.setDeflink(model.getItemValue(i, "deflink"));
				eventinfo.setFolderpath(folderpath);

				String pathname = picpath + "\\ecpic\\eventpic\\" + model.getItemValue(i, "eventid");
				file = new File(pathname);
				List<String> filenamelist = new ArrayList<String>();
				// 递归求取目录文件个数
				File flist[] = file.listFiles();
				if (flist == null) {
				} else {
					file = new File(pathname);
					File fa[] = file.listFiles();
					for (int j = 0; j < fa.length; j++) {
						File fs = fa[j];
						if (fs.isDirectory()) {
						} else {
							filenamelist.add(fs.getName());
						}
					}
					Collections.sort(filenamelist);
				}
				if (filenamelist.size() > 0) {
					eventinfo.setPicname(filenamelist.get(0));
				} else {
					eventinfo.setPicname("");
				}
				eventlist.add(eventinfo);
			}
			map.put("eventlist", eventlist);

			// 推荐商品
			sql = "select a.goodsid,b.goodsname from APP_SORT_DESCRIPTOR_DTL a, GLB_GOODS b "
					+ " where recommendflag = 1 and a.goodsid = b.goodsid order by sortdescdtlid ";
			sh = new SSSelectHelper(sql);
			model = sh.executeSelect(con, 0, 9999);
			for (int i = 0, c = model.getRowCount(); i < c; i++) {
				goodsinfo = new GoodsInfo();
				String goodsPic = pic.getGoodsPic(model.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);
				goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				goodsinfo.setFolderpath(folderpath);
				goodslist.add(goodsinfo);
			}
			map.put("goodslist", goodslist);

			// 首页广告
			sql = "select b.homeareadtlid,b.linktype,b.deflink,b.goodsid,b.goodsname,b.eventid,b.spruleid "
					+ " from APP_HOME_AREA_DOC a, V_APP_HOME_AREA_DTL b "
					+ " where a.floatflag = 1 and a.usestatus = 1 and a.homeareadocid = b.homeareadocid "
					+ " order by a.credate desc";

			sh = new SSSelectHelper(sql);
			model = sh.executeSelect(con, 0, 1);
			HomeAreaDtl dtl = new HomeAreaDtl();
			if (model.getRowCount() > 0) {
				String picname = pic.getActivityPic(model.getItemValue(0, "homeareadtlid"));
				String linktype = model.getItemValue(0, "linktype");
				dtl.setHomeareadtlid(model.getItemValue(0, "homeareadtlid"));
				dtl.setLinktype(linktype);
				dtl.setDeflink(model.getItemValue(0, "deflink"));
				dtl.setGoodsid(model.getItemValue(0, "goodsid"));
				dtl.setGoodsname(model.getItemValue(0, "goodsname"));
				dtl.setEventid(model.getItemValue(0, "eventid"));
				dtl.setPicname(picname);
				dtl.setSpruleid(model.getItemValue(0, "spruleid"));
				dtl.setFolderpath(folderpath);

				if ("4".equals(linktype)) {
					// 促销
					sql = "select spruleid, couponamount, validdate, a.userange "
							+ " from PR_RETAILSALE_SP_RULE a, PR_RETAILSALE_SP b "
							+ " where a.spid = b.spid and b.usestatus = 1 and b.userange in(0, 2) "
							+ " and sysdate between b.startdate and b.enddate + 1 "
							+ " and to_number(to_char(sysdate, 'hh24')) + 1 BETWEEN b.starttime "
							+ " AND b.endtime and a.sptype = 6 and a.usestatus = 1 and a.spruleid = ?";
					sh = new SSSelectHelper(sql);
					sh.bindParam(model.getItemValue(0, "spruleid"));
					SSTableModel spmodel = sh.executeSelect(con, 0, 1);
					if (spmodel.getRowCount() > 0) {
						dtl.setCouponamount(spmodel.getItemValue(0, "couponamount"));
						dtl.setValiddate(spmodel.getItemValue(0, "validdate"));
						dtl.setUserange(spmodel.getItemValue(0, "userange"));
					}
				}
				adlist.add(dtl);
			}
			map.put("adlist", adlist);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return map;
	}

	/**
	 * 首页活动领取代金券,生成代金券以及代金券流水
	 * 
	 * @return
	 * 
	 */
	public int generateCoupon(String memberid, String spruleid, String publishamount, String validdate,
			String userange) {
		Connection con = null;
		SSSelectHelper sh = null;
		int flag = 0;
		try {
			con = DataConnection.getConnection();
			// 检查是否已经领取过
			String sql = "select couponid from rtl_cashcoupon where memberid = ? and spruleid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			sh.bindParam(spruleid);
			SSTableModel model = sh.executeSelect(con, 0, 1);

			if (model.getRowCount() > 0) {
				// 已经领过代金券
				flag = -1;
			} else {
				String couponno = "";
				sql = "select mobile from rtl_member where memberid = ?";
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
					String genRandomNum = OrderSettlementHelper.genRandomNum();
					coupon.append(d);
					coupon.append(genRandomNum);
					couponno = OrderSettlementHelper.getCouponno(coupon.toString());
					String quersql = "select couponno from rtl_cashcoupon where couponno = ? for update ";
					sh = new SSSelectHelper(quersql);
					sh.bindParam(couponno);
					SSTableModel shmodel = sh.executeSelect(con, 0, 1);
					if (shmodel == null || shmodel.getRowCount() <= 0) {
						break;
					}
				}
				// 计算有效日期
				int days = Integer.parseInt(validdate);
				validdate = OrderSettlementHelper.plusDay(days);
				// 生成代金券记录
				SSInsertHelper iih = new SSInsertHelper("rtl_cashcoupon");
				iih.bindSequence("couponid", "seq_rtl_cashcoupon");
				iih.bindParam("couponno", couponno);
				iih.bindParam("amount", publishamount);
				iih.bindParam("publishamount", publishamount);
				iih.bindDate("validdate", validdate);
				iih.bindParam("usestatus", "1");
				iih.bindParam("memberid", memberid);
				// iih.bindParam("shopid", shopid);
				iih.bindParam("mobile", mobile);
				iih.bindParam("spruleid", spruleid);
				// iih.bindParam("memo", "由商城订单" + prescriptionid + "生成");
				iih.bindSysdate("credate");
				// 插入为电子券1
				iih.bindParam("coupontype", "1");
				iih.bindParam("issuemode", "4");
				iih.bindParam("inputmanid", "9");
				// 使用范围
				if ("".equals(userange)) {
					iih.bindParam("userange", "2");
				} else {
					iih.bindParam("userange", userange);
				}
				// 生效日期
				iih.bindSysdate("startdate");
				flag = iih.executeInsert(con);

				String lstsql = "select couponid from rtl_cashcoupon where couponno=?";
				SSSelectHelper lstssh = new SSSelectHelper(lstsql);
				lstssh.bindParam(couponno);
				SSTableModel couponlstModel = lstssh.executeSelect(con, 0, 1);
				SSInsertHelper ssih = new SSInsertHelper("rtl_cashcoupon_lst");
				ssih.bindSequence("lstid", "seq_rtl_cashcoupon_lst");
				ssih.bindParam("couponid", couponlstModel.getItemValue(0, "couponid"));
				ssih.bindParam("amount", publishamount);
				// ssih.bindParam("shopid", shopid);
				ssih.bindParam("inputmanid", "9");
				// ssih.bindParam("sourceid", prescriptionid);
				// 业务类型为零售发放
				ssih.bindParam("comefrom", "51");
				ssih.bindSysdate("credate");
				flag += ssih.executeInsert(con);
				con.commit();

			}

		} catch (Exception e) {
			logger.error("error", e);
			e.printStackTrace();
			DataConnection.rollback(con);
		} finally {
			DataConnection.close(con);
		}
		return flag;
	}

}
