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
		TEAMS.put("consadole", "北海道コンサドーレ札幌");
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
		TEAMS.put("yamaguchi", "レノファ山口FC");
	}
	
	public static final Map<String, String> TEAM_NAMES = new HashMap<String, String>();
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
		TEAM_NAMES.put("Ｇ大阪", "ガンバ大阪");
		TEAM_NAMES.put("新潟", "アルビレックス新潟");
		TEAM_NAMES.put("札幌", "北海道コンサドーレ札幌");
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
		TEAM_NAMES.put("松本", "松本山雅FC");
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
		TEAM_NAMES.put("山口", "レノファ山口FC");
		
		TEAM_NAMES.put("SHA", "上海上港");
		TEAM_NAMES.put("SEO", "ＦＣソウル");
		TEAM_NAMES.put("CHO", "チョンブリ");
		TEAM_NAMES.put("JEO", "全北現代");
		TEAM_NAMES.put("SHD", "山東魯能");
		TEAM_NAMES.put("SUW", "水原三星");
		TEAM_NAMES.put("SYD", "シドニーＦＣ");
		TEAM_NAMES.put("BIN", "ビンズオン");
		TEAM_NAMES.put("MEL", "メルボルンビクトリー");
		TEAM_NAMES.put("POH", "浦項");
		TEAM_NAMES.put("JIA", "江蘇蘇寧");
		TEAM_NAMES.put("BUR", "ブリーラム");
		TEAM_NAMES.put("GUA", "広州恒大");
		
		
		
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

	/**
	 * 引数の短いチーム名に相当する正式チーム名を返す。
	 */
	public static final String getOfficialTeamName(String shortTeamName) {
		String officialTeamName = TEAM_NAMES.get(shortTeamName);
		if (officialTeamName == null) {
			return shortTeamName;
		}
		return officialTeamName;
	}

	/**
	 * 引数の正式チーム名に相当する短いチーム名を返す。
	 */
	public static final String getShortTeamName(String officialTeamName) {
		Iterator<String> keys = TEAM_NAMES.keySet().iterator();
		while (keys.hasNext()) {
			String shortName = keys.next();
			if (officialTeamName.equals(TEAM_NAMES.get(shortName))) {
				return shortName;
			}
		}
		return officialTeamName;
	}

}
