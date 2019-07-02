package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bean.GoodsInfo;
import com.bean.ShopInfo;
import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 搜索帮助类
 * 
 * @author KF_04
 * 
 */
public class SearchHelper {

	private Logger logger = Logger.getLogger(SearchHelper.class);

	/**
	 * 搜索商品详细信息
	 * 
	 * @param word
	 * @param skip
	 * @param limit
	 * @return
	 */
	public Map<String, Object> getSearch(String word, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<GoodsInfo> goodslist = new ArrayList<GoodsInfo>();
		List<ShopInfo> shoplist = new ArrayList<ShopInfo>();
		ShopInfo shopinfo = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql1 = "select k.shopid,l.companyname shopname,a.goodsid,g.goodsname,g.goodsspecs,i.companyname factoryname, h.packname basepackname,"
					+ " g.goodsbarcode,g.approvalno,sum(a.goodsqty) goodsqty,e.mallpriceid,h.packname basepackname,m.price"
					+ " from st_qty_now a,st_acc_def b,st_goods_defpos c,st_pos d,st_area e,glb_goods_lot f,glb_goods g,"
					+ " glb_goodspack h,sys_company i,glb_goodsstatus j,glb_shop   k,sys_company l, pr_goods_price  m"
					+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid and c.partstposid = d.stposid"
					+ " and d.stareaid = e.stareaid  and a.lotid = f.lotid and f.validdate - sysdate > nvl(e.salesvaliddays, 0)"
					+ " and a.goodsid = g.goodsid and g.goodsid = h.goodsid and h.basepackflag = 1 and a.goodsstatusid = j.goodsstatusid"
					+ " and j.cansalesflag = 1 and g.factoryid = i.companyid(+) and g.mallflag = 1 and k.mallflag = 1 "
					+ " and b.stdeptid = k.shopid and k.shopid = l.companyid and g.goodsid = h.goodsid "
					+ " and a.goodsid = m.goodsid and e.mallpriceid = m.priceid" + " and (upper(g.goodsid) like '%"
					+ word + "%' or upper(g.goodsname) like '%" + word + "%' or upper(g.goodsmcode) like '%" + word
					+ "%' or upper(g.goodsbarcode) = '" + word + "')"
					+ " group by k.shopid,l.companyname,a.goodsid,g.goodsname,g.goodsspecs,i.companyname,"
					+ " h.packname,g.goodsbarcode,g.approvalno,e.mallpriceid,m.price";
			sh = new SSSelectHelper(sql1);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model.getRowCount() <= 0) {
			} else {
				// 查询商品
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
			}
			map.put("goodslist", goodslist);

			// 查询门店
			sql1 = "select a.shopid,b.companyname shopname,c.deptid companyid,d.companyname,b.tel,e.addressid,e.address "
					+ " from glb_shop a,sys_company b,sys_entry c,sys_company d,glb_company_address e "
					+ " where a.shopid = b.companyid and a.entryid = c.entryid and c.deptid = d.companyid"
					+ " and a.mallflag = 1 and a.shopid = b.companyid and a.shopid = e.companyid(+)"
					+ " and (upper(b.companyid) = '" + word + "' or upper(b.companyname) like '%" + word + "%' or "
					+ " upper(b.companymcode) like '%" + word + "%' or upper(b.companyshortname) like '%" + word
					+ "%')";
			sh = new SSSelectHelper(sql1);
			SSTableModel model1 = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model1.getRowCount() < 0) {
				// shoplist.add(shopinfo);
			} else {
				String filesql = "";
				for (int i = 0; i < model1.getRowCount(); i++) {
					shopinfo = new ShopInfo();
					filesql = "select attachmentid from glb_shop where shopid = ? ";
					sh = new SSSelectHelper(filesql);
					sh.bindParam(model1.getItemValue(i, "shopid"));
					SSTableModel filemodel = sh.executeSelect(con, 0, 1);
					String attachmentid = filemodel.getItemValue(0, "attachmentid");

					shopinfo.setAttachmentid(attachmentid);
					String shopPic = pic.getShopPic(attachmentid);
					shopinfo.setPicname(shopPic);

					shopinfo.setShopid(model1.getItemValue(i, "shopid"));
					shopinfo.setShopname(ShopHelper.getShopName(con, model1.getItemValue(i, "shopid")));
					shopinfo.setCompanyid(model1.getItemValue(i, "companyid"));
					shopinfo.setCompanyname(model1.getItemValue(i, "companyname"));
					shopinfo.setAddressid(model1.getItemValue(i, "addressid"));
					shopinfo.setAddress(model1.getItemValue(i, "address"));
					shopinfo.setTel(model1.getItemValue(i, "tel"));
					shopinfo.setFolderpath(Parameter.getParameter("folderpath"));

					shoplist.add(shopinfo);
				}
			}
			map.put("shoplist", shoplist);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return map;
	}

	/**
	 * 查询商品(不带门店信息)
	 * 
	 * @param word
	 * @param skip
	 * @param limit
	 * @return
	 */
	public Map<String, Object> queryGoods(String word, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<GoodsInfo> goodslist = new ArrayList<GoodsInfo>();
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select a.goodsid, g.goodsname, g.goodsspecs, i.companyname factoryname, "
					+ "h.packname basepackname, g.goodsbarcode, g.approvalno "
					+ " FROM st_qty_now a, st_acc_def b, st_goods_defpos c, st_pos d, st_area e, "
					+ "glb_goods_lot f, glb_goods g, glb_goodspack h, sys_company i, glb_goodsstatus j, "
					+ "glb_shop k, sys_company l, pr_goods_price m WHERE a.staccid = b.staccid "
					+ "AND a.goodsid = c.goodsid AND b.stdeptid = c.stdeptid AND c.partstposid = d.stposid "
					+ "AND d.stareaid = e.stareaid AND a.lotid = f.lotid "
					+ "AND f.validdate - sysdate > nvl(e.salesvaliddays, 0) "
					+ "AND a.goodsid = g.goodsid AND g.goodsid = h.goodsid "
					+ "AND h.basepackflag = 1 AND a.goodsstatusid = j.goodsstatusid "
					+ "AND j.cansalesflag = 1 AND g.factoryid = i.companyid(+) AND g.mallflag = 1 "
					+ "AND k.mallflag = 1 AND b.stdeptid = k.shopid AND k.shopid = l.companyid "
					+ "AND g.goodsid = h.goodsid AND a.goodsid = m.goodsid AND e.mallpriceid = m.priceid "
					+ "AND(upper(g.goodsid) LIKE '%" + word + "%' OR upper(g.goodsname) LIKE '%" + word
					+ "%' OR upper(g.goodsmcode) LIKE '%" + word + "%' OR upper(g.goodsbarcode) = '" + word
					+ "') GROUP BY a.goodsid, g.goodsname, g.goodsspecs, i.companyname, h.packname, "
					+ "g.goodsbarcode, g.approvalno ";
			sh = new SSSelectHelper(sql);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			SearchHelper sear = new SearchHelper();
			if (model.getRowCount() > 0) {
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
					goodslist.add(goodsinfo);
				}
			}
			map.put("goodslist", goodslist);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return map;
	}

	/**
	 * 点击商品查询门店信息
	 * 
	 * @param goodsid
	 * @return
	 */
	public List<Object> queryShop(String goodsid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<Object> list = new ArrayList<Object>();
		try {
			con = DataConnection.getConnection();
			String sql = "SELECT distinct(k.shopid), k.mallshopname, m.priceid, m.price,l.companyname shopname "
					+ "FROM st_qty_now a, st_acc_def b, st_goods_defpos c, st_pos d, st_area e, "
					+ " glb_goods_lot f, glb_goods g, glb_goodspack h, sys_company i, glb_goodsstatus j, "
					+ " glb_shop k, sys_company l, pr_goods_price m WHERE a.staccid = b.staccid "
					+ " AND a.goodsid = c.goodsid AND b.stdeptid = c.stdeptid AND c.partstposid = d.stposid "
					+ " AND d.stareaid = e.stareaid AND a.lotid = f.lotid "
					+ " AND f.validdate - sysdate > nvl(e.salesvaliddays, 0) AND a.goodsid = g.goodsid "
					+ " AND g.goodsid = h.goodsid AND h.basepackflag = 1 AND a.goodsstatusid = j.goodsstatusid "
					+ " AND j.cansalesflag = 1 AND g.factoryid = i.companyid(+) AND g.mallflag = 1 "
					+ "AND k.mallflag = 1 AND b.stdeptid = k.shopid AND k.shopid = l.companyid "
					+ "AND g.goodsid = h.goodsid AND a.goodsid = m.goodsid AND e.mallpriceid = m.priceid "
					+ "AND g.goodsid = ? order by k.shopid";
			sh = new SSSelectHelper(sql);
			sh.bindParam(goodsid);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			if (model.getRowCount() > 0) {
				for (int i = 0, c = model.getRowCount(); i < c; i++) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("shopid", model.getItemValue(i, "shopid"));
					if ("".equals(model.getItemValue(i, "mallshopname"))) {
						m.put("shopname", model.getItemValue(i, "shopname"));
					}else{
						m.put("shopname", model.getItemValue(i, "mallshopname"));
					}
					m.put("price", model.getItemValue(i, "price"));
					SalesPromotionHelper sph = new SalesPromotionHelper();
					// 单品促销信息
					List<SingleSPInfo> singleSpList = sph.getSingleSpList(model.getItemValue(i, "shopid"), goodsid);
					m.put("singlesplist", singleSpList);
					// 整单促销信息
					List<WholeSPInfo> wholeSpList = sph.getWholeSpList(model.getItemValue(i, "shopid"));
					m.put("wholesplist", wholeSpList);
					list.add(m);
				}
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
	 * 门店内搜索
	 * 
	 * @param shopid
	 * @param word
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<GoodsInfo> getShopSearch(String shopid, String word, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<GoodsInfo> goodslist = new ArrayList<GoodsInfo>();
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select k.shopid,l.companyname shopname,a.goodsid,g.goodsname,g.goodsspecs,i.companyname factoryname, h.packname basepackname,"
					+ " g.goodsbarcode,g.approvalno,sum(a.goodsqty) goodsqty,e.mallpriceid,h.packname basepackname,m.price"
					+ " from st_qty_now a,st_acc_def b,st_goods_defpos c,st_pos d,st_area e,glb_goods_lot f,glb_goods g,"
					+ " glb_goodspack h,sys_company i,glb_goodsstatus j,glb_shop   k,sys_company l, pr_goods_price  m"
					+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid and c.partstposid = d.stposid"
					+ " and d.stareaid = e.stareaid  and a.lotid = f.lotid and f.validdate - sysdate > nvl(e.salesvaliddays, 0)"
					+ " and a.goodsid = g.goodsid and g.goodsid = h.goodsid and h.basepackflag = 1 and a.goodsstatusid = j.goodsstatusid"
					+ " and j.cansalesflag = 1 and g.factoryid = i.companyid(+) and g.mallflag = 1 and k.mallflag = 1 "
					+ " and b.stdeptid = k.shopid and k.shopid = l.companyid and g.goodsid = h.goodsid "
					+ " and a.goodsid = m.goodsid and e.mallpriceid = m.priceid and k.shopid = ?"
					+ " and (upper(g.goodsid) like '%" + word + "%' or upper(g.goodsname) like '%" + word
					+ "%' or upper(g.goodsmcode) like '%" + word + "%' or upper(g.goodsbarcode) = '" + word + "')"
					+ " group by k.shopid,l.companyname,a.goodsid,g.goodsname,g.goodsspecs,i.companyname,"
					+ " h.packname,g.goodsbarcode,g.approvalno,e.mallpriceid,m.price";
			sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			if (model.getRowCount() <= 0) {
			} else {
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
