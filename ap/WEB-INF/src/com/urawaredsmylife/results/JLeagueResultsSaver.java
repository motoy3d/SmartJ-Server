package com.urawaredsmylife.results;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.urawaredsmylife.Team;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;
import com.urawaredsmylife.util.TeamUtils;

/**
 * Jリーグ公式サイトから日程を抽出してDBに保存する。
 * @author motoy3d
 */
public class JLeagueResultsSaver {
	private static final String J1_RESULTS_URL = "http://www.jleague.jp/match/search/j1/all/";
	private static final String J2_RESULTS_URL = "http://www.jleague.jp/match/search/j2/all/";
	private static final String LEAGUECUP_RESULTS_URL = "http://www.jleague.jp/match/search/leaguecup/all/";
	private static final String ACL_RESULTS_URL = "http://www.jleague.jp/match/search/acl/all/";
	private static final String TENNOHAI_RESULTS_URL = "http://www.jleague.jp/match/search/emperor/all/";
	private static final String J1SHOKAKU_PLAYOFF_RESULTS_URL = "http://www.jleague.jp/match/search/playoff/all/";
	private static final String FUJI_XEROX_RESULTS_URL = "http://www.jleague.jp/match/search/fxsc/all/";
//	private static final String NEWYEAR_CUP_RESULTS_URL = "http://www.jleague.jp/match/search/nyc/all/";
	private static final String SURUGA_RESULTS_URL = "http://www.jleague.jp/match/search/suruga/all/";
	private static final String[] URLS = new String[] {
			J1_RESULTS_URL, J2_RESULTS_URL, LEAGUECUP_RESULTS_URL, ACL_RESULTS_URL
			,TENNOHAI_RESULTS_URL, SURUGA_RESULTS_URL, J1SHOKAKU_PLAYOFF_RESULTS_URL
			, FUJI_XEROX_RESULTS_URL
	};
	private static final String DETAIL_URL_BASE = "http://www.jleague.jp";
	private static Logger logger = Logger.getLogger(JLeagueResultsSaver.class.getName());
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			insertAllResults();
            insertEachTeamResults();
		} catch(Exception ex) {
			logger.error("日程・結果更新エラー", ex);
			Mail.send(ex);
			System.exit(1);
		} finally {
			sw.stop();
			logger.info((sw.getTime()/1000.0) + "秒");
		}
	}

	/**
	 * Jリーグ公式サイトから日程・結果を取得してresultsテーブルに登録する。
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void insertAllResults() throws IOException, SQLException {
		String season = new SimpleDateFormat("yyyy").format(new Date());
		List<Object[]> insertDataList = new ArrayList<Object[]>();
		for (String url : URLS) {
			url = url.replace("${SEASON}", season);
			logger.info("------------------------------------------------------------------");
			logger.info(url);
			logger.info("------------------------------------------------------------------");
			Connection connection = Jsoup.connect(url).maxBodySize(0).timeout(60 * 1000);
			Document doc = null;
			try {
				doc = connection.get();
			} catch(HttpStatusException ex) {
				if (ex.getStatusCode() == 404) {
					logger.warn("404 error " + url, ex);
					continue;
				}
			}
			Elements ele = doc.select("section.matchlistWrap");
			Iterator<Element> matchLists = ele.iterator();
			while (matchLists.hasNext()) {
				Element matchSection = matchLists.next();
				// 試合日
				Elements h4 = matchSection.select("h4");
				logger.info("--------------------------- " + h4.text());
				if (h4.isEmpty()) {
					logger.info(">>>>>>> continue h4.isEmpty()");
					continue;
				}
				String gameDate = h4.get(0).text();
				logger.info("★gameDate=" + gameDate);
				String gameDate1 = null;
				String gameDate2 = "";
				if (gameDate.contains("開催日未定")) {
					gameDate2 = "開催日未定";
				} else {
					gameDate1 = gameDate.replace("年", "/").replaceFirst("月", "/").replaceFirst("日", "").replace("（", "(");
					gameDate1 = gameDate1.substring(0, gameDate1.indexOf("("));
					gameDate2 = gameDate.substring(5).replaceFirst("月", "/").replaceFirst("日", "");
				}
				// 大会名、節
				Elements h5 = matchSection.select("h5");
				for (int i=0; i<h5.size(); i++) {	//1日に複数の節の試合がある場合がある
					String compe = h5.get(i).text();
					compe = compe.replace("明治安田生命Ｊ１リーグ", "J1")
							.replace("明治安田生命Ｊ２リーグ", "J2")
							.replace("　１ｓｔステージ　", " 1st ")
							.replace("　２ｎｄステージ　", " 2nd ")
							.replace("ＡＦＣチャンピオンズリーグ", "ACL")
							.replace("ＪリーグYBCルヴァンカップ", "ルヴァン")
							.replace("ヤマザキナビスコカップ", "ナビスコ")
							.replace("グループステージ", "GS ")
							.replace("ラウンド１６　", "ラウンド16")
							.replace("ＭＤ", "MD")
							.replaceAll("ＦＵＪＩ ＸＥＲＯＸ ＳＵＰＥＲ ＣＵＰ", "FUJI XEROX SUPER CUP")
							.replace("　", "")
							;
					System.out.println("🌟 " + gameDate + "  " + compe);
					// 試合
					Elements matchTables = matchSection.select("table.matchTable");
					Element matchTable = matchTables.get(i);
					Elements games = matchTable.select("tbody > tr");
					Iterator<Element> gamesItr = games.iterator();
					while (gamesItr.hasNext()) {
						Element game = gamesItr.next();
//						logger.info("--------------------------------------------------------------");
//						logger.info("game🔴" + game);
//						logger.info("--------------------------------------------------------------");
						Elements timeAndStadiumTd = game.select("td.stadium");
						if (timeAndStadiumTd.isEmpty()) {
//							logger.info(">>>>>>> continue ルヴァンカップ等のグループ名のtr");
							continue;	//ルヴァンカップ等のグループ名のtr
						}
						// 時間
						String timeAndStadium = timeAndStadiumTd.text();
						String time = timeAndStadium.split(" ")[0];
						// スタジアム
//							logger.info("timeAndStadium = " + timeAndStadium);
						String stadium = timeAndStadium.split(" ")[1];
						// チーム、スコア
						Elements gameTableTd = game.select("table.gameTable > tbody > tr > td");
						if (gameTableTd.isEmpty()) {
							Elements gameTableTbody = game.select("table.gameTable > tbody");
							logger.info(">>>>>>> continue gameTableTd.isEmpty()");
							logger.info(gameTableTbody.html());
							continue;
						}
						// ホームチーム
						String homeTeam = gameTableTd.get(0).text();
						if ("未定".equals(homeTeam)) {
							logger.info(">>>>>>> continue チーム未定");
							continue;
						}
						homeTeam = TeamUtils.getOfficialTeamName(homeTeam);
						// ホームスコア
						String homeScore = StringUtils.trimToNull(gameTableTd.get(1).text());
						// アウェイスコア
						String awayScore = StringUtils.trimToNull(gameTableTd.get(3).text());
						// アウェイチーム
						String awayTeam = gameTableTd.get(4).text();
						awayTeam = TeamUtils.getOfficialTeamName(awayTeam);
						// PK
						Elements pkLi = game.select("li.pk");
						String pk = pkLi.text();
						if (StringUtils.isNotBlank(pk)) {
							pk = " " + pk;
						}
						String homePk = null;
						String awayPk = null;
						if (0 < pk.trim().length()) {
							String pk2 = pk.replace("(", "").replace(")", "");
							int idx = pk2.indexOf(" PK");
							homePk = pk2.substring(0, idx);
							awayPk = pk2.substring(idx+4);
						}
						
						// 詳細URL
						Elements link = game.select("td.match > a");
						String detailUrl = link.isEmpty()? null : link.get(0).attr("href");
						if (StringUtils.isNotBlank(detailUrl)) {
							detailUrl = DETAIL_URL_BASE + detailUrl;
						}
						
						int c = 0;
						Object[] oneRec = new Object[14];
						oneRec[c++] = season;
						oneRec[c++] = compe;
						oneRec[c++] = gameDate1;
						oneRec[c++] = gameDate2;
						oneRec[c++] = time;
						oneRec[c++] = stadium;
						oneRec[c++] = homeTeam;
						oneRec[c++] = awayTeam;
						oneRec[c++] = "";	//TV
						oneRec[c++] = homeScore;
						oneRec[c++] = awayScore;					
						oneRec[c++] = homePk;
						oneRec[c++] = awayPk;
						oneRec[c++] = detailUrl;
						if (gameDate1 != null) {	//TODO 開催日未定を登録する
							insertDataList.add(oneRec);
						}

						logger.info("🌟 🌟 🌟 " + gameDate1 + " | " + gameDate2 + " | " + time + " | " + stadium + " | " 
								+ compe + " | " + homeTeam + " " + StringUtils.trimToEmpty(homeScore) + " - " + StringUtils.trimToEmpty(awayScore) + pk + " "
								+ awayTeam + " | " + detailUrl);
					}
				}
			}
		}
		QueryRunner qr = DB.createQueryRunner();
		qr.update("DELETE FROM results WHERE season=" + season);
		String insertSql = "INSERT INTO results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
		int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
		logger.info(ToStringBuilder.reflectionToString(resultCount));
	}
	
	/**
	 * 各チームの結果を各チーム用結果テーブルにINSERTする。
	 * @throws SQLException 
	 */
	private static void insertEachTeamResults() throws SQLException {
		StopWatch sw = new StopWatch();
		sw.start();
		QueryRunner qr = DB.createQueryRunner();
		String sql = "SELECT team_id as teamId, team_name as teamName"
					+ " FROM teamMaster ORDER BY category, team_id";
		List<Team> teams = qr.query(sql, new BeanListHandler<>(Team.class));
		for (Team team : teams) {
			logger.info("---------------------------------------------------------------------------");
			logger.info(team.getTeamName() + " 日程・結果登録");
			logger.info("---------------------------------------------------------------------------");
	        String season = new SimpleDateFormat("yyyy").format(new Date());
			// 一旦削除
			qr.update("DELETE FROM " + team.getTeamId() + "Results WHERE season=" + season);
	
			// resultsテーブルから対象クラブの情報のみSELECTしてINSERT
			String insertSql = "INSERT INTO " + team.getTeamId() + "Results \n"
	        		+ "SELECT season,compe,game_date1,game_date2,kickoff_time,stadium,\n"
	        		+ " case when home_team='${TEAM_NAME}' then true else false end as home_flg,\n"
	        		+ " case when home_team='${TEAM_NAME}' then away_team else home_team end as vs_team,\n"
	        		+ " null tv,\n"
					+ " case when home_team='${TEAM_NAME}' then \n"
					+ " case when home_score is null then null when home_score > away_score then '◯' when home_score < away_score then '×' else '△' end\n"
					+ " 	else case when home_score is null then null when home_score > away_score then '×' when home_score < away_score then '◯' else '△' end\n" 
					+ " end as result,\n"
					+ " CONCAT(\n"
					+ "   case when home_team='${TEAM_NAME}' then concat(home_score,'-',away_score) else concat(away_score,'-',home_score) end, \n"
					+ "   IFNULL(case when home_team='${TEAM_NAME}' then concat(home_pk,'-',away_pk) else concat(away_pk,'-',home_pk) end, '')\n"
					+ " ) as score,\n"
					+ " detail_url,\n"
					+ " null,\n"	//ticket_url
					+ " now()\n"
					+ " FROM results \n"
					+ " where season=" + season
					+ " AND (home_team='${TEAM_NAME}' or away_team='${TEAM_NAME}')\n"
					+ " order by game_date1";
			insertSql = StringUtils.replace(insertSql, "${TEAM_NAME}", team.getTeamName());
			logger.info(insertSql);
			int count = qr.update(insertSql);
			if (count == 0) {
				throw new RuntimeException("登録件数０  " + team.getTeamName());
			}
	        logger.info(team.getTeamName() + " 登録件数：" + count);
		}
		sw.stop();
		logger.info((sw.getTime()/1000.0) + "秒");
	}	
}
