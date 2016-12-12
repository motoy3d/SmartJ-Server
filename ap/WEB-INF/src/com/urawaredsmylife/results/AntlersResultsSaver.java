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
 * 鹿島アントラーズ公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class AntlersResultsSaver {
	private Logger logger = Logger.getLogger(AntlersResultsSaver.class.getName());
	/** チームID */
	private static final String teamId = "antlers";
	/**
	 * 取得元URL
	 */
	private static final String SRC_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from"
			+ "%20html%20where%20url%3D%22http%3A%2F%2Fwww.so-net.ne.jp%2Fantlers%2Fgames%22%20"
			+ "and%20xpath%3D%22%2F%2Fdiv%5B%40class%3D'result_table'%5D%2Ftable%2Ftbody%2Ftr%22&format=json&callback=";
	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public AntlersResultsSaver() {
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
			List<Object> gameList = (List<Object>)((Map<String, Object>)((Map<String, Object>)json.get("query")).get("results")).get("tr");
			logger.info(gameList.getClass().toString());
			
            String insertSql = "INSERT INTO " + teamId + "Results VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            List<Object[]> insertDataList = new ArrayList<Object[]>();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            //４つ目はサテライト,５つ目はプレシーズン
            String[] compeList = new String[] {"J1 1st", "J1 2nd", "CS", "CWC", "ルヴァン", "天皇杯", "スルガ銀行CS"};
            int compeIdx = 0;
            int newYearCupIdx = 1;
			for(int r=1; r<gameList.size(); r++) {
				Object game = gameList.get(r);
				List<Object> gameItems = (List<Object>)((Map)game).get("td");
				if (gameItems == null) {
					compeIdx++;
					if(6 <= compeIdx) { //TODO 天皇杯が始まったら要確認
						break;
					}
					continue;
				}
				//System.out.println("★" + ((Map)gameItems.get(0)).get("p"));
				String compe = "";
				if (gameItems.get(0) instanceof String) {
					compe = compeList[compeIdx] + "/" + 
							StringUtils.trimToEmpty((String)gameItems.get(0));
				} else if(gameItems.get(0) instanceof Map) {
					compe = compeList[compeIdx] + "/" + 
							StringUtils.trimToEmpty((String)((Map)gameItems.get(0)).get("content"));
				}
				if (compe.contains("ニューイヤーカップ")) {
					compe += newYearCupIdx++;
				}
				if (compe.startsWith("/")) {
					compe = compe.substring(1);
				}
				System.out.println("🔵compe=" + compe);
				if (StringUtils.isBlank(compe)) {
					continue;
				}
				Object gameDateViewTmp = gameItems.get(1);
				String gameDateView = null;
				if (gameDateViewTmp instanceof String) {
					gameDateView = (String)gameDateViewTmp;
				} else if (gameDateViewTmp instanceof Map) {
					gameDateView = (String)((Map)gameDateViewTmp).get("content");
				}
				gameDateView = gameDateView.replaceAll("・祝", "").replace(".", "/").replaceAll("※.*", "")
						.replaceAll("\r", "").replaceAll("\n", "").trim();
				String gameDate = null;
				if (gameDateView.contains("(")) {
					gameDate = season + "/" + gameDateView.substring(0, gameDateView.indexOf("("));
				} else {
					gameDate = "";	//未定等
				}
				String time = (String)gameItems.get(2);
				String stadium = (String)gameItems.get(3);
				String homeAway = (String)gameItems.get(4);
				String vsTeam = (String)gameItems.get(5);
				String tv = null;
				String result = null;
				String score = null;
				String detailUrl = null;
				Map resultMap = null;
				if (gameItems.get(6) instanceof String) {
//					System.out.println("🌟" + gameItems.get(6));
				} else {
					resultMap = (Map)((Map)gameItems.get(6)).get("a");
				}
				if (resultMap != null) {
					score = ((String)resultMap.get("content")).replaceAll(" ", "");
					result = score.substring(0, 1);
					score = score.substring(1);
					detailUrl = "http://www.so-net.ne.jp" + (String)resultMap.get("href");
					detailUrl = detailUrl.replace("/games", "/sp/games");
				}
				compe = StringUtils.trim(StringUtils.replace(compe, "\n", ""));
				gameDate = StringUtils.trim(StringUtils.replace(gameDate, "\n", ""));
				gameDateView = StringUtils.trim(StringUtils.replace(gameDateView, "\n", ""));
				time = StringUtils.trim(StringUtils.replace(time, "\n", ""));
				stadium = StringUtils.trim(StringUtils.replace(stadium, "\n", ""));
				vsTeam = StringUtils.trim(StringUtils.replace(vsTeam, "\n", ""));
				homeAway = StringUtils.trim(StringUtils.replace(homeAway, "\n", ""));
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
				oneRec[c++] = "H".equals(homeAway);
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
		AntlersResultsSaver srv = new AntlersResultsSaver();
		srv.extractResults();
	}


}
