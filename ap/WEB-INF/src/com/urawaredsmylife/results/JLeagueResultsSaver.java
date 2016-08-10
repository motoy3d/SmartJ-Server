package com.urawaredsmylife.results;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.urawaredsmylife.util.DB;
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
	private static final String CS_RESULTS_URL = "http://www.jleague.jp/special/match/cs/${SEASON}/";
	private static final String J1SHOKAKU_PLAYOFF_RESULTS_URL = "http://www.jleague.jp/match/search/playoff/all/";
	private static final String FUJI_XEROX_RESULTS_URL = "http://www.jleague.jp/match/search/fxsc/all/";
	private static final String[] URLS = new String[] {
			J1_RESULTS_URL, J2_RESULTS_URL, LEAGUECUP_RESULTS_URL, ACL_RESULTS_URL
			,TENNOHAI_RESULTS_URL, CS_RESULTS_URL, J1SHOKAKU_PLAYOFF_RESULTS_URL
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
		try {
			sw.start();
			String season = new SimpleDateFormat("yyyy").format(new Date());
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for (String url : URLS) {
				url = url.replace("${SEASON}", season);
				logger.info("------------------------------------------------------------------");
				logger.info(url);
				logger.info("------------------------------------------------------------------");
				Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
				Elements ele = doc.select("section.matchListWrap");

				Iterator<Element> matchLists = ele.iterator();
				while (matchLists.hasNext()) {
					Element matchSection = matchLists.next();
					// 試合日
					Elements h4 = matchSection.select("h4");
					if (h4.isEmpty()) {
						continue;
					}
					String gameDate = h4.get(0).text();
					String gameDate1 = gameDate.replace("年", "/").replaceFirst("月", "/").replaceFirst("日", "").replace("（", "(");
					gameDate1 = gameDate1.substring(0, gameDate1.indexOf("("));
					String gameDate2 = gameDate.substring(5).replaceFirst("月", "/").replaceFirst("日", "");
					// 大会名、節
					Elements h5 = matchSection.select("h5");
					String compe = h5.get(0).text();
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
							.replace("　", "")
							;
					// 試合
					Elements games = matchSection.select("table.matchTable > tbody > tr");
					

					Iterator<Element> gamesItr = games.iterator();
					while (gamesItr.hasNext()) {
						Element game = gamesItr.next();
//						logger.info("-------------------------------");
//						logger.info(game);
						Elements timeAndStadiumTd = game.select("td.stadium");
						if (timeAndStadiumTd.isEmpty()) {
							continue;	//ルヴァンカップ等のグループ名のtr
						}
						// 時間
						String timeAndStadium = timeAndStadiumTd.text();
						String time = timeAndStadium.split(" ")[0];
						// スタジアム
//						logger.info("timeAndStadium = " + timeAndStadium);
						String stadium = timeAndStadium.split(" ")[1];
						// チーム、スコア
						Elements gameTableTd = game.select("table.gameTable > tbody > tr > td");
						if (gameTableTd.isEmpty()) {
							Elements gameTableTbody = game.select("table.gameTable > tbody");
							logger.info(gameTableTbody.html());
							continue;
						}
						// ホームチーム
						String homeTeam = gameTableTd.get(0).text();
						if ("未定".equals(homeTeam)) {
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
						String detailUrl = link.get(0).attr("href");
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
						insertDataList.add(oneRec);

						logger.info(gameDate1 + " | " + gameDate2 + " | " + time + " | " + stadium + " | " 
								+ compe + " | " + homeTeam + " " + StringUtils.trimToEmpty(homeScore) + " - " + StringUtils.trimToEmpty(awayScore) + pk + " "
								+ awayTeam + " | " + detailUrl);
					}
				}
			}
			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM results WHERE season=" + season);
			String insertSql = "INSERT INTO results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
            logger.info(ToStringBuilder.reflectionToString(resultCount));
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			sw.stop();
			logger.info((sw.getTime()/1000.0) + "秒");
		}
	}
}
