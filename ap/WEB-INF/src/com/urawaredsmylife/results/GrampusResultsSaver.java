package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

/**
 * 名古屋グランパス公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class GrampusResultsSaver {
	private Logger logger = Logger.getLogger(GrampusResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "grampus";
	/** 試合詳細URLのベース */
	private static final String DETAIL_URL_BASE = "http://nagoya-grampus.jp";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D'http%3A%2F%2Fnagoya-grampus.jp%2Fgame%2F'%20"
			+ "and%20xpath%3D'%2F%2Fdiv%5B%40class%3D%22asset-content%20table-block%22%5D'&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public GrampusResultsSaver() {
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
			//logger.info(json.toString());
			List<Object> compeList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("div");
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            String[] compeNameList = new String[]{"J1 1st", "J1 2nd", "ナビスコ", "天皇杯", "プレシーズン"};
            for(int compeIdx=0; compeIdx<compeList.size(); compeIdx++) {
            	//TODO 天皇杯が始まったら、3を4にする
				if (compeIdx >= 3) {	//プレシーズンは除外
					break;
				}
				Map compeMap = (Map)compeList.get(compeIdx);
				List<Object> gameList = (List<Object>)((Map)((Map)compeMap.get("table")).get("tbody")).get("tr");
//				logger.info(compeIdx + ". gameList.size=" + gameList.size());
				
				for(int r=1; r<gameList.size(); r++) {
					Object game = gameList.get(r);
					//System.out.println("▲game=" + ((Map)game));
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					if(gameItems == null) {
						continue;	//ヘッダはthなので飛ばす
					}
					Object gameNumberTmp = ((Map)gameItems.get(1)).get("content");
					String gameNumber = null;
					if (gameNumberTmp instanceof String) {
						gameNumber = StringUtils.trimToEmpty((String)gameNumberTmp);
					} else if (gameNumberTmp instanceof Map) {
						System.out.println("■gameNumber " + (Map)gameNumberTmp);
						gameNumber = StringUtils.trimToEmpty((String)((Map)gameNumberTmp).get("content"));
					}
					String compe = compeNameList[compeIdx] + " " + gameNumber;
					if((compeIdx <= 2) && NumberUtils.isDigits(gameNumber)) {	//ナビスコ、Jリーグ
						compe += "節";
					}
					else if(compeIdx == 3 && NumberUtils.isDigits(gameNumber)) {	//天皇杯
						compe += "回戦";
					}
					String gameDateView = null;
					if (((Map)gameItems.get(2)).get("content") instanceof String) {
						gameDateView = (String)((Map)gameItems.get(2)).get("content");
					} else {
						gameDateView = (String)((Map)((Map)gameItems.get(2)).get("content")).get("content");
					}
					String time = null;
//logger.info("■gameDateView = [" + gameDateView + "]");
					if (gameDateView != null && 2 <= gameDateView.split("）").length) {
						time = gameDateView.split("）")[1].trim().replace("：", ":");
						gameDateView = gameDateView.split("）")[0].trim().replace("（", "(") + "）";
//						logger.info("  time=[" + time + "]");
//						logger.info("  gameDateView=[" + gameDateView + "]");
					} else {
						gameDateView = gameDateView.trim().replace("（", "(").replace("）", ")");
					}
					String gameDate = null;
					if(gameDateView != null && gameDateView.contains("(")) {
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
					} else {
						gameDate = "";	//未定等
					}
					String stadium = ((String)((Map)gameItems.get(4)).get("content")).replace("\n", "").trim();
					String homeAway = ((Map)gameItems.get(4)).get("img") != null? (String)((Map)((Map)gameItems.get(4)).get("img")).get("alt") : "";
					String vsTeam = ((Map)gameItems.get(3)).get("div") != null ? 
							(String)((Map)((Map)gameItems.get(3)).get("div")).get("span") 
							: (String)((Map)((Map)gameItems.get(3))).get("content");
					String tv = null;	//TODO TV
					Map resultMap = null;
					Object resultTmp = gameItems.get(5);
					if (resultTmp instanceof Map) {
						resultMap = (Map)((Map)resultTmp).get("p");;
					}
					String result = null;
					String score = null;
					String detailUrl = null;
					if(resultMap != null && resultMap.get("span") != null) {
						System.out.println("🌟" + resultMap);
						result = ((String)((Map)((List)resultMap.get("span")).get(0)).get("content")).substring(0, 1);
						score = ((String)((Map)((List)resultMap.get("span")).get(0)).get("content")).substring(2);
						if (score.indexOf("PK") != -1) {
							score = score.replace("［", "\n(").replace("］", ")").replace("：", "").replace(" ", "\n");
						}
						detailUrl = DETAIL_URL_BASE + (String)((Map)((Map)((List)resultMap.get("span")).get(1)).get("a")).get("href");
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
					logger.info("■" + compe + ", " + gameDate + "," + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
							+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
				}
				
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
			Mail.send(e);
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		GrampusResultsSaver srv = new GrampusResultsSaver();
		srv.extractResults();
	}


}
