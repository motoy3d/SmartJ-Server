package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.TeamUtils;

/**
 * DBに格納されている順位表から検索し、JSONを返す。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 *
 */
public class StandingsService {
	private Logger logger = Logger.getLogger(StandingsService.class.getName());
	
	/**
	 * DBに格納されている順位表から検索し、JSONを返す。
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String season = (String)params.get("season");
			String teamId = (String)params.get("teamId");
			String sort = (String)params.get("sort");
			logger.info("順位表 " + season + " : " + teamId + " : " + sort);
			if("gotGoal".equals(sort)) {
				sort = "got_goal DESC, seq";
			} else if("lostGoal".equals(sort)) {
				sort = "lost_goal, seq";
			} else if("win".equals(sort)) {
				sort = "win DESC, seq";
			} else if("lost".equals(sort)) {
				sort = "lose, seq";
			} else if("draw".equals(sort)) {
				sort = "draw DESC, seq";
			} else if("diff".equals(sort)) {
				sort = "diff DESC, seq";
			} else {
				sort = "seq";
			}
			String league = "J1";
			if (StringUtils.isNotBlank(teamId)) {
				String teamName = TeamUtils.getTeamName(teamId);
				String leagueSelectSql = "SELECT league FROM standings WHERE"
						+ " season=" + season
						+ " AND team_name=" + DB.quote(teamName);
				//logger.info(leagueSelectSql);
				Map<String, Object> leagueMap = qr.query(leagueSelectSql, new MapHandler());
				if (leagueMap != null) {
					league = (String)leagueMap.get("league");
					if (StringUtils.isBlank(league)) {
						league = "J1";
					}				
				}
			}

			String sql = "SELECT * FROM standings WHERE"
					+ " season=" + season 
					+ " AND league='" + league + "'"
					+ " ORDER BY " + sort;
			logger.info(sql);
			List<Map<String, Object>> resultList = qr.query(sql, new MapListHandler());
			return resultList;
		} catch (SQLException e) {
			logger.error("順位表読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
