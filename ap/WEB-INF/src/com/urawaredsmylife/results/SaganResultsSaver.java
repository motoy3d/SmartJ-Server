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
 * サガン鳥栖公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class SaganResultsSaver {
	private Logger logger = Logger.getLogger(SaganResultsSaver.class.getName());
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL_BASE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20"
			+ "from%20html%20where%20url%3D%22http%3A%2F%2Fwww.sagan-tosu.net%2Fgame%2F%22%20and%20"
			+ "xpath%3D%22%2F%2Fdiv%5B%40id%3D'contents'%5D%2Fsection%2Ftable%2Ftbody%2Ftr%22&format=json&diagnostics=true&callback=";
	
	/** チームID */
	private static final String teamId = "sagan";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public SaganResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		try {
			String resultsTable = teamId + "Results";
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			qr.update("DELETE FROM " + resultsTable + " WHERE season=" + season);
			String srcUrl = SRC_URL_BASE;
			logger.info("####################################");
			logger.info(srcUrl);
			logger.info("####################################");
			GetMethodWebRequest req = new GetMethodWebRequest(srcUrl);
			StopWatch sw = new StopWatch();
			sw.start();
			WebResponse res = wc.getResponse(req);
			sw.stop();
			System.out.println((sw.getTime()/1000.0) + "秒");
			Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
//			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json
					.get("query")).get("results")).get("tr");
			
            String insertSql = "INSERT INTO " + resultsTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(int r=0; r<gameList.size(); r++) {
				Object game = gameList.get(r);
//				System.out.println("xx=" + ((Map)game));
				boolean isHome = "home_game".equals(((Map)game).get("class"));
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null) {
					continue;
				}

				String compeName = "";
				Map compeImgTmp = (Map)((Map)gameItems.get(3)).get("img");
				if (compeImgTmp != null) {
					String compeImg = (String)((Map)compeImgTmp).get("src");
					if (compeImg.endsWith("j1-1.png") || compeImg.endsWith("j1-2.png")) {
						compeName = "J1";
					} else if (compeImg.endsWith("j2.png")) {
						compeName = "J2";
					} else if (compeImg.endsWith("nabisco.png")) {
						compeName = "ナビスコ";
					} else if (compeImg.endsWith("acl.png")) {
						compeName = "ACL";
					} else if (compeImg.endsWith("tennohai.png")) {
						compeName = "天皇杯";	//天皇杯にはリンクがなかったが念のためこちらにも
					} else if (compeImg.endsWith("b_logo_xerox.png")) {
						compeName = "FUJI XEROX SUPER CUP";
					} else if (compeImg.endsWith("b_logo_panasoniccup.png")) {
						compeName = "Panasonic CUP";
					} else if (compeImg.endsWith("b_logo_suruga.png")) {
						compeName = "スルガ銀行チャンピオンシップ";
					}
				}

				String compe = (String)((Map)gameItems.get(4)).get("content");
				compe = compeName + "/" + compe.replaceAll("ステージ", "").replaceAll("予選リーグ", "")
						.replaceAll("　", " ").replaceAll("\n", "");
				
				String day = (String)((Map)gameItems.get(1)).get("content");
				String gameDateView = ((String)((Map)gameItems.get(0)).get("content")).replaceAll("\\.", "/")
						+ "(" + day + ")";
				String gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				String time = ((String)((Map)gameItems.get(2)).get("content")).replace("：", ":");
				String stadium = "";
				String vsTeam = null;
				String tv = "";
				String result = null;
				String score = null;
				String detailUrl = null;
				
				List resultsTmp = null;
				//結果が出ている場合
				Map item5 = (Map)gameItems.get(5);
				if (((Map)item5).get("div") != null) {
					if (item5.get("div") instanceof Map) {
						Map div = (Map)item5.get("div");
						resultsTmp = (List)((Map)((Map)((Map)div.get("table"))
								.get("tbody")).get("tr")).get("td");
					} else if (item5.get("div") instanceof List) {
						List divList = (List)item5.get("div");
						resultsTmp = (List)((Map)((Map)((Map)((Map)divList.get(1)).get("table"))
								.get("tbody")).get("tr")).get("td");
					}
					System.out.println("🌟" + resultsTmp);
					Map vsTeamMap = (Map)resultsTmp.get(2);
					vsTeam = (String)((Map)vsTeamMap).get("content");
					vsTeam = StringUtils.deleteWhitespace(vsTeam.replaceAll("\n", ""));
					
					//スペースがよく分からない文字になっている確認
//					byte[] b = vsTeam.getBytes("utf-8");
//					System.out.println("------------------------");
//					for(int i=0; i<b.length; i++) {
//						System.out.println(Integer.toHexString(b[i]));
//					}
//					System.out.println("------------------------");
//					vsTeam = "   新潟";
//					b = vsTeam.getBytes("utf-8");
//					for(int i=0; i<b.length; i++) {
//						System.out.println(Integer.toHexString(b[i]));
//					}
//					System.out.println("------------------------");
					
					//なぜかスペースが消せないので文字数で切る
					vsTeam = vsTeam.substring(3);
					Map resultMap = (Map)((Map)(Map)resultsTmp.get(1)).get("a");
					if (resultMap != null) {
						score = StringUtils.deleteWhitespace(((String)resultMap.get("content")).replaceAll("−", "-"));
						System.out.println("スコア " + score + ", " + StringUtils.contains(score, " "));
						// 得点から勝敗を抽出。ホームが左になっている
						int saganScore = Integer.parseInt(score.substring(0, score.indexOf("-") - 1));
						int vsScore = Integer.parseInt(score.substring(score.indexOf("-") + 2));
						score = saganScore + "-" + vsScore;
						if (vsScore < saganScore) {
							result = "○";
						} else if (saganScore < vsScore) {
							result = "●";
						} else {
							result = "△";
						}
						detailUrl = "http://www.sagan-tosu.net/game/" + ((String)resultMap.get("href")).replaceAll("\\./","");
					}
				} else {
					//結果が出ていない場合
					vsTeam = StringUtils.trim(((String)((Map)item5).get("content")).replace("VS", ""));
					vsTeam = StringUtils.deleteWhitespace(vsTeam.replaceAll("\n", ""));
					//なぜかスペースが消せないので文字数で切る
					vsTeam = vsTeam.substring(6);
					
					stadium = StringUtils.trim((String)((Map)((Map)item5).get("p")).get("content"));
//					System.out.println("💢" + stadium);
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
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + isHome + ", " 
						+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
			}
			
			if(insertDataList.isEmpty()) {
				logger.warn("日程データが取得出来ませんでした");
				return -1;
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
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		SaganResultsSaver srv = new SaganResultsSaver();
		srv.extractResults();
	}


}
