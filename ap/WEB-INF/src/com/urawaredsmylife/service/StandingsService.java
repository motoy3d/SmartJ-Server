package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.TeamUtils;

/**
 * DBに格納されているJ1/J2/ルヴァンカップ/ACLの順位表から検索し、JSONを返す。
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
			season = new SimpleDateFormat("yyyy").format(new Date());	//パラメータに関わらず今年をセット
			String teamId = (String)params.get("teamId");
			// 大会(J, Nabisco, ACL)
			String compe = StringUtils.defaultIfEmpty((String)params.get("compe"), "J");
			String sort = (String)params.get("sort");
			logger.info(compe + "順位表 " + season + " : " + teamId + " : " + sort);
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
				sort = "rank, diff, got_goal, seq";
			}
			String sql = null;
			if ("J".equals(compe)) {
				sql = createSqlForJ(qr, season, teamId, sort);
			} else if ("Nabisco".equals(compe)) {
				sql = createSqlForNabisco(qr, season, teamId);
			} else if ("ACL".equals(compe)) {
				sql = createSqlForACL(qr, season, teamId);
			} else {
				return null;
			}
			logger.info(sql);
			List<Map<String, Object>> resultList = qr.query(sql, new MapListHandler());
			for (Map<String, Object> team : resultList) {
				String officialTeamName = (String)team.get("team_name");
				team.put("team_name", TeamUtils.getShortTeamName(officialTeamName));
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("順位表読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}

	/**
	 * Jリーグ順位表取得SQLを生成して返す。
	 * チームIDによってJ1/J2を自動判定する。
	 * @param qr
	 * @param season
	 * @param teamId
	 * @param sort
	 * @return
	 * @throws SQLException
	 */
	private String createSqlForJ(QueryRunner qr, String season,
			String teamId, String sort) throws SQLException {
		String sql;
		String league = "J1";
		// チームのリーグカテゴリを検索
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
		sql = "SELECT *, team_name AS team FROM standings WHERE"
				+ " season=" + season
				+ " AND league='" + league + "'"
				+ " ORDER BY " + sort;
		return sql;
	}

	/**
	 * ルヴァンカップ順位表取得SQLを生成して返す。
	 * チームIDによってグループを自動判定する。
	 * @param qr
	 * @param season
	 * @param teamId
	 * @return
	 * @throws SQLException
	 */
	private String createSqlForNabisco(QueryRunner qr, String season,
			String teamId) throws SQLException {
		String teamName = TeamUtils.getTeamName(teamId);

		String sql = "SELECT * FROM nabiscoStandings WHERE"
				+ " season=" + season
				+ " AND group_name=(SELECT group_name FROM nabiscoStandings"
				+ " WHERE season=" + season + " AND team_name collate utf8_unicode_ci LIKE " + DB.quote(teamName) + ")"
				+ " ORDER BY seq";
		return sql;
	}

	/**
	 * ACLグループリーグ順位表取得SQLを生成して返す。
	 * チームIDによってグループを自動判定する。
	 * @param qr
	 * @param season
	 * @param teamId
	 * @return
	 * @throws SQLException
	 */
	private String createSqlForACL(QueryRunner qr, String season,
			String teamId) throws SQLException {
		String teamSql = "SELECT team_name3 FROM teamMaster WHERE team_id=?";
		String teamName = qr.query(teamSql, new ScalarHandler<String>(), teamId);

		String sql = "SELECT * FROM aclStandings WHERE"
				+ " season=" + season
				+ " AND group_name=(SELECT group_name FROM aclStandings WHERE"
				+ " team_name collate utf8_unicode_ci LIKE " + DB.quote(teamName) + " AND season=" + season + ")"
				+ " ORDER BY seq";
		return sql;
	}
}
