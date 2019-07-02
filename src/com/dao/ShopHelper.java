package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.GoodsInfo;
import com.bean.ShopInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

public class ShopHelper {

	private Logger logger = Logger.getLogger(ShopHelper.class);

	/**
	 * 查询所有商品
	 * 
	 * @param skip
	 * @param limit
	 * @param wareTypeId
	 * @return
	 */
	// public List<GoodsInfo> SelGoods(String skip, String limit) {
	// Connection con = null;
	// SSSelectHelper sh = null;
	// List<GoodsInfo> list = null;
	// try {
	// con = DataConnection.getConnection();
	// GoodsInfo goodsinfo = null;
	// list = new ArrayList<GoodsInfo>();
	// String sql =
	// "select * from v_st_qty_now_app where goodsid <> 0 order by goodsid asc ";
	// sh = new SSSelectHelper(sql);
	// SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip),
	// Integer.parseInt(limit));
	// for (int i = 0; i < model.getRowCount(); i++) {
	// goodsinfo = new GoodsInfo();
	// goodsinfo.setGoodsid(model.getItemValue(i, "goodsid"));
	// goodsinfo.setGoodsname(model.getItemValue(i, "goodsname"));
	// goodsinfo.setGoodsspecs(model.getItemValue(i, "goodsspecs"));
	// goodsinfo.setBasepackname(model.getItemValue(i, "basepackname"));
	// goodsinfo.setFactoryname(model.getItemValue(i, "factoryname"));
	// goodsinfo.setPrice(model.getItemValue(i, "price"));
	// list.add(goodsinfo);
	// }
	// con.commit();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// DataConnection.close(con);
	// }
	// return list;
	// }

	/**
	 * 查询所有门店
	 * 
	 * @return
	 */
	public List<ShopInfo> getAllShop() {
		Connection con = null;
		SSSelectHelper sh = null;
		List<ShopInfo> list = new ArrayList<ShopInfo>();
		ShopInfo shopinfo = null;
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select a.shopid, b.companyname shopname, c.deptid companyid, d.companyname,e.addressid,e.address"
					+ " from glb_shop a, sys_company b, sys_entry c, sys_company d,GLB_COMPANY_ADDRESS e"
					+ " where a.shopid = b.companyid and a.entryid = c.entryid and c.deptid = d.companyid"
					+ " and a.shopid = e.companyid(+) and a.mallflag = 1";
			sh = new SSSelectHelper(sql);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			for (int i = 0; i < model.getRowCount(); i++) {
				shopinfo = new ShopInfo();
				goodslist = new ArrayList<GoodsInfo>();
				shopinfo.setShopid(model.getItemValue(i, "shopid"));
				shopinfo.setShopname(model.getItemValue(i, "shopname"));
				shopinfo.setCompanyid(model.getItemValue(i, "companyid"));
				shopinfo.setCompanyname(model.getItemValue(i, "companyname"));
				shopinfo.setAddressid(model.getItemValue(i, "addressid"));
				shopinfo.setAddress(model.getItemValue(i, "address"));
				sql = "select a.goodsid,g.goodsname,g.goodsspecs,i.companyname factoryname,h.packname basepackname, "
						+ " g.goodsbarcode,g.approvalno,sum(a.goodsqty) goodsqty,e.mallpriceid "
						+ " from st_qty_now a,st_acc_def b,st_goods_defpos c,st_pos d,st_area e, "
						+ " glb_goods_lot f,glb_goods g,glb_goodspack h,sys_company i,glb_goodsstatus j "
						+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid "
						+ " and c.partstposid = d.stposid and d.stareaid = e.stareaid and a.lotid = f.lotid "
						+ " and f.validdate - sysdate > nvl(e.salesvaliddays,0) and a.goodsid = g.goodsid "
						+ " and g.goodsid = h.goodsid and h.basepackflag = 1 and a.goodsstatusid = j.goodsstatusid "
						+ " and j.cansalesflag = 1 and g.factoryid = i.companyid(+) and g.mallflag = 1 and b.stdeptid = ? "
						+ " group by a.goodsid,g.goodsname,g.goodsspecs,i.companyname,h.packname,g.goodsbarcode,g.approvalno,e.mallpriceid";
				sh = new SSSelectHelper(sql);
				sh.bindParam(model.getItemValue(i, "shopid"));
				SSTableModel goodsmodel = sh.executeSelect(con, 0, 2);
				String goodsqty = "0";
				for (int j = 0; j < goodsmodel.getRowCount(); j++) {
					goodsinfo = new GoodsInfo();
					goodsinfo.setGoodsid(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setGoodsname(goodsmodel.getItemValue(j, "goodsname"));
					goodsinfo.setGoodsqty(goodsmodel.getItemValue(j, "goodsqty"));
					goodsinfo.setPrice(goodsmodel.getItemValue(j, "mallpriceid"));
					goodslist.add(goodsinfo);
					goodsqty = SSDecimalHelper.add(goodsqty, goodsmodel.getItemValue(j, "goodsqty"), 2);
				}
				if (SSDecimalHelper.compare(goodsqty, "0") <= 0)
					continue;

				shopinfo.setInfolist(goodslist);
				list.add(shopinfo);
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
	 * 查询附近门店
	 * 
	 * @param arr
	 * @return
	 */
	public List<ShopInfo> getSkipShop(String[] arr) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel model = null;
		List<ShopInfo> list = new ArrayList<ShopInfo>();
		ShopInfo shopinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			if (arr == null) {
				return list;
			}

			String filesql = "";
			for (int i = 0; i < arr.length; i++) {
				shopinfo = new ShopInfo();

				filesql = "select attachmentid from glb_shop where shopid = ? ";
				sh = new SSSelectHelper(filesql);
				sh.bindParam(arr[i]);
				SSTableModel filemodel = sh.executeSelect(con, 0, 1);
				String attachmentid = filemodel.getItemValue(0, "attachmentid");
				System.out.println(i+"------"+attachmentid);
				shopinfo.setAttachmentid(attachmentid);
				shopinfo.setFolderpath(Parameter.getParameter("folderpath"));

				String shopPic = pic.getShopPic(attachmentid);
				shopinfo.setPicname(shopPic);

				String sql = "select a.shopid, b.companyname shopname, c.deptid companyid, d.companyname,e.addressid,e.address,e.telephone"
						+ " from glb_shop a, sys_company b, sys_entry c, sys_company d,glb_company_address e"
						+ " where a.shopid = b.companyid and a.entryid = c.entryid and c.deptid = d.companyid"
						+ " and a.shopid = e.companyid(+) and a.mallflag = 1 and a.shopid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(arr[i]);
				model = sh.executeSelect(con, 0, arr.length);
				shopinfo.setShopid(model.getItemValue(0, "shopid"));
				shopinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(0, "shopid")));
				shopinfo.setCompanyid(model.getItemValue(0, "companyid"));
				shopinfo.setCompanyname(model.getItemValue(0, "companyname"));
				shopinfo.setAddressid(model.getItemValue(0, "addressid"));
				shopinfo.setAddress(model.getItemValue(0, "address"));
				shopinfo.setTel(model.getItemValue(0, "telephone"));

				SalesPromotionHelper sph = new SalesPromotionHelper();
				// 整单促销信息
				List<WholeSPInfo> wholeSpList = sph.getWholeSpList(model.getItemValue(0, "shopid"));
				shopinfo.setWholelist(wholeSpList);

				list.add(shopinfo);
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
	 * 获取门店经纬度及门店id
	 * 
	 * @return
	 */
	public List getShopPosition() {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel model = null;
		List list = new ArrayList();
		try {
			con = DataConnection.getConnection();
			String sql = "select a.shopid, a.longitude,a.latitude from glb_shop a where a.mallflag = 1";
			sh = new SSSelectHelper(sql);
			model = sh.executeSelect(con, 0, 99999);
			for (int i = 0; i < model.getRowCount(); i++) {
				String[] x = { model.getItemValue(i, "longitude"), model.getItemValue(i, "latitude"),
						model.getItemValue(i, "shopid") };
				list.add(x);
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
	 * 获取所有门店信息(地图显示)
	 * 
	 * @return
	 */
	public List getAllShopPosition() {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel model = null;
		List list = new ArrayList();
		try {
			con = DataConnection.getConnection();
			String sql = "select a.shopid,b.companyname shopname,e.address,a.longitude,a.latitude,b.tel"
					+ " from glb_shop a,sys_company b,sys_entry c,sys_company d,glb_company_address e"
					+ " where a.shopid = b.companyid and a.entryid = c.entryid and c.deptid = d.companyid"
					+ " and a.shopid = e.companyid(+) and a.mallflag = 1";
			sh = new SSSelectHelper(sql);
			model = sh.executeSelect(con, 0, 99999);
			for (int i = 0; i < model.getRowCount(); i++) {
				String[] x = { model.getItemValue(i, "longitude"), model.getItemValue(i, "latitude"),
						model.getItemValue(i, "shopid"), model.getItemValue(i, "shopname"),
						model.getItemValue(i, "address"), model.getItemValue(i, "tel") };
				list.add(x);
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
	 * 查询门店名称
	 * 
	 * @param shopid
	 * @return
	 * @throws Exception
	 */
	public static String getShopName(Connection con, String shopid) {
		String shopname = "";
		try {
			String sql = "select shopname,mallshopname from v_glb_shop where shopid = ?";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if ("".equals(model.getItemValue(0, "mallshopname"))) {
				shopname = model.getItemValue(0, "shopname");
			} else {
				shopname = model.getItemValue(0, "mallshopname");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shopname;
	}
}
