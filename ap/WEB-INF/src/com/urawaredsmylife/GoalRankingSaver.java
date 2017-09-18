package com.urawaredsmylife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.urawaredsmylife.util.DB;

/**
 * Jリーグ公式サイトから得点ランキングを取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class GoalRankingSaver {
	private Logger logger = Logger.getLogger(GoalRankingSaver.class.getName());
	/**
	 * 順位表URL
	 */
	private static final String SRC_URL = "http://www.jleague.jp/stats/goal.html";

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		GoalRankingSaver srv = new GoalRankingSaver();
		int status = srv.extract();
		System.exit(status);
	}

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public GoalRankingSaver() {
	}

	/**
	 * Yahooスポーツにアクセスし、順位表を抽出する
	 * @return
	 * @throws ParseException
	 */
	private int extract() {
		try {
			int result = 0;
			Connection connection = Jsoup.connect(SRC_URL).maxBodySize(0).timeout(60 * 1000);
			Document doc = connection.get();
			Elements articles = doc.select("article");
			String season = new SimpleDateFormat("yyyy").format(new Date());
			String[] leagues = new String[] {"J1", "YLC", "J2", "J3"};
			List<Object[]> insertDataList = new ArrayList<>();
			for (int articleIdx=0; articleIdx<articles.size(); articleIdx++) {
				Elements rankingTrs = articles.get(articleIdx).select("table > tbody > tr");
				String league = leagues[articleIdx];
				for (int tr=0; tr<rankingTrs.size(); tr++) {
					Element rankTr = rankingTrs.get(tr);
					int seq = tr + 1;
					String rank = rankTr.select("th").text();
					String goal = rankTr.select(".data").text();
					String playerName = rankTr.select(".name").text();
					String position = rankTr.select("td").get(4).text();
					String team = rankTr.select("td").get(5).text();
					Object[] rankData = new Object[] {
							season, league, seq, rank, playerName, goal, position, team};
					insertDataList.add(rankData);
//					System.out.println(rank + "," + goal + "," + playerName + "," + position + "," + team);
				}
			}
			QueryRunner qr = DB.createQueryRunner();
			int deletedCount = qr.update("DELETE FROM goalRanking WHERE season=" + season);
			logger.info("削除件数: " + deletedCount);
			String insertSql = "INSERT INTO goalRanking VALUES(?, ?, ?, ?, ?, ?, ?, ?, now())";
			Object[][] insertDatas = insertDataList.toArray(new Object[insertDataList.size()][]);
			int[] insertCounts = qr.batch(insertSql, insertDatas);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(insertCounts));
			return result;
		} catch(Exception ex) {
			logger.error("得点ランキング取得エラー", ex);
			return 1;
		}
	}
}
