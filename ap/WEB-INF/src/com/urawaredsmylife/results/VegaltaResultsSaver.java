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

/**
 * ベガルタ仙台公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class VegaltaResultsSaver {
	private Logger logger = Logger.getLogger(VegaltaResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "vegalta";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from"
			+ "%20html%20where%20url%3D%22http%3A%2F%2Fwww.vegalta.co.jp%2Fleagues%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40id%3D'main_contents'%5D%2Ftable%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public VegaltaResultsSaver() {
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
			for(int r=0; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null || gameItems.size() == 7) {	//7はプレシーズンマッチ
					continue;
				}
				String compe = StringUtils.trimToEmpty((String)((Map)gameItems.get(0)).get("p"));
				String compeName = "";
				if (compe.startsWith("YN")) {
					compeName = "YNC";
					compe = compe.replace("YN0", "").replace("YN", "") + "節";
				} else if (compe.startsWith("EM")) {
					compeName = "天皇杯";
					compe = compe.replace("EM_0", "");
				} else if (compe.startsWith("PSM")) {
					compeName = "プレシーズンマッチ";
					compe = "";
				} else if (compe.startsWith("ACL")) {
					compeName = "ACL";
					compe = compe.replace("ACL0", "");
				} else {
					compeName = "J";
					compe += "節";
					if(compe.startsWith("0")) {
						compe = compe.substring(1);
					}
				}
				compe = compeName + (compe.equals("")? "" : "/" + compe);
				String gameDateView = (String)((Map)gameItems.get(1)).get("p") + "(" + (String)((Map)gameItems.get(2)).get("p") + ")";
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
							.replace("月", "/").replace("日", "");
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)((Map)gameItems.get(3)).get("p");
				String stadium = (String)((Map)gameItems.get(5)).get("p");
				String homeAway = (String)((Map)game).get("class");
				String vsTeam = (String)((Map)gameItems.get(4)).get("p");
				String tv = (String)((Map)gameItems.get(6)).get("p");
				Map resultMap = (Map)((Map)gameItems.get(7)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
//				System.out.println("★" + resultMap);
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					int idx = 0;
					if (score.indexOf("●") != -1) {
						idx = score.indexOf("●");
					} else if (score.indexOf("○") != -1) {
						idx = score.indexOf("○");
					} else if (score.indexOf("△") != -1) {
						idx = score.indexOf("△");
					}
					result = score.substring(idx, idx + 1);
					score = score.substring(0, idx) + "-" + score.substring(idx + 1);
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
				oneRec[c++] = "home".equals(homeAway);
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
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		VegaltaResultsSaver srv = new VegaltaResultsSaver();
		srv.extractResults();
	}


}
