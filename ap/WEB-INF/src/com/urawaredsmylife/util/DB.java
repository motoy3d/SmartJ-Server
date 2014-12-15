package com.urawaredsmylife.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

/**
 * データベース(RDB)ユーティリティ
 * 
 * @author motoi, kosugi
 */
public class DB {
	/**
	 * ロガー
	 */
	public static Logger logger = Logger.getLogger(DB.class.getName());
	/**
	 * データソース(DBCP)
	 */
	private static BasicDataSource dataSource;

	/**
	 * コネクションを返す。主にトランザクション用途。 コミット・ロールバックは下記メソッドを使用する。
	 * DbUtils.commitAndCloseQuietly(connection);
	 * DbUtils.rollbackAndCloseQuietly(connection);
	 * 
	 * @param autoCommit
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(boolean autoCommit)
			throws SQLException {
		if (dataSource == null) {
			initDataSource();
		}
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(autoCommit);
		return conn;
	}

	/**
	 * DataSourceを初期化する
	 */
	private static void initDataSource() {
		// DB接続初期化処理
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error in DriverManager.registerDriver();", e);
		}

		dataSource = new BasicDataSource();

		ResourceBundle bundle = ResourceBundle.getBundle("app");
		String jdbcUrl = bundle.getString("jdbc.url");

		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(bundle.getString("jdbc.user"));
		dataSource.setPassword(bundle.getString("jdbc.pass"));
		
		dataSource.setMinIdle(0);
		dataSource.setMaxIdle(0); // "Invalid connection id"が発生するのを防止するため、プールしない
		dataSource.setPoolPreparedStatements(true);
	}

	/**
	 * QueryRunnerを返す。
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static QueryRunner createQueryRunner() throws SQLException {
		if (dataSource == null) {
			initDataSource();
		}
		// pmdKnownBroken(Parameter MetaDataが壊れている)=trueにしないとエラーが起きる
		return new QueryRunner(dataSource, true);
	}

	/**
	 * シングルクォートで囲んで返す
	 * @param s
	 * @return
	 */
	public static String quote(String s) {
		if(s == null) {
			return null;
		}
		return "'" + s.replaceAll("'", "''") + "'";
	}
}
