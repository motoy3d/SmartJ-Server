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
 * ジュビロ磐田公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class JubiloResultsSaver {
	private Logger logger = Logger.getLogger(JubiloResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "jubilo";
	/** 試合詳細URLのベース */
//	private static final String DETAIL_URL_BASE = "http://www.jubilo-iwata.co.jp/live/!YEAR!/";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D%22http%3A%2F%2Fwww.jubilo-iwata.co.jp%2Fmatch%2F!YEAR!%2F%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'list%20f12'%5D%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public JubiloResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
        String season = new SimpleDateFormat("yyyy").format(new Date());
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		logger.info(SRC_URL.replace("!YEAR!", season));
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL.replace("!YEAR!", season));
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
//			logger.info("##### gameList\n" + gameList);

            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String[] compeNameList = new String[]{"J1 1st", "J1 2nd", "ナビスコ", "天皇杯", "練習・親善試合"};
            int compeIdx = 0;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				//System.out.println("▲game=" + ((Map)game));
				Object thItems = ((Map)game).get("th");
				if (thItems != null) {
					if (thItems instanceof Map) {	//thが１つしかなくListにならずMapになる。ヘッダは複数thがあるのでList
						System.out.println("############ 月の変わり目");
						continue;
					} else {
						System.out.println("############ ヘッダ");
						compeIdx++;
						if (compeIdx == 4) {
							break;
						}
					}
					continue;	//ヘッダはthなので飛ばす
				}
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if(gameItems == null) {
					System.out.println("############ gameItems is null　本来ありえない");
					continue;	//本来ありえない
				}
				Object gameNumberTmp = ((Map)gameItems.get(0)).get("content");
				System.out.println("■gameNumberTmp " + gameNumberTmp);
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
				System.out.println("compe: " + compe);
				String gameDateView = null;
				if (((Map)gameItems.get(1)).get("content") instanceof String) {
					gameDateView = (String)((Map)gameItems.get(1)).get("content");
				} else {
					gameDateView = (String)((Map)((Map)gameItems.get(1)).get("content")).get("content");
				}
				String time = (String)((Map)gameItems.get(2)).get("content");
				System.out.println("■time = [" + time + "]");
				if (gameDateView != null && 2 <= gameDateView.split("）").length) {
					gameDateView = gameDateView.split("）")[0].trim().replace("（", "(") + "）";
//						logger.info("  time=[" + time + "]");
//						logger.info("  gameDateView=[" + gameDateView + "]");
				} else {
					gameDateView = gameDateView.trim().replace("（", "(").replace("）", ")");
				}
System.out.println("■gameDateView = [" + gameDateView + "]");
				String gameDate = null;
				if(gameDateView != null && gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
//				System.out.println("■gameDate = [" + gameDate + "]");
				String stadium = ((String)((Map)gameItems.get(5)).get("content")).replace("\n", "").trim();
//				System.out.println("■stadium = [" + stadium + "]");
				Map homeAwayImg = (Map)((Map)gameItems.get(5)).get("img");
				String homeAway = "AWAY";
				if (homeAwayImg != null && ((String)(homeAwayImg.get("src"))).endsWith("home.png")) {
					homeAway = "HOME";
				}
//				System.out.println("■homeAway = [" + homeAway + "]    " + gameItems.get(5));
				List vsList = (List)((Map)gameItems.get(3)).get("span");
				String vsTeam = (String)((Map)vsList.get(1)).get("content");
				System.out.println("■vsTeam = [" + vsTeam + "]    " + vsList.get(1));
				String tv = (String)((Map)gameItems.get(6)).get("content");	//TODO TV
				System.out.println("tv = [" + tv + "]    " + gameItems.get(6));
				Map resultMap = null;
				Object resultTmp = gameItems.get(4);
				System.out.println("resultTmp=" + resultTmp);
				if (resultTmp instanceof Map) {
					resultMap = (Map)((Map)resultTmp).get("a");;
				}
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					System.out.println("🌟" + resultMap);
					result = ((String)resultMap.get("content")).substring(0, 1);
					score = ((String)resultMap.get("content")).substring(1);
					if (score.indexOf("PK") != -1) {
						score = score.replace("［", "\n(").replace("］", ")").replace("：", "").replace(" ", "\n");
					}
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
				logger.info("■" + compe + ", " + gameDate + "," + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
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
			Mail.send(e);
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		JubiloResultsSaver srv = new JubiloResultsSaver();
		srv.extractResults();
	}


}
