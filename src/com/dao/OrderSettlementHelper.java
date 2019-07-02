package com.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bean.Info;
import com.shinesend.basebean.UserInfo;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSDeleteHelper;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.shinesend.svr.glbsvr.RetailHelper;
import com.shinesend.svr.glbsvr.STHelper;
import com.util.DataConnection;

/**
 * @author 作者 :Gzy
 * @version 创建时间：2018年3月21日 上午9:14:04 类说明:订单结算帮助类
 */
public class OrderSettlementHelper {

	private Logger logger = Logger.getLogger(OrderSettlementHelper.class);

	// OrderSettlementHelper.java|上午9:14:04

	public Map<String, String> savePrescription(String[] arr, String[] couponarr, String total_fee, String totalamount,
			String paymethod) throws Exception {
		SSSelectHelper sh = null;
		SSUpdateHelper uh = null;
		String sql = "";
		Connection con = null;
		UserInfo userinfo = null;
		Info info = null;
		// 药方字符串(微信支付用)
		StringBuffer prescriptionidstr = new StringBuffer();
		String prescriptionid = "";
		Map<String, String> map = new HashMap<String, String>();
		try {
			con = DataConnection.getConnection();
			userinfo = new UserInfo();
			String userid = "9";
			String username = "电商销售员";

			// 计算代金券分摊
			// 代金券总金额
			String publishamount = "";
			// 用来计算最后一条数据
			int count = arr.length - 1;
			// 代金券已分摊金额
			String amount = "0";
			// 代金券分摊金额
			String apportionamount = "0";
			// 代金券分摊系数
			String coefficient = "0";
			// 代金券ID
			String couponid = "";

			List<String> idin = null;
			List<String> qtyin = null;
			List<String> pricein = null;
			List<String> goodstypein = null;
			List<String> singlereducein = null;
			for (int j = 0; j < arr.length; j++) {
				info = JSON.parseObject(arr[j], new TypeReference<Info>() {
				});
				String entryid = info.getEntryid();
				String shopid = info.getShopid();
				// 整单优惠金额
				String wholereduce = info.getWholereduce();
				idin = info.getGoodsid();
				qtyin = info.getGoodsqty();
				pricein = info.getPrice();
				goodstypein = info.getGoodstype();
				singlereducein = info.getSinglereduce();

				// 细单总和
				String newpricesum = "0";
				String oldprice = "0";
				for (int i = 0; i < singlereducein.size(); i++) {
					oldprice = SSDecimalHelper.multi(pricein.get(i), qtyin.get(i), 2);
					// if (!"1".equals(goodstypein.get(i))) {
					newpricesum = SSDecimalHelper.add(newpricesum,
							SSDecimalHelper.sub(oldprice, singlereducein.get(i), 2), 2);
					// }
				}

				// 计算整单优惠分摊系数
				String wholeratio = "0";
				// 记录最后一条数据
				int lastitem = idin.size() - 1;
				// 已经分摊金额
				String alreadyamount = "0";

				userinfo.setUserid(userid);
				userinfo.setUsername(username);
				userinfo.setEntryid(entryid);
				userinfo.setShopid(shopid);
				String shopnamesql = "select shopname from v_glb_shop where shopid = ?";
				sh = new SSSelectHelper(shopnamesql);
				sh.bindParam(shopid);
				SSTableModel shopnamemodel = sh.executeSelect(con, 0, 1);
				userinfo.setShopname(shopnamemodel.getItemValue(0, "shopname"));

				// 总单保存
				String goodsid = null;
				String goodsqty = null;
				String goodsprice = null;
				String goodstype = null;
				String singlereduce = null;
				String rtltype = null;
				String phonenumber = info.getPhonenumber();
				String address = info.getAddress();
				String rtlamount = info.getRtlamount();
				String memberid = info.getMemberid();

				shopid = userinfo.getShopid();
				sql = "select eccomgoodsid from glb_shop where shopid = ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(shopid);
				SSTableModel escmodel = sh.executeSelect(con, 0, 1);
				// 组合商品ID
				String combogoodsid = escmodel.getItemValue(0, "eccomgoodsid");
				if ("".equals(combogoodsid)) {
					map.put("prescriptionidstr", prescriptionidstr.toString());
					throw new Exception("门店信息维护不全,不能提交订单!");
					// return map;
				}
				String patientmanname = info.getReceivename();
				entryid = userinfo.getEntryid();

				prescriptionid = masterSave(con, patientmanname, rtlamount, combogoodsid, "1", phonenumber, address,
						memberid, userinfo);
				logger.info("药方-------" + prescriptionid);

				// prescriptionidstr += prescriptionid + "-";
				prescriptionidstr.append(prescriptionid + "-");
				for (int l = 0; l < idin.size(); l++) {
					goodsid = idin.get(l);
					goodsqty = qtyin.get(l);
					// 实际售价
					goodsprice = pricein.get(l);
					// 商品类型
					goodstype = goodstypein.get(l);
					// 单品优惠金额
					singlereduce = singlereducein.get(l);
					String saleableQtyString = getSaleableQtyString(goodsid, userinfo);
					sql = saleableQtyString;
					sh = new SSSelectHelper(sql);
					SSTableModel model = sh.executeSelect(con, 0, 99999);
					if (model.getRowCount() <= 0) {
						return null;
					}
					String managerstqty = null;
					String stposid = null;
					String staccid = null;
					String presentflag = null;
					String useareaprice = null;
					String useshopprice = null;
					String retailpriceid = null;
					String retailprice = null;
					String execpriceid = null;
					String execprice = null;
					String retailamount = null;
					String taxrate = null;
					String goodsname = null;
					String counterid = null;
					// 商品类型
					String goodstype1 = null;

					// 细单自动折扣额
					String dtlautodiscountamount = "";
					// 细单自动折扣
					String dtlautodiscount = "";
					// 整单自动优惠额
					String docautofavoramount = "";
					// 细单自动优惠额
					String dtlautofavoramount = "";
					for (int i = 0; i < model.getRowCount(); i++) {
						String goodsnamesql = "select goodsname from glb_goods where goodsid = " + goodsid;
						sh = new SSSelectHelper(goodsnamesql);
						SSTableModel goodsnamemodel = sh.executeSelect(con, 0, 1);
						goodsname = goodsnamemodel.getItemValue(0, "goodsname");
						// 是否维护商品货架,如果没有维护商品货架
						SSTableModel stposModel = RetailHelper.getGoodsDefStPos(con, goodsid, shopid);
						if (stposModel != null && stposModel.getRowCount() > 0) {
							// 先查出所有的商品，把管理库存且不显示无库存记录且没有库存的删除 modify by
							// 20120714
							managerstqty = stposModel.getItemValue(0, "managerstqty");
							// 管理库存且不显示无库存记录且没有库存的删除
							stposid = stposModel.getItemValue(0, "partstposid");
							// 保管账
							staccid = stposModel.getItemValue(0, "recstaccid");
							// 查询零售价
							useareaprice = stposModel.getItemValue(0, "useareaprice");
							useshopprice = stposModel.getItemValue(0, "useshopprice");

							if ("3".equals(goodstype)) {
								rtltype = "3";
								execpriceid = "";
								String sql1 = "select goodstype from GLB_GOODS where goodsid = ?";
								sh = new SSSelectHelper(sql1);
								sh.bindParam(goodsid);
								SSTableModel typemodel = sh.executeSelect(con, 0, 1);
								goodstype1 = typemodel.getItemValue(0, "goodstype");
								presentflag = "2";
							} else {
								rtltype = "1";
							}
							if (!"2".equals(goodstype1) && !"1".equals(goodstype1)) {
								SSTableModel retailPriceModel = RetailHelper.getGoodsRetailPrice(con, useareaprice,
										useshopprice, goodsid, shopid, entryid, staccid);
								if (retailPriceModel != null && retailPriceModel.getRowCount() > 0) {
									retailpriceid = retailPriceModel.getItemValue(0, "priceid");
									retailprice = retailPriceModel.getItemValue(0, "price");

									// 执行价id
									sql = "select a.mallpriceid from v_ST_GOODS_DEFPOS a where a.stdeptid=? and a.goodsid=?";
									sh = new SSSelectHelper(sql);
									sh.bindParam(shopid);
									sh.bindParam(goodsid);
									SSTableModel execpriceidmodel = sh.executeSelect(con, 0, 1);
									execpriceid = execpriceidmodel.getItemValue(0, "mallpriceid");

									// 执行价
									sql = "select price from pr_goods_price where priceid = ? and goodsid = ?";
									sh = new SSSelectHelper(sql);
									sh.bindParam(execpriceid);
									sh.bindParam(goodsid);
									SSTableModel execpricemodel = sh.executeSelect(con, 0, 1);
									try {
										execprice = execpricemodel.getItemValue(0, "price");
									} catch (Exception e) {
										throw new Exception("商品信息维护不全!");
									}
									retailamount = SSDecimalHelper.multi(execprice, goodsqty, 2);

									// 判断是否为加价购商品
									if ("1".equals(goodstype)) {
										execpriceid = "12";
										dtlautodiscountamount = SSDecimalHelper.sub(execprice, goodsprice, 2);
										dtlautodiscount = SSDecimalHelper
												.multi(SSDecimalHelper.divide(goodsprice, execprice, 2), "100", 2);
										execprice = goodsprice;
									}

									// 是否为折扣商品
									if (!"0".equals(singlereduce)) {
										dtlautofavoramount = singlereduce;
										execpriceid = "12";
										String divide = SSDecimalHelper.divide(singlereduce, goodsqty, 2);
										execprice = SSDecimalHelper.sub(execprice, divide, 2);
										goodsprice = execprice;
									}
									// 计算整单优惠金额
									// if (!"1".equals(goodstype)) {
									if (!"0".equals(wholereduce)) {
										if (lastitem == l) {
											docautofavoramount = SSDecimalHelper.sub(wholereduce, alreadyamount, 2);
										} else {
											wholeratio = SSDecimalHelper.divide(wholereduce, newpricesum, 6);
											docautofavoramount = SSDecimalHelper.multi(wholeratio,
													SSDecimalHelper.multi(execprice, goodsqty, 2), 2);
											alreadyamount = SSDecimalHelper.add(alreadyamount, docautofavoramount, 2);
										}
									}
									// }

								}
							} else {
								if ("1".equals(goodstype1)) {
									staccid = stposModel.getItemValue(0, "recstaccid");
								} else {
									staccid = stposModel.getItemValue(0, "presentstaccid");
								}

							}

							// 销售税率
							sql = "select sataxrate,goodsname from glb_goods where goodsid = " + goodsid;
							sh = new SSSelectHelper(sql);
							SSTableModel goodsmodel = sh.executeSelect(con, 0, 99999);
							taxrate = goodsmodel.getItemValue(0, "sataxrate");

							// 柜组id
							sql = " select counterid from V_ST_GOODS_DEFPOS t where goodsid = " + goodsid
									+ " and t.stdeptid = " + userinfo.getShopid();
							sh = new SSSelectHelper(sql);
							SSTableModel countermodel = sh.executeSelect(con, 0, 99999);
							if (countermodel.getRowCount() <= 0) {
								throw new Exception(goodsname + "此商品数据错误,请选择其他商品!!!");
							}
							counterid = countermodel.getItemValue(0, "counterid");
						} else {
							throw new Exception(goodsname + "库存不足!!!");
						}
					}

					detailsave(con, goodsid, stposid, "", "2", prescriptionid, retailpriceid, retailprice, execpriceid,
							execprice, goodsprice, goodsqty, staccid, retailamount, taxrate, "", "0", "0", "0",
							managerstqty, "1", "1", counterid, dtlautodiscountamount, dtlautodiscount,
							dtlautofavoramount, docautofavoramount, rtltype, presentflag, userinfo);

					sql = "select managerstqty, staccid, counterid, rtltinyid, goodsid, goodsqty, lotid"
							+ " from rtl_tiny where prescriptionid = " + prescriptionid + " and goodsid = " + goodsid;
					sh = new SSSelectHelper(sql);
					SSTableModel saveddbmodel = sh.executeSelect(con, 0, 99999);
					int row = saveddbmodel.getRowCount();
					generateTemporaryWarehouseReceipt(con, saveddbmodel, row, goodsname, userinfo);

				}

				// STFixHelper.doFixSto(con, stoinfo, fixMode, onlyPartFix,
				// errInfo, userInfo, stqtyWheres)

				saveCheck(con, prescriptionid);
				// 确认操作更改状态为待审核
				confirm(con, prescriptionid);
				// 审核操作更改状态为审核通过
				approvePrescription(con, prescriptionid);
				// 将状态改为电商待收款
				updatePrescription(con, prescriptionid, paymethod);

				publishamount = info.getPublishamount();
				couponid = info.getCouponid();
				// if ("1".equals(paymethod)) {
				if (!"".equals(publishamount)) {
					if (j == count) {
						// 计算最后一条分摊金额
						apportionamount = SSDecimalHelper.sub(publishamount, amount, 2);
					} else {
						// 不是最后一条
						// 分摊系数
						coefficient = SSDecimalHelper.divide(publishamount, totalamount, 2);
						// 分摊金额
						apportionamount = SSDecimalHelper.multi(info.getRtlamount(), coefficient, 2);
						amount = SSDecimalHelper.add(amount, apportionamount, 2);
					}
					// 代金券分摊金额插入关系表 APP_PRESCRIPTION_TO_CASHCOUPON
					insertNexusTable(con, userinfo, prescriptionid, couponid, publishamount, apportionamount);
				}
				// }

				sql = "update rtl_prescription set paymethod = ? ";
				if (paymethod.equals("1")) {
					// 药方上微信支付金额
					String sub = SSDecimalHelper.sub(info.getRtlamount(), apportionamount, 2);
					sql += ",wxcashfee = " + sub;
				}
				sql += " where prescriptionid = ?";
				uh = new SSUpdateHelper(sql);
				uh.bindParam(paymethod);
				uh.bindParam(prescriptionid);
				uh.executeUpdate(con);
				// 删除购物车商品
				deleteCartOrder(con, memberid, shopid, idin);
				// 生成代金券
				if (couponarr != null && couponarr.length > 0) {
					for (int i = 0; i < couponarr.length; i++) {
						// 截取长度为4 避免将最后的空值去掉
						String[] shopcouponarr = couponarr[i].split(",", 4);
						if (shopid.equals(shopcouponarr[0])) {
							generateCoupon(con, shopid, memberid, prescriptionid, shopcouponarr[1], shopcouponarr[2],
									paymethod, shopcouponarr[3]);
						}
					}
				}
			}
			// if ("1".equals(paymethod)) {
			if (!couponid.equals("")) {
				// 修改代金券使用状态
				modifyCouponUsestatus(con, userinfo, couponid);
				// 插入代金券流水
				InserCashCouponLst(con, userinfo, couponid, prescriptionid);
			}
			// }
			// 检查库存信息
			sql = "select b.ioid, a.lotid,b.staccid,b.goodsid,b.goodsname "
					+ "from rtl_tiny a,v_st_io_uk b where a.rtltinyid = b.sourcedtlid "
					+ "and a.prescriptionid = ? and b.comefrom = 51";
			sh = new SSSelectHelper(sql);
			sh.bindParam(prescriptionid);
			SSTableModel checkiomodel = sh.executeSelect(con, 0, 999999);
			for (int i = 0; i < checkiomodel.getRowCount(); i++) {
				// String ioid = checkiomodel.getItemValue(i, "ioid");
				String lotid = checkiomodel.getItemValue(i, "lotid");
				// StringBuilder sb = new StringBuilder();
				// 获取出库总单信息：
				// sb.append("select staccid,goodsid,ioqty,comefrom from
				// st_io_uk ");
				// sb.append(" where ioid = ? for update");
				// sh = new SSSelectHelper(sb.toString());
				// sh.bindParam(ioid);
				// SSTableModel iomodel = sh.executeSelect(con, 0, 1);
				// String staccid = iomodel.getItemValue(0, "staccid");
				// String goodsid = iomodel.getItemValue(0, "goodsid");
				// sb = new StringBuilder();
				String staccid = checkiomodel.getItemValue(i, "staccid");
				String goodsid = checkiomodel.getItemValue(i, "goodsid");
				// 锁库存表
				// sb.append("select stqtynowid from st_qty_now where goodsid =
				// ? \n");
				// sb.append(" and staccid = ? for update ");
				sql = "select stqtynowid from st_qty_now where goodsid = ? and staccid = ? for update ";
				sh = new SSSelectHelper(sql);
				sh.bindParam(goodsid);
				sh.bindParam(staccid);
				sh.executeSelect(con, 0, 1);

				String lackqty = STHelper.checkQty(con, "", staccid, goodsid, "", "", lotid, "", "", true);
				if (SSDecimalHelper.compare(lackqty, "0") != 0) {
					throw new Exception(goodsid + "库存不足，缺货数量为" + lackqty + "！");
				}
			}

			con.commit();
			map.put("prescriptionidstr", prescriptionidstr.toString());
			map.put("errormsg", "");
			return map;
		} catch (SQLException e) {
			map.put("prescriptionidstr", prescriptionidstr.toString());
			map.put("errormsg", "下单失败!");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			map.put("prescriptionidstr", prescriptionidstr.toString());
			map.put("errormsg", e.getMessage());
			return map;
		} finally {
			DataConnection.close(con);
		}

	}

	/**
	 * 确认操作
	 * 
	 * @author Gzy
	 * @date 2018年3月29日
	 * @version 1.0.0
	 * @param con
	 * @param prescriptionid
	 */
	private void confirm(Connection con, String prescriptionid) {
		try {
			String sql = "update rtl_prescription set usestatus = 3 where prescriptionid = " + prescriptionid;
			SSUpdateHelper uh = new SSUpdateHelper(sql);
			uh.executeUpdate(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 审核操作
	 * 
	 * @author Gzy
	 * @date 2018年3月29日
	 * @version 1.0.0
	 * @param con
	 * @param prescriptionid
	 */
	private void approvePrescription(Connection con, String prescriptionid) {
		try {
			String sql = "update rtl_prescription set usestatus = 4 where prescriptionid = " + prescriptionid;
			SSUpdateHelper uh = new SSUpdateHelper(sql);
			uh.executeUpdate(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 将状态改为电商待收款
	 * 
	 * @author Gzy
	 * @date 2018年3月29日
	 * @version 1.0.0
	 * @param con
	 * @param prescriptionid
	 */
	private void updatePrescription(Connection con, String prescriptionid, String paymethod) {
		try {
			String usestatus = "";
			if ("1".equals(paymethod)) {
				usestatus = "7";
			} else {
				usestatus = "4";
			}
			String sql = "update rtl_prescription set usestatus = ? where prescriptionid = " + prescriptionid;
			SSUpdateHelper uh = new SSUpdateHelper(sql);
			uh.bindParam(usestatus);
			uh.executeUpdate(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 细单保存
	 * 
	 * @author Gzy
	 * @date 2018年3月29日
	 * @version 1.0.0
	 * @param con
	 * @param goodsid
	 * @param stposid
	 * @param lotid
	 * @param combintype
	 * @param prescriptionid
	 * @param retailpriceid
	 * @param retailprice
	 * @param execpriceid
	 * @param execprice
	 * @param realprice
	 * @param goodsqty
	 * @param staccid
	 * @param retailamount
	 * @param taxrate
	 * @param tinyretailflag
	 * @param nopromogoods
	 * @param nopointgoods
	 * @param nowholedis
	 * @param managerstqty
	 * @param dose
	 * @param qtys
	 * @param counterid
	 * @param userinfo
	 */
	private void detailsave(Connection con, String goodsid, String stposid, String lotid, String combintype,
			String prescriptionid, String retailpriceid, String retailprice, String execpriceid, String execprice,
			String realprice, String goodsqty, String staccid, String retailamount, String taxrate,
			String tinyretailflag, String nopromogoods, String nopointgoods, String nowholedis, String managerstqty,
			String dose, String qtys, String counterid, String dtlautodiscountamount, String dtlautodiscount,
			String dtlautofavoramount, String docautofavoramount, String rtltype, String presentflag,
			UserInfo userinfo) {
		try {
			String amount = SSDecimalHelper.multi(realprice, goodsqty, 2);
			// if (!"1".equals(goodstype)) {
			// }
			SSInsertHelper ih = new SSInsertHelper("rtl_tiny");
			ih.bindSequence("rtltinyid", "seq_rtl_dtl");
			ih.bindParam("goodsid", goodsid);
			ih.bindParam("stposid", stposid);
			ih.bindParam("lotid", lotid);
			ih.bindParam("amount", SSDecimalHelper.sub(amount, docautofavoramount, 2));
			ih.bindParam("combintype", combintype);
			ih.bindParam("prescriptionid", prescriptionid);
			ih.bindParam("retailpriceid", retailpriceid);
			ih.bindParam("retailprice", retailprice);
			ih.bindParam("execpriceid", execpriceid);
			ih.bindParam("execprice", execprice);
			ih.bindParam("realprice", realprice);
			ih.bindParam("goodsqty", goodsqty);
			ih.bindParam("inputmanid", userinfo.getUserid());
			ih.bindSysdate("credate");
			ih.bindParam("staccid", staccid);
			ih.bindParam("retailamount", retailamount);
			ih.bindParam("taxrate", taxrate);
			ih.bindParam("tinyretailflag", tinyretailflag);
			ih.bindParam("nopromogoods", nopromogoods);
			ih.bindParam("nopointgoods", nopointgoods);
			ih.bindParam("nowholedis", nowholedis);
			ih.bindParam("managerstqty", managerstqty);
			ih.bindParam("counterid", counterid);
			ih.bindParam("dose", dose);
			ih.bindParam("qtys", qtys);
			ih.bindParam("dtlautodiscountamount", dtlautodiscountamount);
			ih.bindParam("dtlautodiscount", dtlautodiscount);
			ih.bindParam("dtlautofavoramount", dtlautofavoramount);
			ih.bindParam("docautofavoramount", docautofavoramount);
			ih.bindParam("rtltype", rtltype);
			ih.bindParam("presentflag", presentflag);

			ih.executeInsert(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}

	}

	/**
	 * 生成临时出入库单
	 * 
	 * @author Gzy
	 * @date 2018年3月29日
	 * @version 1.0.0
	 * @param saveddbmodel
	 * @param row
	 * @param goodsname
	 * @param userinfo
	 * @return
	 * @throws Exception
	 */
	private void generateTemporaryWarehouseReceipt(Connection con, SSTableModel saveddbmodel, int row, String goodsname,
			UserInfo userinfo) throws Exception {
		if (row > 0) {
			for (int i = 0; i < saveddbmodel.getRowCount(); i++) {
				// 管理库存
				String managerstqty = saveddbmodel.getItemValue(i, "managerstqty");
				if ("1".equals(managerstqty)) {
					// 新增修改时，生成临时的出库单（因为生成出入库单是先删除，后生成，所以修改时这么做没有问题）
					SSTableModel iomodel = STHelper.genStIoModel();
					SSTableModel iodtlmodel = STHelper.genStIoDtlModel();
					iomodel.appendRow();
					iomodel.setItemValue(0, "ioid", "0");
					iomodel.setItemValue(0, "companyid", userinfo.getShopid());
					iomodel.setItemValue(0, "companyname", userinfo.getShopname());
					iomodel.setItemValue(0, "staccid", saveddbmodel.getItemValue(i, "staccid"));
					iomodel.setItemValue(0, "counterid", saveddbmodel.getItemValue(i, "counterid"));
					iomodel.setItemValue(0, "sourceid", "");
					iomodel.setItemValue(0, "sourcedtlid", saveddbmodel.getItemValue(i, "rtltinyid"));
					iomodel.setItemValue(0, "sourcetable", "51");
					iomodel.setItemValue(0, "comefrom", "51");
					iomodel.setItemValue(0, "goodsid", saveddbmodel.getItemValue(i, "goodsid"));
					iomodel.setItemValue(0, "ioflag", "1");
					iomodel.setItemValue(0, "ioqty", saveddbmodel.getItemValue(i, "goodsqty"));
					iodtlmodel.appendRow();
					iodtlmodel.setItemValue(0, "ioid", "0");
					iodtlmodel.setItemValue(0, "iodtlid", "0");
					iodtlmodel.setItemValue(0, "lotid", saveddbmodel.getItemValue(i, "lotid"));
					iodtlmodel.setItemValue(0, "dtlgoodsqty", saveddbmodel.getItemValue(i, "goodsqty"));
					STHelper.genStIo(con, iomodel, iodtlmodel, userinfo);
					// 以前参数为false，但所有出库给客户的，都应该是可销的才对，所以改为true。
					String lackqty = STHelper.checkQty(con, "", saveddbmodel.getItemValue(i, "staccid"),
							saveddbmodel.getItemValue(i, "goodsid"), "", "", saveddbmodel.getItemValue(i, "lotid"), "",
							"", true);

					if (SSDecimalHelper.compare(lackqty, "0") != 0) {
						throw new Exception(goodsname + "库存不足，缺货数量为" + lackqty + "！");
					}
				}

			}

		} else {
			// 管理库存
			String managerstqty = saveddbmodel.getItemValue(row, "managerstqty");
			if ("1".equals(managerstqty)) {
				// 删除时，需要删除临时出库单
				SSTableModel ioidsmodel = STHelper.getStIoIDSByComeFrom(con, "51",
						saveddbmodel.getItemValue(0, "rtltinyid"));
				if (ioidsmodel == null || ioidsmodel.getRowCount() <= 0) {
					throw new Exception("没有找到临时的出库单！");
				}
				for (int i = 0; i < ioidsmodel.getRowCount(); i++) {

					STHelper.delStIo(con, ioidsmodel.getItemValue(i, "ioid"), userinfo);

				}
			}
		}
	}

	/**
	 * 保存检查
	 * 
	 * @author Gzy
	 * @date 2018年3月24日
	 * @version 1.0.0
	 */
	private String saveCheck(Connection con, String prescriptionid) {
		String message = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select usestatus from rtl_prescription ");
		sb.append("where prescriptionid = ? for update");
		SSSelectHelper sh = new SSSelectHelper(sb.toString());
		sh.bindParam(prescriptionid);
		SSTableModel model;
		try {
			model = sh.executeSelect(con, 0, 1);

			if (model == null || model.getRowCount() <= 0) {
				throw new Exception("此药方已被别人删除！");
			} else {
				String usestatus = model.getItemValue(0, "usestatus");
				if ("1".equals(usestatus)) {
					throw new Exception("此药方已收款！");
				}
				if ("0".equals(usestatus)) {
					throw new Exception("此药方已作废！");
				}
				if (!"2".equals(usestatus)) {
					throw new Exception("此药方不是临时状态！");
				}

			}
		} catch (Exception e) {
			message = e.getMessage();
			logger.error("error", e);
		}
		return message;
	}

	/**
	 * 总单保存
	 * 
	 * @author Gzy
	 * @date 2018年3月24日
	 * @version 1.0.0
	 * @param con
	 * @param userinfo
	 * @param combogoodsid
	 * @param rtlamount
	 * @param prescriptionnum
	 * @param phonenumber
	 * @param address
	 */
	private String masterSave(Connection con, String patientmanname, String rtlamount, String combogoodsid,
			String prescriptionnum, String phonenumber, String address, String memberid, UserInfo userinfo) {
		String prescriptionid = "";
		try {
			String usestatus = "2";
			SSInsertHelper ih = new SSInsertHelper("rtl_prescription");
			prescriptionid = SSModelHelper.getSequenceValue(con, "SEQ_RTL_PRESCRIPTION");
			ih.bindParam("prescriptionid", prescriptionid);
			ih.bindParam("shopid", userinfo.getShopid());
			ih.bindParam("combogoodsid", combogoodsid);
			ih.bindParam("memo", phonenumber);
			ih.bindParam("address", address);
			ih.bindParam("patientmanname", patientmanname);
			ih.bindParam("clerkerid", userinfo.getUserid());
			ih.bindParam("prescriptionnum", prescriptionnum);
			ih.bindParam("usestatus", usestatus);
			ih.bindParam("rtlamount", rtlamount);
			ih.bindParam("memberid", memberid);
			ih.bindParam("inputmanid", userinfo.getUserid());
			ih.bindParam("inputmethod", "2");
			ih.bindSysdate("credate");
			ih.executeInsert(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
		return prescriptionid;
	}

	/**
	 * 查询可销库存
	 * 
	 * @author Gzy
	 * @date 2018年3月24日
	 * @version 1.0.0
	 * @param goodsid
	 * @param userinfo
	 * @return
	 */
	private static String getSaleableQtyString(String goodsid, UserInfo userinfo) {
		StringBuffer sb = new StringBuffer();
		String roletype = "2";
		String entryid = "22";
		String shopid = userinfo.getShopid();
		sb.append("select goodsid, ");
		sb.append("get_saleable_qty(goodsid,");
		sb.append(roletype + ",");
		sb.append(entryid + ",");
		if (shopid == null || "".equals(shopid)) {
			sb.append("0,");
		} else {
			sb.append(shopid + ",");
		}
		sb.append("'" + "" + "',");
		sb.append("'" + "" + "',");
		sb.append("0)");
		sb.append(" from glb_goods where ");
		Integer.parseInt(goodsid);
		sb.append("(").append("goodsid = ").append(goodsid).append(" or goodsmcode like '")
				.append(goodsid.toUpperCase()).append("%' or goodsname like '").append(goodsid)
				.append("%' or goodsbarcode like '").append(goodsid).append("%'").append(")");
		return sb.toString();

	}

	/**
	 * 生成药方和代金券关系表
	 * 
	 * @param con
	 * @param userinfo
	 * @param prescriptionid
	 * @param couponid
	 * @param publishamount
	 * @param couponamount
	 */
	public void insertNexusTable(Connection con, UserInfo userinfo, String prescriptionid, String couponid,
			String publishamount, String couponamount) {
		try {
			SSInsertHelper ih = new SSInsertHelper("APP_PRESCRIPT_TO_COUPON");
			ih.bindSequence("prescripttocouponid", "SEQ_APP_PRESCRIPT_TO_COUPON");
			ih.bindParam("prescriptionid", prescriptionid);
			ih.bindParam("couponid", couponid);
			ih.bindParam("publishamount", publishamount);
			ih.bindParam("couponamount", couponamount);
			ih.bindParam("inputmanid", userinfo.getUserid());
			ih.bindSysdate("credate");
			ih.executeInsert(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 修改代金券状态
	 * 
	 * @param con
	 * @param userinfo
	 * @param couponid
	 */
	public void modifyCouponUsestatus(Connection con, UserInfo userinfo, String couponid) {
		try {
			String sql = "update RTL_CASHCOUPON set usestatus = 2 where couponid = ?";
			SSUpdateHelper uh = new SSUpdateHelper(sql);
			// uh.bindParam(userinfo.getUserid());
			uh.bindParam(couponid);
			uh.executeUpdate(con);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 插入代金券流水
	 * 
	 * @param con
	 * @param userinfo
	 * @param couponid
	 * @param prescriptionid
	 * @throws Exception
	 */
	public void InserCashCouponLst(Connection con, UserInfo userinfo, String couponid, String prescriptionid)
			throws Exception {

		SSSelectHelper ssh = new SSSelectHelper("select amount from rtl_cashcoupon where couponid = ?");
		ssh.bindParam(couponid);
		SSTableModel executeSelectModel = ssh.executeSelect(con, 0, 1);
		String multi = SSDecimalHelper.multi(executeSelectModel.getItemValue(0, "amount"), "-1", 2);

		SSInsertHelper ih = new SSInsertHelper("rtl_cashcoupon_lst");
		ih.bindSequence("lstid", "seq_rtl_cashcoupon_lst");
		ih.bindParam("couponid", couponid);
		ih.bindParam("amount", multi);
		ih.bindParam("comefrom", "52");
		ih.bindParam("sourceid", prescriptionid);
		ih.bindSysdate("credate");
		ih.bindParam("inputmanid", userinfo.getUserid());
		ih.bindParam("shopid", userinfo.getShopid());
		ih.executeInsert(con);
	}

	/**
	 * 订单提交完成删除购物车商品
	 * 
	 * @param userid
	 * @param shopid
	 * @param goodsidlist
	 */
	public void deleteCartOrder(Connection con, String memberid, String shopid, List<String> goodsidlist) {
		SSDeleteHelper dh = null;
		try {
			String sql = null;
			for (int i = 0; i < goodsidlist.size(); i++) {
				sql = " delete from APP_SHOPPINGCART where userid = (select DISTINCT (b.appuserid) "
						+ "from APP_SHOPPINGCART a, app_user b where a.userid = b.appuserid "
						+ "and b.memberid = ?) and shopid = ? and  goodsid = ?";
				dh = new SSDeleteHelper(sql);
				dh.bindParam(memberid);
				dh.bindParam(shopid);
				dh.bindParam((String) goodsidlist.get(i));
				dh.executeDelete(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}

	/**
	 * 生成代金券以及代金券流水
	 * (货到付款生成发行代金券,在送货时生成代金券流水及将代金券状态改为正式,线上支付生成发行状态代金券,支付成功后生成流水以及将代金券状态改为正式)
	 * 
	 * @param con
	 * @param memberid
	 * @param publishamount
	 * @param validdate
	 * @param validdate
	 * @throws Exception
	 */
	public void generateCoupon(Connection con, String shopid, String memberid, String prescriptionid,
			String publishamount, String validdate, String paymethod, String userange) throws Exception {
		String couponno = "";
		SSSelectHelper sh = null;
		String sql = "select mobile from v_rtl_member where memberid = ? ";
		sh = new SSSelectHelper(sql);
		sh.bindParam(memberid);
		sh.executeSelect(con, 0, 1);
		SSTableModel m = sh.executeSelect(con, 0, 1);
		String mobile = m.getItemValue(0, "mobile");
		String usestatus = "0";

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
			SSTableModel shmodel = sh.executeSelect(con, 0, 1);
			if (shmodel == null || shmodel.getRowCount() <= 0) {
				break;
			}
		}
		// if ("2".equals(paymethod)) {
		// usestatus = "1";
		// }
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
		iih.bindParam("usestatus", usestatus);
		iih.bindParam("memberid", memberid);
		iih.bindParam("shopid", shopid);
		iih.bindParam("mobile", mobile);
		iih.bindParam("sourceid", prescriptionid);
		iih.bindParam("memo", "由商城订单" + prescriptionid + "生成");
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
		iih.executeInsert(con);

		// if ("2".equals(paymethod)) {
		// String lstsql = "select couponid from rtl_cashcoupon where
		// couponno=?";
		// SSSelectHelper lstssh = new SSSelectHelper(lstsql);
		// lstssh.bindParam(couponno);
		// SSTableModel couponlstModel = lstssh.executeSelect(con, 0, 1);
		// SSInsertHelper ssih = new SSInsertHelper("rtl_cashcoupon_lst");
		// ssih.bindSequence("lstid", "seq_rtl_cashcoupon_lst");
		// ssih.bindParam("couponid", couponlstModel.getItemValue(0,
		// "couponid"));
		// ssih.bindParam("amount", publishamount);
		// ssih.bindParam("shopid", shopid);
		// ssih.bindParam("inputmanid", "9");
		// ssih.bindParam("sourceid", prescriptionid);
		// // 业务类型为零售发放
		// ssih.bindParam("comefrom", "51");
		// ssih.bindSysdate("credate");
		// ssih.executeInsert(con);
		// }
	}

	// 生成随机代金卷号码
	public static String genRandomNum() {
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
	public static String getCouponno(String couponno) {
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

}
