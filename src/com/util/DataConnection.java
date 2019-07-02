package com.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.shinesend.platform.logger.SSLogger;

/**
 * @author 作者:liujianyi
 * 
 *         WMS-RF平台数据库连接类
 * 
 * @author 2017年9月6日下午2:54:57
 */

public class DataConnection {

	/**
	 * 得到数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */

	public static Connection getConnection() throws Exception {
		// 正式运行模式
		return conn();
	}

	// private static Connection conn() throws Exception {
	// // ApplicationContext ac = new
	// // ClassPathXmlApplicationContext("applicationContext.xml");
	// // JdbcTemplate ds = (JdbcTemplate) ac.getBean("jt");
	// // Connection con = ds.getDataSource().getConnection();
	// Context ctx = new InitialContext();
	// DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/mall");
	// Connection con = ds.getConnection();
	// con.setAutoCommit(false);
	// return con;
	// }
	private static Connection conn() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/grape_mall");
		Connection con = ds.getConnection();
		con.setAutoCommit(false);
		return con;
	}

	/**
	 * 回退数据库连接
	 * 
	 * @param con
	 */
	public static void rollback(Connection con) {
		try {
			if (con != null)
				con.rollback();
		} catch (SQLException e) {
			SSLogger.getLogger().error("con rollback error:" + e.getMessage(), e);
		}
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param con
	 */
	public static void close(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {
			SSLogger.getLogger().error("con close error:" + e.getMessage(), e);
		}
	}

}
