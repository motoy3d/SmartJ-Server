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
import com.urawaredsmylife.util.Mail;

/**
 * FC東京公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class FCTokyoResultsSaver {
	private Logger logger = Logger.getLogger(FCTokyoResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "fctokyo";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D'http%3A%2F%2Fwww.fctokyo.co.jp%2Fcategory%2Fschedule'%20"
			+ "and%20xpath%3D'%2F%2Ftable%5B%40class%3D%22ticket_vsbox%22%5D%2Ftbody%2Ftr'&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public FCTokyoResultsSaver() {
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
			//logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
			logger.info("gameList.size=" + gameList.size());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			//TODO　決勝T行った場合と行かなかった場合で違う
//            String[] compeList = new String[]{"J1 1st", "J1 2nd", "ナビスコ", "ナビスコ決勝T", "天皇杯"};
            String[] compeList = new String[]{"J1 1st", "J1 2nd", "ACL決勝T", "ACL", "ACL", "ニューイヤーカップ"};
            int compeIdx = 0;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				//System.out.println("▲game=" + ((Map)game));
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				String bgcolor = (String)((Map)gameItems.get(0)).get("bgcolor");
				if(gameItems.size() != 8) {//プレシーズンやナビスコ試合なしは省略
					logger.info("#プレシーズンやナビスコ試合なしは省略 " + r);
					continue;
				}
				Map gameNumberMap = (Map)gameItems.get(0);
				String gameNumber = StringUtils.trimToEmpty((String)(gameNumberMap).get("content"));
				if ("".equals(gameNumber) && gameNumberMap.get("span") != null) {
					gameNumber = StringUtils.trimToEmpty((String)((Map)gameNumberMap.get("span")).get("content"));
				}
				System.out.println("🔴gameNumber=" + gameNumber + "   gemeNumberMap=" + gameNumberMap);
				if("#808080".equals(bgcolor) || "節".equals(gameNumber) 
						|| "戦".equals(gameNumber) || "回".equals(gameNumber) 
						|| "".equals(gameNumber)) {//ヘッダは省略
					logger.info("#ヘッダ " + r);
					compeIdx++;
					if(compeIdx == 4) {
//						break;
					}
					continue;
				}
				if((compeIdx == 0 || compeIdx == 1 /*|| compeIdx == 2*/) && NumberUtils.isDigits(gameNumber)) {	//ナビスコ、Jリーグ
					gameNumber = "第" + gameNumber + "節";
				}
				//TODO ACL決勝T
				else if(compeIdx == 2) {
					gameNumber = gameNumber.equals("1") ? "1st leg" : "2nd leg";
				}
				//ACLグループリーグ
				else if(compeIdx == 3 && NumberUtils.isDigits(gameNumber)) {
					gameNumber = "第" + gameNumber + "節";
				}
				else if(compeIdx == 4 && NumberUtils.isDigits(gameNumber)) {	//天皇杯
					gameNumber += "回戦";
				}
				String compe = compeList[compeIdx] + " " + gameNumber;
				String gameDateView = (String)((Map)gameItems.get(1)).get("content");
				if(gameDateView == null && (Map)((Map)gameItems.get(1)).get("span") != null) {
					gameDateView = (String)((Map)((Map)gameItems.get(1)).get("span")).get("content");
				}
				gameDateView = gameDateView.replaceAll("\r", "").replaceAll("\n", "");
				String gameDate = null;
				if(gameDateView != null && gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, 
							gameDateView.indexOf("(")).replace("月", "/").replace("日", "");
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)((Map)gameItems.get(2)).get("content");
				String stadium = (String)((Map)gameItems.get(4)).get("content");
				if(stadium == null) {
					stadium = (String)((Map)((Map)gameItems.get(4)).get("span")).get("content");
				}
				stadium = StringUtils.deleteWhitespace(stadium);
				System.out.println("スタジアム：" + stadium);
				String homeAway = "味の素スタジアム".equals(stadium) || "東京スタジアム".equals(stadium)? "H" : "A";
				String vsTeam = (String)((Map)gameItems.get(3)).get("content");
				if(vsTeam == null && ((Map)gameItems.get(3)).get("span") != null) {
					vsTeam = (String)((Map)((Map)gameItems.get(3)).get("span")).get("content");
				}
				if("-".equals(vsTeam)) {
					continue;
				}
//				String tv = ((Map)gameItems.get(7)).get("span") != null? 
//						(String)((Map)((List)((Map)gameItems.get(7)).get("span")).get(0)).get("content") : null;
				String tv = null;
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null && resultMap.get("content") != null) {
					score = (String)resultMap.get("content");
					System.out.println("◉スコア：" + score);
					if (score.indexOf("△") != -1) {
						result = "△";
						score = score.replace("△", "-");
					} else if (score.indexOf("●") != -1) {
						result = "●";
						score = score.replace("●", "-");
					} else {
						result = "○";
						score = score.replace("○", "-").replace("○", "-");
					}
//					int myScore = Integer.parseInt(score.substring(0, idx));
//					int vsScore = Integer.parseInt(score.substring(idx + 1));
//					logger.info("＞＞＞" + myScore + "-" + vsScore + " [" + score + "]");
//					if (myScore < vsScore) {
//						result = "●";
//					} else if (vsScore < myScore) {
//						result = "○";
//					} else {
//						result = "△";
//					}
					detailUrl = (String)resultMap.get("href");
				}
				compe = StringUtils.replace(compe, "\r\n", "");
				gameDate = StringUtils.replace(gameDate, "\r\n", "");
				gameDateView = StringUtils.replace(gameDateView, "\r\n", "");
				time = StringUtils.replace(time, "\r\n", "");
				stadium = StringUtils.replace(stadium, "\r\n", "");
				vsTeam = StringUtils.replace(vsTeam, "\r\n", "");
				tv = StringUtils.replace(tv, "\r\n", "");
				result = StringUtils.replace(result, "\r\n", "");
				score = StringUtils.replace(score, "\r\n", "");
				detailUrl = StringUtils.replace(detailUrl, "\r\n", "");

				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = gameDate;
				oneRec[c++] = gameDateView;
				oneRec[c++] = time;
				oneRec[c++] = stadium;
				oneRec[c++] = "H".equals(homeAway);
				oneRec[c++] = vsTeam;
				oneRec[c++] = tv;
				oneRec[c++] = result;
				oneRec[c++] = score;
				oneRec[c++] = detailUrl;
				insertDataList.add(oneRec);
				System.out.println("🔵" + gameDate + " " + gameDateView + " " + time + "");
				logger.info("■" + compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
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
		FCTokyoResultsSaver srv = new FCTokyoResultsSaver();
		srv.extractResults();
	}


}
