package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.util.DB;

/**
 * 浦和レッズ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class RedsResultsSaver {
	private Logger logger = Logger.getLogger(RedsResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "reds";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20"
			+ "html%20WHERE%20url%3D'http%3A%2F%2Fwww.urawa-reds.co.jp%2Fgame%2F'%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40class%3D'mainContentColumn'%5D%2Ftable%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public RedsResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL);
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			//logger.info("json = " + json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");	
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				if (((Map)game).get("td") instanceof Map) {
					logger.info("無観客試合など");
					continue;
				}
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if(gameItems == null) {
					continue;
				}
				if (gameItems.get(0) == null) {
					logger.info("無観客試合など");
					continue;
				}
				String compe = null;
				if (((Map)gameItems.get(0)).get("p") instanceof Map) {
					compe = ((String)((Map)((Map)gameItems.get(0)).get("p")).get("content")).replaceAll("\n", "");
				} else {
					compe = (String)((Map)gameItems.get(0)).get("p");
				}
				String gameDateView = null;
				if (((Map)gameItems.get(1)).get("p") instanceof Map) {
					gameDateView = ((String)((Map)((Map)gameItems.get(1)).get("p")).get("content"))
							.replaceAll("\n", "").replaceAll("<br/>", "");
				} else {
					gameDateView = (String)((Map)gameItems.get(1)).get("p");
				}
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)((Map)gameItems.get(2)).get("p");
				String homeAway = ((String)((Map)game).get("class")).startsWith("home")? "HOME" : "AWAY";
				String vsTeam = (String)((Map)gameItems.get(3)).get("p");
				String stadium = "";
				String tv = null;
				if (gameItems.get(4) != null && ((Map)gameItems.get(4)).get("p") != null) {
					stadium = (String)((Map)((Map)gameItems.get(4)).get("p")).get("content");
					int idx = stadium.indexOf("\n");
					if (idx != -1) {
						tv = stadium.substring(idx + 1);
						stadium = stadium.substring(0, idx);
					}
				}
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = ((String)resultMap.get("content")).substring(0, 1);
					score = ((String)resultMap.get("content")).substring(1);					
					detailUrl = (String)resultMap.get("href");
				}
				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "HOME".equals(homeAway);
				oneRec[c++] = vsTeam;
				oneRec[c++] = tv;
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				insertDataList.add(oneRec);
				logger.info(compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
						+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
			}
			
			if(insertDataList.isEmpty()) {
				logger.warn("日程データが取得出来ませんでした");
				return -1;
			}
			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM " + teamId + "Results WHERE season=" + season);
            int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("試合日程・結果抽出エラー " + teamId, e);
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		RedsResultsSaver srv = new RedsResultsSaver();
		srv.extractResults();
	}


}
