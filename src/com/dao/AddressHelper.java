package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.AddressInfo;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;

/**
 * 收货地址帮助类
 * 
 * @author KF_04
 * 
 */
public class AddressHelper {

	private Logger logger = Logger.getLogger(AddressHelper.class);

	/**
	 * 获取所有收货地址
	 * 
	 * @param userid
	 * @return
	 */
	public List<AddressInfo> getAddress(String userid) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<AddressInfo> list = null;
		AddressInfo addressinfo = null;
		try {
			con = DataConnection.getConnection();
			list = new ArrayList<AddressInfo>();

			// 查询状态为1 的收货地址.1为正式2为作废
			String sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and userid = ? order by appreceiveaddressid";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 99999);
			if (model.getRowCount() <= 0) {
				return list;
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				addressinfo = new AddressInfo();
				addressinfo.setAppreceiveaddressid(model.getItemValue(i, "appreceiveaddressid"));
				addressinfo.setReceivename(model.getItemValue(i, "receivename"));
				addressinfo.setPhonenumber(model.getItemValue(i, "phonenumber"));
				addressinfo.setProvince(model.getItemValue(i, "province"));
				addressinfo.setCity(model.getItemValue(i, "city"));
				addressinfo.setArea(model.getItemValue(i, "area"));
				addressinfo.setAddress(model.getItemValue(i, "address"));
				addressinfo.setSign(model.getItemValue(i, "defsign"));
				list.add(addressinfo);
			}
			con.commit();
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
			return list;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 删除收货地址
	 * 
	 * @param id
	 * @return
	 */
	public int deleteAddress(String userid, String id) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			// 当前条状态置为作废
			String sql = "update APP_RECEIVE_ADDRESS set usestatus = 2 where appreceiveaddressid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(id);
			influence = uh.executeUpdate(con);

			sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and defsign = 1 and userid = ? and appreceiveaddressid <> ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			sh.bindParam(id);
			SSTableModel defsignmodel = sh.executeSelect(con, 0, 1);
			if (defsignmodel.getRowCount() > 0) {
				influence = 1;
			} else {
				sql = "select min(appreceiveaddressid) minid from APP_RECEIVE_ADDRESS where usestatus = 1 and userid = ? and appreceiveaddressid <> ?";
				sh = new SSSelectHelper(sql);
				sh.bindParam(userid);
				sh.bindParam(id);
				SSTableModel model = sh.executeSelect(con, 0, 1);
				String minid = model.getItemValue(0, "minid");

				sql = "update APP_RECEIVE_ADDRESS set defsign = 0 where usestatus = 1 and userid = ?";
				uh = new SSUpdateHelper(sql);
				uh.bindParam(userid);
				uh.executeUpdate(con);

				sql = "update APP_RECEIVE_ADDRESS set defsign = 1 where appreceiveaddressid = ?";
				uh = new SSUpdateHelper(sql);
				uh.bindParam(minid);
				uh.executeUpdate(con);
			}
			con.commit();
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			return influence;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 新增收货地址
	 * 
	 * @param userid
	 * @param receivename
	 * @param phonenumber
	 * @param province
	 * @param city
	 * @param area
	 * @param address
	 * @return
	 */
	public int insertAddress(String userid, String memberid, String receivename, String phonenumber, String province,
			String city, String area, String address, String orderflag) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSInsertHelper ih = null;
		int influence = 0;
		String defsign = "0";
		try {
			con = DataConnection.getConnection();
			// 检查是否存在收货地址没有将新增的地址设置为默认地址
			String sql = "select * from APP_RECEIVE_ADDRESS where usestatus = 1 and userid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 99999);
			if (model.getRowCount() <= 0) {
				defsign = "1";
			}

			ih = new SSInsertHelper("APP_RECEIVE_ADDRESS");
			// ih.bindSequence("appreceiveaddressid",
			// "seq_app_receive_address");
			String appreceiveaddressid = SSModelHelper.getSequenceValue(con, "seq_app_receive_address");
			ih.bindParam("appreceiveaddressid", appreceiveaddressid);
			ih.bindParam("userid", userid);
//			ih.bindParam("memberid", memberid);
			ih.bindParam("receivename", receivename);
			ih.bindParam("phonenumber", phonenumber);
			ih.bindParam("province", province);
			ih.bindParam("city", city);
			ih.bindParam("area", area);
			ih.bindParam("address", address);
			ih.bindParam("defsign", defsign);
			ih.bindParam("usestatus", "1");
			influence = ih.executeInsert(con);
			con.commit();
			if ("2".equals(orderflag)) {
				setDefAddress(userid, appreceiveaddressid);
			}
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			return influence;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 订单页面切换收货地址
	 * 
	 * @param id
	 * @return
	 */
	public int changeAdress(String id) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int setDefAddress = 0;
		try {
			con = DataConnection.getConnection();
			int influence = 0;
			con = DataConnection.getConnection();
			String sql = "update APP_RECEIVE_ADDRESS set defsign = 1 where appreceiveaddressid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(id);
			influence = uh.executeUpdate(con);
			con.commit();
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			DataConnection.rollback(con);
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return setDefAddress;
	}

	/**
	 * 获取修改收货地址信息
	 * 
	 * @param id
	 * @return
	 */
	public AddressInfo getUpdateAdressInfo(String id) {
		Connection con = null;
		SSSelectHelper sh = null;
		AddressInfo addressinfo = null;
		try {
			con = DataConnection.getConnection();
			addressinfo = new AddressInfo();
			String sql = "select * from APP_RECEIVE_ADDRESS where appreceiveaddressid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(id);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			addressinfo.setAppreceiveaddressid(id);
			addressinfo.setReceivename(model.getItemValue(0, "receivename"));
			addressinfo.setPhonenumber(model.getItemValue(0, "phonenumber"));
			addressinfo.setProvince(model.getItemValue(0, "province"));
			addressinfo.setCity(model.getItemValue(0, "city"));
			addressinfo.setArea(model.getItemValue(0, "area"));
			addressinfo.setAddress(model.getItemValue(0, "address"));
			addressinfo.setSign(model.getItemValue(0, "defsign"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return addressinfo;
	}

	/**
	 * 保存修改后的收货地址
	 * 
	 * @param id
	 * @param receivename
	 * @param phonenumber
	 * @param province
	 * @param city
	 * @param area
	 * @param address
	 * @return
	 */
	public int saveUpdateAddress(String id, String receivename, String phonenumber, String province, String city,
			String area, String address) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "update APP_RECEIVE_ADDRESS set phonenumber = ?,province = ?, "
					+ "city = ?,area =?,address = ?,receivename = ? where appreceiveaddressid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(phonenumber);
			uh.bindParam(province);
			uh.bindParam(city);
			uh.bindParam(area);
			uh.bindParam(address);
			uh.bindParam(receivename);
			uh.bindParam(id);
			influence = uh.executeUpdate(con);
			con.commit();
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
			return influence;
		} finally {
			DataConnection.close(con);
		}
	}

	/**
	 * 修改默认收获地址
	 * 
	 * @param id
	 * @return
	 */
	public int setDefAddress(String userid, String id) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "update APP_RECEIVE_ADDRESS set defsign = 0 where userid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(userid);
			uh.executeUpdate(con);
			sql = "update APP_RECEIVE_ADDRESS set defsign = 1 where appreceiveaddressid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(id);
			influence = uh.executeUpdate(con);
			con.commit();
			return influence;
		} catch (Exception e) {
			// TODO: handle exception
			DataConnection.rollback(con);
			e.printStackTrace();
			logger.error("error", e);
			return influence;
		} finally {
			DataConnection.close(con);
		}
	}
}
