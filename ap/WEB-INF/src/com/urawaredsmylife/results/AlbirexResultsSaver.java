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
 * アルビレックス新潟公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * TODO 天皇杯が始まったら変更
 * @author motoy3d
 *
 */
public class AlbirexResultsSaver {
	private Logger logger = Logger.getLogger(AlbirexResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "albirex";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20html"
			+ "%20WHERE%20url%3D'http%3A%2F%2Fwww.albirex.co.jp%2Fgames%2Fold'%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40class%3D'game-archives-section'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&diagnostics=true&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public AlbirexResultsSaver() {
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
//			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
			logger.info("gameList.size() = " + gameList.size());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            String[] compeList = new String[]{"J1 1st", "J1 2nd", "ルヴァン", "サテライト", "プレシーズン", "天皇杯"};
            int compeIdx = 0;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
//System.out.println("game=" + ((Map)game));
				List<Object> gameItems = (List<Object>)((Map)game).get("th");
				if(gameItems == null) {
					System.out.println("continue...............");
					continue;
				}
				if ("bg".equals((String)((Map)game).get("class"))) {
					compeIdx++;
					System.out.println("continue............... compeIdx=" + compeIdx);
					continue;
				}
				if(compeIdx >= 3) { //プレシーズン //TODO ルヴァン決勝T行ったら or 天皇杯が始まったら5に変更
					break;
				}
				String compe = compeList[compeIdx] + " " + StringUtils.trimToEmpty((String)((Map)gameItems.get(0)).get("content"));
				String gameDateView = StringUtils.trim((String)((Map)gameItems.get(1)).get("content"));
//				System.out.println("gameDateView=" + gameDateView);
				String[] dateAndTime = gameDateView.split("\n");
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
					continue;
				}
				System.out.println("🌟日付=" + gameDate);
				gameDateView = dateAndTime[0];
				String time = dateAndTime != null && 2 <= dateAndTime.length? dateAndTime[1].trim() : "";
				List vsTeamObj = null;
				String vsTeam = null;
				if (((Map)gameItems.get(2)).get("table") != null) {
					vsTeamObj = (List)((Map)((Map)((Map)((Map)gameItems.get(2)).get("table")).get("tbody")).get("tr")).get("td");
					vsTeam = ((String)((Map)vsTeamObj.get(1)).get("content")).trim();
				}
				String homeAway = (String)((Map)gameItems.get(4)).get("p");
				String stadium = null;
				List stadiumDiv = (List)((Map)gameItems.get(3)).get("div");
				if (stadiumDiv != null) {
					homeAway = (String)((Map)((Map)stadiumDiv.get(0)).get("img")).get("alt");
					stadium = StringUtils.trim((String)((Map)stadiumDiv.get(1)).get("content"));
				}
				String tv = null;
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = ((String)resultMap.get("content")).substring(0, 1);
					score = ((String)resultMap.get("content")).substring(1);
					//if ("away".equals(homeAway)) { //公式サイトでaltが全部homeになっているのでスタジアム名で判定
					if (!"デンカＳ".equals(stadium)) {
						score = StringUtils.reverse(score);
					}
					detailUrl = "http://www.albirex.co.jp" + (String)resultMap.get("href");
				}
				int c = 0;
				Object[] oneRec = new Object[13];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
//				oneRec[c++] = "home".equals(homeAway);
				oneRec[c++] = "デンカＳ".equals(stadium);
				oneRec[c++] = vsTeam;
				oneRec[c++] = tv;
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				oneRec[c++] = null;
				insertDataList.add(oneRec);
//				logger.info(compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
//						+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
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
		AlbirexResultsSaver srv = new AlbirexResultsSaver();
		srv.extractResults();
	}


}
