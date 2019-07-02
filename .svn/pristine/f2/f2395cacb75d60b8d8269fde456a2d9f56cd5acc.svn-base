package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.SingleSPInfo;
import com.bean.WholeSPInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;

/**
 * 策略帮助类
 * 
 * @author KF_04
 * 
 */
public class SalesPromotionHelper {

	private Logger logger = Logger.getLogger(SalesPromotionHelper.class);

	/**
	 * 取零售单品促销策略
	 * 
	 * @param con
	 * @param shopid
	 * @param goodsid
	 */
	public List<SingleSPInfo> getSingleSpList(String shopid, String goodsid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<SingleSPInfo> list = null;
		try {
			con = DataConnection.getConnection();
			list = new ArrayList<SingleSPInfo>();
			String sql = "select d.spruleid, d.sptype, d.price, d.discount, d.goodsqty, d.subtractamount, "
					+ " d.startvalue, d.endvalue, d.addprice, d.presentid, g.goodsname presentname, "
					+ " d.presentqty "
					+ " FROM pr_retailsale_sp a, pr_retailsale_sp_area b, pr_retailsale_sp_goods c, "
					+ " pr_retailsale_sp_rule d, glb_shop e, glb_goodsset_dtl f, glb_goods g "
					+ " WHERE a.spid = b.spid AND a.spid = c.spid AND c.spgoodsid = d.spgoodsid "
					+ " AND b.shopid = e.shopid(+) AND c.goodssetid = f.goodssetid(+) "
					+ " AND d.presentid = g.goodsid(+) AND spclass = 1 AND a.usestatus = 1 "
					+ " AND d.sptype NOT IN(9) AND b.usestatus = 1 AND c.usestatus = 1 AND d.usestatus = 1 "
					+ " AND nvl(a.userange, 0) IN (0, 2) AND b.shopid = ? AND nvl(a.usestatus, 0) <> 0 "
					+ " AND (c.goodsid = ? OR c.goodsid is null OR c.goodsid = 0) "
					+ " AND (f.goodsid = ? OR f.goodsid is null OR f.goodsid = 0) "
					+ " AND sysdate BETWEEN a.startdate AND a.enddate + 1 "
					+ " AND to_number(to_char(sysdate, 'hh24')) + 1 BETWEEN starttime AND endtime "
					+ " AND decode(a.tacticstype, 3, (decode(decode(instr((to_char(sysdate, 'ww') - "
					+ " to_char(a.startdate, 'ww')) / A.SPACEUNIT, '.') + sign('1'), 1, 1, 0), 1, "
					+ " to_char(sysdate, 'D'), 0)), 4, to_char(sysdate, 'DD'), 1) = decode(a.tacticstype, 3, "
					+ " to_char(a.startdate, 'd'), 4, to_char(a.startdate, 'dd'), 1) "
					+ " ORDER BY a.startdate DESC, instr('1,2,3,5,6,7,4,8,10,11', d.sptype) ";
			sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			sh.bindParam(goodsid);
			sh.bindParam(goodsid);
			SSTableModel model = sh.executeSelect(con, 0, 999);
			if (model.getRowCount() > 0) {
				for (int i = 0, count = model.getRowCount(); i < count; i++) {
					SingleSPInfo sspinfo = new SingleSPInfo();
					sspinfo.setSpruleid(model.getItemValue(i, "spruleid"));
					sspinfo.setSptype(model.getItemValue(i, "sptype"));
					sspinfo.setPrice(model.getItemValue(i, "price"));
					sspinfo.setDiscount(SSDecimalHelper.divide(model.getItemValue(i, "discount"), "10", 2));
					sspinfo.setGoodsqty(model.getItemValue(i, "goodsqty"));
					sspinfo.setSubtractamount(model.getItemValue(i, "subtractamount"));
					sspinfo.setStartvalue(model.getItemValue(i, "startvalue"));
					sspinfo.setEndvalue(model.getItemValue(i, "endvalue"));
					sspinfo.setAddprice(model.getItemValue(i, "addprice"));
					sspinfo.setPresentid(model.getItemValue(i, "presentid"));
					sspinfo.setPresentname(model.getItemValue(i, "presentname"));
					sspinfo.setPresentqty(model.getItemValue(i, "presentqty"));

					list.add(sspinfo);
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
	 * 取零售整单促销策略
	 * 
	 * @param con
	 * @param shopid
	 * @return
	 */
	public List<WholeSPInfo> getWholeSpList(String shopid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<WholeSPInfo> list = null;
		try {
			con = DataConnection.getConnection();
			list = new ArrayList<WholeSPInfo>();
			String sql = "SELECT d.spruleid, d.sptype, d.discount, d.amount, d.subtractamount, d.startvalue, "
					+ "d.endvalue, d.presentid, g.goodsname presentname, d.presentqty, d.goodsqty, d.addprice, "
					+ "d.couponamount, d.validdate, d.coupondiscount, d.userange "
					+ " FROM pr_retailsale_sp a, pr_retailsale_sp_area b, pr_retailsale_sp_rule d, glb_shop e, "
					+ " glb_goods g WHERE a.spid = b.spid AND a.spid = d.spid AND b.shopid = e.shopid(+) "
					+ " AND d.presentid = g.goodsid(+) AND spclass = 2 AND a.usestatus = 1 AND b.usestatus = 1 "
					+ " AND d.usestatus = 1 AND nvl(a.userange, 0) IN(0, 2) AND b.shopid = ? "
					+ " AND sysdate BETWEEN a.startdate AND a.enddate + 1 "
					+ " AND to_number(to_char(sysdate, 'hh24')) "
					+ " + 1 BETWEEN starttime AND endtime AND decode(a.tacticstype, 3, "
					+ " (decode(decode(instr((to_char(sysdate, 'ww') - to_char(a.startdate, 'ww')) / A.SPACEUNIT, '.') "
					+ " + sign('1'), 1, 1, 0), 1, to_char(sysdate, 'D'), 0)), 4, to_char(sysdate, 'DD'), 1) = "
					+ " decode(a.tacticstype, 3, to_char(a.startdate, 'd'), 4, to_char(a.startdate, 'dd'), 1) "
					+ " ORDER BY a.startdate DESC , instr('1,3,4,2,5,6,7,8,9,10', d.sptype) ";
			// 最后这行sql排序用，按照这个id排序
			sh = new SSSelectHelper(sql);
			sh.bindParam(shopid);
			SSTableModel model = sh.executeSelect(con, 0, 999);
			if (model.getRowCount() > 0) {
				for (int i = 0, count = model.getRowCount(); i < count; i++) {
					WholeSPInfo wspinfo = new WholeSPInfo();
					wspinfo.setSpruleid(model.getItemValue(i, "spruleid"));
					wspinfo.setSptype(model.getItemValue(i, "sptype"));
					wspinfo.setDiscount(SSDecimalHelper.divide(model.getItemValue(i, "discount"), "10", 2));
					wspinfo.setGoodsqty(model.getItemValue(i, "goodsqty"));
					wspinfo.setAmount(model.getItemValue(i, "amount"));
					wspinfo.setSubtractamount(model.getItemValue(i, "subtractamount"));
					wspinfo.setStartvalue(model.getItemValue(i, "startvalue"));
					wspinfo.setEndvalue(model.getItemValue(i, "endvalue"));
					wspinfo.setPresentid(model.getItemValue(i, "presentid"));
					wspinfo.setPresentname(model.getItemValue(i, "presentname"));
					wspinfo.setPresentqty(model.getItemValue(i, "presentqty"));
					wspinfo.setAddprice(model.getItemValue(i, "addprice"));
					wspinfo.setValiddate(model.getItemValue(i, "validdate"));
					wspinfo.setCouponamount(model.getItemValue(i, "couponamount"));
					wspinfo.setCoupondiscount(model.getItemValue(i, "coupondiscount"));
					wspinfo.setUserange(model.getItemValue(i, "userange"));
					list.add(wspinfo);
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

}
