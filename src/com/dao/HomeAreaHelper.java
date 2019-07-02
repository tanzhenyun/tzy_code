package com.dao;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.HomeAreaDoc;
import com.bean.HomeAreaDtl;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;

/**
 * 首页活动帮助类
 * 
 * @author KF_04
 * 
 */
public class HomeAreaHelper {

	private Logger logger = Logger.getLogger(HomeAreaHelper.class);

	/**
	 * 获取所有活动
	 * 
	 * @return
	 */
	public List<HomeAreaDoc> getAllActivities() {
		Connection con = null;
		SSSelectHelper sh = null;
		List<HomeAreaDoc> doclist = null;
		List<HomeAreaDtl> dtllist = null;
		HomeAreaDoc doc = null;
		HomeAreaDtl dtl = null;
		SSTableModel docmodel = null;
		SSTableModel dtlmodel = null;
		List<String> filenamelist = null;
		File file = null;
		try {
			con = DataConnection.getConnection();
			doclist = new ArrayList<HomeAreaDoc>();
			String sql = "SELECT homeareadocid, homeareaname,(SELECT count(1) FROM APP_HOME_AREA_DTL b "
					+ " WHERE a.homeareadocid = b.homeareadocid) count, position, height,titleflag "
					+ " FROM APP_HOME_AREA_DOC a WHERE usestatus = 1 and (floatflag <> 1 or floatflag is null) "
					+ " ORDER BY sortno ";
			sh = new SSSelectHelper(sql);
			docmodel = sh.executeSelect(con, 0, 99999);
			String homeareadocid = "";
			if (docmodel.getRowCount() <= 0) {
				return null;
			}
			String folderpath = Parameter.getParameter("folderpath");
			for (int i = 0; i < docmodel.getRowCount(); i++) {
				doc = new HomeAreaDoc();
				int piccount = 0;
				dtllist = new ArrayList<HomeAreaDtl>();
				homeareadocid = docmodel.getItemValue(i, "homeareadocid");
				doc.setHomeareadocid(homeareadocid);
				doc.setHomeareaname(docmodel.getItemValue(i, "homeareaname"));
				doc.setPosition(docmodel.getItemValue(i, "position"));
				doc.setHeight(docmodel.getItemValue(i, "height"));
				doc.setTitleflag(docmodel.getItemValue(i, "titleflag"));

				sql = "select homeareadtlid,linktype,deflink,goodsid,goodsname,eventid,spruleid "
						+ " from V_APP_HOME_AREA_DTL" + " where homeareadocid = ? order by homeareadtlid";
				sh = new SSSelectHelper(sql);
				sh.bindParam(homeareadocid);
				dtlmodel = sh.executeSelect(con, 0, 99999);
				if (dtlmodel.getRowCount() <= 0) {
				} else {
					for (int j = 0; j < dtlmodel.getRowCount(); j++) {
						dtl = new HomeAreaDtl();
						dtl.setFolderpath(folderpath);
						dtl.setHomeareadtlid(dtlmodel.getItemValue(j, "homeareadtlid"));
						dtl.setLinktype(dtlmodel.getItemValue(j, "linktype"));
						dtl.setDeflink(dtlmodel.getItemValue(j, "deflink"));
						dtl.setGoodsid(dtlmodel.getItemValue(j, "goodsid"));
						dtl.setGoodsname(dtlmodel.getItemValue(j, "goodsname"));
						dtl.setEventid(dtlmodel.getItemValue(j, "eventid"));
						dtl.setSpruleid(dtlmodel.getItemValue(j, "spruleid"));
						if ("4".equals(dtlmodel.getItemValue(j, "linktype"))) {
							// 促销
							sql = "select spruleid, couponamount, validdate, a.userange "
									+ " from PR_RETAILSALE_SP_RULE a, PR_RETAILSALE_SP b "
									+ " where a.spid = b.spid and b.usestatus = 1 and b.userange in(0, 2) "
									+ " and sysdate between b.startdate and b.enddate + 1 "
									+ " and to_number(to_char(sysdate, 'hh24')) + 1 BETWEEN b.starttime "
									+ " AND b.endtime and a.sptype = 6 and a.usestatus = 1 and a.spruleid = ?";
							sh = new SSSelectHelper(sql);
							sh.bindParam(dtlmodel.getItemValue(j, "spruleid"));
							SSTableModel spmodel = sh.executeSelect(con, 0, 1);
							if (spmodel.getRowCount() > 0) {
								dtl.setCouponamount(spmodel.getItemValue(0, "couponamount"));
								dtl.setValiddate(spmodel.getItemValue(0, "validdate"));
								dtl.setUserange(spmodel.getItemValue(0, "userange"));
							}
						}

						String picpath = Parameter.getParameter("picpath");
						String pathname = picpath + "\\ecpic\\echomepic\\" + dtlmodel.getItemValue(j, "homeareadtlid");
						file = new File(pathname);
						filenamelist = new ArrayList<String>();
						// 递归求取目录文件个数
						File flist[] = file.listFiles();
						if (flist == null) {
						} else {
							file = new File(pathname);
							File fa[] = file.listFiles();
							for (int k = 0; k < fa.length; k++) {
								File fs = fa[k];
								if (fs.isDirectory()) {
								} else {
									filenamelist.add(fs.getName());
								}
							}
							Collections.sort(filenamelist);
						}

						piccount += filenamelist.size();
						dtl.setPiclist(filenamelist);
						dtllist.add(dtl);
					}
				}
				doc.setPiccount(piccount);
				doc.setDtllist(dtllist);

				doclist.add(doc);
			}
			return doclist;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return null;
	}
}
