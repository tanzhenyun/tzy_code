package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.CartInfo;
import com.bean.GoodsInfo;
import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSDeleteHelper;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 购物车帮助类
 * 
 * @author KF_04
 * 
 */
public class ShoppingCartHelper {

	private Logger logger = Logger.getLogger(ShoppingCartHelper.class);

	/**
	 * 购物车商品
	 * 
	 * @param userid
	 * @return
	 */
	public List<CartInfo> getWareList(String userid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<CartInfo> cartinfolist = new ArrayList<CartInfo>();
		CartInfo cartinfo = null;
		List<GoodsInfo> goodsinfolist = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select shopid, shopname, max(credate) from V_APP_SHOPPINGCART "
					+ " where userid = ? group by shopid, shopname order by max(credate) desc ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 99999);
			if (model.getRowCount() <= 0) {
				return cartinfolist;
			} else {
				for (int i = 0; i < model.getRowCount(); i++) {
					String shopid = model.getItemValue(i, "shopid");
					cartinfo = new CartInfo();
					goodsinfolist = new ArrayList<GoodsInfo>();
					sql = "select * from v_app_shoppingcart where userid = ? and shopid = ? order by credate desc";
					sh = new SSSelectHelper(sql);
					sh.bindParam(userid);
					sh.bindParam(shopid);
					SSTableModel cartmodel = sh.executeSelect(con, 0, 99999);
					cartinfo.setShopid(model.getItemValue(i, "shopid"));
					cartinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(i, "shopid")));

					sql = "select entryid from  glb_shop where shopid = ?";
					sh = new SSSelectHelper(sql);
					sh.bindParam(shopid);
					SSTableModel entrymodel = sh.executeSelect(con, 0, 1);
					String entryid = entrymodel.getItemValue(0, "entryid");
					cartinfo.setEntryid(entryid);
					// 整单促销信息
					SalesPromotionHelper sph = new SalesPromotionHelper();
					List<WholeSPInfo> wholeSpList = sph.getWholeSpList(shopid);
					cartinfo.setWholelist(wholeSpList);

					for (int j = 0; j < cartmodel.getRowCount(); j++) {
						goodsinfo = new GoodsInfo();

						String goodsPic = pic.getThumbnailGoodsPic(cartmodel.getItemValue(j, "goodsid"));
						goodsinfo.setPicname(goodsPic);

						goodsinfo.setGoodsid(cartmodel.getItemValue(j, "goodsid"));
						goodsinfo.setGoodsname(cartmodel.getItemValue(j, "goodsname"));
						goodsinfo.setGoodsspecs(cartmodel.getItemValue(j, "goodsspecs"));
						goodsinfo.setBasepackname(cartmodel.getItemValue(j, "basepackname"));
						goodsinfo.setPrice(cartmodel.getItemValue(j, "price"));
						goodsinfo.setGoodsqty(cartmodel.getItemValue(j, "goodsqty"));
						goodsinfo.setInventoryqty(cartmodel.getItemValue(j, "stqty"));
						goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

						// 单品促销信息
						List<SingleSPInfo> singleSpList = sph.getSingleSpList(shopid,
								cartmodel.getItemValue(j, "goodsid"));
						goodsinfo.setSinglelist(singleSpList);

						goodsinfolist.add(goodsinfo);
					}
					cartinfo.setGoodsinfolist(goodsinfolist);
					cartinfolist.add(cartinfo);
				}
				con.commit();
				return cartinfolist;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
			return null;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 购物车数量修改
	 * 
	 * @param goodsid
	 * @param wareCount
	 * @param userid
	 * @return
	 */
	public int updateShoppingCart(String shopid, String goodsid, String wareCount, String userid) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSInsertHelper ih = null;
		SSUpdateHelper uh = null;
		SSDeleteHelper dh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from APP_SHOPPINGCART where goodsid = ? and userid = ? and shopid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(goodsid);
			sh.bindParam(userid);
			sh.bindParam(shopid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				ih = new SSInsertHelper("app_shoppingcart");
				ih.bindSequence("shoppingcartid", "seq_app_shoppingcart");
				ih.bindParam("goodsid", goodsid);
				ih.bindParam("goodsqty", wareCount);
				ih.bindParam("usestatus", "2");
				ih.bindParam("userid", userid);
				ih.bindParam("shopid", shopid);
				ih.bindSysdate("credate");
				influence = ih.executeInsert(con);
			} else {
				String shoppingcartid = model.getItemValue(0, "shoppingcartid");
				String oldgoodsqty = model.getItemValue(0, "goodsqty");
				String goodsqty = SSDecimalHelper.add(oldgoodsqty, wareCount, 2);
				// 商品数量减到0时删除此条商品数据
				// if (SSDecimalHelper.compare(goodsqty, "0") == 0) {
				// sql =
				// "delete from app_shoppingcart where userid = ? and shopid = ? and goodsid = ?";
				// dh = new SSDeleteHelper(sql);
				// dh.bindParam(userid);
				// dh.bindParam(shopid);
				// dh.bindParam(goodsid);
				// influence = dh.executeDelete(con);
				// sql =
				// "select * from app_shoppingcart where userid = ? and shopid = ?";
				// sh = new SSSelectHelper(sql);
				// sh.bindParam(userid);
				// sh.bindParam(shopid);
				// model = sh.executeSelect(con, 0, 99999);
				// if (model.getRowCount() == 0) {
				// sql =
				// "delete from app_shoppingcart where userid = ? and shopid = ?";
				// dh = new SSDeleteHelper(sql);
				// dh.bindParam(userid);
				// dh.bindParam(shopid);
				// influence = dh.executeDelete(con);
				// }
				// } else {
				sql = "select t.goodsqty from APP_SHOPPINGCART t where userid = ? and shopid = ? and goodsid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(userid);
				sh.bindParam(shopid);
				sh.bindParam(goodsid);
				model = sh.executeSelect(con, 0, 1);
				// 购物车商品数量
				String cartgoodsqty = model.getItemValue(0, "goodsqty");
				sql = "select sum(a.goodsqty) goodsqty from st_qty_now a, st_acc_def b, st_goods_defpos c,st_pos d,st_area e,"
						+ "glb_goods_lot f,glb_goods g,glb_goodspack h,sys_company i,glb_goodsstatus j,glb_shop k,sys_company l"
						+ " where a.staccid = b.staccid and a.goodsid = c.goodsid and b.stdeptid = c.stdeptid"
						+ " and c.partstposid = d.stposid and d.stareaid = e.stareaid and a.lotid = f.lotid"
						+ " and f.validdate - sysdate > nvl(e.salesvaliddays, 0) and a.goodsid = g.goodsid"
						+ " and g.goodsid = h.goodsid and h.basepackflag = 1 and a.goodsstatusid = j.goodsstatusid"
						+ " and j.cansalesflag = 1 and g.factoryid = i.companyid(+) and g.mallflag = 1 and k.mallflag = 1"
						+ " and b.stdeptid = k.shopid and k.shopid = l.companyid and a.goodsid = ? and k.shopid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(goodsid);
				sh.bindParam(shopid);
				model = sh.executeSelect(con, 0, 1);
				// 当前库存
				String stgoodsqty = model.getItemValue(0, "goodsqty");
				int compare = SSDecimalHelper.compare(SSDecimalHelper.add(wareCount, cartgoodsqty, 2), stgoodsqty);
				if (compare > 0) {
					influence = -1;
				} else {
					sql = "update app_shoppingcart set goodsqty = ? where shoppingcartid = ?";
					uh = new SSUpdateHelper(sql);
					uh.bindParam(goodsqty);
					uh.bindParam(shoppingcartid);
					influence = uh.executeUpdate(con);
				}
				// }
			}
			con.commit();
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			return 0;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 删除购物车商品
	 * 
	 * @param userid
	 * @param str
	 * @return
	 */
	public int deleteGoods(String userid, String str) {
		Connection con = null;
		SSDeleteHelper dh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String[] split = str.split(",");
			String sql = "", shopid = "", goodsid = "";
			for (int i = 0; i < split.length; i++) {
				shopid = split[i].split("-")[0];
				goodsid = split[i].split("-")[1];
				sql = "delete from app_shoppingcart where userid = ? and shopid = ? and goodsid = ?";
				dh = new SSDeleteHelper(sql);
				dh.bindParam(userid);
				dh.bindParam(shopid);
				dh.bindParam(goodsid);
				influence = dh.executeDelete(con);
				con.commit();
			}
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			return 0;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 检查商品数量
	 * 
	 * @param shopid
	 * @param goodsid
	 * @param goodsqtynow
	 * @return
	 */
	// public int checkGoodsQty(String shopid, String goodsid, String
	// goodsqtynow) {
	// Connection con = null;
	// SSSelectHelper sh = null;
	// int influence = 0;
	// try {
	// con = DataConnection.getConnection();
	// String sql =
	// "select sum(goodsqty) goodsqty from v_st_qty_now where goodsid = ? and "
	// + "staccid in (select staccid from st_acc_def where stdeptid = ?) "
	// + "and TRUNC(validdate) - TRUNC(sysdate) > 30";
	// sh = new SSSelectHelper(sql);
	// sh.bindParam(goodsid);
	// sh.bindParam(shopid);
	// SSTableModel model = sh.executeSelect(con, 0, 1);
	// if (model.getRowCount() <= 0) {
	// return -1;
	// }
	// String goodsqty = model.getItemValue(0, "goodsqty");
	// if (SSDecimalHelper.compare(goodsqty, "0") <= 0) {
	// influence = 0;
	// } else if (SSDecimalHelper.compare(goodsqty, goodsqtynow) <= 0) {
	// influence = 0;
	// } else {
	// influence = (int) Math.floor(Float.parseFloat(goodsqty));
	// }
	// // }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// DataConnection.close(con);
	// }
	// return influence;
	// }

}
