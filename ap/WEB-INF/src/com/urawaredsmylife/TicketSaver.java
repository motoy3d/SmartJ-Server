package com.urawaredsmylife;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

/**
 * Jリーグチケットサイトからチケット情報を抽出してDBに保存する。
 * @author motoy3d
 */
public class TicketSaver {
	private static final String TICKET_LIST_URL_BASE = "https://www.jleague-ticket.jp/club/";
	private static final String TICKET_URL_BASE = "https://www.jleague-ticket.jp";
	private static Logger logger = Logger.getLogger(TicketSaver.class.getName());
	
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			updateTicketUrl();
		} catch(Exception ex) {
			logger.error("チケットURL更新エラー", ex);
			Mail.send(ex);
		} finally {
			sw.stop();
			logger.info((sw.getTime()/1000.0) + "秒");
		}
	}

	/**
	 * Jリーグチケットサイトから情報を取得してresultsテーブルに更新する。
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void updateTicketUrl() throws IOException, SQLException {
		String season = new SimpleDateFormat("yyyy").format(new Date());
		QueryRunner qr = DB.createQueryRunner();
		// チームマスタから全チーム取得(team_name4はJリーグチケットサイト上のチーム略称)
		String getTeamName4Sql = "SELECT team_id, team_name4 FROM teamMaster ORDER BY team_id";
		List<Map<String, Object>> teamNames = qr.query(getTeamName4Sql, new MapListHandler());
		for (Map<String, Object> team : teamNames) {
			String teamId = (String)team.get("team_id");
			String teamName4 = (String)team.get("team_name4");
			String url = TICKET_LIST_URL_BASE + teamName4 + "/top/";
			logger.info("------------------------------------------------------------------");
			logger.info(url);
			logger.info("------------------------------------------------------------------");
			// JリーグチケットサイトからチームごとにチケットURLを取得
			Document doc = Jsoup.connect(url).maxBodySize(0).timeout(60 * 1000).get();
			// Jリーグ主催試合(リーグ戦、ルヴァンカップ)のチケット情報を保存
			saveJLeagueAndLevainCup(season, qr, teamId, doc);
			// ACL のチケット情報を保存　TODO 天皇杯も?
//			saveACL(qr, teamId, doc);
		}
	}

	/**
	 * Jリーグ主催試合(リーグ戦、ルヴァンカップ)のチケット情報を保存する
	 * @param season
	 * @param qr
	 * @param teamId
	 * @param doc
	 * @throws SQLException
	 */
	private static void saveJLeagueAndLevainCup(
			String season, QueryRunner qr, String teamId, Document doc)
			throws SQLException {
		Elements jGames = doc.select("div.game-list > ul > li");
//		System.out.println("jGames=" + jGames.html());
		Iterator<Element> gamesItr = jGames.iterator();
		while (gamesItr.hasNext()) {
			Element game = gamesItr.next();
			logger.info("---------------------------------------");
//				logger.info(game.text());
			Elements dateTime = game.select("span.vs-box-info-day");
//			logger.info("🔴dateTime=" + dateTime);
			if (dateTime.isEmpty()) {	//ACLは形式が違うので別メソッドで別途処理 TODO サイトフォーマットが変更になったので2018年に要確認
				continue;
			}
			Element dateEle = dateTime.get(0);
			String date = season + "/" + dateEle.text();
//			logger.info("date=" + date);
			
			Element link = game.select("div.vs-box-ticket > span").first();
			String ticketUrl = TICKET_URL_BASE + link.attr("href");
			logger.info(ticketUrl);
			// 各チームのResultsテーブルに更新する。
			updateDb(qr, teamId, date, ticketUrl);
		}
	}

	/**
	 * ACL のチケット情報を保存する。　TODO 天皇杯も?
	 * DOM構造がJリーグと違う。
	 * @param qr
	 * @param teamId
	 * @param doc
	 * @throws SQLException
	 */
	private static void saveACL(QueryRunner qr, String teamId, Document doc) throws SQLException {
		Elements otherGames = doc.select("div.otherTktWrap > a");
		Iterator<Element> gamesItr = otherGames.iterator();
		while (gamesItr.hasNext()) {
			Element game = gamesItr.next();
			logger.info("---------------------------------------");
//				logger.info(game.text());
			Elements dateTime = game.select("p.day");
			if (dateTime.isEmpty()) {
				logger.info("🔴dateTimeがEmpty");
				continue;
			}
			Element dateEle = dateTime.get(0);
			String date = dateEle.text().substring(0, 10);
			logger.info(date);
			
			String ticketUrl = TICKET_URL_BASE + game.attr("href");
			logger.info(ticketUrl);
			// 各チームのResultsテーブルに更新する。
			updateDb(qr, teamId, date, ticketUrl);
		}
	}		

	/**
	 * DBの結果テーブルのチケットURLを更新する。
	 * @param qr
	 * @param teamId
	 * @param date
	 * @param ticketUrl
	 * @return
	 * @throws SQLException
	 */
	private static int updateDb(QueryRunner qr, String teamId, String date, String ticketUrl) throws SQLException {
		String table = teamId + "Results";
		String updateSql = "UPDATE " + table + " SET "
				+ "ticket_url=?" 
				+ " WHERE game_date1=?";
		logger.info(updateSql + " (" + ticketUrl + ", " + date + ")");
		return qr.update(updateSql, ticketUrl, date);
	}
}
