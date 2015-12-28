package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.QueryRunner;
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
 * サンフレッチェ広島公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class SanfrecceResultsSaver {
	private Logger logger = Logger.getLogger(SanfrecceResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "sanfrecce";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html"
			+ "%20where%20url%3D%22http%3A%2F%2Fwww.sanfrecce.co.jp%2Finfo%2Fgame_schedule%2F%22%20"
			+ "and%20xpath%3D%22%2F%2Fdiv%5B%40class%3D'section'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public SanfrecceResultsSaver() {
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
			logger.info("gameList=" + gameList);
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            int compeIdx = 0;
            String[] compeList = new String[] {"FIFAｸﾗﾌﾞﾜｰﾙﾄﾞｶｯﾌﾟ", "ﾁｬﾝﾋﾟｵﾝｼｯﾌﾟ", "J1 1st", "J1 2nd", "ナビスコ", "天皇杯"};
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null) {
					compeIdx++;
					continue;
				}
				
				
				if (!(gameItems.get(4) instanceof Map)) {
					System.out.println("game🌟=" + game);
				}

				
				
				String compe = null;
				String matchNo = ((String)gameItems.get(0)).replaceAll("※.*", "");
				compe = compeList[compeIdx]
						+ ("/" + matchNo)
						+ (NumberUtils.isDigits(matchNo) ? "節" : "");
				String gameDateView = ((String)gameItems.get(1))
						.replaceAll("祝", "").replace("・", "").replace("()", "").replace("\n", "");
//				System.out.println("★" + gameDateView);
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
							.replace("月", "/").replace("日", "");
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)gameItems.get(2);
				String stadium = "";
				if (gameItems.get(5) instanceof Map && ((Map)gameItems.get(5)).get("a") != null) {
					stadium = (String)((Map)((Map)gameItems.get(5)).get("a")).get("content");
				} else {
					stadium = (String)gameItems.get(5);
				}
				String homeAway = "エディオンスタジアム広島".equals(stadium) ? "H" : "A";
				String vsTeam = (String)gameItems.get(3);
				String tv = (String)gameItems.get(6);
				Map resultMap = null;
				String result = null;
				String score = null;
				String detailUrl = null;
				if (gameItems.get(4) instanceof Map) {
					resultMap = (Map)((Map)gameItems.get(4)).get("a");
				} else {
					Map<String, Object> map = ((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results"));
					System.out.println("map=" + map);
					score = (String)gameItems.get(4);
				}
//				System.out.println("★" + resultMap);
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					int hiroshimaScore = Integer.parseInt(score.substring(0, score.indexOf("-")));
					int vsTeamScore = Integer.parseInt(score.substring(score.indexOf("-") + 1));
					if (hiroshimaScore < vsTeamScore) {
						result = "●";
					} else if (hiroshimaScore == vsTeamScore) {
						result = "△";
					} else {
						result = "○";
					}
					detailUrl = "http://www.sanfrecce.co.jp/info/" + ((String)resultMap.get("href")).replace("../", "");
				}
				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "H".equals(homeAway);
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
		SanfrecceResultsSaver srv = new SanfrecceResultsSaver();
		srv.extractResults();
	}


}
