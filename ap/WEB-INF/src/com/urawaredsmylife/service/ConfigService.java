package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
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
//TODO アプリバージョンによるメッセージ判定			
			String appVersion = (String)params.get("appversion");
			if (StringUtils.isBlank(appVersion)) {
				appVersion = "1.0.0";
			}
			String teamId = (String)params.get("teamId");
			String sql = "SELECT * FROM message WHERE os=" + DB.quote(os) 
					+ " AND " + DB.quote(appVersion) + " BETWEEN min_ver AND max_ver"
					+ " AND team_id = " + DB.quote(teamId);
			logger.info(sql);
			List<Map<String, Object>> messageList = qr.query(sql, new MapListHandler());
			
			// 設定
			String sql2 = "SELECT * FROM teamMaster WHERE team_id = ?";
			logger.info(sql2 + " / " + teamId);
			Map<String, Object> team = qr.query(sql2, new MapHandler(), teamId);
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			Map<String, Object> conf = new HashMap<String, Object>();
			if(messageList != null && !messageList.isEmpty()) {
				conf.put("message", messageList.get(0).get("message"));
			}
			conf.put("jcategory", team.get("category"));
			conf.put("adType", team.get("adType"));
			conf.put("aclFlg", team.get("aclFlg"));
			
			// TODO 他チーム情報表示機能はJ1のみ有効
			if ("J1".equals(team.get("category"))) {
				conf.put("isOtherTeamNewsFeatureEnable", "true");
			} else {
				conf.put("isOtherTeamNewsFeatureEnable", "false");
			}
			//  2ステージ制フラグ.  2017シーズンは１ステージ制
			conf.put("is2stages", "false");
			
			// 現在のステージ
			conf.put("currentStage", "total");
//			try {
//				Date secondStageOpenDate = DateUtils.parseDate(
//						Const.J1_SECOND_STAGE_OPEN_DATE, new String[] {"yyyy/MM/dd"});
//				if (new Date().getTime() < secondStageOpenDate.getTime()) {
////				TODO アプリ側で順位表初期表示時にタブが2ndに切り替わらないため、とりあえず1st固定としておく。
//					conf.put("currentStage", "1st");
//				} else {
//					conf.put("currentStage", "total");
////					conf.put("currentStage", "2nd");
//				}
//			} catch (ParseException e) {
//			}
			
			// NGサイトリスト(リジェクト対策)
			String sql3 = "SELECT * FROM ngSite";
			logger.info(sql3);
			List<Map<String, Object>> ngSiteMapList = qr.query(sql3, new MapListHandler());
			String[] ngSiteList = new String[ngSiteMapList.size()];
			int idx = 0;
			for(Map<String, Object> ng : ngSiteMapList) {
				ngSiteList[idx++] = (String)ng.get("domain");
			}
			conf.put("ngSiteList", ngSiteList);
			
			resultList.add(conf);
			return resultList;
		} catch (SQLException e) {
			logger.error("メッセージ読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
