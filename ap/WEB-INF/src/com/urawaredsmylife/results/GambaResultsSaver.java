package com.urawaredsmylife.results;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * ガンバ大阪公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class GambaResultsSaver {
	private Logger logger = Logger.getLogger(GambaResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "gamba";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from"
			+ "%20html%20where%20url%3D%22http%3A%2F%2Fwww.gamba-osaka.net%2Fgame%2F%22%20"
			+ "and%20xpath%3D%22%2F%2Fdiv%5B%40class%3D'schedule_month_area'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public GambaResultsSaver() {
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
//			logger.info(json.toString());
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query"))
					.get("results")).get("tr");
//			logger.info(gameList.getClass().toString());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null) {
					continue;
				}
				String compeName = "";
				Map compeA = (Map)((Map)gameItems.get(2)).get("a");
				System.out.println("(Map)gameItems.get(2)=" + (Map)gameItems.get(2));
				System.out.println("compeA=" + compeA);
				String compeImg = null;
				if (compeA != null) {
					compeImg = (String)((Map)compeA.get("img")).get("src");
				} else {
					compeImg = (String)((Map)((Map)gameItems.get(2)).get("img")).get("src");
				}
				if (compeImg != null) {
					System.out.println("compeImg画像=" + compeImg);
					if (compeImg.contains("j1")) {
						compeName = "J1";
					} else if (compeImg.contains("j2")) {
						compeName = "J2";
					} else if (compeImg.contains("j3")) {
						System.out.println("J3除外(U-23)");
						continue;
					} else if(compeImg.endsWith("b_logo_cs.png")) {
						compeName = "ﾁｬﾝﾋﾟｵﾝｼｯﾌﾟ";
					} else if (compeImg.contains("nabisco") || compeImg.contains("levaincup")) {
						compeName = "ルヴァン";
					} else if (compeImg.contains("acl")) {
						compeName = "ACL";
					} else if (compeImg.contains("tennohai")) {
						compeName = "天皇杯";	//天皇杯にはリンクがなかったが念のためこちらにも
					} else if (compeImg.contains("xerox")) {
						compeName = "FUJI XEROX SUPER CUP";
					} else if (compeImg.endsWith("b_logo_panasoniccup.png")) {
						compeName = "Panasonic CUP";
					} else if (compeImg.endsWith("b_logo_suruga.png")) {
						compeName = "スルガ銀行チャンピオンシップ";
					}
				}
				Map compeA2 = (Map)((Map)gameItems.get(2)).get("img");	//天皇杯はリンクがない
				if (compeA2 != null) {
					String compeImg2 = (String)compeA2.get("src");
					if (compeImg2.endsWith("tennohai.png")) {
						compeName = "天皇杯";
					}
				}
				String period = StringUtils.trimToEmpty((String)((Map)gameItems.get(3)).get("content"));
				String compe = compeName + (StringUtils.isNotBlank(period)? "/" + period : "");
				compe = compe.replaceAll(" ステージ", "").replaceAll("ステージ", "");
				
				Object gameDateViewTmp = ((Map)gameItems.get(0)).get("content");
				String gameDateView = null;
				if (gameDateViewTmp instanceof String) {
					gameDateView = ((String)gameDateViewTmp);
				} else if (gameDateViewTmp instanceof Map) {
					gameDateView = ((String)((Map)gameDateViewTmp).get("content"));
				}
				gameDateView = gameDateView.replaceAll(" 祝", "").replace(".", "/").replaceAll("※.*", "")
						.replaceAll("\r", "").replaceAll("\n", "").replaceAll(" ", "");
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = gameDateView.substring(0, gameDateView.indexOf("("));
					if ("1/1".equals(gameDate)) {	//翌年の天皇杯決勝
						gameDate = (Integer.parseInt(season)+1) + "/" + gameDate;
					} else {
						gameDate = season + "/" + gameDate;
					}
				} else {
					gameDate = "";	//未定等
				}
				
				Object timeTmp = ((Map)gameItems.get(1)).get("content");
				String time = null;
				if (timeTmp instanceof String) {
					time = (String)timeTmp;
				} else if (timeTmp instanceof Map) {
					time = (String)((Map)timeTmp).get("content");
				}
				time = StringUtils.deleteWhitespace(time);
				String stadium = null;
				if (true) {
					Map stadiumTmp = (Map)((Map)gameItems.get(6)).get("a");
					if (stadiumTmp != null) {
						stadium = (String)stadiumTmp.get("content");
					} else {
						stadium = (String)((Map)gameItems.get(6)).get("content");
						if ("未定".equals(stadium)) {
							stadium = "会場未定";
						}
					}
				}
				stadium = StringUtils.deleteWhitespace(stadium);
				String homeAway = ((String)((Map)((Map)gameItems.get(5)).get("img")).get("src"))
						.endsWith("icon_home_s.png")? "H" : "A";
				List resultMapList = (List)((Map)((Map)((Map)((Map)gameItems.get(4)).get("table")).get("tbody")).get("tr")).get("td");
				String homeTeam = StringUtils.deleteWhitespace((String)((Map)resultMapList.get(0)).get("content"));
				String awayTeam = StringUtils.deleteWhitespace((String)((Map)resultMapList.get(4)).get("content"));
				boolean isGambaHome = "Ｇ大阪".equals(homeTeam);
				String vsTeam = isGambaHome ? awayTeam : homeTeam;
				if ("Ｇ大23".equals(homeTeam) || "Ｇ大23".equals(vsTeam)) {
					System.out.println("Ｇ大23除外");
					continue;
				}
				if(TEAM_NAMES.containsKey(vsTeam)) {
					vsTeam = TEAM_NAMES.get(vsTeam);
				}
				String tv = (String)((Map)gameItems.get(7)).get("content");
				Map resultMap = (Map)((Map)resultMapList.get(2)).get("a");
				String result = null;
				String score = null;
				String detailUrl = null;
//				System.out.println("★" + resultMap);
				if (resultMap != null && resultMap.get("content") != null) {
					score = StringUtils.deleteWhitespace(((String)resultMap.get("content")).replaceAll(" ", ""));
					int homeScore = Integer.parseInt(score.substring(0, score.indexOf("-")));
					int awayScore = Integer.parseInt(score.substring(score.indexOf("-") + 1));
					if(awayScore < homeScore) {
						result = isGambaHome? "○" : "●";
					} else if(homeScore < awayScore) {
						result = isGambaHome? "●" : "○";
					} else {
						result = "△";
					}
					score = isGambaHome? homeScore + "-" + awayScore : awayScore + "-" + homeScore;					
					detailUrl = (String)resultMap.get("href");
				}
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
				logger.info("■" + compe + ", " + gameDate + ", " + gameDateView + ", " + time + ", " + stadium + ", " + homeAway + ", " 
						+ vsTeam + ", " + tv + ", " + result + ", " + score + ", " + detailUrl);
			}
			
			if (insertDataList.isEmpty()) {
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
	
	private static final Map<String, String> TEAM_NAMES = new HashMap<String, String>();
	static {
		TEAM_NAMES.put("仙台", "ベガルタ仙台");
		TEAM_NAMES.put("広島", "サンフレッチェ広島");
		TEAM_NAMES.put("磐田", "ジュビロ磐田");
		TEAM_NAMES.put("清水", "清水エスパルス");
		TEAM_NAMES.put("浦和", "浦和レッズ");
		TEAM_NAMES.put("F東京", "FC東京");
		TEAM_NAMES.put("Ｆ東京", "FC東京");
		TEAM_NAMES.put("川崎", "川崎フロンターレ");
		TEAM_NAMES.put("川崎F", "川崎フロンターレ");
		TEAM_NAMES.put("川崎Ｆ", "川崎フロンターレ");
		TEAM_NAMES.put("鳥栖", "サガン鳥栖");
		TEAM_NAMES.put("横浜FM", "横浜F・マリノス");
		TEAM_NAMES.put("横浜ＦＭ", "横浜F・マリノス");
		TEAM_NAMES.put("鹿島", "鹿島アントラーズ");
		TEAM_NAMES.put("C大阪", "セレッソ大阪");
		TEAM_NAMES.put("Ｃ大阪", "セレッソ大阪");
		TEAM_NAMES.put("柏", "柏レイソル");
		TEAM_NAMES.put("名古屋", "名古屋グランパス");
		TEAM_NAMES.put("神戸", "ヴィッセル神戸");
		TEAM_NAMES.put("大宮", "大宮アルディージャ");
		TEAM_NAMES.put("G大阪", "ガンバ大阪");
		TEAM_NAMES.put("新潟", "アルビレックス新潟");
		TEAM_NAMES.put("札幌", "コンサドーレ札幌");
		TEAM_NAMES.put("甲府", "ヴァンフォーレ甲府");
		TEAM_NAMES.put("湘南", "湘南ベルマーレ");
		TEAM_NAMES.put("大分", "大分トリニータ");
		TEAM_NAMES.put("京都", "京都サンガF.C");
		TEAM_NAMES.put("千葉", "ジェフユナイテッド千葉");
		TEAM_NAMES.put("東京V", "東京ヴェルディ");
		TEAM_NAMES.put("東京Ｖ", "東京ヴェルディ");
		TEAM_NAMES.put("山形", "モンテディオ山形");
		TEAM_NAMES.put("横浜FC", "横浜FC");
		TEAM_NAMES.put("横浜ＦＣ", "横浜FC");
		TEAM_NAMES.put("栃木", "栃木FC");
		TEAM_NAMES.put("北九州", "ギラヴァンツ北九州");
		TEAM_NAMES.put("岡山", "ファジアーノ岡山");
		TEAM_NAMES.put("水戸", "水戸ホーリーホック");
		TEAM_NAMES.put("松本山雅", "松本山雅FC");
		TEAM_NAMES.put("徳島", "徳島ヴォルティス");
		TEAM_NAMES.put("群馬", "ザスパクサツ群馬");
		TEAM_NAMES.put("熊本", "ロアッソ熊本");
		TEAM_NAMES.put("福岡", "アビスパ福岡");
		TEAM_NAMES.put("愛媛", "愛媛FC");
		TEAM_NAMES.put("岐阜", "FC岐阜");
		TEAM_NAMES.put("富山", "カターレ富山");
		TEAM_NAMES.put("鳥取", "ガイナーレ鳥取");
		TEAM_NAMES.put("町田", "FC町田ゼルビア");
		TEAM_NAMES.put("金沢", "ツエーゲン金沢");
		TEAM_NAMES.put("長野", "AC長野パルセイロ");
		TEAM_NAMES.put("盛岡", "グルージャ盛岡");
		TEAM_NAMES.put("相模原", "SC相模原");
		TEAM_NAMES.put("琉球", "FC琉球");
		TEAM_NAMES.put("福島", "福島ユナイテッドFC");
		TEAM_NAMES.put("秋田", "ブラウブリッツ秋田");
		TEAM_NAMES.put("J-22", "Jリーグ・アンダー22選抜");
		TEAM_NAMES.put("藤枝", "藤枝MYFC");
		TEAM_NAMES.put("YS横浜", "Y.S.C.C.横浜");
		TEAM_NAMES.put("ＹＳ横浜", "Y.S.C.C.横浜");
		TEAM_NAMES.put("長崎", "V・ファーレン長崎");
		TEAM_NAMES.put("讃岐", "カマタマーレ讃岐");
	}
	
	/**
	 * テスト用メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		GambaResultsSaver srv = new GambaResultsSaver();
		srv.extractResults();
	}


}
