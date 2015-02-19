package com.urawaredsmylife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.urawaredsmylife.util.DB;

/**
 * YahooスポーツからJリーグ、ナビスコカップの順位表を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class StandingsSaver {
	private Logger logger = Logger.getLogger(StandingsSaver.class.getName());
	/**
	 * 順位表URL
	 */
	private static final String SRC_URL_J1 = "http://soccer.yahoo.co.jp/jleague/standings/j1";
	private static final String SRC_URL_J2 = "http://soccer.yahoo.co.jp/jleague/standings/j2";
	private static final String SRC_URL_NABISCO = "http://soccer.yahoo.co.jp/jleague/standings/jleaguecup";
	/**
	 * J1開幕日
	 */
	private static final String J1_OPEN_DATE = "2015/03/07";
	/**
	 * ナビスコカップ参加チーム数（年によって変わる可能性あり）
	 */
	private static final int NABISCO_TEAM_COUNT = 14;
	
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StandingsSaver srv = new StandingsSaver();
		int status = srv.extractStandings();
		System.exit(status);
	}

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public StandingsSaver() {
	}

	/**
	 * Yahooスポーツにアクセスし、順位表を抽出する
	 * @return
	 */
	private int extractStandings() {
		//1/1〜開幕まではデータ更新しない
		try {
			Date j1OpenDate = DateUtils.parseDate(J1_OPEN_DATE, new String[] {"yyyy/MM/dd"});
			if (new Date().getTime() < j1OpenDate.getTime()) {
				logger.info("---------- J1開幕前");
				return 0;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		// J1
		int j1Result = insertJ(SRC_URL_J1, "J1", 18);
		// J2
		int j2Result = insertJ(SRC_URL_J2, "J2", 22);
		// ナビスコカップ
		int nabiscoResult = insertNabisco();
		if (j1Result == 0 && j2Result == 0 && nabiscoResult ==0) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * Jリーグ順位表URLにアクセスして解析し、standingsテーブルにINSERTする。
	 * @param srcUrl
	 * @param league
	 * @param teamCount
	 * @return
	 */
	private int insertJ(String srcUrl, String league, int teamCount) {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(srcUrl);
		try {
			WebResponse res = wc.getResponse(req);
			WebTable[] tables = res.getTables();
			System.out.println(tables);
			TableRow[] rows = tables[0].getRows();
            String insertSql = "INSERT INTO standings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[teamCount][];
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<rows.length; r++) {
				System.out.println("-----------------------------");
				String rank = tables[0].getCellAsText(r, 0);
				String team = tables[0].getCellAsText(r, 3);
				String point = tables[0].getCellAsText(r, 4);
				String games = tables[0].getCellAsText(r, 5);
				String win = tables[0].getCellAsText(r, 6);
				String draw = tables[0].getCellAsText(r, 7);
				String lose = tables[0].getCellAsText(r, 8);
				String gotGoal = tables[0].getCellAsText(r, 9);
				String lostGoal = tables[0].getCellAsText(r, 10);
				String diff = tables[0].getCellAsText(r, 11);
				System.out.println(rank + " : " + team);
				int c = 0;
				insertDataList[r-1] = new Object[14];
				insertDataList[r-1][c++] = season;
				insertDataList[r-1][c++] = league;
				insertDataList[r-1][c++] = "J1".equals(league)? "1st" : null;	//TODO ステージ
				insertDataList[r-1][c++] = r;
				insertDataList[r-1][c++] = rank;
				insertDataList[r-1][c++] = team;
				insertDataList[r-1][c++] = point;
				insertDataList[r-1][c++] = games;
				insertDataList[r-1][c++] = win;
				insertDataList[r-1][c++] = draw;
				insertDataList[r-1][c++] = lose;
				insertDataList[r-1][c++] = gotGoal;
				insertDataList[r-1][c++] = lostGoal;
				insertDataList[r-1][c++] = diff;
			}
			if(rows.length == 0) {
				logger.warn("順位表データが取得出来ませんでした");
				return -1;
			}
			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM standings WHERE season=" + season + " AND league='" + league + "'");
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("J1/J2順位表抽出エラー", e);
		}
		return 0;
	}

	/**
	 * ナビスコカップ順位表URLにアクセスして解析し、nabiscoStandingsテーブルにINSERTする。
	 * @param srcUrl
	 * @param league
	 * @param teamCount
	 * @return
	 */
	private int insertNabisco() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL_NABISCO);
		try {
			WebResponse res = wc.getResponse(req);
			WebTable[] tables = res.getTables();
			System.out.println("テーブル数：" + tables.length);
            String insertSql = "INSERT INTO nabiscoStandings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[NABISCO_TEAM_COUNT][];
            String season = new SimpleDateFormat("yyyy").format(new Date());
            // tables = グループごと
            int rowIdx = 0;
			for(int g=0; g<tables.length; g++) {
				WebTable table = tables[g];
				TableRow[] rows = table.getRows();
				for(int r=1; r<rows.length; r++) {
					System.out.println("-----------------------------");
					String rank = table.getCellAsText(r, 0);
					String team = table.getCellAsText(r, 2);
					String point = table.getCellAsText(r, 3);
					String games = table.getCellAsText(r, 4);
					String win = table.getCellAsText(r, 5);
					String draw = table.getCellAsText(r, 6);
					String lose = table.getCellAsText(r, 7);
					String gotGoal = table.getCellAsText(r, 8);
					String lostGoal = table.getCellAsText(r, 9);
					String diff = table.getCellAsText(r, 10);
					String group = g == 0? "A" : "B";
					System.out.println(group + "-" + rank + " : " + team);
					int c = 0;
					insertDataList[rowIdx] = new Object[13];
					insertDataList[rowIdx][c++] = season;
					insertDataList[rowIdx][c++] = group;	//グループ
					insertDataList[rowIdx][c++] = r;
					insertDataList[rowIdx][c++] = rank;
					insertDataList[rowIdx][c++] = team;
					insertDataList[rowIdx][c++] = point;
					insertDataList[rowIdx][c++] = games;
					insertDataList[rowIdx][c++] = win;
					insertDataList[rowIdx][c++] = draw;
					insertDataList[rowIdx][c++] = lose;
					insertDataList[rowIdx][c++] = gotGoal;
					insertDataList[rowIdx][c++] = lostGoal;
					insertDataList[rowIdx][c++] = diff;
					rowIdx++;
				}
			}
			
			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM nabiscoStandings WHERE season=" + season);
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("ナビスコカップ順位表抽出エラー", e);
		}
		return 0;
	}

}
