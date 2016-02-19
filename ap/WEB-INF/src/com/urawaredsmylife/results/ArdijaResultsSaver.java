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
 * 大宮アルディージャ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class ArdijaResultsSaver {
	private Logger logger = Logger.getLogger(ArdijaResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "ardija";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%0Awhere%20url%3D%22http%3A%2F%2Fwww.ardija.co.jp%2Fmatch%2F%22%20and%20%0Axpath%3D%22%2F%2Fdiv%5B%40class%3D'matchCategory'%5D%2Fdiv%22&format=json&diagnostics=true&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public ArdijaResultsSaver() {
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
			System.out.println(res.getText());
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("div");
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=0; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("div");
				if (gameItems == null) {
					continue;
				}
				System.out.println("gameItems=" + gameItems);
				String compeName = "";
				if (((Map)gameItems.get(0)).get("img") != null) {
					compeName = (String)((Map)((Map)gameItems.get(0)).get("img")).get("src");
				}
				if (compeName.endsWith("logo_j.png")) {
					compeName = "J";
				} else if (compeName.endsWith("logo_yn.png")) {
					compeName = "ナビスコ";
				} else if (compeName.endsWith("logo_empcup.png")) {
					compeName = "天皇杯";
				} else if (compeName.endsWith("logo_acl.png")) {
					compeName = "ACL";
				} else {
					compeName = "";
				}
				String compe = "";
				List<Map> item0DivList = (List<Map>)((Map)gameItems.get(0)).get("div");
				Map compeMap = item0DivList.get(0);
				compe = (String)compeMap.get("content");
				if (!compeName.equals("")) {
					compe = compeName + "/" + compe;
				}
				if (compe.contains("1st") || compe.contains("2nd")) {
					compe = "J1 " + compe;
				} else {
					//TODO 時期によって要確認
					compe = "ﾅﾋﾞｽｺ" + compe;
				}
				System.out.println("compe====" + compe);
				System.out.println("0：" + gameItems.get(0));
				System.out.println("1：" + gameItems.get(1));
				System.out.println("2：" + gameItems.get(2));
				Map datetimeStadiumMap = item0DivList.get(1);
				String gameDateView = StringUtils.deleteWhitespace(((String)datetimeStadiumMap.get("content"))
						.replaceAll("祝", "").replaceAll("休", ""));
				String time = gameDateView.substring(gameDateView.indexOf(")") + 1);
				gameDateView = gameDateView.substring(0, gameDateView.indexOf(")") + 1);
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
							.replace("月", "/").replace("日", "");
				} else {
					gameDate = "";	//未定等
				}
				System.out.println("日付：" + gameDateView + "   " + gameDate + "    時刻：" + time);
				String stadium = "";
				if (datetimeStadiumMap.get("a") != null) {
					stadium = (String)((Map)datetimeStadiumMap.get("a")).get("content");
				} else {
					stadium = "";
				}
				String homeAway = null;
				Map homeAwayMap = item0DivList.get(2);
				if (((String)((Map)homeAwayMap).get("content")).contains("HOME")) {
					homeAway = "HOME";
				}
				Map vsTeamMap = (Map)gameItems.get(1);
				List<Map> div1Map = (List<Map>)vsTeamMap.get("div");
				Map homeTeamMap = div1Map.get(0);
				String homeTeam = (String)homeTeamMap.get("content");
				Map awayTeamMap = div1Map.get(5);
				String awayTeam = (String)awayTeamMap.get("content");
				System.out.println("home=" + homeTeam + " vs " + awayTeam);
				
				String vsTeam = "HOME".equals(homeAway)? awayTeam : homeTeam;
				String tv = null;
				Map resultMap = null;
				if ((Map)((Map)gameItems.get(2)).get("span") != null) {
					resultMap = (Map)((Map)((Map)gameItems.get(2)).get("span")).get("a");
				}
				String result = null;
				String score = null;
				String detailUrl = null;
				if (resultMap != null) {					
					if (resultMap.get("span") != null) {	//勝ちの場合構造が違う
						score = ((String)resultMap.get("content")).replaceAll(" ", "");
						result = (String)((Map)resultMap.get("span")).get("content");
					} else {
						score = ((String)resultMap.get("content")).replaceAll(" ", "");
						result = score.substring(0, 1);
						score = score.substring(1);
					}
					// ホームが左になっている
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

					detailUrl = "http://www.ardija.co.jp/" + (String)resultMap.get("href");
				}
				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "HOME".equals(homeAway);
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
			Mail.send(e);
		}
		return 0;
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		ArdijaResultsSaver srv = new ArdijaResultsSaver();
		srv.extractResults();
	}


}
