package com.urawaredsmylife.results;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

/**
 * セレッソ大阪公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class CerezoResultsSaver {
	private Logger logger = Logger.getLogger(CerezoResultsSaver.class.getName());
	/**
	 * 結果取得元URL
	 */
	private static final String SRC_URL_BASE1 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22https%3A%2F%2Fwww.cerezo.co.jp%2Fmatches%2Fresult%2Ftop-teams%22%20and%20xpath%3D%22%2F%2Ful%5B%40class%3D'list'%5D%2Fli%22&format=json&callback=";
	/**
	 * 予定取得元URL
	 */
	private static final String SRC_URL_BASE2 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22https%3A%2F%2Fwww.cerezo.co.jp%2Fmatches%2Ftop-teams%22%20and%20xpath%3D%22%2F%2Ful%5B%40class%3D'list'%5D%22&format=json&callback=";

	/** チームID */
	private static final String teamId = "cerezo";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public CerezoResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		try {
			String resultsTable = teamId + "Results";
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            Connection conn = DB.getConnection(false);
			qr.update(conn, "DELETE FROM " + resultsTable + " WHERE season=" + season);
			boolean isSchedule = false;
            String[] urls = new String[] {SRC_URL_BASE1, SRC_URL_BASE2};
			for(int idx=0; idx<urls.length; idx++) {
				String srcUrl = urls[idx];
				logger.info("####################################");
				logger.info(srcUrl);
				logger.info("####################################");
				GetMethodWebRequest req = new GetMethodWebRequest(srcUrl);
				StopWatch sw = new StopWatch();
				sw.start();
				WebResponse res = wc.getResponse(req);
				sw.stop();
				System.out.println((sw.getTime()/1000.0) + "秒");
				Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
				logger.info(json.toString());
				Map<String, Object> results = (Map<String, Object>)((Map<String, Object>)json
						.get("query")).get("results");
				List<Object> gameList = (List<Object>)results.get("li");
				if (gameList == null) {	//予定の方はulが入る
					List ulList = (List)results.get("ul");
					gameList = new ArrayList();
					for(Object ul : ulList) {
						gameList.addAll((List<Object>)((Map)ul).get("li"));
					}
					isSchedule = true;
				}
				logger.info(gameList.getClass().toString());
				
	            String insertSql = "INSERT INTO " + resultsTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
	            List<Object[]> insertDataList = new ArrayList<Object[]>();
				for(int r=0; r<gameList.size(); r++) {
					Map game = (Map)gameList.get(r);
					List list1 = (List)((Map)game.get("div")).get("div");
					String gameDateTime = "";
					boolean isHome = false;
					String gameDateView = "";
					String time = "";
					String gameDate = "";
					String compe = "";
					String stadium = "";
					String vsTeam = "";
					String score = "";
					String result = "";
					String detailUrl = "";
					List teamAndResultList = null;
					boolean isFirst = false;
					// 最初の１件とそれ以降で構造が違う
					List spanList = null;
					List list2 = null;
					Map gameDateCompeStadiumMap = null;
					if (((Map)list1.get(0)).get("div") instanceof List) {
						list2 = (List)((Map)list1.get(0)).get("div");
						spanList = (List)((Map)list2.get(0)).get("span");
						gameDateCompeStadiumMap = (Map)spanList.get(0);
						isFirst = true;
					} else {
						if (isSchedule) {
							gameDateCompeStadiumMap = (Map)((Map)((Map)list1.get(0)).get("div")).get("span");
						} else {
							spanList = (List)((Map)((Map)list1.get(0)).get("div")).get("span");
							gameDateCompeStadiumMap = (Map)spanList.get(0);
						}
					}
					
					gameDateTime = (String)((Map)gameDateCompeStadiumMap.get("time")).get("content");
					isHome = "home-game".equals((String)gameDateCompeStadiumMap.get("class"));					
					gameDateView = gameDateTime.substring(0, gameDateTime.indexOf(")") + 1);
					time = StringUtils.deleteWhitespace(gameDateTime.substring(gameDateTime.indexOf(")") + 1));
					gameDate = gameDateView.substring(0, gameDateView.indexOf("(")).replaceAll("\\.", "/");
					// compe, stadium
//					if (spanList != null) {
						List list3 = (List)((Map)gameDateCompeStadiumMap.get("span")).get("span");
						compe = StringUtils.deleteWhitespace((String)((Map)list3.get(0)).get("content"));
						compe = compe.replace("明治安田生命", "").replace("リーグ", "/");
						if (isFirst) {
							stadium = ((String)((Map)list3.get(1)).get("content")).trim();
						} else {
							vsTeam = ((String)((Map)list3.get(1)).get("content")).trim();
						}
//					} else {
						//TODO
//					}
					
					logger.info("▲" + compe + ", " + gameDateView + ", " + gameDate + ", " + time + ", " + stadium + ", " + isHome + ", " 
							+ vsTeam + ", " + ", " + result + ", " + score /*+ ", " + detailUrl*/);

					// vsTeam, result
					if (isFirst && !isSchedule) {
						teamAndResultList = (List)((Map)((Map)((Map)list2.get(0)).get("div")).get("strong")).get("span");
					}
					Integer leftScore = null;
					Integer rightScore = null;
					if (teamAndResultList != null) {
						vsTeam = StringUtils.deleteWhitespace((String)((Map)teamAndResultList.get(0)).get("content"));
						Map scoreMap = (Map)teamAndResultList.get(1);
						score = (String)((Map)scoreMap.get("span")).get("content")
								+ scoreMap.get("content");
						leftScore = Integer.parseInt(score.substring(0, score.indexOf(" ")));
						rightScore = Integer.parseInt(score.substring(score.indexOf("-")+2));
					} else {
						if (!isSchedule) {
							List scoreList = (List)((Map)((Map)spanList.get(1)).get("span")).get("span");
							leftScore = Integer.parseInt((String)((Map)scoreList.get(0)).get("content"));
							rightScore = Integer.parseInt((String)((Map)scoreList.get(1)).get("content"));
						}
					}
					if (leftScore != null) {
						if (leftScore > rightScore) {
							result = isHome? "○" : "●";
						} else if (leftScore < rightScore) {
							result = isHome? "●" : "○";
						} else {
							result = "△";
						}
						score = isHome? leftScore + " - " + rightScore : rightScore + " - " + leftScore;
					}
					String tv = "";
					int c = 0;
					Object[] oneRec = new Object[12];
					oneRec[c++] = season;
					oneRec[c++] = compe;
					oneRec[c++] = gameDate;
					oneRec[c++] = gameDateView;
					oneRec[c++] = time;
					oneRec[c++] = stadium;
					oneRec[c++] = isHome;
					oneRec[c++] = vsTeam;
					oneRec[c++] = tv;
					oneRec[c++] = result;
					oneRec[c++] = score;
					oneRec[c++] = detailUrl;
					insertDataList.add(oneRec);
					logger.info("■" + compe + ", " + gameDateView + ", " + gameDate + ", " + time + ", " + stadium + ", " + isHome + ", " 
							+ vsTeam + ", " + tv + ", " + result + ", " + score /*+ ", " + detailUrl*/);
				}
				
				if(insertDataList.isEmpty()) {
					logger.warn("日程データが取得出来ませんでした ");
					continue;
				}
	            int[] resultCount = qr.batch(conn, insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
	            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
			}
			DbUtils.commitAndCloseQuietly(conn);
		} catch (Exception e) {
			logger.error("試合日程・結果抽出エラー", e);
			Mail.send(e);
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		CerezoResultsSaver srv = new CerezoResultsSaver();
		srv.extractResults();
	}


}
