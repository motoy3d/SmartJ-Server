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
 * 徳島ヴォルティス公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class VortisResultsSaver {
	private Logger logger = Logger.getLogger(VortisResultsSaver.class.getName());
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL_J = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from"
			+ "%20html%20where%20url%3D%22http%3A%2F%2Fwww.vortis.jp%2Fgame%2Findex.php%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40id%3D'wrapMain'%5D%2Ftable%2Ftr%22&format=json&callback=";
	private static final String SRC_URL_NABISCO = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20"
			+ "from%20html%20where%20url%3D%22http%3A%2F%2Fwww.vortis.jp%2Fgame%2Findex.php%3"
			+ "Ftype%3D9%26cd%3D45%26backnumber%3D%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40id%3D'wrapMain'%5D%2Ftable%2Ftr%22&format=json&callback=";
	private static final String SRC_URL_TENNOHAI = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20"
			+ "from%20html%20where%20url%3D%22http%3A%2F%2Fwww.vortis.jp%2Fgame%2Findex.php%3"
			+ "Ftype%3D9%26cd%3D46%26backnumber%3D%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40id%3D'wrapMain'%5D%2Ftable%2Ftr%22&format=json&callback=";

	/** チームID */
	private static final String teamId = "vortis";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public VortisResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		String[] urls = new String[] {SRC_URL_J, /*SRC_URL_NABISCO,*/ SRC_URL_TENNOHAI};
        String[] compeList = new String[]{"J", "天皇杯"};
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
				for(int r=0; r<gameList.size(); r+=3) {
					Object game = gameList.get(r);
//					System.out.println("★" + r);
					boolean isHome = false;
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					if (gameItems == null || gameItems.isEmpty()) {
						continue;	//ヘッダ
					}
//					if (gameItems.size() != 8) {
//						rowIdx++;
//						continue;	//試合なしの場合
//					}
					
					String compeName = null;
					if (compeIdx == 0) {
						compeName = "J2";
					} else if (compeIdx == 1){
						compeName = "天皇杯";
					}
					String compe = (String)((Map)gameItems.get(0)).get("p");
					if (NumberUtils.isDigits(compe)) {
						compe = "第" + compe + "節";
					}
					compe = compeName + "/" + compe;
					System.out.println("compe=" + compe);
					System.out.println("gameList.get(1) = " + gameItems.get(1));
					String gameDateView = (String)((Map)gameItems.get(1)).get("p");
					String time = null;

					if (gameDateView.contains(")")) {
						time = gameDateView.substring(gameDateView.indexOf(")") + 1);
						gameDateView = gameDateView.substring(0, gameDateView.indexOf(")") + 1);
					}
					String detailUrl = null;
					gameDateView = gameDateView.replace("･祝", "").replace("･休", "").replace("（", "(").replace("）", ")");
	System.out.println("★gameDateView=" + gameDateView + "   time=" + time);
					String gameDate = null;
					if(gameDateView.contains("(")) {//半角(
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
								.replace("月", "/").replace("日", "");
					} else {
						gameDate = "";	//未定等
					}
					Object game2 = gameList.get(r+1);	//2行目
					Map vsTeamMap = (Map)((Map)game2).get("td");
					String vsTeam = (String)vsTeamMap.get("p");
					if ("試合なし".equals(vsTeam)) {
						continue;
					}
					
					Object game3 = gameList.get(r+2);	//3行目
					Map stadiumMap = (Map)((Map)game3).get("td");
					String stadium = null;
					if (stadiumMap.get("p") instanceof Map) {
						stadium = ((String)((Map)stadiumMap.get("p")).get("content")).replace("\n", "").trim();
					}
					
					String tv = null;
					String resultAndScore = (String)((Map)((Map)gameItems.get(3)).get("p")).get("content");
					String result = null;
					String score = null;
					if ("-".equals(resultAndScore)) {
						score = "";
					} else {
						result = resultAndScore.substring(0,1);
						if ("-".equals(result)) {
							result = null;
						}
						score = resultAndScore.substring(1).replace("\n", "").trim();
						Object detailUrlDiv = ((Map)gameItems.get(3)).get("div");
						if (detailUrlDiv != null) {
							detailUrl = "http://www.vortis.jp/game/" + (String)((Map)((Map)detailUrlDiv).get("a")).get("href");
						}
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
					logger.info("■" + compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + isHome + ", " 
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
		VortisResultsSaver srv = new VortisResultsSaver();
		srv.extractResults();
	}


}
