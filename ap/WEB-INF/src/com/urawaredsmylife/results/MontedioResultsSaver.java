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
 * モンテディオ山形公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class MontedioResultsSaver {
	private Logger logger = Logger.getLogger(MontedioResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "montedio";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.montedioyamagata.jp%2Fmatch%2F%22%20and%20xpath%3D%22%2F%2Ftable%5B%40id%3D'list-schedule'%5D%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public MontedioResultsSaver() {
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
			//logger.info("json = " + ((Map)((Map)((List)((Map)((Map)json.get("query")).get("results")).get("table")).get(0)).get("tbody")).get("tr"));
			List<Object> gameList = (List)((Map)((Map)((List)((Map)((Map)json.get("query")).get("results"))
					.get("table")).get(0)).get("tbody")).get("tr");
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=0; r<gameList.size(); r++) {
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
//				System.out.println("gameItems.get(1)=" + gameItems.get(1));
				if (gameItems.get(1) instanceof Map) {
					compe = ((String)((Map)gameItems.get(1)).get("content")).replace("\n","/");
				} else {
					compe = (String)gameItems.get(1);
				}
				if ("大会/節".equals(compe) || "Jリーグ・スカパー！ニューイヤーカップ".equals(compe)) {
					continue;
				}
				String compeImg = (String)((Map)((Map)gameItems.get(0)).get("img")).get("src");
				String compeName = "";
				if (compeImg.endsWith("obj_emblem_j1.png")) {
					compeName = "J1 ";
				} else if (compeImg.endsWith("obj_emblem_ync.png")) {
					compeName = "ルヴァン";
				}
				compe = compeName + compe;
				String gameDateView = null;
				String time = null;
				if (gameItems.get(2) instanceof Map) {
					gameDateView = (String)((Map)gameItems.get(2)).get("span");
					String dayAndTime = (String)((Map)gameItems.get(2)).get("content");
					gameDateView += dayAndTime.substring(0, dayAndTime.indexOf('）')+1);
					time = dayAndTime.substring(dayAndTime.indexOf('）')+1);
				}
				if (StringUtils.isNotBlank(gameDateView)) {
					gameDateView = gameDateView.replaceAll("\n", "").replaceAll("<br/>", "").replaceAll("※.*", "")
							.replace("（", "(").replace("）", ")");
				}
//				System.out.println("日●" + gameDateView);
				String gameDate = null;
				if(gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				String homeAway = "";
				if (((Map)gameItems.get(4)).get("em") instanceof String) {
					homeAway = (String)((Map)gameItems.get(4)).get("em");
//					System.out.println("■" + homeAway);
				} else if (((Map)gameItems.get(4)).get("em") instanceof Map) {
					homeAway = (String)((Map)((Map)gameItems.get(4)).get("em")).get("content");
//					System.out.println("○" + homeAway);
				}
				String vsTeam = (String)((Map)gameItems.get(3)).get("content");
				String stadium = "";
				String tv = null;
				stadium = (String)((Map)gameItems.get(4)).get("content");
				Map resultMap = (Map)((Map)gameItems.get(5)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if(resultMap != null) {
					result = (String)resultMap.get("content");
					score = (String)resultMap.get("span");					
					detailUrl = "http://www.montedioyamagata.jp" + (String)resultMap.get("href");
//				} else if (((Map)gameItems.get(5)).get("content") != null){
//					// 親善試合などでスコアにリンクがない場合
//					result = ((String)((Map)gameItems.get(5)).get("content")).substring(0, 1);
//					score = ((String)((Map)gameItems.get(5)).get("content")).substring(1);
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
				Object[] oneRec = new Object[13];
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
		MontedioResultsSaver srv = new MontedioResultsSaver();
		srv.extractResults();
	}


}
