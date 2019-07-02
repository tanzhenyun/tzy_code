package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.ClassInfo;
import com.bean.DescriotorDoc;
import com.bean.DescriotorDtl;
import com.bean.GoodsInfo;
import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 类别帮助类
 * 
 * @author KF_04
 * 
 */
public class ClassifyHelper {

	private Logger logger = Logger.getLogger(ClassifyHelper.class);

	/**
	 * 获取中类
	 * 
	 * @return
	 */
	public List getMidClass() {
		Connection con = null;
		SSSelectHelper sh = null;
		List classinfolist = new ArrayList();
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			String sql = "select a.midclassid,a.midclassname from GLB_GOODS_MIDCLASS a, GLB_GOODS_MAINCLASS b"
					+ " where a.mainclassid = b.mainclassid and b.mallflag =1 and b.onlineflag = 1"
					+ " order by midclassid";
			sh = new SSSelectHelper(sql);
			// 查询多条数据
			SSTableModel classifymodel = sh.executeSelect(con, 0, 9999);
			if (classifymodel.getRowCount() > 0) {
				for (int i = 0; i < classifymodel.getRowCount(); i++) {
					ClassInfo classinfo = new ClassInfo();
					// bean类设置定义变量的值，存在设置的list里面，返回前台
					classinfo.setMidclassid(classifymodel.getItemValue(i, "midclassid"));
					classinfo.setMidclassname(classifymodel.getItemValue(i, "midclassname"));
					classinfolist.add(classinfo);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return classinfolist;
	}

	/**
	 * 获取当前中类下的小类
	 * 
	 * @param midclassid
	 * @return
	 */
	public List getMinClass(String midclassid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List classinfolist = new ArrayList();
		SSTableModel goodsidmodel = null;
		Pic pic = new Pic();
		try {
			// 得到数据库连接
			con = DataConnection.getConnection();
			String sql = "select a.minclassid,a.minclassname,c.drugflag "
					+ " from GLB_GOODS_MINCLASS a, GLB_GOODS_MIDCLASS b, GLB_GOODS_MAINCLASS c"
					+ " where a.midclassid = ? and a.midclassid = b.midclassid and b.mainclassid = c.mainclassid"
					+ " order by a.minclassid";
			sh = new SSSelectHelper(sql);
			sh.bindParam(midclassid);
			// 查询多条数据
			SSTableModel classifymodel = sh.executeSelect(con, 0, 9999);
			String goodsPic;
			String goodsid;
			if (classifymodel.getRowCount() > 0) {
				for (int i = 0; i < classifymodel.getRowCount(); i++) {
					ClassInfo classinfo = new ClassInfo();
					goodsPic = "";
					goodsid = "";

					// 查询小类下的商品ID 显示图片
					// sql =
					// "select b.goodsid from APP_SORT_DESCRIPTOR_DOC a,
					// APP_SORT_DESCRIPTOR_DTL b"
					// +
					// " where a.sortdescdocid = b.sortdescdocid and
					// a.minclassid = ? order by b.goodsid";
					sql = "select * from GLB_GOODS where mallclassid = ? order by goodsid";

					sh = new SSSelectHelper(sql);
					sh.bindParam(classifymodel.getItemValue(i, "minclassid"));
					goodsidmodel = sh.executeSelect(con, 0, 1);
					if (goodsidmodel.getRowCount() > 0) {
						goodsid = goodsidmodel.getItemValue(0, "goodsid");
						goodsPic = pic.getThumbnailGoodsPic(goodsid);
					}
					classinfo.setPicname(goodsPic);
					classinfo.setGoodsid(goodsid);
					// bean类设置定义变量的值，存在设置的list里面，返回前台
					classinfo.setMinclassid(classifymodel.getItemValue(i, "minclassid"));
					classinfo.setMinclassname(classifymodel.getItemValue(i, "minclassname"));
					classinfo.setDrugflag(classifymodel.getItemValue(i, "drugflag"));
					classinfo.setFolderpath(Parameter.getParameter("folderpath"));

					classinfolist.add(classinfo);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return classinfolist;
	}

	/**
	 * 药品小类信息
	 * 
	 * @param minclassid
	 * @return
	 */
	public DescriotorDoc getMinDescriptor(String minclassid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		DescriotorDoc docinfo = null;
		DescriotorDtl dtlinfo = null;
		List<DescriotorDtl> dtllist = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			docinfo = new DescriotorDoc();
			dtllist = new ArrayList<DescriotorDtl>();
			String sql = "select * from APP_SORT_DESCRIPTOR_DOC t where t.minclassid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(minclassid);
			SSTableModel docmodel = sh.executeSelect(con, 0, 1);
			if (docmodel.getRowCount() <= 0) {
				return docinfo;
			}
			String sortdescdocid = docmodel.getItemValue(0, "sortdescdocid");
			docinfo.setSortdescdocid(sortdescdocid);
			docinfo.setDiseasedesc(docmodel.getItemValue(0, "diseasedesc"));
			docinfo.setTreatmentplan(docmodel.getItemValue(0, "treatmentplan"));

			sql = "select * from V_APP_SORT_DESCRIPTOR_DTL where sortdescdocid = ? and recommendflag = 1 and mallflag = 1";
			sh = new SSSelectHelper(sql);
			sh.bindParam(sortdescdocid);
			SSTableModel dtlmodel = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (dtlmodel.getRowCount() <= 0) {
			} else {
				for (int i = 0; i < dtlmodel.getRowCount(); i++) {
					dtlinfo = new DescriotorDtl();
					dtlinfo.setSortdescdtlid(dtlmodel.getItemValue(i, "sortdescdtlid"));
					dtlinfo.setGoodsid(dtlmodel.getItemValue(i, "goodsid"));
					dtlinfo.setGoodsname(dtlmodel.getItemValue(i, "goodsname"));
					dtlinfo.setGoodsspecs(dtlmodel.getItemValue(i, "goodsspecs"));
					dtlinfo.setFactoryname(dtlmodel.getItemValue(i, "factoryname"));
					String goodsPic = pic.getThumbnailGoodsPic(dtlmodel.getItemValue(i, "goodsid"));
					dtlinfo.setPicname(goodsPic);
					dtlinfo.setFolderpath(Parameter.getParameter("folderpath"));

					dtlinfo.setRecommendflag(dtlmodel.getItemValue(i, "recommendflag"));
					dtlinfo.setImportflag(dtlmodel.getItemValue(i, "importflag"));
					dtlinfo.setAssistflag(dtlmodel.getItemValue(i, "assistflag"));
					dtllist.add(dtlinfo);
				}
			}
			docinfo.setDtllist(dtllist);
			return docinfo;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return docinfo;
	}

	/**
	 * 非药品list
	 * 
	 * @param minclassid
	 */
	public List<GoodsInfo> getGoodsList(String minclassid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		Pic pic = new Pic();
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		try {
			con = DataConnection.getConnection();
			goodslist = new ArrayList<GoodsInfo>();
			// String sql = "SELECT k.shopid, l.companyname shopname, a.goodsid,
			// g.goodsname, g.goodsspecs, i.companyname factoryname,"
			// + " h.packname basepackname, g.goodsbarcode, g.approvalno,
			// sum(a.goodsqty) goodsqty, e.mallpriceid,"
			// + " h.packname basepackname, m.price"
			// + " FROM st_qty_now a, st_acc_def b, st_goods_defpos c, st_pos d,
			// st_area e, glb_goods_lot f,"
			// + " glb_goods g, glb_goodspack h, sys_company i, glb_goodsstatus
			// j, glb_shop k, sys_company l,"
			// + " pr_goods_price m WHERE a.staccid = b.staccid AND a.goodsid =
			// c.goodsid AND b.stdeptid = c.stdeptid"
			// + " AND c.partstposid = d.stposid AND d.stareaid = e.stareaid AND
			// a.lotid = f.lotid"
			// + " AND f.validdate - sysdate > nvl(e.salesvaliddays, 0) AND
			// a.goodsid = g.goodsid AND g.goodsid = h.goodsid"
			// + " AND h.basepackflag = 1 AND a.goodsstatusid = j.goodsstatusid
			// AND j.cansalesflag = 1"
			// + " AND g.factoryid = i.companyid(+) AND g.mallflag = 1 AND
			// k.mallflag = 1 AND b.stdeptid = k.shopid"
			// + " AND k.shopid = l.companyid AND g.goodsid = h.goodsid AND
			// a.goodsid = m.goodsid AND e.mallpriceid = m.priceid"
			// + " AND g.MALLCLASSID = ? GROUP BY k.shopid, l.companyname,
			// a.goodsid, g.goodsname, g.goodsspecs, "
			// + " i.companyname, h.packname, g.goodsbarcode, g.approvalno,
			// e.mallpriceid, m.price ";
			String sql = "SELECT a.goodsid, g.goodsname, g.goodsspecs, i.companyname factoryname, "
					+ "h.packname basepackname, g.goodsbarcode, g.approvalno, h.packname basepackname "
					+ "FROM st_qty_now a, st_acc_def b, st_goods_defpos c, st_pos d, st_area e, glb_goods_lot f, "
					+ " glb_goods g, glb_goodspack h, sys_company i, glb_goodsstatus j, glb_shop k, sys_company l, "
					+ " pr_goods_price m WHERE a.staccid = b.staccid AND a.goodsid = c.goodsid "
					+ " AND b.stdeptid = c.stdeptid AND c.partstposid = d.stposid AND d.stareaid = e.stareaid "
					+ " AND a.lotid = f.lotid AND f.validdate - sysdate > nvl(e.salesvaliddays, 0) "
					+ " AND a.goodsid = g.goodsid AND g.goodsid = h.goodsid AND h.basepackflag = 1 "
					+ " AND a.goodsstatusid = j.goodsstatusid AND j.cansalesflag = 1 AND g.factoryid = i.companyid(+) "
					+ " AND g.mallflag = 1 AND k.mallflag = 1 AND b.stdeptid = k.shopid AND k.shopid = l.companyid "
					+ " AND g.goodsid = h.goodsid AND a.goodsid = m.goodsid AND e.mallpriceid = m.priceid "
					+ " AND g.MALLCLASSID = ? " + " GROUP BY a.goodsid, g.goodsname, g.goodsspecs, i.companyname, "
					+ " h.packname, g.goodsbarcode, g.approvalno ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(minclassid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model.getRowCount() <= 0) {
				return goodslist;
			}
			SearchHelper sear = new SearchHelper();
			for (int i = 0; i < model.getRowCount(); i++) {
				goodsinfo = new GoodsInfo();

				String goodsPic = pic.getThumbnailGoodsPic(model.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);

				goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				goodsinfo.setGoodsspecs(model.getItemValue(i, "goodsspecs"));
				goodsinfo.setBasepackname(model.getItemValue(i, "basepackname"));
				goodsinfo.setFactoryname(model.getItemValue(i, "factoryname"));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				List<Object> queryShop = sear.queryShop(model.getItemValue(i, "goodsid"));
				goodsinfo.setList(queryShop);
				// goodsinfo.setPrice(model.getItemValue(i, "price"));
				// goodsinfo.setShopid(model.getItemValue(i, "shopid"));
				// goodsinfo.setShopname(ShopHelper.getShopName(con,
				// model.getItemValue(i, "shopid")));

				// SalesPromotionHelper sph = new SalesPromotionHelper();
				// // 单品促销信息
				// List<SingleSPInfo> singleSpList =
				// sph.getSingleSpList(model.getItemValue(i, "shopid"),
				// model.getItemValue(i, "goodsid"));
				// goodsinfo.setSinglelist(singleSpList);
				// // 整单促销信息
				// List<WholeSPInfo> wholeSpList =
				// sph.getWholeSpList(model.getItemValue(i, "shopid"));
				// goodsinfo.setWholelist(wholeSpList);

				goodslist.add(goodsinfo);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return goodslist;
	}

	/**
	 * 药品小类商品列表
	 * 
	 * @param minclassid
	 */
	public List<GoodsInfo> getGoods(String goodsid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		Pic pic = new Pic();
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		try {
			con = DataConnection.getConnection();
			goodslist = new ArrayList<GoodsInfo>();
			String sql = "SELECT k.shopid, l.companyname shopname, a.goodsid, g.goodsname, g.goodsspecs, i.companyname factoryname,"
					+ " h.packname basepackname, g.goodsbarcode, g.approvalno, sum(a.goodsqty) goodsqty, e.mallpriceid,"
					+ " h.packname basepackname, m.price"
					+ " FROM st_qty_now a, st_acc_def b, st_goods_defpos c, st_pos d, st_area e, glb_goods_lot f,"
					+ " glb_goods g, glb_goodspack h, sys_company i, glb_goodsstatus j, glb_shop k, sys_company l,"
					+ " pr_goods_price m WHERE a.staccid = b.staccid AND a.goodsid = c.goodsid AND b.stdeptid = c.stdeptid"
					+ " AND c.partstposid = d.stposid AND d.stareaid = e.stareaid AND a.lotid = f.lotid"
					+ " AND f.validdate - sysdate > nvl(e.salesvaliddays, 0) AND a.goodsid = g.goodsid AND g.goodsid = h.goodsid"
					+ " AND h.basepackflag = 1 AND a.goodsstatusid = j.goodsstatusid AND j.cansalesflag = 1"
					+ " AND g.factoryid = i.companyid(+) AND g.mallflag = 1 AND k.mallflag = 1 AND b.stdeptid = k.shopid"
					+ " AND k.shopid = l.companyid AND g.goodsid = h.goodsid AND a.goodsid = m.goodsid AND e.mallpriceid = m.priceid"
					+ " AND g.goodsid = ? GROUP BY k.shopid, l.companyname, a.goodsid, g.goodsname, g.goodsspecs, "
					+ "i.companyname, h.packname, g.goodsbarcode, g.approvalno, e.mallpriceid, m.price ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(goodsid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model.getRowCount() <= 0) {
				return goodslist;
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				goodsinfo = new GoodsInfo();

				String goodsPic = pic.getThumbnailGoodsPic(model.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);

				goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				goodsinfo.setGoodsspecs(model.getItemValue(i, "goodsspecs"));
				goodsinfo.setBasepackname(model.getItemValue(i, "basepackname"));
				goodsinfo.setFactoryname(model.getItemValue(i, "factoryname"));
				goodsinfo.setPrice(model.getItemValue(i, "price"));
				goodsinfo.setShopid(model.getItemValue(i, "shopid"));
				goodsinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(i, "shopid")));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				SalesPromotionHelper sph = new SalesPromotionHelper();
				// 单品促销信息
				List<SingleSPInfo> singleSpList = sph.getSingleSpList(model.getItemValue(i, "shopid"),
						model.getItemValue(i, "goodsid"));
				goodsinfo.setSinglelist(singleSpList);
				// 整单促销信息
				List<WholeSPInfo> wholeSpList = sph.getWholeSpList(model.getItemValue(i, "shopid"));
				goodsinfo.setWholelist(wholeSpList);

				goodslist.add(goodsinfo);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return goodslist;
	}

	/**
	 * 药品小类更多商品
	 * 
	 * @param minclassid
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<GoodsInfo> getDrugGoodsList(String minclassid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		Pic pic = new Pic();
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		try {
			con = DataConnection.getConnection();
			goodslist = new ArrayList<GoodsInfo>();
			String sql = "select b.* from app_sort_descriptor_doc a,V_APP_SORT_DESCRIPTOR_DTL b "
					+ " where a.sortdescdocid = b.sortdescdocid and b.mallflag = 1 and a.minclassid = ? ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(minclassid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model.getRowCount() <= 0) {
				return goodslist;
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				goodsinfo = new GoodsInfo();

				String goodsPic = pic.getThumbnailGoodsPic(model.getItemValue(i, "goodsid"));
				goodsinfo.setPicname(goodsPic);

				goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
				goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
				goodsinfo.setGoodsspecs(model.getItemValue(i, "goodsspecs"));
				goodsinfo.setFactoryname(model.getItemValue(i, "factoryname"));
				goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

				goodslist.add(goodsinfo);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return goodslist;
	}
}
