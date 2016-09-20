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
 * 松本山雅FC公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class YamagaResultsSaver {
	private Logger logger = Logger.getLogger(YamagaResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "yamaga";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html"
			 + "%20where%20url%3D%22http%3A%2F%2Fwww.yamaga-fc.com%2Fmatch%2Fcategory%2F"
			+ "{SEASON}"
			+ "%22%20and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'p-match-list'%5D%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public YamagaResultsSaver() {
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
        String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
		try {
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			StopWatch sw = new StopWatch();
			sw.start();
			System.out.println("URL = " + SRC_URL);
			GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL.replace("{SEASON}", season));
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
//			logger.info("json = " + json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");	
			for(int r=2; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				System.out.println(r + "------------------------------");
				if (((Map)game).get("td") instanceof Map) {
					logger.info("無観客試合など");
					continue;
				}
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if(gameItems == null) {
					continue;
				}
				if (gameItems.get(0) == null) {
					logger.info("無観客試合など");
					continue;
				}
				System.out.println("gameItems.get(0)=" + gameItems.get(0));
				System.out.println("gameItems.get(1)=" + gameItems.get(1));
				System.out.println("gameItems.get(2).get(div).get(div)=" + ((Map)((Map)gameItems.get(2)).get("div")).get("div"));
				System.out.println("gameItems.get(3)=" + gameItems.get(3));
				String compe = StringUtils.deleteWhitespace(((String)((Map)gameItems.get(1)).get("content")));
				if ("大会/節".equals(compe) || "Jリーグ・スカパー！ニューイヤーカップ".equals(compe)) {
					continue;
				}
				compe = compe.replaceAll("J1リーグ", "J1").replaceAll("1stステージ", "1st/").replaceAll("2ndステージ", "2nd/")
						.replaceAll("YBCルヴァンカップ ", "ルヴァン/")
						.replaceAll("ACLノックアウトステージ　", "ACL/").replaceAll("　", "")
						.replaceAll("明治安田生命", "")
						.replace(season, "");
				String gameDateView = null;
				String time = null;
				//System.out.println("gameItems.get(2)=" + gameItems.get(2));
					gameDateView = (String)((Map)gameItems.get(0)).get("content");
					time = StringUtils.deleteWhitespace((String)((Map)((Map)gameItems.get(0)).get("span"))
							.get("content")).replace(" Kick off", "").replace("Kickoff", "");
				if (StringUtils.isNotBlank(gameDateView)) {
					gameDateView = StringUtils.deleteWhitespace(gameDateView.replaceAll("<br/>", "").replaceAll("※.*", ""));
				}
				//System.out.println("日●" + gameDateView);
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("))
							.replace("月", "/").replace("日", "/");
				} else {
					gameDate = "";	//未定等
				}
				//System.out.println("gameItems.get(1)=" + gameItems.get(1));
				String vsTeam = "";
				boolean isHome = false;
				Map vsTeamMap = null;
//				if (((Map)gameItems.get(2)).get("div") instanceof List) {
//					System.out.println("🔴List");
//					vsTeamMap = (Map)((List)((Map)gameItems.get(2)).get("div")).get(0);
//				} else {
					vsTeamMap = (Map)((List)((Map)((Map)gameItems.get(2)).get("div")).get("div")).get(0);
//				}
				if (vsTeamMap != null) {
					vsTeam = (String)((Map)vsTeamMap.get("p")).get("content");
					if ("松本山雅FC".equals(vsTeam)) {
						isHome = true;
						vsTeamMap = (Map)((List)((Map)((Map)gameItems.get(2)).get("div")).get("div")).get(2);
						vsTeam = (String)((Map)vsTeamMap.get("p")).get("content");
					}
				}
				String stadium = "";
				String tv = null;
//				System.out.println("gameItems.get(3)=" + gameItems.get(3));
				if (gameItems.get(3) != null && gameItems.get(3) instanceof Map &&
						((Map)gameItems.get(3)).get("p") != null &&
						((Map)((Map)gameItems.get(3)).get("p")).get("a") != null) {
					stadium = (String)((Map)((Map)((Map)gameItems.get(3)).get("p")).get("a")).get("content");
					if ("未定".equals(stadium)) {
						stadium = "会場未定";
					}
				}
				//System.out.println("gameItems.get(5)=" + gameItems.get(5));
				Map map1 = (Map)((Map)gameItems.get(2)).get("div");
				System.out.println("map1 = " + map1);
				System.out.println("((Map)((List)map1.get(1))).get(p) = " + ((Map)((List)(map1.get("div"))).get(1)).get("p"));
				Map resultMap = (Map)((Map)((Map)((List)(map1.get("div"))).get(1)).get("p")).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					List scoreList = (List)resultMap.get("span");
					int myScore = 0;
					int vsScore = 0;
					if (scoreList != null) {
						myScore = Integer.parseInt((String)((Map)scoreList.get(isHome? 0 : 1)).get("content"));
						vsScore = Integer.parseInt((String)((Map)scoreList.get(isHome? 1 : 0)).get("content"));
						score = myScore + "-" + vsScore;
						if (myScore < vsScore) {
							result = "●";
						} else if (vsScore < myScore){
							result = "○";
						} else {
							result = "△";
						}
					}
					detailUrl = (String)resultMap.get("href");
				} else if (5 < gameItems.size() && ((Map)gameItems.get(5)).get("content") != null){
					// 親善試合などでスコアにリンクがない場合
					result = ((String)((Map)gameItems.get(5)).get("content")).substring(0, 1);
					score = ((String)((Map)gameItems.get(5)).get("content")).substring(1);
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
				Object[] oneRec = new Object[13];
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
				oneRec[c++] = null;
				insertDataList.add(oneRec);
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + "アルウィン".equals(stadium) + ", " 
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
		YamagaResultsSaver srv = new YamagaResultsSaver();
		srv.extractResults();
	}
}
