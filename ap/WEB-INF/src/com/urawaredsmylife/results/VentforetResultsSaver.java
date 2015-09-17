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
 * ヴァンフォーレ甲府公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class VentforetResultsSaver {
	private Logger logger = Logger.getLogger(VentforetResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "ventforet";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from"
			+ "%20html%20where%20url%3D%22http%3A%2F%2Fwww.ventforet.jp%2Fgames%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40class%3D'game-day'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public VentforetResultsSaver() {
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
			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
			logger.info(gameList.getClass().toString());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            String[] compeList = new String[] {"J1 1st", "J1 2nd", "ナビスコ", "天皇杯", "プレシーズン"};
            int compeIdx = 0;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				boolean isHeader = "bg".equals((String)((Map)game).get("class"));
				List<Object> gameItems = (List<Object>)((Map)game).get("th");
				if (gameItems == null || isHeader) {
					compeIdx++;
					continue;
				}
				String compe = compeList[compeIdx] + "/" 
						+ StringUtils.trimToEmpty((String)((Map)gameItems.get(0)).get("content")).replaceAll("※.*", "");
				String gameDateView = ((String)((Map)gameItems.get(1)).get("content"))
						.replaceAll("・祝", "").replaceAll("・休", "").replace("                       ", "");
				String time = gameDateView.substring(gameDateView.indexOf(")") + 1);
				gameDateView = gameDateView.substring(0, gameDateView.indexOf(" "));
				
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = null;	//未定等
				}
				String stadium = (String)((Map)gameItems.get(3)).get("content");
				String homeAway = null;
				if (((Map)(Map)gameItems.get(3)).get("img") != null) {
					homeAway = (String)((Map)((Map)gameItems.get(3)).get("img")).get("alt");
				}
				String vsTeam = null;
				if (((Map)gameItems.get(2)).get("div") != null) {
					List<Object> vsList = (List)((Map)gameItems.get(2)).get("div");
					if (vsList != null) {
						vsTeam = (String)((Map)vsList.get(1)).get("content");
					}
				}
				String tv = null;
				if (((Map)gameItems.get(4)).get("div") != null) {
					List<Object> tvList = (List)((Map)gameItems.get(4)).get("div");
					if (tvList != null) {
						tv = (String)((Map)tvList.get(0)).get("content");
					}
				}
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
//				System.out.println("★" + resultMap);
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					result = score.substring(0, 1);
					score = score.substring(1);

					// ホームが左になっている
					if (score.contains("PK")) {
						//特になし
					} else {
						int homeScore = Integer.parseInt(score.substring(0, score.indexOf("-")));
						int awayScore = Integer.parseInt(score.substring(score.indexOf("-") + 1));
						if ("○".equals(result)) {
							if (awayScore < homeScore) {
								score = homeScore + "-" + awayScore;
							} else {
								score = awayScore + "-" + homeScore;
							}
						} else if ("●".equals(result)) {
							if (awayScore < homeScore) {
								score = awayScore + "-" + homeScore;
							} else {
								score = homeScore + "-" + awayScore;
							}
						}
					}
					detailUrl = "http://www.ventforet.jp" + (String)resultMap.get("href");
				}

				compe = StringUtils.trim(StringUtils.replace(compe, "\n", ""));
				gameDate = StringUtils.trim(StringUtils.replace(gameDate, "\n", ""));
				gameDateView = StringUtils.trim(StringUtils.replace(gameDateView, "\n", ""));
				time = StringUtils.trim(StringUtils.replace(time, "\n", ""));
				stadium = StringUtils.trim(StringUtils.replace(stadium, "\n", ""));
				vsTeam = StringUtils.trim(StringUtils.replace(vsTeam, "\n", ""));
				homeAway = StringUtils.trim(StringUtils.replace(homeAway, "\n", ""));
				tv = StringUtils.trim(StringUtils.replace(tv, "\n", ""));
				result = StringUtils.trim(StringUtils.replace(result, "\n", ""));
				score = StringUtils.trim(StringUtils.replace(score, "\n", ""));
				detailUrl = StringUtils.trim(StringUtils.replace(detailUrl, "\n", ""));

				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "home".equals(homeAway);
				oneRec[c++] = vsTeam;
				oneRec[c++] = tv;
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				insertDataList.add(oneRec);
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
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
		VentforetResultsSaver srv = new VentforetResultsSaver();
		srv.extractResults();
	}


}
