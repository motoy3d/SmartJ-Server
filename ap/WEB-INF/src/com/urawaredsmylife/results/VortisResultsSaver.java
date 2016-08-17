package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

/**
 * Jリーグ公式サイトから取得した試合日程・結果がDBに入っているので、
 * そこから徳島ヴォルティスの情報を取得して徳島ヴォルティス用テーブルに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class VortisResultsSaver {
	private Logger logger = Logger.getLogger(VortisResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "vortis";
	/** チーム名 */
	private static final String teamName = "徳島ヴォルティス";

	/**
	 * コンストラクタ
	 */
	public VortisResultsSaver() {
	}

	/**
	 * resultsテーブルから抽出して徳島ヴォルティス用テーブルに登録する。
	 * @return
	 */
	public int updateResults() {
		try {
			StopWatch sw = new StopWatch();
			sw.start();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			QueryRunner qr = DB.createQueryRunner();
			// 一旦削除
			qr.update("DELETE FROM " + teamId + "Results WHERE season=" + season);

			// resultsテーブルから徳島ヴォルティスの情報のみSELECTしてINSERT
			String insertSql = "INSERT INTO " + teamId + "Results \n"
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
					+ " now()\n"
					+ " FROM results \n"
					+ " where season=" + season
					+ " AND home_team='${TEAM_NAME}' or away_team='${TEAM_NAME}'\n"
					+ " order by game_date1";
			insertSql = StringUtils.replace(insertSql, "${TEAM_NAME}", teamName);
			logger.info(insertSql);
			int count = qr.update(insertSql);
            logger.info("登録件数：" + count);
		} catch (Exception e) {
			logger.error("試合日程・結果抽出エラー " + teamId, e);
			Mail.send(e);
		}
		return 0;
	}

	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		VortisResultsSaver srv = new VortisResultsSaver();
		srv.updateResults();
	}


}
