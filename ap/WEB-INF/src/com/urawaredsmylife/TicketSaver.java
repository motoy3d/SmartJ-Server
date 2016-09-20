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
			logger.error("日程・結果更新エラー", ex);
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
		String getTeamName4Sql = "SELECT team_id, team_name4 FROM teamMaster ORDER BY team_id";
		List<Map<String, Object>> teamNames = qr.query(getTeamName4Sql, new MapListHandler());
		for (Map<String, Object> team : teamNames) {
			String teamId = (String)team.get("team_id");
			String teamName4 = (String)team.get("team_name4");
			String url = TICKET_LIST_URL_BASE + teamName4 + "/";
			logger.info("------------------------------------------------------------------");
			logger.info(url);
			logger.info("------------------------------------------------------------------");
			// JリーグチケットサイトからチームごとにチケットURLを取得
			Document doc = Jsoup.connect(url).maxBodySize(0).timeout(60 * 1000).get();
			Elements games = doc.select("div.gDetailInner");

			Iterator<Element> gamesItr = games.iterator();
			while (gamesItr.hasNext()) {
				Element game = gamesItr.next();
				logger.info("---------------------------------------");
//				logger.info(game.text());
				Elements dateTime = game.select("p.time");
				if (dateTime.isEmpty()) {
					continue;
				}
				Element dateEle = dateTime.get(0);
				String date = season + "/" + dateEle.text();
				logger.info(date);
				
				Element link = game.parent().parent().parent();
				String ticketUrl = TICKET_URL_BASE + link.attr("href");
				logger.info(ticketUrl);
				// 各チームのResultsテーブルに更新する。
				String updateSql = "UPDATE " + teamId + "Results SET "
						+ "ticket_url=" + DB.quote(ticketUrl) 
						+ " WHERE game_date1=" + DB.quote(date);
				logger.info(updateSql);
				qr.update(updateSql);
			}
		}
	}
		
}
