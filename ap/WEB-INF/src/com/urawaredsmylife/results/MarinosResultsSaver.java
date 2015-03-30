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
 * 横浜F・マリノス公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class MarinosResultsSaver {
	private Logger logger = Logger.getLogger(MarinosResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "marinos";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20"
			+ "where%20url%3D'http%3A%2F%2Fwww.f-marinos.com%2Fschedule%2Fmatch'%20and%20"
			+ "xpath%3D'%2F%2Ftable%5B%40class%3D%22matchSchedule%20mb10%20views-view-grid%20"
			+ "cols-4%22%5D%2Ftbody%2Ftr'&format=json&diagnostics=true&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public MarinosResultsSaver() {
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
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).
					get("results")).get("tr");
			logger.info(gameList.getClass().toString());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
//				System.out.println("xx=" + ((Map)game));
				if (game == null) {
					continue;
				}
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if(gameItems == null) {
					continue;
				}
				String compe = StringUtils.trimToEmpty((String)((Map)((Map)gameItems.get(1)).get("p")).get("span"))
						.replaceAll("明治安田", "").replaceAll("ナビスコカップ", "ﾅﾋﾞｽｺ");
				System.out.println("★compe=" + compe);
				Object compe2 = ((Map)gameItems.get(2)).get("p");
				if(compe2 != null) {
					if(compe2 instanceof String) {
						compe += StringUtils.trimToEmpty((String)compe2);
						System.out.println("★compe2=" + compe2);
					} else if(compe2 instanceof List) {
						for(Object s : (List)compe2) {
							if (s instanceof Map) {
								compe += ((Map)s).get("content");
							} else {
								compe += s;
							}
							System.out.println("◯compe2=" + s);
						}
					}
					compe = compe.replaceAll("ステージ", "").replaceAll("Ｊ１", "J1 ")
							.replaceAll("１ｓｔ", "1st").replaceAll("２ｎｄ", "2nd").replaceAll("予選リーグ", "");
				}
				Object gameDateViewTmp = ((Map)gameItems.get(0)).get("p");
				String gameDateView = null;
				if (gameDateViewTmp instanceof List) {
					gameDateView = (String)((Map)((Map)((List)gameDateViewTmp).get(0)).get("span")).get("content");
				} else if (gameDateViewTmp instanceof Map) {
					gameDateView = (String)((Map)((Map)gameDateViewTmp).get("span")).get("content");
				}
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				String time = null;
				if (gameDateViewTmp instanceof List) {
					time = (String)((Map)((List)gameDateViewTmp).get(1)).get("content");
				}
				String stadium = StringUtils.trim((String)((List)((Map)gameItems.get(4)).get("p")).get(1));
				String homeAway = StringUtils.deleteWhitespace(
						(String)((Map)((List)((Map)gameItems.get(4)).get("p")).get(0)).get("span"));
				String vsTeam = StringUtils.deleteWhitespace((String)((Map)gameItems.get(3)).get("content"));
//				String tv = (String)((Map)((Map)((Map)gameItems.get(7)).get("p")).get("span")).get("content");
				String tv = null;
				String result = null;
				String score = null;
				String detailUrl = null;
				try {
					Map resultMap = (Map)((Map)((Map)((Map)gameItems.get(5)).get("div")).get("div")).get("div");
					if(resultMap != null && resultMap.get("span") != null) {
						result = ((String)resultMap.get("span")).substring(0, 1);
						score = ((String)resultMap.get("span")).substring(1).replaceAll(" ", "");
						Map detailUrlP = (Map)((List)((Map)gameItems.get(6)).get("p")).get(0);
						detailUrl = "http://www.f-marinos.com" + (String)((Map)detailUrlP.get("a")).get("href");
					}
				} catch(Exception ex) {
					logger.warn(ex);
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
		MarinosResultsSaver srv = new MarinosResultsSaver();
		srv.extractResults();
	}


}
