package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bean.User;
import com.shinesend.helper.MD5Encoding;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;

/**
 * 登录注册帮助类
 * 
 * @author KF_04
 * 
 */
public class LoginHelper {

	private Logger logger = Logger.getLogger(LoginHelper.class);

	/**
	 * 注册
	 * 
	 * @param phonenumber
	 * @param password
	 * @return
	 */
	public List<User> register(String phonenumber) {
		Connection con = null;
		List<User> list = new ArrayList<User>();
		User user = null;
		try {
			con = DataConnection.getConnection();
			// 检查会员信息里是否存在
			String sql = "select * from RTL_MEMBER where usestatus = 1 and mobile = ? ";
			SSSelectHelper sh = new SSSelectHelper(sql);
			sh.bindParam(phonenumber);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			if (model.getRowCount() > 0) {
				for (int i = 0; i < model.getRowCount(); i++) {
					user = new User();
					user.setMemberid(model.getItemValue(i, "memberid"));
					user.setMemberno(model.getItemValue(i, "memberno"));
					user.setMenbername(model.getItemValue(i, "membername"));
					user.setCurpoint(model.getItemValue(i, "curpoint"));
					if (model.getItemValue(0, "amount").equals("")) {
						user.setAmount("0");
					} else {
						user.setAmount(model.getItemValue(i, "amount"));
					}
					list.add(user);
				}
			} else {
				return list;
			}
			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return list;
	}

	/**
	 * 绑定会员卡
	 * 
	 * @param memberid
	 * @param phonenumber
	 * @param openid
	 * @param access_token
	 * @return
	 */
	public User bindMember(String memberid, String phonenumber, String openid, String access_token) {
		Connection con = null;
		SSInsertHelper ih = null;
		SSUpdateHelper uh = null;
		User userInfo = null;
		try {
			con = DataConnection.getConnection();
			if (openid != null || !"".equals(openid)) {
				logger.info("*********绑定会员卡没有微信openid***********");

			}
			// memberid为空是新建卡
			String appuserid = SSModelHelper.getSequenceValue(con, "SEQ_APP_USER");
			if ("".equals(memberid) || memberid == null) {
				memberid = SSModelHelper.getSequenceValue(con, "SEQ_RTL_MEMBER");
				ih = new SSInsertHelper("RTL_MEMBER");
				ih.bindParam("memberid", memberid);
				ih.bindParam("membername", "微信用户");
				ih.bindParam("memberno", "DS" + memberid);
				ih.bindParam("mobile", phonenumber);
				ih.bindParam("membertypeid", "0");
				ih.bindParam("usestatus", "1");
				ih.bindParam("curpoint", "0");
				ih.bindParam("inputmanid", "9");
				ih.bindParam("memo", appuserid);
				ih.bindParam("wxopenid", openid);
				ih.bindParam("shopid", "-1");
				ih.bindSysdate("credate");
				ih.bindSysdate("binddate");
				ih.bindSysdate("issuancedate");
				ih.executeInsert(con);
			} else {
				String sql = "update RTL_MEMBER set wxopenid = ?,memo = ?,binddate = sysdate where memberid = ?";
				uh = new SSUpdateHelper(sql);
				uh.bindParam(openid);
				uh.bindParam(appuserid);
				uh.bindParam(memberid);
				uh.executeUpdate(con);
			}
			ih = new SSInsertHelper("APP_USER");
			ih.bindParam("appuserid", appuserid);
			ih.bindParam("phonenumber", phonenumber);
			ih.bindParam("wxopenid", openid);
			ih.bindParam("memberid", memberid);
			ih.bindSysdate("credate");
			ih.executeInsert(con);
			con.commit();

			userInfo = getUserInfo(appuserid, access_token);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return userInfo;
	}

	/**
	 * 检查用户是否存在
	 * 
	 * @param phonenumber
	 * @return
	 */
	public int checkPhonenumber(String phonenumber) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel model = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from app_user where phonenumber = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(phonenumber);
			model = sh.executeSelect(con, 0, 10);
			if (model.getRowCount() <= 0) {
				return -1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return model.getRowCount();
	}

	/**
	 * 检查用户是否存在
	 * 
	 * @param phonenumber
	 * @return
	 */
	public int checkUserByOpenid(String openid) {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel model = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from app_user where wxopenid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(openid);
			model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0)
				return -1;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return model.getRowCount();
	}

	/**
	 * 重置密码
	 * 
	 * @param phonenumber
	 * @param password
	 * @return
	 */
	public int resetPassword(String phonenumber, String password) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "update app_user set password = ? where phonenumber = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(password);
			uh.bindParam(phonenumber);
			influence = uh.executeUpdate(con);
			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return influence;
	}

	/**
	 * 查询用户信息
	 * 
	 * @param
	 * @return
	 */
	public User getUserInfo(String userid, String acscess_token) {
		Connection con = null;
		SSSelectHelper sh = null;
		User user = new User();
		JSONObject json = null;
		try {
			con = DataConnection.getConnection();
			String sql = "select * from app_user where appuserid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(userid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				return user;
			}
			user.setUserid(model.getItemValue(0, "appuserid"));
			user.setPhonenumber(model.getItemValue(0, "phonenumber"));
			user.setWxopenid(model.getItemValue(0, "wxopenid"));
			String wxuser = new WxHelper().getWxUserInfo(acscess_token, model.getItemValue(0, "wxopenid"));

			json = JSON.parseObject(wxuser);
			String wxnickname = (String) json.get("nickname");
			String wxheadimgurl = (String) json.get("headimgurl");

			user.setWxnickname(wxnickname);
			user.setWxheadimgurl(wxheadimgurl);
			String memberid = model.getItemValue(0, "memberid");
			// 查询会员信息
			sql = "select * from RTL_MEMBER where memberid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			model = sh.executeSelect(con, 0, 1);
			user.setMemberid(memberid);
			user.setMemberno(model.getItemValue(0, "memberno"));
			if (model.getItemValue(0, "amount").equals("")) {
				user.setAmount("0");
			} else {
				user.setAmount(model.getItemValue(0, "amount"));
			}
			if (model.getItemValue(0, "curpoint").equals("")) {
				user.setCurpoint("0");
			} else {
				user.setCurpoint(model.getItemValue(0, "curpoint"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return user;
	}

	/**
	 * 根据微信acscess_token查询用户信息
	 * 
	 * @param phonenumber
	 * @return
	 */
	public User getUser(String accessinfo) {
		Connection con = null;
		SSSelectHelper sh = null;
		User user = new User();
		JSONObject json = null;
		WxHelper wh = null;
		try {
			wh = new WxHelper();
			con = DataConnection.getConnection();
			JSONObject jsonobject = JSONObject.parseObject(accessinfo);// 转成Json格式
			String access_token = (String) jsonobject.get("access_token");
			String openid = (String) jsonobject.get("openid");

			String sql = "select * from app_user where wxopenid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(openid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				return user;
			}
			user.setUserid(model.getItemValue(0, "appuserid"));
			user.setPhonenumber(model.getItemValue(0, "phonenumber"));
			user.setWxopenid(model.getItemValue(0, "wxopenid"));

			// 获取微信信息
			String wxuser = wh.getWxUserInfo(access_token, openid);
			json = JSON.parseObject(wxuser);

			System.out.println("微信用户信息-----" + json);
			logger.info("微信用户信息-----" + json);
			if (json.toString().indexOf("errcode") > 0) {
				System.out.println("获取微信用户信息错误----" + accessinfo);
			}
			String wxnickname = (String) json.get("nickname");
			String wxheadimgurl = (String) json.get("headimgurl");

			user.setWxnickname(wxnickname);
			user.setWxheadimgurl(wxheadimgurl);

			String memberid = model.getItemValue(0, "memberid");
			// 查询会员信息
			sql = "select memberid,memberno,amount,curpoint,membertypeid,membertypename from v_RTL_MEMBER where memberid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(memberid);
			model = sh.executeSelect(con, 0, 1);
			user.setMemberid(memberid);
			user.setMemberno(model.getItemValue(0, "memberno"));
			if (model.getItemValue(0, "amount").equals("")) {
				user.setAmount("0");
			} else {
				user.setAmount(model.getItemValue(0, "amount"));
			}
			if (model.getItemValue(0, "curpoint").equals("")) {
				user.setCurpoint("0");
			} else {
				user.setCurpoint(model.getItemValue(0, "curpoint"));
			}
			user.setMembertypeid(model.getItemValue(0, "membertypeid"));
			user.setMembertypename(model.getItemValue(0, "membertypename"));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return user;
	}

	/**
	 * 保存昵称
	 * 
	 * @param userid
	 * @param gender
	 * @return
	 */
	public int saveNickname(String appuserid, String nickname) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			String sql = "update app_user set nickname = ? where appuserid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(nickname);
			uh.bindParam(appuserid);
			influence = uh.executeUpdate(con);
			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return influence;
	}

	/**
	 * 修改手机号码
	 * 
	 * @param oldphonenumber
	 * @param phonenumber
	 * @return
	 */
	public int updatePhoneNumber(String appuserid, String phonenumber, String memberid) {
		Connection con = null;
		SSUpdateHelper uh = null;
		int influence = 0;
		try {
			con = DataConnection.getConnection();
			// 修改商城用户表手机号
			String sql = "update app_user set phonenumber = ? where appuserid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(phonenumber);
			uh.bindParam(appuserid);
			influence = uh.executeUpdate(con);
			// 修改会员资料手机号
			sql = "update rtl_member set mobile = ? where memberid = ?";
			uh = new SSUpdateHelper(sql);
			uh.bindParam(phonenumber);
			uh.bindParam(memberid);
			influence = uh.executeUpdate(con);

			con.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return influence;
	}

	/**
	 * 客服登录
	 * 
	 * @param userid
	 * @param password
	 * @return
	 */
	public User login(String userid, String password) {
		Connection con = null;
		User user = null;
		try {
			con = DataConnection.getConnection();
			user = new User();
			boolean checkpass = checkpass(con, userid, password);
			if (checkpass) {
				// 查用户信息
				String sql = "select * from v_sys_user where userid = ?";
				SSSelectHelper sh = new SSSelectHelper(sql);
				sh.bindParam(userid);
				SSTableModel usermodel = sh.executeSelect(con, 0, 1);
				user.setMemberid(usermodel.getItemValue(0, "userid"));
				user.setMenbername(usermodel.getItemValue(0, "username"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataConnection.close(con);
		}
		return user;
	}

	/**
	 * 检查用户与密码
	 * 
	 * @param con
	 * @param userid
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static boolean checkpass(Connection con, String userid, String password) throws Exception {
		String sql = "select password from sys_user where userid = ?";
		SSSelectHelper sh = new SSSelectHelper(sql);
		sh.bindParam(userid);
		SSTableModel usermodel = sh.executeSelect(con, 0, 1);
		if (usermodel.getRowCount() > 0) {
			password = MD5Encoding.encoding(password);
			String dbpassword = usermodel.getItemValue(0, "password");
			if (dbpassword == null || "".equals(dbpassword)) {
				dbpassword = MD5Encoding.encoding("");
			}
			return password.equals(dbpassword);
		}
		return false;
	}

}
