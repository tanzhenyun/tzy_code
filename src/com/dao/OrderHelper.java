package com.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.AddressInfo;
import com.bean.CartInfo;
import com.bean.GoodsInfo;
import com.bean.MemberInfo;
import com.bean.OrderInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 订单帮助类
 * 
 * @author KF_04
 * 
 */
public class OrderHelper {

	private Logger logger = Logger.getLogger(OrderHelper.class);

	/**
	 * 检查收货地址
	 * 
	 * @param userid
	 * @return
	 */
	public int checkAddress(String userid) {
		Connection con = null;
		SSSelectHelper sh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and userid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 99999);
			con.commit();
			if (model.getRowCount() <= 0) {
				return influence;
			} else {
				return model.getRowCount();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
			return 0;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 获取提交订单页面商品同时将默认收货地地址带上
	 * 
	 * @param userid
	 * @param str
	 * @return
	 */
	public OrderInfo getOrderGoods(String userid, String str, String[] arr) {
		Connection con = null;
		SSSelectHelper sh = null;
		OrderInfo order = new OrderInfo();
		AddressInfo addressinfo = new AddressInfo();
		List<CartInfo> cartinfolist = new ArrayList<CartInfo>();
		List<GoodsInfo> goodsinfolist = null;
		CartInfo cartinfo = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and defsign = 1 and userid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and userid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(userid);
				model = sh.executeSelect(con, 0, 1);
				new AddressHelper().setDefAddress(userid, model.getItemValue(0, "appreceiveaddressid"));
			}
			// 收货地址信息
			addressinfo.setAppreceiveaddressid(model.getItemValue(0, "appreceiveaddressid"));
			addressinfo.setPhonenumber(model.getItemValue(0, "phonenumber"));
			addressinfo.setProvince(model.getItemValue(0, "province"));
			addressinfo.setCity(model.getItemValue(0, "city"));
			addressinfo.setArea(model.getItemValue(0, "area"));
			addressinfo.setAddress(model.getItemValue(0, "address"));
			addressinfo.setReceivename(model.getItemValue(0, "receivename"));
			order.setAddressinfo(addressinfo);
			// 门店商品信息
			// String[] split = str.split("-");
			String shopid = "";
			String shopid1 = "";
			for (int i = 0; i < arr.length; i++) {
				shopid = arr[i].split(",")[0];
				if (!shopid.equals(shopid1)) {
					cartinfo = new CartInfo();
					// sql = "select * from v_app_shoppingcart where userid = ?
					// and shopid = ?";
					// sh = new SSSelectHelper(sql);
					// sh.bindParam(userid);
					// sh.bindParam(shopid);
					// model = sh.executeSelect(con, 0, 1);
					cartinfo.setShopid(shopid);
					cartinfo.setShopname(ShopHelper.getShopName(con, shopid));

					sql = "select entryid from  glb_shop where shopid = ?";
					sh = new SSSelectHelper(sql);
					sh.bindParam(shopid);
					SSTableModel entrymodel = sh.executeSelect(con, 0, 1);
					String entryid = entrymodel.getItemValue(0, "entryid");
					cartinfo.setEntryid(entryid);
					shopid1 = shopid;
					cartinfolist.add(cartinfo);
					goodsinfolist = new ArrayList<GoodsInfo>();
					for (int j = 0; j < arr.length; j++) {
						if (arr[j].split(",")[0].equals(shopid)) {
							goodsinfo = new GoodsInfo();
							String goodsid = arr[j].split(",")[1];

							String goodsPic = pic.getThumbnailGoodsPic(goodsid);
							goodsinfo.setPicname(goodsPic);

							sql = "select goodsname,price,goodsqty from v_app_shoppingcart "
									+ " where userid = ? and shopid = ? and goodsid = ?";
							sh = new SSSelectHelper(sql);
							sh.bindParam(userid);
							sh.bindParam(shopid);
							sh.bindParam(goodsid);
							model = sh.executeSelect(con, 0, 999999);
							goodsinfo.setGoodsid(goodsid);
							goodsinfo.setGoodsname(model.getItemValue(0, "goodsname"));
							goodsinfo.setPrice(model.getItemValue(0, "price"));
							goodsinfo.setGoodsqty(model.getItemValue(0, "goodsqty"));
							goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

							goodsinfolist.add(goodsinfo);
						}
					}
				}
				cartinfo.setGoodsinfolist(goodsinfolist);
			}
			order.setCartinfolist(cartinfolist);
			con.commit();
			return order;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return order;
	}

	/**
	 * 所有订单
	 * 
	 * @param memberid
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List getAllOrder(String memberid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<CartInfo> list = new ArrayList<CartInfo>();
		CartInfo cartinfo = null;
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			// 查询门店信息
			String sql = "select a.prescriptionid,a.shopid,a.shopname,a.rtlamount,a.credate,a.usestatus,c.keyid,c.keyname,a.orderid,"
					+ " nvl(a.couponamount,0) couponamount, a.rtlamount - nvl(a.couponamount,0) payamount "
					+ " from V_RTL_PRESCRIPTION a, GLB_SHOP b, V_SYS_DIC c where a.shopid = b.shopid(+)"
					+ " and a.combogoodsid = b.eccomgoodsid and a.memberid = ? and a.paymethod is not null "
					+ " and c.keyword = 'RTL_PRESCRIPTION_USESTATUS' and a.usestatus = c.keyid order by a.credate desc";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			String prescriptionid = "";
			for (int i = 0; i < model.getRowCount(); i++) {
				cartinfo = new CartInfo();
				goodslist = new ArrayList<GoodsInfo>();
				prescriptionid = model.getItemValue(i, "prescriptionid");
				cartinfo.setShopid(model.getItemValue(i, "shopid"));
				cartinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(i, "shopid")));
				cartinfo.setPrescriptionid(prescriptionid);
				cartinfo.setUsestatus(model.getItemValue(i, "keyid"));

				BigDecimal bd = new BigDecimal(model.getItemValue(i, "rtlamount"));
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				cartinfo.setRtlamount(bd.toString());

				String credate = model.getItemValue(i, "credate");
				if (!"".equals(credate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					credate = formatter.format(formatter.parse(model.getItemValue(i, "credate")));
				}
				cartinfo.setCredate(credate);
				cartinfo.setOrderid(model.getItemValue(i, "orderid"));
				cartinfo.setCouponamount(model.getItemValue(i, "couponamount"));
				cartinfo.setPayamount(model.getItemValue(i, "payamount"));

				// 查询商品信息
				sql = "select goodsid,goodsname,nvl(execprice,0) execprice,goodsqty,realprice,goodsspecs,basepackname "
						+ " from V_RTL_TINY where prescriptionid = ? and goodsqty > 0";
				sh = new SSSelectHelper(sql);
				sh.bindParam(prescriptionid);
				SSTableModel goodsmodel = sh.executeSelect(con, 0, 99999);
				for (int j = 0; j < goodsmodel.getRowCount(); j++) {
					goodsinfo = new GoodsInfo();

					String goodsPic = pic.getThumbnailGoodsPic(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setPicname(goodsPic);

					goodsinfo.setGoodsid(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setGoodsname(goodsmodel.getItemValue(j, "goodsname"));
					goodsinfo.setPrice(goodsmodel.getItemValue(j, "execprice"));
					goodsinfo.setGoodsqty(goodsmodel.getItemValue(j, "goodsqty"));
					goodsinfo.setAmount(goodsmodel.getItemValue(j, "realprice"));
					goodsinfo.setGoodsspecs(goodsmodel.getItemValue(j, "goodsspecs"));
					goodsinfo.setBasepackname(goodsmodel.getItemValue(j, "basepackname"));
					goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));
					goodslist.add(goodsinfo);
				}
				sql = "select count(1) from V_RTL_TINY where prescriptionid = ? and goodsqty < 0";
				sh = new SSSelectHelper(sql);
				sh.bindParam(prescriptionid);
				SSTableModel goodsmodel1 = sh.executeSelect(con, 0, 99999);

				if (SSDecimalHelper.compare(goodsmodel1.getItemValue(0, "count(1)"), "0") > 0) {
					cartinfo.setUsestatusname("已退货");
				} else {
					cartinfo.setUsestatusname(model.getItemValue(i, "keyname"));
				}
				cartinfo.setGoodsinfolist(goodslist);
				list.add(cartinfo);
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
	 * 获取待支付订单
	 * 
	 * @param memberid
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List getPendingPayOrder(String memberid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List list = null;
		OrderInfo order = null;
		List<CartInfo> cartlist = null;
		List<GoodsInfo> goodslist = null;
		CartInfo cartinfo = null;
		GoodsInfo goodsinfo = null;
		SSTableModel ordermodel = null;
		SSTableModel shopmodel = null;
		SSTableModel goodsmodel = null;
		try {
			con = DataConnection.getConnection();
			Pic pic = new Pic();
			list = new ArrayList();

			// 根据会员id查询所有订单id
			String sql = "select orderid from RTL_PRESCRIPTION where memberid = ? "
					+ " and orderid is not null and usestatus = 7 group by orderid order by orderid desc";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			ordermodel = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			String orderid = "";
			// if (ordermodel.getRowCount() > 0) {
			for (int j = 0, rowcount = ordermodel.getRowCount(); j < rowcount; j++) {
				order = new OrderInfo();
				cartlist = new ArrayList<CartInfo>();
				// 根据orderid查询所有药方及门店信息
				orderid = ordermodel.getItemValue(j, "orderid");
				String sql1 = "select shopid, shopname, prescriptionid, rtlamount, orderid, credate, nvl(couponamount,0) couponamount,"
						+ " rtlamount - nvl(couponamount,0) payamount from V_RTL_PRESCRIPTION where orderid = ?";
				sh = new SSSelectHelper(sql1);
				sh.bindParam(orderid);
				shopmodel = sh.executeSelect(con, 0, 9999);
				// 药方id
				String prescriptionid = "";
				// if (shopmodel.getRowCount() > 0) {
				for (int i = 0, rowcount1 = shopmodel.getRowCount(); i < rowcount1; i++) {
					goodslist = new ArrayList<GoodsInfo>();
					cartinfo = new CartInfo();
					// 药方id
					prescriptionid = shopmodel.getItemValue(i, "prescriptionid");
					cartinfo.setShopid(shopmodel.getItemValue(i, "shopid"));
					cartinfo.setShopname(ShopHelper.getShopName(con, shopmodel.getItemValue(i, "shopid")));
					cartinfo.setPrescriptionid(prescriptionid);
					BigDecimal bd = new BigDecimal(shopmodel.getItemValue(i, "rtlamount"));
					bd = bd.setScale(2, RoundingMode.HALF_UP);
					cartinfo.setRtlamount(bd.toString());
					String credate = shopmodel.getItemValue(i, "credate");
					if (!"".equals(credate)) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						credate = formatter.format(formatter.parse(shopmodel.getItemValue(i, "credate")));
					}
					cartinfo.setCredate(credate);
					cartinfo.setCouponamount(shopmodel.getItemValue(i, "couponamount"));
					cartinfo.setPayamount(shopmodel.getItemValue(i, "payamount"));

					// 商品信息
					String sql3 = "select goodsid,goodsname,nvl(execprice,0) execprice,goodsqty,realprice,goodsspecs,basepackname "
							+ " from V_RTL_TINY where prescriptionid = ?";
					sh = new SSSelectHelper(sql3);
					sh.bindParam(prescriptionid);
					goodsmodel = sh.executeSelect(con, 0, 99999);
					for (int k = 0, rowcount2 = goodsmodel.getRowCount(); k < rowcount2; k++) {
						goodsinfo = new GoodsInfo();
						String goodsPic = pic.getThumbnailGoodsPic(goodsmodel.getItemValue(k, "goodsid"));
						goodsinfo.setPicname(goodsPic);
						goodsinfo.setGoodsid(goodsmodel.getItemValue(k, "goodsid"));
						goodsinfo.setGoodsname(goodsmodel.getItemValue(k, "goodsname"));
						goodsinfo.setPrice(goodsmodel.getItemValue(k, "execprice"));
						goodsinfo.setGoodsqty(goodsmodel.getItemValue(k, "goodsqty"));
						goodsinfo.setAmount(goodsmodel.getItemValue(k, "realprice"));
						goodsinfo.setGoodsspecs(goodsmodel.getItemValue(k, "goodsspecs"));
						goodsinfo.setBasepackname(goodsmodel.getItemValue(k, "basepackname"));
						goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

						goodslist.add(goodsinfo);
					}
					cartinfo.setGoodsinfolist(goodslist);
					cartlist.add(cartinfo);
				}
				order.setCartinfolist(cartlist);
				// }
				order.setOrderid(orderid);
				list.add(order);
			}
			// }
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return null;
	}

	/**
	 * 待收货订单信息
	 * 
	 * @param phonenumber
	 * @return
	 */
	public List getWaitReceiveOrder(String memberid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<CartInfo> list = new ArrayList<CartInfo>();
		CartInfo cartinfo = null;
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();

			// 查询门店信息
			String sql = "select a.prescriptionid,a.shopid,a.rtlamount,a.credate,a.paymethod,a.orderid,"
					+ " nvl(a.couponamount,0) couponamount,a.rtlamount - nvl(a.couponamount,0) payamount"
					+ " from V_RTL_PRESCRIPTION a, glb_shop b"
					+ " where a.shopid = b.shopid(+) and a.combogoodsid = b.eccomgoodsid and a.memberid = ?"
					+ " and a.usestatus in (6,4)  order by a.credate desc";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			String prescriptionid = "";
			for (int i = 0; i < model.getRowCount(); i++) {
				cartinfo = new CartInfo();
				goodslist = new ArrayList<GoodsInfo>();
				prescriptionid = model.getItemValue(i, "prescriptionid");
				cartinfo.setShopid(model.getItemValue(i, "shopid"));
				cartinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(i, "shopid")));
				cartinfo.setPrescriptionid(prescriptionid);
				BigDecimal bd = new BigDecimal(model.getItemValue(i, "rtlamount"));
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				cartinfo.setRtlamount(bd.toString());

				String credate = model.getItemValue(i, "credate");
				if (!"".equals(credate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					credate = formatter.format(formatter.parse(model.getItemValue(i, "credate")));
				}
				cartinfo.setCredate(credate);
				if ("1".equals(model.getItemValue(i, "paymethod"))) {
					cartinfo.setPaymethod("已支付");
				} else {
					cartinfo.setPaymethod("货到付款");
				}
				cartinfo.setOrderid(model.getItemValue(i, "orderid"));
				cartinfo.setCouponamount(model.getItemValue(i, "couponamount"));
				cartinfo.setPayamount(model.getItemValue(i, "payamount"));

				// 查询商品信息
				sql = "select goodsid,goodsname,nvl(execprice,0) execprice,goodsqty,realprice,goodsspecs,basepackname "
						+ " from V_RTL_TINY where prescriptionid = ? order by credate desc";
				sh = new SSSelectHelper(sql);
				sh.bindParam(prescriptionid);
				SSTableModel goodsmodel = sh.executeSelect(con, 0, 99999);
				for (int j = 0; j < goodsmodel.getRowCount(); j++) {
					goodsinfo = new GoodsInfo();

					String goodsPic = pic.getThumbnailGoodsPic(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setPicname(goodsPic);

					goodsinfo.setGoodsid(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setGoodsname(goodsmodel.getItemValue(j, "goodsname"));
					goodsinfo.setPrice(goodsmodel.getItemValue(j, "execprice"));
					goodsinfo.setGoodsqty(goodsmodel.getItemValue(j, "goodsqty"));
					goodsinfo.setAmount(goodsmodel.getItemValue(j, "realprice"));
					goodsinfo.setGoodsspecs(goodsmodel.getItemValue(j, "goodsspecs"));
					goodsinfo.setBasepackname(goodsmodel.getItemValue(j, "basepackname"));
					goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));

					goodslist.add(goodsinfo);
				}
				cartinfo.setGoodsinfolist(goodslist);
				list.add(cartinfo);
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
	 * 已收货订单信息
	 * 
	 * @param phonenumber
	 * @return
	 */
	public List getCompletedOrder(String memberid, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<CartInfo> list = new ArrayList<CartInfo>();
		CartInfo cartinfo = null;
		List<GoodsInfo> goodslist = null;
		GoodsInfo goodsinfo = null;
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			// 查询门店信息
			String sql = "select a.prescriptionid,a.shopid,a.rtlamount,a.credate,a.paymethod,a.orderid,"
					+ " nvl(a.couponamount,0) couponamount,a.rtlamount - nvl(a.couponamount,0) payamount"
					+ " from V_RTL_PRESCRIPTION a, glb_shop b"
					+ " where a.shopid = b.shopid(+) and a.combogoodsid = b.eccomgoodsid and a.memberid = ?"
					+ " and a.usestatus = 1 and a.credate > add_months(sysdate, -3) order by a.credate desc";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			SSTableModel model = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			String prescriptionid = "";
			for (int i = 0; i < model.getRowCount(); i++) {
				cartinfo = new CartInfo();
				goodslist = new ArrayList<GoodsInfo>();
				prescriptionid = model.getItemValue(i, "prescriptionid");
				cartinfo.setShopid(model.getItemValue(i, "shopid"));
				cartinfo.setShopname(ShopHelper.getShopName(con, model.getItemValue(i, "shopid")));
				cartinfo.setPrescriptionid(prescriptionid);
				BigDecimal bd = new BigDecimal(model.getItemValue(i, "rtlamount"));
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				cartinfo.setRtlamount(bd.toString());

				String credate = model.getItemValue(i, "credate");
				if (!"".equals(credate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					credate = formatter.format(formatter.parse(model.getItemValue(i, "credate")));
				}
				cartinfo.setCredate(credate);
				cartinfo.setOrderid(model.getItemValue(i, "orderid"));
				cartinfo.setCouponamount(model.getItemValue(i, "couponamount"));
				cartinfo.setPayamount(model.getItemValue(i, "payamount"));

				// 查询商品信息
				sql = "select goodsid,goodsname,nvl(execprice,0) execprice,goodsqty,realprice,goodsspecs,basepackname "
						+ " from V_RTL_TINY where prescriptionid = ? and goodsqty>0";
				sh = new SSSelectHelper(sql);
				sh.bindParam(prescriptionid);
				SSTableModel goodsmodel = sh.executeSelect(con, 0, 99999);
				for (int j = 0; j < goodsmodel.getRowCount(); j++) {
					goodsinfo = new GoodsInfo();

					String goodsPic = pic.getThumbnailGoodsPic(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setPicname(goodsPic);

					goodsinfo.setGoodsid(goodsmodel.getItemValue(j, "goodsid"));
					goodsinfo.setGoodsname(goodsmodel.getItemValue(j, "goodsname"));
					goodsinfo.setPrice(goodsmodel.getItemValue(j, "execprice"));
					goodsinfo.setGoodsqty(goodsmodel.getItemValue(j, "goodsqty"));
					goodsinfo.setAmount(goodsmodel.getItemValue(j, "realprice"));
					goodsinfo.setGoodsspecs(goodsmodel.getItemValue(j, "goodsspecs"));
					goodsinfo.setBasepackname(goodsmodel.getItemValue(j, "basepackname"));
					goodsinfo.setFolderpath(Parameter.getParameter("folderpath"));
					goodslist.add(goodsinfo);
				}
				cartinfo.setGoodsinfolist(goodslist);
				list.add(cartinfo);
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
	 * 查询代金券
	 * 
	 * @param memberid
	 * @return
	 */
	public List<MemberInfo> queryCoupon(String memberid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<MemberInfo> list = null;
		MemberInfo info = null;
		try {
			con = DataConnection.getConnection();
			list = new ArrayList<MemberInfo>();
			String sql = "select couponid, publishamount, validdate from RTL_CASHCOUPON "
					+ " where memberid = ? and usestatus = 1 and validdate >= sysdate "
					+ "and startdate <= sysdate and userange in(0,2) order by validdate";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			if (model.getRowCount() <= 0) {
				return list;
			}
			String validdate = "";
			for (int i = 0; i < model.getRowCount(); i++) {
				info = new MemberInfo();
				info.setCouponid(model.getItemValue(i, "couponid"));
				info.setPublishamount(model.getItemValue(i, "publishamount"));
				validdate = model.getItemValue(i, "validdate");
				if (!"".equals(validdate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = formatter.parse(validdate);
					validdate = formatter.format(parse);
				}
				info.setValiddate(validdate);
				list.add(info);
			}
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return list;
	}

}
