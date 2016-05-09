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
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

/**
 * ヴィッセル神戸公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class VisselResultsSaver {
	private Logger logger = Logger.getLogger(VisselResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "vissel";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.vissel-kobe.co.jp%2Fmatch%2F%22%20and%20xpath%3D%22%2F%2Fdl%2Fdd%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public VisselResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		logger.info(SRC_URL);
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL);
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)
					json.get("query")).get("results")).get("dd");
			logger.info(gameList.getClass().toString());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null || gameItems.size() == 7) {	//7はプレシーズンマッチ
					continue;
				}
				String compeName = (String)((Map)((Map)gameItems.get(0)).get("img")).get("src");
				if (compeName.endsWith("logo-presea.png")) {
					compeName = "プレシーズンマッチ";
				} else if (compeName.contains("logo-j")) {
					compeName = "J1";
				} else if (compeName.endsWith("logo-ync.png")) {
					compeName = "ナビスコ";
				} else if (compeName.endsWith("logo-emp.png")) {
					compeName = "天皇杯";
				} else if (compeName.endsWith("logo-acl.png")) {
					compeName = "ACL";
				} else {
					compeName = null;
				}
				String compe = null;
				if ("プレシーズンマッチ".equals(compeName) || compeName == null) {
					compe = compeName;
				} else {
					if (((Map)gameItems.get(1)).get("content") instanceof Map) {
						compe = compeName + "/" + ((String)((Map)((Map)gameItems.get(1)).get("content")).get("content"))
								.replaceAll("\n", "").replaceAll(" ", "");
					} else {
						compe = compeName + "/" + StringUtils.trimToEmpty((String)((Map)gameItems.get(1)).get("content"));
					}
				}
				String gameDateView = ((String)((Map)gameItems.get(2)).get("content"))
						.replaceAll("・祝", "").replaceAll("・休", "").replace("\n", "")
						.replace(" ", "").replace("（", "(").replace("）", ")");
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)((Map)gameItems.get(3)).get("content");
				String stadium = null;
				if (((Map)gameItems.get(5)).get("a") != null) {
					stadium = (String)((Map)((Map)gameItems.get(5)).get("a")).get("content");
				}
//				System.out.println("★" + (Map)gameItems.get(5));
				String homeAway = null;
				if (((Map)(Map)gameItems.get(5)).get("span") != null) {
					homeAway = (String)((Map)((Map)gameItems.get(5)).get("span")).get("content");
				}
				String vsTeam = (String)((Map)gameItems.get(4)).get("content");
				String tv = null;
				Map resultMap = (Map)((Map)gameItems.get(6)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
//				System.out.println("★" + resultMap);
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					result = score.substring(0, 1);
					score = score.substring(1);
					detailUrl = "http://www.vissel-kobe.co.jp/match/" + (String)resultMap.get("href");
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
			
			if (insertDataList.isEmpty()) {
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
		VisselResultsSaver srv = new VisselResultsSaver();
		srv.extractResults();
	}


}
