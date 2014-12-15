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
 * セレッソ大阪公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class CerezoResultsSaver {
	private Logger logger = Logger.getLogger(CerezoResultsSaver.class.getName());
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL_BASE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20"
			+ "from%20html%20where%20url%3D%22http%3A%2F%2Fwww.cerezo.co.jp%2Fgame_schedule1.asp"
			+ "%3Fcode_s%3D{COMPE_ID}%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game_table'%5D%2Ftr%22&format=json&callback=";
	private static final String COMPE_ID_J = "101100";
	private static final String COMPE_ID_NABISCO = "101101";
	private static final String COMPE_ID_TENNOHAI = "101102";
	private static final String COMPE_ID_ACL = "101106";

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
		String[] htmls = new String[] {COMPE_ID_J, COMPE_ID_NABISCO, COMPE_ID_TENNOHAI, COMPE_ID_ACL};
        String[] compeList = new String[]{"J", "YNC", "天皇杯", "ACL"};
		try {
			String resultsTable = teamId + "Results";
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			qr.update("DELETE FROM " + resultsTable + " WHERE season=" + season);
            int minusIdx = 0;
			for(int compeIdx=0; compeIdx<htmls.length; compeIdx++) {
	            minusIdx = (compeIdx == 1 || compeIdx == 2)? 1 : 0;
				String compeId = htmls[compeIdx];
				String srcUrl = SRC_URL_BASE.replace("{COMPE_ID}", compeId);
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
	            int leg = 1;
	            String prevCompe = "";
				for(int r=1; r<gameList.size(); r++) {
					Object game = gameList.get(r);
	//				System.out.println("xx=" + ((Map)game));
					boolean isHome = "home_game".equals(((Map)game).get("class"));
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					
					String compe = "";
					if (minusIdx == 0) {
						compe = (String)((Map)gameItems.get(0)).get("p");
						boolean isR = compe.startsWith("R");
						if (isR) {
							if (prevCompe.equals(compe)) {
								leg = 2;
							} else {
								leg = 1;
							}
						}
						prevCompe = compe;
						compe = compeList[compeIdx] + "/" + compe + (isR? "-" + leg : "");
						if(compeIdx == 0 || (compeIdx == 3 && !isR)) {
							compe += "節";
						}
					} else {
						compe = compeList[compeIdx];
					}
					
					String gameDateView = ((String)((Map)gameItems.get(1 - minusIdx)).get("p"))
							+ "(" + ((String)((Map)gameItems.get(2 - minusIdx)).get("p")).replace("･祝", "") + ")";
					String gameDate = null;
					if(gameDateView.contains("(")) {//半角(
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
					} else {
						gameDate = "";	//未定等
					}
					if(!"".equals(gameDate)) {
						gameDate = gameDate.replaceAll("月", "/").replaceAll("日", "");
					}
					String time = ((String)((Map)gameItems.get(3 - minusIdx)).get("p")).replace("：", ":");
					String stadium = "";
					if(((Map)gameItems.get(5 - minusIdx)).get("a") != null) {
						stadium = (String)((Map)((Map)gameItems.get(5 - minusIdx)).get("a")).get("content");
					} else {
						stadium = (String)((Map)gameItems.get(5 - minusIdx)).get("p");
					}
					String vsTeam = (String)((Map)gameItems.get(4 - minusIdx)).get("p");
					String tv = "";
					Map resultMap = (Map)gameItems.get(6 - minusIdx) == null? 
							null : (Map)((Map)gameItems.get(6 - minusIdx)).get("a");
					String result = null;
					String score = null;
					String detailUrl = null;
					if (resultMap != null) {
						score = ((String)resultMap.get("content")).replaceAll(" ", "");
						result = score.substring(0, 1);
						score = score.substring(1);
						detailUrl = "http://www.cerezo.co.jp" + ((String)resultMap.get("href"));
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
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		CerezoResultsSaver srv = new CerezoResultsSaver();
		srv.extractResults();
	}


}
