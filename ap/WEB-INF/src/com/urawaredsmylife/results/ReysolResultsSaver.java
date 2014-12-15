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
	private static final String SRC_URL_J_TENNOHAI = "https://query.yahooapis.com/v1/public/yql?q="
			+ "select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.reysol.co.jp%2Fgame%2F"
			+ "results%2Findex.php%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game_results_tbl"
			+ "'%5D%2Ftr%22&format=json&callback=";
	private static final String SRC_URL_NABISCO = "https://query.yahooapis.com/v1/public/yql?q="
			+ "select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.reysol.co.jp%2Fgame%2F"
			+ "results%2Findex.php%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game_results_tbl"
			+ "%20m20'%5D%2Ftr%22&format=json&callback=";

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
		String[] urls = new String[] {SRC_URL_J_TENNOHAI, SRC_URL_NABISCO};
        String[] compeList = new String[]{"J1", "YNC", "天皇杯", "ACL"};
		try {
			String resultsTable = teamId + "Results";
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			qr.update("DELETE FROM " + resultsTable + " WHERE season=" + season);
			for(int compeIdx=0; compeIdx<urls.length; compeIdx++) {
				String srcUrl = urls[compeIdx];
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
				List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json
						.get("query")).get("results")).get("tr");
				logger.info(gameList.getClass().toString());
				
	            String insertSql = "INSERT INTO " + resultsTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
	            List<Object[]> insertDataList = new ArrayList<Object[]>();
				for(int r=0; r<gameList.size(); r++) {
					Object game = gameList.get(r);
	//				System.out.println("xx=" + ((Map)game));
					boolean isHome = "yellow_zone".equals(((Map)game).get("class"));
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					boolean isTennohai = compeIdx == 0 && gameItems.size() == 7;
					if (!isTennohai && gameItems.size() != 8) {
						continue;	//試合なしの場合
					}
					
					String compeName = null;
					if (compeIdx == 0) {
						if (isTennohai) {
							compeName = "天皇杯";
						} else {
							compeName = "J1";
						}
					} else {
						compeName = "YNC";
					}
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
						gameDateView = (String)((Map)gameItems.get(0)).get("p");
					}
					gameDateView = gameDateView.replace("･祝", "").replace("･休", "").replace("（", "(").replace("）", ")");
					String gameDate = null;
					if(gameDateView.contains("(")) {//半角(
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
								.replace("月", "/").replace("日", "");
					} else {
						gameDate = "";	//未定等
					}
					String time = ((String)((Map)gameItems.get(1)).get("p")).replace("：", ":");
					String stadium = (String)((Map)gameItems.get(2)).get("p");
					String vsTeam = (String)((Map)gameItems.get(3)).get("p");
					String tv = null;
					if (((Map)gameItems.get(4)).get("p") instanceof Map) {
						tv = (String)((Map)((Map)gameItems.get(4)).get("p")).get("content");
					} else {
						tv = (String)((Map)gameItems.get(4)).get("p");
					}
					String result = (String)((Map)gameItems.get(isTennohai? 5 : 6)).get("p");
					String score = null;
					if (((Map)gameItems.get(isTennohai? 6 : 7)).get("p") instanceof Map) {
						score = ((String)((Map)((Map)gameItems.get(isTennohai? 6 : 7)).get("p")).get("content"))
								.replaceAll(" ", "");
					} else {
						score = (String)((Map)gameItems.get(isTennohai? 6 : 7)).get("p");
					}
					score = toHankakuNum(score);
					if ("-".equals(score)) {
						score = "";
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
					logger.info(compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + isHome + ", " 
							+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
				}
				
				if(insertDataList.isEmpty()) {
					logger.warn("日程データが取得出来ませんでした " + compeList[compeIdx]);
					continue;
				}
	            int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
	            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
			}
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
