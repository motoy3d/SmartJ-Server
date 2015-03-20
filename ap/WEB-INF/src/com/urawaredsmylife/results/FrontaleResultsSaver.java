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

/**
 * 川崎フロンターレ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class FrontaleResultsSaver {
	private Logger logger = Logger.getLogger(FrontaleResultsSaver.class.getName());
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL_BASE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.frontale.co.jp%2Fschedule%2F{HTML}'%20and%20xpath%3D%22%2F%2Ftable%5B%40id%3D'{TABLE_ID}'%5D%2Ftbody%2Ftr%22&format=json&diagnostics=true&callback=";
	private static final String SRC_HTML_J1_1st = "j_league_1st.html";
	private static final String SRC_HTML_J1_2nd = "j_league_2nd.html";
	private static final String SRC_HTML_NABISCO = "yamazaki_nabisco.html";
	private static final String SRC_HTML_TENNOHAI = "emperors_cup.html";
//	private static final String SRC_HTML_ACL = "acl.html";

	/** テーブル名 */
	private static final String TABLE = "frontaleResults";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public FrontaleResultsSaver() {
	}

	/**
	 * チーム公式サイトにアクセスし、日程・結果を抽出する
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int extractResults() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		String[] htmls = new String[] {SRC_HTML_J1_1st, SRC_HTML_J1_2nd, SRC_HTML_NABISCO, SRC_HTML_TENNOHAI};
		String[] tableIds = new String[] {"tbl_cate_j_league", "tbl_cate_j_league", "tbl_cate_yamazaki_nabisco", "tbl_cate_emperors_cup"};
        String[] compeList = new String[]{"J1 1st", "J1 2nd", "YNC", "天皇杯"};
		try {
			QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(int compeIdx=0; compeIdx<htmls.length; compeIdx++) {
				String srcHtml = htmls[compeIdx];
				String tableId = tableIds[compeIdx];
				String srcUrl = SRC_URL_BASE.replace("{HTML}", srcHtml).replace("{TABLE_ID}", tableId);
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
				logger.info(json.toString());
				List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.
						get("query")).get("results")).get("tr");
				logger.info(gameList.getClass().toString());
				
				for(int r=0; r<gameList.size(); r++) {
					Object game = gameList.get(r);
	//				System.out.println("xx=" + ((Map)game));
					boolean isHome = "home_game".equals(((Map)game).get("class"));
					List<Object> gameItems = (List<Object>)((Map)game).get("td");
					
					logger.info("★" + gameItems.get(0));
					String gameDateView = null;
					String compe = "";
					if (gameItems.get(0) instanceof String) {
						compe = compeList[compeIdx] + "/" + gameItems.get(0);
					} else {
						Object compeSrc = ((Map)gameItems.get(0)).get("content");
						if (compeSrc == null) {
							compeSrc = ((Map)gameItems.get(0)).get("strong");
						}
						if(compeSrc instanceof String) {
							compe = compeList[compeIdx] + "/" + StringUtils.trimToEmpty((String)compeSrc);
						} else if(compeSrc instanceof Map) {
							compeSrc = ((Map)compeSrc).get("content");
							compe = compeList[compeIdx] + " " + StringUtils.trimToEmpty((String)compeSrc);
						}
						if(compe.contains("\n")) {
	//						System.out.println("改行あり！！！");
							compe = compe.replaceAll("\n", "");
						}
					}
					if(compeIdx == 0 || compeIdx == 1) {
						compe += "節";
					}
					if (gameItems.get(2) instanceof Map) {
						gameDateView = (String)((Map)gameItems.get(2)).get("content");
					} else {
						gameDateView = ((String)gameItems.get(2));
					}
					System.out.println(">>>>> " + gameDateView);
					gameDateView = gameDateView.replaceAll("（", "(").replaceAll("）", ")").replaceAll("・祝", "").replaceAll("\n", "")
							.replaceAll("※.*", "");
					String gameDate = null;
					
					if(gameDateView.contains("(")) {//半角(
						gameDate = gameDateView.substring(0, gameDateView.indexOf("(")).replaceAll("月", "/").replaceAll("日", "");
						if ("1/1".equals(gameDate)) {	//翌年の天皇杯決勝
							gameDate = (Integer.parseInt(season)+1) + "/" + gameDate;
						} else {
							gameDate = season + "/" + gameDate;
						}
					} else {
						gameDate = "";	//未定等
					}
					if(!"".equals(gameDate)) {
						gameDate = gameDate.replaceAll("月", "/").replaceAll("日", "");
					}
					String time = null;
					if (gameItems.get(3) instanceof Map) {
						time = (String)((Map)gameItems.get(3)).get("content");
					} else {
						time = (String)gameItems.get(3);
					}
					String stadium = "";
					if(gameItems.get(4) instanceof Map) {
						if(((Map)gameItems.get(4)).get("a") != null) {
							stadium = (String)((Map)((Map)gameItems.get(4)).get("a")).get("content");
						} else {
							stadium = (String)((Map)gameItems.get(4)).get("p");
						}
					}
					String vsTeam = null;
					if (gameItems.get(1) instanceof Map) {
						vsTeam = ((String)((Map)gameItems.get(1)).get("content")).replaceAll("※.*", "");
					} else if (gameItems.get(1) instanceof String){
						vsTeam = (String)gameItems.get(1);
					}
//					System.out.println("🌟" + vsTeam);
					String tv = "";
					String resultOrg = (String)((Map)gameItems.get(5)).get("content");
					String result = null;
					String score = null;
					Map detailUrlMap = (Map)((Map)gameItems.get(7)).get("a");
					//System.out.println("△" + resultOrg);
					if(resultOrg != null && !" - ".equals(resultOrg) &&
							StringUtils.isNotBlank(resultOrg.substring(0, 1)) &&
							resultOrg.length() != 1) {	//何故かよく分からない半角スペースのようなものがあるため・・・意味不明
						result = resultOrg.substring(0, 1).trim();
						//System.out.println("■result=[" + result + "] " + StringUtils.isBlank(result));
						score = resultOrg.substring(1);
					}
					String detailUrl = "";
					if(detailUrlMap != null) {
						detailUrl = "http://www.frontale.co.jp" + ((String)detailUrlMap.get("href")).substring(2);
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
					logger.info(compe + ", " + gameDateView + ", " + time + ", " + stadium + ", " + isHome + ", " 
							+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
				}
			}
			// 各大会をまとめて登録
			if(insertDataList.isEmpty()) {
				logger.warn("日程データが取得出来ませんでした ");
			} else {
				qr.update("DELETE FROM " + TABLE + " WHERE season=" + season);
	            String insertSql = "INSERT INTO " + TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
	            int[] resultCount = qr.batch(insertSql, insertDataList.toArray(new Object[insertDataList.size()][]));
	            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
			}
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
		FrontaleResultsSaver srv = new FrontaleResultsSaver();
		srv.extractResults();
	}


}
