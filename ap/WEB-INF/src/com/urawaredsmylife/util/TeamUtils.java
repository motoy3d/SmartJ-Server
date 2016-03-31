package com.urawaredsmylife.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * チームユーティリティ
 * @author motoi
 */
public class TeamUtils {
	public static Map<String, String> TEAMS = new HashMap<String, String>();
	static {
		TEAMS.put("vegalta", "ベガルタ仙台");
		TEAMS.put("sanfrecce", "サンフレッチェ広島");
		TEAMS.put("jubilo", "ジュビロ磐田");
		TEAMS.put("spulse", "清水エスパルス");
		TEAMS.put("reds", "浦和レッズ");
		TEAMS.put("fctokyo", "FC東京");
		TEAMS.put("frontale", "川崎フロンターレ");
		TEAMS.put("sagan", "サガン鳥栖");
		TEAMS.put("marinos", "横浜F・マリノス");
		TEAMS.put("antlers", "鹿島アントラーズ");
		TEAMS.put("cerezo", "セレッソ大阪");
		TEAMS.put("reysol", "柏レイソル");
		TEAMS.put("grampus", "名古屋グランパス");
		TEAMS.put("vissel", "ヴィッセル神戸");
		TEAMS.put("ardija", "大宮アルディージャ");
		TEAMS.put("gamba", "ガンバ大阪");
		TEAMS.put("albirex", "アルビレックス新潟");
		TEAMS.put("consadole", "コンサドーレ札幌");
		TEAMS.put("ventforet", "ヴァンフォーレ甲府");
		TEAMS.put("bellmare", "湘南ベルマーレ");
		TEAMS.put("torinita", "大分トリニータ");
		TEAMS.put("sanga", "京都サンガF.C.");
		TEAMS.put("jef", "ジェフユナイテッド千葉");
		TEAMS.put("verdy", "東京ヴェルディ");
		TEAMS.put("montedio", "モンテディオ山形");
		TEAMS.put("yokohamafc", "横浜FC");
		TEAMS.put("tochigi", "栃木SC");
		TEAMS.put("giravanz", "ギラヴァンツ北九州");
		TEAMS.put("fagiano", "ファジアーノ岡山");
		TEAMS.put("hollyhock", "水戸ホーリーホック");
		TEAMS.put("yamaga", "松本山雅FC");
		TEAMS.put("vortis", "徳島ヴォルティス");
		TEAMS.put("thespa", "ザスパクサツ群馬");
		TEAMS.put("roasso", "ロアッソ熊本");
		TEAMS.put("avispa", "アビスパ福岡");
		TEAMS.put("ehime", "愛媛FC");
		TEAMS.put("gifu", "FC岐阜");
		TEAMS.put("kataller", "カターレ富山");
		TEAMS.put("gainare", "ガイナーレ鳥取");
		TEAMS.put("zelvia", "FC町田ゼルビア");
		TEAMS.put("zweigen", "ツエーゲン金沢");
		TEAMS.put("parceiro", "AC長野パルセイロ");
		TEAMS.put("grulla", "グルージャ盛岡");
		TEAMS.put("sagamihara", "SC相模原");
		TEAMS.put("ryukyu", "FC琉球");
		TEAMS.put("fukushima", "福島ユナイテッドFC");
		TEAMS.put("blaublitz", "ブラウブリッツ秋田");
		TEAMS.put("U22", "Jリーグ・アンダー22選抜");
		TEAMS.put("myfc", "藤枝MYFC");
		TEAMS.put("yscc", "Y.S.C.C.横浜");
		TEAMS.put("v-varen", " V・ファーレン長崎");
		TEAMS.put("kamatamare", "カマタマーレ讃岐");
	}
	
	/**
	 * チームIDに該当するチーム名を返す。
	 */
	public static final String getTeamName(String teamId) {
		return TEAMS.get(teamId);
	}

	/**
	 * チーム名に該当するチームIDを返す。
	 */
	public static final String getTeamId(String teamName) {
		Iterator<String> keys = TEAMS.keySet().iterator();
		while(keys.hasNext()) {
			String teamId = keys.next();
			if (teamName.equals(TEAMS.get(teamId))) {
				return teamId;
			}
		}
		return null;
	}

}
