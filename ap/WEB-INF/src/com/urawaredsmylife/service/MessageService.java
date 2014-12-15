package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;

/**
 * アプリ起動時に表示するメッセージのJSONを返す。
 * レッズ用。
 * 他チームは↓
 * @see ConfigService
 * @author motoy3d
 *
 */
public class MessageService {
	private Logger logger = Logger.getLogger(MessageService.class.getName());
	
	/**
	 * アプリ起動時に表示するメッセージのJSONを返す。
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String os = (String)params.get("os");
			String version = (String)params.get("version");
			String teamId = (String)params.get("teamId");
			String sql = "SELECT * FROM message WHERE os=" + DB.quote(os) 
					+ " AND " + DB.quote(version) + " BETWEEN min_ver AND max_ver"
					+ " AND team_id = " + DB.quote(teamId);
			logger.info(sql);
			List<Map<String, Object>> resultList = qr.query(sql, new MapListHandler());
			return resultList;
		} catch (SQLException e) {
			logger.error("メッセージ読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
