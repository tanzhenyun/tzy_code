package com.dao;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.shinesend.helper.MD5Encoding;
import com.shinesend.helper.SSDecimalHelper;
import com.shinesend.helper.SSInsertHelper;
import com.shinesend.helper.SSModelHelper;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.helper.SSUpdateHelper;
import com.shinesend.helper.pyhelper.SSPinYinHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;
import com.util.Pic;

/**
 * 生活服务帮助类
 * 
 * @author KF_04
 */
public class LifeServiceHelper {

	private Logger logger = Logger.getLogger(LifeServiceHelper.class);

	/**
	 * 查询分类信息
	 * 
	 * @return
	 */
	public List<Object> getClassInfo() {
		Connection con = null;
		SSSelectHelper sh = null;
		List<Object> list = new ArrayList<Object>();
		try {
			con = DataConnection.getConnection();
			// 查询分类
			String sql = "select servicetypeid,servicetype from GLB_SERVICE_TYPE order by sort";
			sh = new SSSelectHelper(sql);
			SSTableModel model = sh.executeSelect(con, 0, 9999);
			for (int i = 0, c = model.getRowCount(); i < c; i++) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("servicetypeid", model.getItemValue(i, "servicetypeid"));
				m.put("servicetype", model.getItemValue(i, "servicetype"));
				list.add(m);
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
	 * 查询商户
	 * 
	 * @param searchval
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<Object> getAll(String searchval, String text, String skip, String limit) {
		Connection con = null;
		SSSelectHelper sh = null;
		List<Object> list = new ArrayList<Object>();
		Pic pic = new Pic();
		try {
			con = DataConnection.getConnection();
			String sql = "select lifeserviceid,companyname,username,tel,tel1,companywx,attachmentid,"
					+ "provincename,cityname,countyname,address "
					+ " from V_GLB_LIFE_SERVICE where usestatus = 1 and approvestatus = 2 ";
			if (!"".equals(searchval)) {
				sql += " and servicetypeid = " + searchval;
			}
			if (!"".equals(text)) {
				sql += " and (companyname like '%" + text.toUpperCase() + "%' or companyshortname like '%"
						+ text.toUpperCase() + "%' or companynamemcode like '%" + text.toUpperCase() + "%')";
			}
			sh = new SSSelectHelper(sql);
			SSTableModel model1 = sh.executeSelect(con, Integer.parseInt(skip), Integer.parseInt(limit));
			for (int i = 0, c = model1.getRowCount(); i < c; i++) {
				Map<String, String> m1 = new HashMap<String, String>();
				m1.put("lifeserviceid", model1.getItemValue(i, "lifeserviceid"));
				m1.put("companyname", model1.getItemValue(i, "companyname"));
				m1.put("username", model1.getItemValue(i, "username"));
				m1.put("tel", model1.getItemValue(i, "tel"));
				m1.put("tel1", model1.getItemValue(i, "tel1"));
				// m1.put("shopid", model1.getItemValue(i, "shopid"));
				m1.put("companywx", model1.getItemValue(i, "companywx"));
				m1.put("attachmentid", model1.getItemValue(i, "attachmentid"));
				m1.put("provincename", model1.getItemValue(i, "provincename"));
				if (model1.getItemValue(i, "provincename").indexOf("内蒙古自治区") > -1) {
					m1.put("provincename", "");
				}
				m1.put("cityname", model1.getItemValue(i, "cityname"));
				if (model1.getItemValue(i, "cityname").indexOf("赤峰") > -1) {
					m1.put("cityname", "");
				}
				m1.put("countyname", model1.getItemValue(i, "countyname"));
				m1.put("address", model1.getItemValue(i, "address"));
				if (model1.getItemValue(i, "address").indexOf("赤峰市") > -1) {
					m1.put("address", model1.getItemValue(i, "address").substring(9));
				}
				String picname = pic.getShopPic(model1.getItemValue(i, "attachmentid"));
				m1.put("picname", picname);
				m1.put("folderpath", Parameter.getParameter("folderpath"));

				list.add(m1);
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
	 * 申请入驻保存
	 * 
	 * @param companyname
	 * @param companyshortname
	 * @param servicetypeid
	 * @param username
	 * @param tel
	 * @param tel1
	 * @param address
	 * @param memo
	 * @return
	 */
	public String saveBusiness(MultipartFile multipartFile, String companyname, String companyshortname,
			String servicetypeid, String username, String tel, String tel1, String provinceid, String cityid,
			String countyid, String address, String wxnumber, String memo) {
		Connection con = null;
		SSInsertHelper ih = null;
		String executeInsert = "";
		try {
			con = DataConnection.getConnection();
			// 获取文件名
			String picpath = Parameter.getParameter("picpath");
			String name = "";
			String attachmentid = "";
			long size = 0;
			String fileName = null;
			String filemd5 = null;
			String subffix = null;
			if (multipartFile != null) {
				size = multipartFile.getSize();
				logger.info(size);
				if (size > 10485760) {// 文件设置大小，我这里设置10M。
					System.err.println("太大了！！！！但是还是上传上去了哈哈哈");
					logger.info("太大了！！！！但是还是上传上去了哈哈哈");
					throw new Exception("图片太大!");
				}
				// 直接返回文件的名字
				name = multipartFile.getOriginalFilename();
				// 我这里取得文件后缀
				subffix = name.substring(name.lastIndexOf(".") + 1, name.length());
				// 文件保存进来，重新命名，数据库保存有原本的名字，所以输出的时候再把他附上原本的名字就行了。
				fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				attachmentid = SSModelHelper.getSequenceValue(con, "SEQ_SYS_ATTACHMENT");
				String filepath = picpath + "\\附件存储\\" + "\\" + attachmentid;
				File file = new File(filepath);
				// 目录不存在就创建
				if (!file.exists()) {
					file.mkdirs();
				}
				// 保存文件
				multipartFile.transferTo(new File(file + "\\" + fileName + "." + subffix));
				String realpath = file + "\\" + fileName + "." + subffix;
				System.out.println(realpath + "----");
				logger.info("地址------" + realpath);
				filemd5 = MD5Encoding.encoding(realpath);

			}

			ih = new SSInsertHelper("GLB_LIFE_SERVICE");
			// ih.bindSequence("lifeserviceid", "SEQ_GLB_LIFE_SERVICE");
			String lifeserviceid = SSModelHelper.getSequenceValue(con, "SEQ_GLB_LIFE_SERVICE");
			ih.bindParam("lifeserviceid", lifeserviceid);
			ih.bindParam("companyname", companyname);
			ih.bindParam("companynamemcode", SSPinYinHelper.getStringFirstPinYin(companyname).toUpperCase());
			ih.bindParam("companyshortname", companyshortname);
			ih.bindParam("servicetypeid", servicetypeid);
			ih.bindParam("username", username);
			if (!"".equals(attachmentid)) {
				ih.bindParam("attachmentid", attachmentid);
			}
			if (!"".equals(tel)) {
				ih.bindParam("tel", tel);
			}
			if (!"".equals(tel1)) {
				ih.bindParam("tel1", tel1);
			}
			if (!"".equals(wxnumber)) {
				ih.bindParam("companywx", wxnumber);
			}
			// if (!"".equals(provinceid)) {
			// ih.bindParam("provinceid", provinceid);
			// }
			// if (!"".equals(cityid)) {
			// ih.bindParam("cityid", cityid);
			// }
			// if (!"".equals(countyid)) {
			// ih.bindParam("countyid", countyid);
			// }
			ih.bindParam("address", address);
			ih.bindParam("memo", memo);
			ih.bindParam("approvestatus", "1");
			ih.bindParam("usestatus", "1");
			ih.bindParam("inputmanid", "0");
			ih.bindSysdate("credate");
			executeInsert = ih.executeInsert(con) + "";

			if (!"".equals(attachmentid)) {
				ih = new SSInsertHelper("sys_attachment");
				ih.bindParam("attachmentid", attachmentid);
				ih.bindParam("reletetable", "GLB_LIFE_SERVICE");
				ih.bindParam("releteid", lifeserviceid);
				ih.bindSysdate("credate");
				ih.bindParam("inputmanid", "0");
				ih.executeInsert(con);

				String fileid = SSModelHelper.getSequenceValue(con, "seq_sys_files");
				// 插附件文件表
				ih = new SSInsertHelper("sys_attachment_file");
				ih.bindSequence("attachmentfileid", "seq_sys_attachment_file");
				ih.bindParam("attachmentid", attachmentid);
				ih.bindParam("fileid", fileid);
				ih.bindParam("usestatus", "1");
				ih.bindSysdate("credate");
				ih.bindParam("inputmanid", "0");
				ih.executeInsert(con);

				// 更新附件信息
				String sql = "update sys_attachment_file set filename = ?,filesize = ?,mdcode = ?,"
						+ "filelength = ?,storagemode = ?,fileurl = ? where fileid = ?";
				SSUpdateHelper uh = new SSUpdateHelper(sql);
				uh.bindParam(fileName + "." + subffix);
				uh.bindParam(size + "");
				uh.bindParam(filemd5);
				uh.bindParam(size + "");
				uh.bindParam("2");
				uh.bindParam(picpath + "\\附件存储");
				uh.bindParam(fileid);
				uh.executeUpdate(con);
			}

			if ("1".equals(executeInsert)) {
				executeInsert = "";
			} else {
				executeInsert = "保存失败!";
			}
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			executeInsert = e.getMessage();
			DataConnection.rollback(con);
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return executeInsert;
	}

	/**
	 * 查询省市区
	 * 
	 * @return
	 */
	public Map<String, Object> getArea() {
		Connection con = null;
		SSSelectHelper sh = null;
		SSTableModel provmodel = null;
		SSTableModel citymodel = null;
		SSTableModel countymodel = null;
		Map<String, Object> map = new HashMap<String, Object>();
		// 省份
		List<Object> list = new ArrayList<Object>();
		// 市
		List<Object> ll1 = new ArrayList<Object>();
		// 区
		List<Object> ll2 = new ArrayList<Object>();

		try {
			con = DataConnection.getConnection();
			String sql = "select t.provinceid,t.provincename from GLB_PROVINCE t order by t.provinceid";
			sh = new SSSelectHelper(sql);
			provmodel = sh.executeSelect(con, 0, 9999);

			for (int i = 0, c = provmodel.getRowCount(); i < c; i++) {
				// List<String> prov = Arrays.asList(provmodel.getItemValue(i,
				// "provinceid"),
				// provmodel.getItemValue(i, "provincename"));
				String[] x = { provmodel.getItemValue(i, "provinceid"), provmodel.getItemValue(i, "provincename") };
				list.add(x);

				sql = "select t.cityid, t.cityname from GLB_CITY t where t.provinceid = ? order by t.cityid";
				sh = new SSSelectHelper(sql);
				sh.bindParam(provmodel.getItemValue(i, "provinceid"));
				citymodel = sh.executeSelect(con, 0, 99999);

				List<Object> list1 = new ArrayList<Object>();
				List<Object> list3 = new ArrayList<Object>();
				for (int j = 0, l = citymodel.getRowCount(); j < l; j++) {
					// List<String> city =
					// Arrays.asList(citymodel.getItemValue(j, "cityid"),
					// citymodel.getItemValue(j, "cityname"));
					String[] y = { citymodel.getItemValue(j, "cityid"), citymodel.getItemValue(j, "cityname") };
					list1.add(y);

					sql = "select t.countyid, t.countyname from GLB_COUNTY t where t.cityid = ? order by t.countyid";
					sh = new SSSelectHelper(sql);
					sh.bindParam(citymodel.getItemValue(j, "cityid"));
					countymodel = sh.executeSelect(con, 0, 99999);
					List<Object> list2 = new ArrayList<Object>();
					for (int k = 0, s = countymodel.getRowCount(); k < s; k++) {
						String[] z = { countymodel.getItemValue(k, "countyid"),
								countymodel.getItemValue(k, "countyname") };
						list2.add(z);
					}

					list3.add(list2);
				}
				ll1.add(list1);
				ll2.add(list3);
			}

			map.put("prov", list);
			map.put("city", ll1);
			map.put("county", ll2);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return map;
	}

}
