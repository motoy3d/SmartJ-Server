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
 * 浦和レッズ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class RedsResultsSaver {
	private Logger logger = Logger.getLogger(RedsResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "reds";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20"
			+ "html%20WHERE%20url%3D'http%3A%2F%2Fwww.urawa-reds.co.jp%2Fgame%2F?<dummy>'%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40class%3D'mainContentColumn'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public RedsResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		String srcUrl = SRC_URL.replace("<dummy>", String.valueOf(System.currentTimeMillis()));
		logger.info(srcUrl);
		GetMethodWebRequest req = new GetMethodWebRequest(srcUrl);
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
			//logger.info("json = " + json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");	
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<gameList.size(); r++) {
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
				String compe = "";
				if (gameItems.get(0) instanceof Map) {
					if (((Map)gameItems.get(0)).get("content") instanceof Map) {
						compe = ((String)((Map)((Map)gameItems.get(0)).get("content")).get("content")).replaceAll("\n", "");
					} else {
						compe = (String)((Map)gameItems.get(0)).get("content");
					}
				} else if (gameItems.get(0) instanceof String) {
					compe = (String)gameItems.get(0);
				}
				if ("大会/節".equals(compe) || "Jリーグ・スカパー！ニューイヤーカップ".equals(compe)) {
					continue;
				}
				compe = compe.replaceAll("J1リーグ", "J1").replaceAll("1stステージ", "1st").replaceAll("2ndステージ", "2nd")
						.replaceAll("Jリーグ YBC ルヴァンカップ", "ルヴァン")
						.replaceAll("ノックアウトステージ", "").replaceAll("　", "");
				String gameDateView = null;
				if (gameItems.get(1) instanceof Map) {
					if (((Map)gameItems.get(1)).get("content") instanceof Map) {
						gameDateView = (String)((Map)((Map)gameItems.get(1)).get("content")).get("content");
					} else {
						gameDateView = ((String)((Map)gameItems.get(1)).get("content"));
					}
				} else if (gameItems.get(1) instanceof String) {
					gameDateView = ((String)gameItems.get(1));
				}
				if (StringUtils.isNotBlank(gameDateView)) {
					gameDateView = gameDateView.replaceAll("\n", "").replaceAll("<br/>", "").replaceAll("\\(※.*\\)", "");
				}
//				gameDateView = gameDateView.replace("(日)", "(Sun)").replace("日", "/").replace("(Sun)", "(日)");
				if (gameDateView.startsWith("4月20日(水)")) {
					gameDateView = "4/20(水)";
				}
				int astaIndex = gameDateView.indexOf("(※");
				if (astaIndex != -1) {
					gameDateView = gameDateView.substring(0, astaIndex);
				}
				System.out.println("gameDateView=" + gameDateView);
				String gameDate = null;
				if(gameDateView.contains("(") || gameDateView.contains("（")) {
					String md = null;
					if (gameDateView.contains("(")) {
						md = gameDateView.substring(0, gameDateView.indexOf("("));
					} else {
						md = gameDateView.substring(0, gameDateView.indexOf("（"));
					}
					String month = StringUtils.leftPad(md.substring(0, md.indexOf("/")), 2, '0');
					String date = StringUtils.leftPad(md.substring(md.indexOf("/") + 1), 2, '0');
					gameDate = season + "/" + month + "/" + date;
				} else {
					gameDate = "";	//未定等
				}
//				System.out.println("gameDate=" + gameDate);
//				System.out.println("時間★" + ((Map)gameItems.get(2)).get("p"));
				String time = null;
				if (((Map)gameItems.get(2)).get("content") instanceof Map) {
					time = "時間未定";
				} else {
					time = ((String)((Map)gameItems.get(2)).get("content")).replace("　現地時刻", "(現地)").replaceAll("\\(※.*\\)", "");
//					System.out.println("★時間=" + time);
				}
				String homeAway = "";
				if (((Map)game).get("class") != null) {
					homeAway = ((String)((Map)game).get("class")).startsWith("home")? "HOME" : "AWAY";
				}
				String vsTeam = (String)((Map)gameItems.get(3)).get("content");
				String stadium = "";
				String tv = null;
				if (gameItems.get(4) != null && gameItems.get(4) instanceof Map &&
						((Map)gameItems.get(4)).get("content") != null) {
System.out.println("スタジアム🌟" + ((Map)gameItems.get(4)).get("content"));
					if (((Map)gameItems.get(4)).get("content") instanceof String) {
						stadium = (String)((Map)gameItems.get(4)).get("content");
					} else {
						stadium = (String)((Map)((Map)gameItems.get(4)).get("content")).get("content");
					}
					int idx = stadium.indexOf("/");
					if (idx != -1) {
						tv = stadium.substring(idx + 1);
						stadium = stadium.substring(0, idx);
					}
					if ("未定".equals(stadium)) {
						stadium = "会場未定";
					}
				} else if(gameItems.get(4) instanceof String) {
					stadium = (String)gameItems.get(4);
					int idx1 = stadium.indexOf("/");
					int idx2 = stadium.indexOf("／");
					if (idx1 != -1) {
						tv = stadium.substring(idx1 + 1);
						stadium = stadium.substring(0, idx1);
					} else if (idx2 != -1) {
						tv = stadium.substring(idx2 + 1);
						stadium = stadium.substring(0, idx2);
					}
				}
				System.out.println("スタジアム🔵" + gameItems.get(4));
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = ((String)resultMap.get("content")).substring(0, 1);
					score = ((String)resultMap.get("content")).substring(1);					
					detailUrl = (String)resultMap.get("href");
				} else if (((Map)gameItems.get(5)).get("content") != null){
					// 親善試合などでスコアにリンクがない場合
					result = ((String)((Map)gameItems.get(5)).get("content")).substring(0, 1);
					score = ((String)((Map)gameItems.get(5)).get("content")).substring(1);
				}
				int c = 0;
				Object[] oneRec = new Object[12];
				oneRec[c++] = season;
				oneRec[c++] = compe;
				oneRec[c++] = 0 < gameDate.length()? new SimpleDateFormat("yyyy/MM/dd").parse(gameDate) : null;
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
				logger.info(compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
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
		RedsResultsSaver srv = new RedsResultsSaver();
		srv.extractResults();
	}
}
