package com.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.shinesend.helper.SSSelectHelper;
import com.shinesend.ste.SSTableModel;
import com.util.DataConnection;

public class AskHelper {

	private Logger logger = Logger.getLogger(AskHelper.class);

	public List getService() {
		Connection con = null;
		SSSelectHelper sh = null;
		List list = new ArrayList();

		try {
			con = DataConnection.getConnection();
			String sql = "select a.userid,a.username,b.sessionid from SYS_USER a, SYS_ONLINE_USER_SERVICE b where a.userid = b.userid";
			sh = new SSSelectHelper(sql);
			SSTableModel serviceModel = sh.executeSelect(con, 0, 9999);
			if (serviceModel.getRowCount() < 0) {
				return list;
			}
			for (int i = 0, count = serviceModel.getRowCount(); i < count; i++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userid", serviceModel.getItemValue(i, "userid"));
				map.put("username", serviceModel.getItemValue(i, "username"));
				map.put("sessionid", serviceModel.getItemValue(i, "sessionid"));
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

}
