package com.dao;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.HttpUtil;
import com.util.Parameter;
import com.util.wx.CommonUtil;
import com.util.wx.WXPayUtil;

public class PayHelper {

	private Logger logger = Logger.getLogger(AddressHelper.class);
	// 微信统一提交订单地址
	private String uni_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 微信退款地址
	private String refund_url = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	/**
	 * 微信支付
	 * 
	 * @param openId
	 * @param spbill_create_ip
	 * @param total_fee
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> doPay(String openId, String total_fee, String totalamount, String[] arr,
			String[] couponarr) {
		Connection con = null;
		Map<String, String> params = null;
		Map<String, String> finalpackage = null;
		Map<String, String> parameters = null;
		try {
			con = DataConnection.getConnection();
			// 生成药方
			OrderSettlementHelper osh = new OrderSettlementHelper();
			// 药方字符串
			Map<String, String> prescriptionidmap = osh.savePrescription(arr, couponarr, total_fee, totalamount, "1");
			String prescriptionidstr = (String) prescriptionidmap.get("prescriptionidstr");
			logger.info("药方id=========" + prescriptionidstr);
			if ("".equals(prescriptionidmap.get("errormsg"))) {
				// 公众账号ID
				String appid = Parameter.getParameter("appid");
				// 商户号
				String mchid = Parameter.getParameter("mchid");
				// 商户API密钥
				String apikey = Parameter.getParameter("apikey");
				// 项目名称(以后给多个用户使用时方便部署,在config.properties配置文件中读取)
				String projectname = Parameter.getParameter("projectname");
				// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
				String notify_url = "http://www.shinesend.com/" + projectname + "/successpay.do";

				// 使用序列号作为订单号码
				String out_trade_no = SSModelHelper.getSequenceValue(con, "seq_orderid");
				// 将orderid 存到药方表
				String[] split = prescriptionidstr.split("-");
				for (int i = 0, len = split.length; i < len; i++) {
					String sql = "update rtl_prescription set orderid = " + out_trade_no + " where prescriptionid = "
							+ split[i];
					SSUpdateHelper uh = new SSUpdateHelper(sql);
					int executeUpdate = uh.executeUpdate(con);
					logger.info("药方信息=====" + out_trade_no + "====" + split[i] + "====" + executeUpdate);
					con.commit();
				}

				String sql = "select shopid from rtl_prescription where orderid = ? order by shopid asc";
				SSSelectHelper sh = new SSSelectHelper(sql);
				sh.bindParam(out_trade_no);
				SSTableModel shopidmodel = sh.executeSelect(con, 0, 9999);
				String device_info = shopidmodel.getItemValue(0, "shopid");

				// 微信支付的 单位是分
				total_fee = SSDecimalHelper.multi(total_fee, "100", 0);
				parameters = new TreeMap<String, String>();
				parameters.put("appid", appid);
				parameters.put("mch_id", mchid);
				parameters.put("body", "商城支付");// 购买支付信息
				parameters.put("trade_type", "JSAPI");
				parameters.put("nonce_str", WXPayUtil.generateNonceStr());
				parameters.put("notify_url", notify_url);
				parameters.put("out_trade_no", out_trade_no);// 订单号
				parameters.put("total_fee", total_fee);// 总金额单位为分
				parameters.put("openid", openId);
				parameters.put("spbill_create_ip", "");
				parameters.put("device_info", device_info);

				String sign = WXPayUtil.generateSignature(parameters, apikey);
				parameters.put("sign", sign);
				// String requestXML = getRequestXml(parameters);
				String requestXML = WXPayUtil.mapToXml(parameters);

				logger.info("微信提交支付xml-----" + requestXML);
				String result = CommonUtil.httpsRequest(uni_url, "POST", requestXML);
				logger.info("微信提交支付结果-----" + result);

				Map<String, String> map = WXPayUtil.xmlToMap(result);

				// 封装返回给前台信息
				finalpackage = new HashMap<String, String>();
				if (map.get("prepay_id") == null) {
					logger.info("支付错误-----");
					// 如果失败直接返回结果的map
					finalpackage = map;
				} else {
					String timeStamp = WXPayUtil.getCurrentTimestamp() + "";
					String nonceStr = WXPayUtil.generateNonceStr();
					String packages = "prepay_id=" + map.get("prepay_id");
					params = new TreeMap<String, String>();
					params.put("appId", appid);
					params.put("nonceStr", nonceStr);
					params.put("package", packages);
					params.put("signType", "MD5");
					params.put("timeStamp", timeStamp);
					String respsign = WXPayUtil.generateSignature(params, apikey);

					finalpackage.put("appid", appid);
					finalpackage.put("nonceStr", nonceStr);
					finalpackage.put("timeStamp", timeStamp);
					finalpackage.put("packageValue", packages);
					finalpackage.put("paySign", respsign);
					finalpackage.put("return_code", "SUCCESS");
					finalpackage.put("prescriptionidstr", prescriptionidstr);
				}

			} else {
				finalpackage = new HashMap<String, String>();
				finalpackage.put("dataflag", prescriptionidmap.get("errormsg"));
			}
			return finalpackage;
		} catch (Exception e) {
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return null;
	}

	/**
	 * 重新支付订单
	 * 
	 * @param orderid
	 * @param openid
	 * @param total_fee
	 */
	public Map<String, String> rePay(String orderid, String openid, String total_fee) {
		Connection con = null;
		Map<String, String> params = null;
		Map<String, String> finalpackage = null;
		Map<String, String> parameters = null;
		try {
			con = DataConnection.getConnection();
			// 公众账号ID
			String appid = Parameter.getParameter("appid");
			// 商户号
			String mchid = Parameter.getParameter("mchid");
			// 商户API密钥
			String apikey = Parameter.getParameter("apikey");
			// 项目名称(以后给多个用户使用时方便部署,在config.properties配置文件中读取)
			String projectname = Parameter.getParameter("projectname");
			// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
			String notify_url = "http://www.shinesend.com/" + projectname + "/successpay.do";

			// 使用序列号作为订单号码
			String out_trade_no = orderid;
			logger.info("商户订单号-------" + out_trade_no);

			String sql = "select shopid from rtl_prescription where orderid = ? order by shopid asc";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(out_trade_no);
			SSTableModel shopidmodel = sh.executeSelect(con, 0, 9999);
			String device_info = shopidmodel.getItemValue(0, "shopid");

			// 微信支付的 单位是分
			total_fee = SSDecimalHelper.multi(total_fee, "100", 0);
			parameters = new TreeMap<String, String>();
			parameters.put("appid", appid);
			parameters.put("mch_id", mchid);
			parameters.put("body", "商城支付");// 购买支付信息
			parameters.put("trade_type", "JSAPI");
			parameters.put("nonce_str", WXPayUtil.generateNonceStr());
			parameters.put("notify_url", notify_url);
			parameters.put("out_trade_no", out_trade_no);// 订单号
			parameters.put("total_fee", total_fee);// 总金额单位为分
			parameters.put("openid", openid);
			parameters.put("spbill_create_ip", "");
			parameters.put("device_info", device_info);
			String sign = WXPayUtil.generateSignature(parameters, apikey);
			parameters.put("sign", sign);
			// String requestXML = getRequestXml(parameters);
			String requestXML = WXPayUtil.mapToXml(parameters);

			logger.info("微信提交支付xml-----" + requestXML);
			String result = CommonUtil.httpsRequest(uni_url, "POST", requestXML);
			logger.info("微信提交支付结果-----" + result);

			Map<String, String> map = WXPayUtil.xmlToMap(result);
			for (String key : map.keySet()) {
				logger.info("Key = " + key + "  ******" + map.get(key));
				System.out.println("Key = " + key + "  value= " + map.get(key));
			}
			// 封装返回给前台信息map
			finalpackage = new HashMap<String, String>();
			if (map.get("prepay_id") == null) {
				logger.info("支付错误-----");
				// 如果失败直接返回结果的map
				finalpackage = map;
			} else {
				String timeStamp = WXPayUtil.getCurrentTimestamp() + "";
				String nonceStr = WXPayUtil.generateNonceStr();
				String packages = "prepay_id=" + map.get("prepay_id");
				params = new TreeMap<String, String>();
				params.put("appId", appid);
				params.put("nonceStr", nonceStr);
				params.put("package", packages);
				params.put("signType", "MD5");
				params.put("timeStamp", timeStamp);
				String respsign = WXPayUtil.generateSignature(params, apikey);

				finalpackage.put("appid", appid);
				finalpackage.put("nonceStr", nonceStr);
				finalpackage.put("timeStamp", timeStamp);
				finalpackage.put("packageValue", packages);
				finalpackage.put("paySign", respsign);
				finalpackage.put("return_code", map.get("return_code"));

				for (String e : finalpackage.keySet()) {
					logger.info(e + "-------------" + finalpackage.get(e));
				}
			}
			return finalpackage;
		} catch (Exception e) {
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return null;
	}

	/**
	 * 微信退款
	 * 
	 * @param transaction_id
	 * @param out_refund_no
	 * 
	 * @param total_fee
	 * @param refund_fee
	 * @return
	 */
	public String refund(String transaction_id, String out_refund_no, String total_fee, String refund_fee) {
		Map<String, String> map = new TreeMap<String, String>();
		String result = "";
		try {
			String appid = Parameter.getParameter("appid");
			String mchid = Parameter.getParameter("mchid");
			String apikey = Parameter.getParameter("apikey");
			if (total_fee == null) {
				total_fee = "1";
			}
			if (refund_fee == null) {
				refund_fee = "1";
			}

			map.put("appid", appid);
			map.put("mch_id", mchid);
			map.put("nonce_str", WXPayUtil.generateNonceStr());
			if (transaction_id != null) {
				// 微信订单号
				map.put("transaction_id", transaction_id);
			}
			if (out_refund_no != null) {
				// 商户退款单号
				map.put("out_refund_no", out_refund_no);
			}
			// 订单金额
			map.put("total_fee ", total_fee);
			// 退款金额
			map.put("refund_fee", refund_fee);
			String sign = WXPayUtil.generateSignature(map, apikey);
			map.put("sign", sign);
			System.out.println("退款map-----" + map);
			result = CommonUtil.sendPost(refund_url, getRequestXml(map));
			System.out.println("退款结果-----" + result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
		return result;
	}

	/**
	 * 将请求参数转换为xml格式的string
	 * 
	 * @param parameters
	 * @return
	 */
	public String getRequestXml(Map<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
				sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
			} else {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 支付完成后将药方状态改为电商收款
	 * 
	 * @param out_trade_no
	 */
	public void updatePrescription(String out_trade_no) {
		Connection con = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from rtl_prescription where orderid = ?";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(out_trade_no);
			SSTableModel model = sh.executeSelect(con, 0, 99999);
			if (model.getRowCount() > 0) {
				for (int i = 0, len = model.getRowCount(); i < len; i++) {
					String sql1 = "update rtl_prescription set usestatus = 6 where prescriptionid = "
							+ model.getItemValue(i, "prescriptionid");
					SSUpdateHelper uh = new SSUpdateHelper(sql1);
					uh.executeUpdate(con);
					con.commit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 插入电子支付表
	 * 
	 * @param orderid
	 * @param shopid
	 * @param cashfee
	 * @param transactionid
	 * @param timeend
	 * @param appid
	 * @param mchid
	 * @param sign
	 */
	public void InsertWXResult(String orderid, String totalfee, String cashfee, String transactionid, String timeend,
			String appid, String mchid, String sign) {
		Connection con = null;
		SSInsertHelper ih = null;
		try {
			con = DataConnection.getConnection();

			String sql = "select * from rtl_prescription where orderid = ? order by shopid asc";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(orderid);
			SSTableModel model = sh.executeSelect(con, 0, 99999);

			ih = new SSInsertHelper("RTL_WX_RESULTOK");
			ih.bindSequence("resultid", "seq_rtl_wx_resultok");
			ih.bindParam("onlineorderid", orderid);
			// 设备号
			ih.bindParam("deviceinfo", model.getItemValue(0, "shopid"));
			// 门店id
			ih.bindParam("shopid", model.getItemValue(0, "shopid"));
			// 支付类型
			ih.bindParam("paytype", "1");
			// 订单金额
			ih.bindParam("totalfee", SSDecimalHelper.divide(totalfee, 100 + "", 2));
			// 支付金额
			ih.bindParam("cashfee", SSDecimalHelper.divide(cashfee, 100 + "", 2));
			// 业务结果
			ih.bindParam("resultcode", "2");
			// 微信支付订单号
			ih.bindParam("transactionid", transactionid);
			// 交易完成时间
			ih.bindParam("timeend", timeend);
			ih.bindParam("appid", appid);
			ih.bindParam("mchid", mchid);
			// 签名
			ih.bindParam("sign", sign);
			ih.bindParam("inputmanid", "9");
			// 备注
			ih.bindParam("memo", "13");
			ih.bindSysdate("credate");
			ih.executeInsert(con);
			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 执行赠券策略生成代金券流水,并将代金券状态改为正式
	 * 
	 * @param prescriptionid
	 * @throws Exception
	 */
	public void generateCouponLst(String orderid) {
		Connection con = null;
		SSSelectHelper sh = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select prescriptionid from RTL_PRESCRIPTION where orderid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(orderid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			String prescriptionid = "";
			if (model.getRowCount() > 0) {
				prescriptionid = model.getItemValue(0, "prescriptionid");
			}
			String lstsql = "select couponid,publishamount,shopid from RTL_CASHCOUPON where sourceid = ?";
			sh = new SSSelectHelper(lstsql);
			sh.bindParam(prescriptionid);
			SSTableModel couponlstModel = sh.executeSelect(con, 0, 1);
			logger.info("支付完成后药方id--" + prescriptionid);
			if (couponlstModel.getRowCount() > 0) {
				logger.info("支付完成后修改代金券状态-----------------");
				// 将代金券状态改为正式
				sql = "update RTL_CASHCOUPON set usestatus = 1 where sourceid = ?";
				SSUpdateHelper uh = new SSUpdateHelper(sql);
				uh.bindParam(prescriptionid);
				uh.executeUpdate(con);
				// 生成代金券流水
				SSInsertHelper ssih = new SSInsertHelper("rtl_cashcoupon_lst");
				ssih.bindSequence("lstid", "seq_rtl_cashcoupon_lst");
				ssih.bindParam("couponid", couponlstModel.getItemValue(0, "couponid"));
				ssih.bindParam("amount", couponlstModel.getItemValue(0, "publishamount"));
				ssih.bindParam("shopid", couponlstModel.getItemValue(0, "shopid"));
				ssih.bindParam("inputmanid", "9");
				ssih.bindParam("sourceid", prescriptionid);
				// 业务类型为零售发放
				ssih.bindParam("comefrom", "51");
				ssih.bindSysdate("credate");
				ssih.executeInsert(con);
			}
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
			DataConnection.rollback(con);
		} finally {
			DataConnection.close(con);
		}

	}

	/**
	 * 发送请求用来公众号给用户发模板消息
	 * 
	 * @param orderid
	 * @param openid
	 */
	public void sendMsg(String orderid, String openid, String paymethod, String totalamount) {
		try {
			logger.info("调用公众号发消息方法--------------");
			Map<String, String> map = new HashMap<String, String>();
			String url = "http://www.shinesend.com/grape_thirdweixin/MessagePush.do";
			map.put("orderid", orderid);
			map.put("openid", openid);
			// String httpsRequest = CommonUtil.httpsRequest(url, "POST",
			// WXPayUtil.mapToXml(map));
			String appid = Parameter.getParameter("appid");
			// String sendPost = HttpUtil.sendPost(url, "openid=" + openid +
			// "&orderid=" + orderid + "&paymethod="
			// + paymethod + "&appid=" + appid + "&totalamount=" + totalamount);
			// logger.info("调用公众号发消息返回结果----" + sendPost);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		}
	}
}
