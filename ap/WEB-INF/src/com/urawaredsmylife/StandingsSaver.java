package com.urawaredsmylife;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.urawaredsmylife.util.DB;

/**
 * Yahooスポーツから順位表を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class StandingsSaver {
	private Logger logger = Logger.getLogger(StandingsSaver.class.getName());
	/**
	 * YahooスポーツのURL
	 */
	private static final String SRC_URL = "http://soccer.yahoo.co.jp/jleague/standings/j1";
//	private static final String SRC_URL = "http://localhost/j1standings.html";
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StandingsSaver srv = new StandingsSaver();
		srv.extractStandings();
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
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL);
		try {
			WebResponse res = wc.getResponse(req);
			WebTable[] tables = res.getTables();
			System.out.println(tables);
			TableRow[] rows = tables[0].getRows();
            String insertSql = "INSERT INTO standings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[18][];
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
				insertDataList[r-1] = new Object[12];
				insertDataList[r-1][c++] = season;
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
			qr.update("DELETE FROM standings");
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("順位表抽出エラー", e);
		}
		return 0;
	}
}
