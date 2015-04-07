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
 * 湘南ベルマーレ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class BellmareResultsSaver {
	private Logger logger = Logger.getLogger(BellmareResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "bellmare";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.bellmare.co.jp%2Ffixtures_{SEASON}%22%20and%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'jleague_fixtures'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public BellmareResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
        String season = new SimpleDateFormat("yyyy").format(new Date());
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL.replace("{SEASON}", season));
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			//logger.info("json = " + ((Map)((Map)((List)((Map)((Map)json.get("query")).get("results")).get("table")).get(0)).get("tbody")).get("tr"));
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(int r=0; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				System.out.println(r + "------------------------------");
				if (((Map)game).get("td") instanceof Map) {
					logger.info("無観客試合など");
					continue;
				}
				//1行の中にthとtdが混在している
				List<Object> gameItemsTh = (List<Object>)((Map)game).get("th");
				List<Object> gameItemsTd = (List<Object>)((Map)game).get("td");
				if(gameItemsTd == null) {
					continue;
				}
				if (gameItemsTd.get(0) == null) {
					logger.info("無観客試合など");
					continue;
				}
//				System.out.println("th0 : " + gameItemsTh.get(0));
//				System.out.println("th1 : " + gameItemsTh.get(1));
//				System.out.println("th2 : " + gameItemsTh.get(2));
//				System.out.println("td0 : " + gameItemsTd.get(0));
//				System.out.println("td1 : " + gameItemsTd.get(1));
//				System.out.println("td2 : " + gameItemsTd.get(2));
//				System.out.println("td3 : " + gameItemsTd.get(3));
				String compe = "";
//				System.out.println("gameItems.get(1)=" + gameItems.get(1));
				if (gameItemsTh.get(1) instanceof Map) {
					compe = ((String)((Map)gameItemsTh.get(1)).get("content")).replace("\n","/");
				} else {
					compe = (String)gameItemsTh.get(1);
				}
				compe = compe.replace("ステージ", "");
				String compeImg = (String)((Map)((Map)gameItemsTh.get(0)).get("img")).get("src");
				String compeName = "";
				if (compeImg.endsWith("j1_s.png")) {
					compeName = "J1 ";
				} else if (compeImg.endsWith("ync_s.png")) {
					compeName = "ナビスコ";
				}
				compe = compeName + compe;
				String gameDateView = null;
				String time = null;
				if (gameItemsTd.get(0) instanceof Map) {
					gameDateView = (String)((Map)gameItemsTd.get(0)).get("content");
				} else {
					gameDateView = (String)gameItemsTd.get(0);
				}
				if (StringUtils.isNotBlank(gameDateView)) {
					gameDateView = gameDateView.replaceAll("\n", "").replaceAll("<br/>", "").replaceAll("※.*", "")
							.replace("（", "(").replace("）", ")").replace("月", "/").replaceFirst("日", "");
				}
				System.out.println("日●" + gameDateView + " [" + gameItemsTd.get(0) + "]");
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				//時間
				if (gameItemsTh.get(2) instanceof Map) {
					time = (String)((Map)gameItemsTh.get(2)).get("content");
				} else {
					time = (String)gameItemsTh.get(2);
				}
				//スタジアム
				String stadium = null;
				if (gameItemsTd.get(2) instanceof Map) {
					stadium = (String)((Map)gameItemsTd.get(2)).get("content");
				} else {
					stadium = (String)gameItemsTd.get(2);
				}
				
				boolean homeFlg = "BMWス".equals(stadium);
				String vsTeam = null;
				if (gameItemsTd.get(1) instanceof Map) {
					vsTeam = (String)((Map)gameItemsTd.get(1)).get("content");
				} else {
					vsTeam = (String)gameItemsTd.get(1);
				}
				String tv = null;
				Map resultMap = (Map)((Map)gameItemsTd.get(3)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = (String)((Map)resultMap.get("span")).get("content");
					score = ((String)resultMap.get("content")).trim();
					if (!homeFlg) {
						int homeScore = Integer.parseInt(score.substring(0, score.indexOf("-")));
						int awayScore = Integer.parseInt(score.substring(score.indexOf("-") + 1));
						score = awayScore + "-" + homeScore;
					}
					detailUrl = (String)resultMap.get("href");
				}
				if ("試合前".equals(result)) {
					result = "";
				}
				compe = StringUtils.trim(StringUtils.replace(compe, "\n", ""));
				gameDate = StringUtils.trim(StringUtils.replace(gameDate, "\n", ""));
				gameDateView = StringUtils.trim(StringUtils.replace(gameDateView, "\n", ""));
				time = StringUtils.trim(StringUtils.replace(time, "\n", ""));
				stadium = StringUtils.trim(StringUtils.replace(stadium, "\n", ""));
				vsTeam = StringUtils.trim(StringUtils.replace(vsTeam, "\n", ""));
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
				oneRec[c++] = homeFlg;
				oneRec[c++] = vsTeam;
				oneRec[c++] = tv;
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				insertDataList.add(oneRec);
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeFlg + ", " 
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
		BellmareResultsSaver srv = new BellmareResultsSaver();
		srv.extractResults();
	}


}
