package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.util.DB;

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
	private static final String SRC_URL_BASE = "https://query.yahooapis.com/v1/public/yql?q="
			+ "select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.sagantosu.jp%2Fgame%2F"
			+ "game_schedule.html%22%20%0Aand%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'schedule'%5D%2F"
			+ "table%2Ftr%22&format=json&callback=";
	
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

				String compeName = "";
				Map compeImgTmp = (Map)((Map)gameItems.get(2)).get("img");
				if (compeImgTmp != null) {
					String compeImg = (String)((Map)compeImgTmp).get("src");
					if (compeImg.endsWith("j1.png")) {
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

				String compe = (String)((Map)gameItems.get(3)).get("p");
				compe = compeName + "/" + compe.replaceAll("ステージ", "").replaceAll("予選リーグ", "")
						.replaceAll("　", " ");
				
				String day = (String)((Map)gameItems.get(0)).get("p");
				String gameDateView = ((String)((Map)((Map)game).get("th")).get("p")).replace(".", "/")
						+ "(" + day + ")";
				String gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				String time = ((String)((Map)gameItems.get(1)).get("p")).replace("：", ":");
				String stadium = (String)((Map)gameItems.get(11)).get("p");
				String vsTeam = null;
				Object vsTeamTmp = ((Map)gameItems.get(10)).get("p");
				if (vsTeamTmp instanceof String) {
					vsTeam = (String)vsTeamTmp;
				} else if(vsTeamTmp instanceof Map) {
					vsTeam = (String)((Map)vsTeamTmp).get("content");
				}
				vsTeam = vsTeam.replaceAll("\n", "").trim();
				String tv = "";
				Map resultMap = (Map)gameItems.get(6) == null? 
						null : (Map)((Map)gameItems.get(6)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					result = score.substring(0, 1);
					score = score.substring(1);
					detailUrl = "http://www.sagantosu.jp/sp" + ((String)resultMap.get("href"));
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
