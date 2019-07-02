package com.dao;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.bean.EventInfo;
import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;
import com.util.Parameter;

/**
 * 活动帮助类
 * 
 * @author KF_04
 * 
 */
public class EventHelper {
	
	private Logger logger = Logger.getLogger(EventHelper.class);

	/**
	 * 获取活动详情
	 * 
	 * @param eventid
	 * @return
	 */
	public EventInfo getEventInfo(String eventid) {
		Connection con = null;
		SSSelectHelper sh = null;
		File file = null;
		EventInfo eventinfo = null;
		try {
			con = DataConnection.getConnection();
			eventinfo = new EventInfo();
			String sql = "select eventname,eventdesc,linktype,goodsid,linkeventid,deflink,goodsname from V_APP_EVENT where eventid = ?";
			sh = new SSSelectHelper(sql);
			sh.bindParam(eventid);
			SSTableModel model = sh.executeSelect(con, 0, 1);
			if (model.getRowCount() <= 0) {
				return eventinfo;
			}
			eventinfo.setEventid(eventid);
			eventinfo.setEventname(model.getItemValue(0, "eventname"));
			eventinfo.setEventdesc(model.getItemValue(0, "eventdesc"));
			eventinfo.setLinktype(model.getItemValue(0, "linktype"));
			eventinfo.setGoodsid(model.getItemValue(0, "goodsid"));
			eventinfo.setLinkeventid(model.getItemValue(0, "linkeventid"));
			eventinfo.setDeflink(model.getItemValue(0, "deflink"));
			eventinfo.setGoodsname(model.getItemValue(0, "goodsname"));
			eventinfo.setFolderpath(Parameter.getParameter("folderpath"));

			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\eventpic\\" + eventid;
			file = new File(pathname);
			List<String> filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int i = 0; i < fa.length; i++) {
					File fs = fa[i];
					if (fs.isDirectory()) {
					} else {
						filenamelist.add(fs.getName());
					}
				}
				Collections.sort(filenamelist);
			}
			eventinfo.setPiclist(filenamelist);
			return eventinfo;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error", e);
		} finally {
			DataConnection.close(con);
		}
		return eventinfo;
	}
}
