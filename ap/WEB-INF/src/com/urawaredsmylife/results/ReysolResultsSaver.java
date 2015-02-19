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

/**
 * 柏レイソル公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class ReysolResultsSaver {
	private Logger logger = Logger.getLogger(ReysolResultsSaver.class.getName());
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q="
			+ "select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.reysol.co.jp%2Fgame%2F"
			+ "results%2Findex.php%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game_results_tbl"
			+ "'%5D%22&format=json&callback=";

	/** チームID */
	private static final String teamId = "reysol";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public ReysolResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
        String[] compeList = new String[]{"ACL", "ACL", "J1 1st", "J1 2nd", "ﾅﾋﾞｽｺ", "天皇杯"};
		try {
			String resultsTable = teamId + "Results";
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			qr.update("DELETE FROM " + resultsTable + " WHERE season=" + season);
			GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL);
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			logger.info(json.toString());
			List<Object> gameGroupList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json
					.get("query")).get("results")).get("table");
			logger.info(gameGroupList.getClass().toString());
			
            String insertSql = "INSERT INTO " + resultsTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(int compeIdx = 0; compeIdx<gameGroupList.size(); compeIdx++) {
				Object tmp = ((Map)gameGroupList.get(compeIdx)).get("tr");
				List<Object> gameList = null;
				if (tmp instanceof Map) {
					gameList = new ArrayList<>();
					gameList.add(tmp);
				} else if (tmp instanceof List) {
					gameList = (List<Object>)tmp;
				}
	            for(int r=0; r<gameList.size(); r++) {
					Object game = gameList.get(r);
					boolean isHome = "yellow_zone".equals(((Map)game).get("class"));
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					if (gameItems.size() <= 2) {
						logger.info("日程候補：" + gameItems.get(0));
						continue;
					}
//					if (((Map)game).get("th") == null) {
//						logger.info("??：" + game + " ★gameItems.size()=" + gameItems.size());
//						continue;
//					}
					String compeName = compeList[compeIdx];
					String compe = (String)((Map)((Map)game).get("th")).get("p");
					if (NumberUtils.isDigits(compe)) {
						compe = "第" + compe + "節";
					}
					compe = compeName + "/" + compe;
					
					String gameDateView = null;
					String detailUrl = null;
					if (((Map)gameItems.get(0)).get("a") != null) {
						gameDateView = (String)((Map)((Map)gameItems.get(0)).get("a")).get("content");
						detailUrl = "http://www.reysol.co.jp/game/results/" + 
								(String)((Map)((Map)gameItems.get(0)).get("a")).get("href");
					} else {
						Object gameDateViewTmp = ((Map)gameItems.get(0)).get("p");
						if (gameDateViewTmp instanceof String) {
							gameDateView = (String)gameDateViewTmp;
						} else if (gameDateViewTmp instanceof Map) {
//							System.out.println("★" + gameDateViewTmp);
							gameDateView = (String)((Map)gameDateViewTmp).get("content");
						}
					}
					gameDateView = gameDateView.replace("･祝", "").replace("･休", "").replace("（", "(").replace("）", ")")
							.replaceAll("\n", "").trim();
					String gameDate = null;
					if(gameDateView.contains("(")) {//半角(
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
								.replace("月", "/").replace("日", "");
					} else {
						gameDate = "";	//未定等
						continue;
					}
					String time = null;
					if (gameItems.get(1) != null && ((Map)gameItems.get(1)).get("p") != null) {
						time = ((String)((Map)gameItems.get(1)).get("p")).replace("：", ":").replaceAll("※.*", "");
					}
					String stadium = (String)((Map)gameItems.get(2)).get("p");
					Object vsTeamTmp = ((Map)gameItems.get(3)).get("p");
					String vsTeam = null;
					if (vsTeamTmp instanceof String) {
						vsTeam = (String)vsTeamTmp;
					} else if (vsTeamTmp instanceof Map) {
//						System.out.println("★vsTeamTmp=" + vsTeamTmp);
						vsTeam = (String)((Map)vsTeamTmp).get("content");
					}
					if (vsTeam != null) {
						vsTeam = vsTeam.replaceAll("\n", "").replaceAll(" ", "").replaceAll("（", "(").replaceAll("）", ")");
					}
					String tv = null;
					if (((Map)gameItems.get(4)).get("p") instanceof Map) {
						tv = (String)((Map)((Map)gameItems.get(4)).get("p")).get("content");
					} else {
						tv = (String)((Map)gameItems.get(4)).get("p");
					}
					String result = StringUtils.trimToNull(((String)((Map)gameItems.get(5)).get("p"))
							.replaceAll(" ", ""));	//←普通の半角スペースとは違うらしい
//					System.out.println("★結果 [" + result + "]");
					String score = null;
					if (((Map)gameItems.get(6)).get("p") instanceof Map) {
						score = ((String)((Map)((Map)gameItems.get(6)).get("p")).get("content"))
								.replaceAll(" ", "");
					} else {
						score = (String)((Map)gameItems.get(6)).get("p");
					}
					score = toHankakuNum(score);
					if ("-".equals(score) || " ".equals(score)|| StringUtils.isBlank(score)) {
						score = null;		//↑普通の半角スペースとは違うらしい
					}
					
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
					logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " 
							+ isHome + ", " + vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
				}
			}
			if(insertDataList.isEmpty()) {
				logger.warn("日程データが取得出来ませんでした ");
			}
            int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("試合日程・結果抽出エラー", e);
		}
		return 0;
	}
	
	/**
	 * 半角変換
	 * @param text
	 * @return
	 */
	public static String toHankakuNum(String text) {
		StringBuilder res = new StringBuilder();
		final String listZens = "０１２３４５６７８９";
		final String listHans = "0123456789";

		for (int textIdx = 0; textIdx < text.length(); textIdx++) {
			char ch = text.charAt(textIdx);
			int listIdx = listZens.indexOf(ch);
			if (listIdx >= 0) {
				res.append(listHans.charAt(listIdx));
			} else {
				res.append(ch);
			}
		}

		return res.toString();
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		ReysolResultsSaver srv = new ReysolResultsSaver();
		srv.extractResults();
	}


}
