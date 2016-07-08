package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.urawaredsmylife.util.Mail;

import net.arnx.jsonic.JSON;

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
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D%22http%3A%2F%2Fwww.reysol.co.jp%2Fgame%2Fresults%2Findex.php%22%20and%20"
			+ "xpath%3D%22%2F%2Ftbody%22&format=json&callback=";

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
        String[] compeList = new String[]{ "J1 1st", "J1 2nd", "ルヴァン", "天皇杯", "プレシーズン"};
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
					.get("query")).get("results")).get("tbody");
			logger.info("gameGroupList = " + gameGroupList.size());

            String insertSql = "INSERT INTO " + resultsTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(int compeIdx = 0; compeIdx<gameGroupList.size(); compeIdx++) {
				if (compeIdx == 4) {
					break;	//プレシーズン
				}
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
//					Object th = ((Map)game).get("th");
//	System.out.println("🌟" + th + ", gameItems.size=" + gameItems.size());
					System.out.println("gameItems.get(0) " + gameItems.get(0));
					String compe = gameItems.get(0).toString();
//					if (th instanceof String) {
//						compe = (String)th;
//					} else if (th instanceof Map) {
//						compe = (String)((Map)th).get("content");
//					}
					if (NumberUtils.isDigits(compe)) {
						compe = "第" + compe + "節";
					}
					compe = compeName + "/" + compe;
					System.out.println("🔵compe=" + compe);
					String gameDateView = null;
					String detailUrl = null;
					System.out.println("gameItems.get(1) " + gameItems.get(1));
					Object item1Obj = gameItems.get(1);
					Map item1 = item1Obj instanceof Map? (Map)item1Obj : null;
					if (item1 != null && item1.get("a") != null) {
						gameDateView = (String)((Map)item1.get("a")).get("content");
						detailUrl = "http://www.reysol.co.jp/game/results/" +
								(String)((Map)item1.get("a")).get("href");
//						System.out.println("gameDateView 0 = " + gameDateView);
					} else if (item1 != null) {
						Object gameDateViewTmp = item1.get("content");
						if (gameDateViewTmp == null && item1.get("p") != null) {
							System.out.println("gameDateView 6 = " + item1.get("p"));
							if (item1.get("p") instanceof String) {
								gameDateViewTmp = (String)item1.get("p");
							} else {
								gameDateViewTmp = ((Map)item1.get("p")).get("content");
							}
						}
						System.out.println(">> gameDateViewTmp = " + gameDateViewTmp);
						if (gameDateViewTmp instanceof String) {
							gameDateView = (String)gameDateViewTmp;
							System.out.println("gameDateView 1 = " + gameDateView);
						} else if (gameDateViewTmp instanceof Map) {
							gameDateView = (String)((Map)gameDateViewTmp).get("content");
							System.out.println("gameDateView 2 = " + gameDateView);
						}
					} else if (item1Obj instanceof String){
						gameDateView = (String)item1Obj;
					}
					System.out.println("gameDateView 3 = " + gameDateView);
					if (StringUtils.isBlank(gameDateView)) {
						logger.info("日付空欄のため飛ばす");
						continue;
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
					Object timeObj = gameItems.get(2);
					if (timeObj != null) {
						if (timeObj instanceof String) {
							time = ((String)timeObj);
							System.out.println("time 1 = " + time);
						} else if (timeObj instanceof Map) {
							time = (String)((Map)timeObj).get("content");
							if (time == null) {
								time = (String)((Map)((Map)timeObj).get("span")).get("content");
							}
							System.out.println("time 2 = " + time);
						}
						if (time != null) {
							time = time.replace("：", ":").replaceAll("※.*", "");
						} else {
							System.out.println("●" + timeObj);
						}
					}
					if (StringUtils.isNotBlank(time)) {
						time = time.replaceAll("\r\n","").replaceAll(" ", "").replace("JPN", "(日本時間)").replace("★", "");
						System.out.println("time 3 = " + time);
					}
					String stadium = null;
					Object item3 = gameItems.get(3);
					if (item3 instanceof String) {
						stadium = (String)item3;
					} else if (item3 instanceof Map) {
						stadium = (String)((Map)item3).get("content");
					}
					Object vsTeamTmp = gameItems.get(4);
					String vsTeam = null;
					if (vsTeamTmp instanceof String) {
						vsTeam = (String)vsTeamTmp;
					} else if (vsTeamTmp instanceof Map) {
						System.out.println("★vsTeamTmp=" + vsTeamTmp);
						vsTeam = (String)((Map)vsTeamTmp).get("content");
						if (StringUtils.isBlank(vsTeam) && ((Map)vsTeamTmp).get("span") instanceof Map) {
							vsTeam = (String)((Map)((Map)vsTeamTmp).get("span")).get("content");
						}
					} else {
						System.out.println("★vsTeamTmp=" + vsTeamTmp.getClass() + " / "  + vsTeamTmp);
					}
					if (vsTeam != null) {
						vsTeam = vsTeam.replaceAll("\n", "").replaceAll(" ", "").replaceAll("（", "(").replaceAll("）", ")");
					}
					String tv = null;
					Object item5 = gameItems.get(5);
					if (item5 instanceof Map && ((Map)item5).get("p") instanceof Map) {
						tv = (String)((Map)((Map)item5).get("p")).get("content");
					} else if (item5 instanceof String) {
						tv = (String)item5;
					}
					int resultIdx = gameItems.size() == 8? 6 : 7;	//TV中継２がある場合とない場合で違う
					String result = "";
					Object resultItem = gameItems.get(resultIdx);
					if (resultItem instanceof String) {
						result = StringUtils.trimToNull(((String)resultItem)
								.replaceAll(" ", ""));	//←普通の半角スペースとは違うらしい
					} else if (resultItem instanceof Map) {
						result = StringUtils.trimToNull(((String)((Map)resultItem).get("content"))
								.replaceAll(" ", ""));	//←普通の半角スペースとは違うらしい
					}
					if ("-".equals(result)) {
						result = "";
					}
//					System.out.println("★結果 [" + result + "]");
					String score = null;
					System.out.println("gameItems.size()=" + gameItems.size());
					int scoreIdx = gameItems.size() == 8? 7 : 8;	//TV中継２がある場合とない場合で違う
					Object scoreItem = gameItems.get(scoreIdx);
					if (scoreItem instanceof Map) {
						if (((Map)scoreItem).get("content") != null) {
							score = ((String)((Map)scoreItem).get("content"))
									.replaceAll(" ", "");
						} else {
							score = ((String)((Map)scoreItem).get("p"))
									.replaceAll(" ", "");
						}
					} else {
						score = (String)scoreItem;
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
			Mail.send(e);
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
