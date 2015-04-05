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
	private static final String J_SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html"
			+ "%20where%20url%3D%22http%3A%2F%2Fwww.yamaga-fc.com%2Fgame{SEASON}%2Findex.html%22%20"
			+ "and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game'%5D%2Ftbody%2Ftr%22&format=json&callback=";
	private static final String NABISCO_SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html"
			+ "%20where%20url%3D%22http%3A%2F%2Fwww.yamaga-fc.com%2Fgame{SEASON}%2Fcategories%2Fnabisco.html%22%20"
			+ "and%20xpath%3D%22%2F%2Ftable%5B%40class%3D'game'%5D%2Ftbody%2Ftr%22&format=json&callback=";

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
			String[] urls = new String[] {J_SRC_URL, NABISCO_SRC_URL};
            List<Object[]> insertDataList = new ArrayList<Object[]>();
			for(String url : urls) {
				StopWatch sw = new StopWatch();
				sw.start();
				GetMethodWebRequest req = new GetMethodWebRequest(url.replace("{SEASON}", season));
				WebResponse res = wc.getResponse(req);
				sw.stop();
				System.out.println((sw.getTime()/1000.0) + "秒");
				
				Map<String, Object> json = (Map<String, Object>)JSON.decode(res.getText());
				//logger.info("json = " + json.toString());
				List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");	
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
					//System.out.println("gameItems.get(0)=" + gameItems.get(0));
					String compe = ((String)((Map)((Map)gameItems.get(0)).get("div")).get("content")).replaceAll("\n", "");
					if ("大会/節".equals(compe) || "Jリーグ・スカパー！ニューイヤーカップ".equals(compe)) {
						continue;
					}
					compe = compe.replaceAll("J1リーグ", "J1").replaceAll("1stステージ", "1st/").replaceAll("2ndステージ", "2nd/")
							.replaceAll("ヤマザキナビスコカップ ", "ナビスコ/")
							.replaceAll("ACLノックアウトステージ　", "ACL/").replaceAll("　", "")
							.replaceAll("明治安田生命", "")
							.replace(season, "");
					String gameDateView = null;
					String time = null;
					//System.out.println("gameItems.get(2)=" + gameItems.get(2));
					if (gameItems.get(2) instanceof Map) {
						gameDateView = (String)((Map)gameItems.get(2)).get("span");
						time = StringUtils.deleteWhitespace((String)((Map)gameItems.get(2)).get("content")).replace("～", "");
					}
					if (StringUtils.isNotBlank(gameDateView)) {
						gameDateView = StringUtils.deleteWhitespace(gameDateView.replaceAll("<br/>", "").replaceAll("※.*", ""));
					}
					//System.out.println("日●" + gameDateView);
					String gameDate = null;
					if(gameDateView.contains("(")) {
						gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
					} else {
						gameDate = "";	//未定等
					}
					//System.out.println("gameItems.get(1)=" + gameItems.get(1));
					String vsTeam = "";
					if (((Map)gameItems.get(1)).get("a") != null) {
						vsTeam = (String)((Map)((Map)gameItems.get(1)).get("a")).get("content");
					}
					String stadium = "";
					String tv = null;
	//				System.out.println("gameItems.get(3)=" + gameItems.get(3));
					if (gameItems.get(3) != null && gameItems.get(3) instanceof Map &&
							((Map)gameItems.get(3)).get("a") != null) {
						stadium = (String)((Map)((Map)gameItems.get(3)).get("a")).get("content");
						if ("未定".equals(stadium)) {
							stadium = "会場未定";
						}
					}
					//System.out.println("gameItems.get(5)=" + gameItems.get(5));
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
					Object[] oneRec = new Object[12];
					oneRec[c++] = season;
					oneRec[c++] = compe;
					oneRec[c++] = gameDate;
					oneRec[c++] = gameDateView;
					oneRec[c++] = time;
					oneRec[c++] = stadium;
					oneRec[c++] = "アルウィン".equals(stadium);
					oneRec[c++] = vsTeam;
					oneRec[c++] = tv;
					oneRec[c++] = result;
					oneRec[c++] = score;
					oneRec[c++] = detailUrl;
					insertDataList.add(oneRec);
					logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + "アルウィン".equals(stadium) + ", " 
							+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
				}
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
