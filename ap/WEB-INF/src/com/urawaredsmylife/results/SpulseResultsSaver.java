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
 * 清水エスパルス公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class SpulseResultsSaver {
	private Logger logger = Logger.getLogger(SpulseResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "spulse";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D%22http%3A%2F%2Fwww.s-pulse.co.jp%2Fgames%2F%22%20"
			+ "and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'general-table2'%5D%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public SpulseResultsSaver() {
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
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            String[] compeList = new String[]{"J2", "天皇杯"};
            int compeIdx = 0;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
//				System.out.println("xx=" + ((Map)game));
				if (((Map)game).get("td") instanceof Map) {
					logger.info("大会名の行");
					compeIdx++;
					if(compeIdx == 3) {	//TODO 天皇杯が始まったら4に変更
						break;
					}
					continue;
				}
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if(gameItems == null) {
					logger.info("ヘッダ行");
					continue;
				}
				String compe = compeList[compeIdx] + " " + StringUtils.trimToEmpty((String)((Map)gameItems.get(0)).get("content"));
				String gameDateTime = (String)((Map)gameItems.get(1)).get("content");
				String time = gameDateTime.split("）")[1];
				String gameDateView = gameDateTime.split("）")[0].replace("（", "(").replace("）", ")") + "）";
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
							.replace("月", "/").replace("日", "");
				} else {
					gameDate = "";	//未定等
				}
				String stadium = (String)gameItems.get(3);
				String homeAway = "spulse".equals((String)((Map)game).get("class"))? "H" : "A";
				String vsTeam = (String)gameItems.get(2);
				Object tvObj = ((Map)gameItems.get(5)).get("content");
				String tv = null;
				if (tvObj != null) {
					tv = tvObj instanceof String? (String)tvObj : (String)((Map)tvObj).get("content");
					tv = StringUtils.replace(tv, "\n", " ");
				}
				Map resultMap = (Map)((Map)gameItems.get(4)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = ((String)resultMap.get("content")).substring(0, 1);
					score = ((String)resultMap.get("content")).substring(1);					
					detailUrl = "http://www.s-pulse.co.jp" + (String)resultMap.get("href");
				}
				int c = 0;
				Object[] oneRec = new Object[13];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "H".equals(homeAway);
				oneRec[c++] = vsTeam;
				oneRec[c++] = StringUtils.replace(tv, "\n", " ");
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				oneRec[c++] = null;
				insertDataList.add(oneRec);
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
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
		SpulseResultsSaver srv = new SpulseResultsSaver();
		srv.extractResults();
	}


}
