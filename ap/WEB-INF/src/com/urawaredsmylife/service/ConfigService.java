package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.Const;
import com.urawaredsmylife.util.DB;

/**
 * アプリ起動時に表示するメッセージ等の設定情報のJSONを返す。
 * @author motoy3d
 */
public class ConfigService {
	private Logger logger = Logger.getLogger(ConfigService.class.getName());
	
	/**
	 * アプリ起動時に表示するメッセージ等の設定情報のJSONを返す。
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String os = (String)params.get("os");
			String osVersion = (String)params.get("osversion");
			if (StringUtils.isBlank(osVersion)) {
				osVersion = (String)params.get("version");		//古いアプリへの対応
			}
//TODO アプリバージョンによるメッセージ判定			String appVersion = (String)params.get("appversion");
			String teamId = (String)params.get("teamId");
			String sql = "SELECT * FROM message WHERE os=" + DB.quote(os) 
					+ " AND " + DB.quote(osVersion) + " BETWEEN min_ver AND max_ver"
					+ " AND team_id = " + DB.quote(teamId);
			logger.info(sql);
			List<Map<String, Object>> messageList = qr.query(sql, new MapListHandler());
			
			// 設定
			String sql2 = "SELECT * FROM teamMaster WHERE" 
					+ " team_id = " + DB.quote(teamId);
			logger.info(sql2);
			Map<String, Object> team = qr.query(sql2, new MapHandler());
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			Map<String, Object> conf = new HashMap<String, Object>();
			if(messageList != null && !messageList.isEmpty()) {
				conf.put("message", messageList.get(0).get("message"));
			}
			conf.put("jcategory", team.get("category"));
			conf.put("adType", team.get("adType"));
			conf.put("aclFlg", team.get("aclFlg"));
			
			// 現在のステージ
			try {
				Date secondStageOpenDate = DateUtils.parseDate(
						Const.J1_SECOND_STAGE_OPEN_DATE, new String[] {"yyyy/MM/dd"});
				if (new Date().getTime() < secondStageOpenDate.getTime()) {
					conf.put("currentStage", "1st");
				} else {
					conf.put("currentStage", "2nd");
				}
			} catch (ParseException e) {
			}
			
			resultList.add(conf);
			return resultList;
		} catch (SQLException e) {
			logger.error("メッセージ読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
