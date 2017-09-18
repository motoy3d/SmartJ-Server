package com.urawaredsmylife.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * チームユーティリティ
 * @author motoi
 */
public class TeamUtils {
	/**
	 * チームIDとチーム名を紐付けるマップ
	 */
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
		TEAMS.put("trinita", "大分トリニータ");
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
		TEAMS.put("ehimefc", "愛媛FC");
		TEAMS.put("fcgifu", "FC岐阜");
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
		TEAMS.put("v_varen", " V・ファーレン長崎");
		TEAMS.put("kamatamare", "カマタマーレ讃岐");
		TEAMS.put("renofa", "レノファ山口FC");
	}
	
	/**
	 * チーム略称とチーム名を紐付けるマップ
	 */
	public static final Map<String, String> TEAM_NAMES_SHORT_FULL = new HashMap<String, String>();
	static {
		TEAM_NAMES_SHORT_FULL.put("仙台", "ベガルタ仙台");
		TEAM_NAMES_SHORT_FULL.put("広島", "サンフレッチェ広島");
		TEAM_NAMES_SHORT_FULL.put("磐田", "ジュビロ磐田");
		TEAM_NAMES_SHORT_FULL.put("清水", "清水エスパルス");
		TEAM_NAMES_SHORT_FULL.put("浦和", "浦和レッズ");
		TEAM_NAMES_SHORT_FULL.put("F東京", "FC東京");
		TEAM_NAMES_SHORT_FULL.put("Ｆ東京", "FC東京");
		TEAM_NAMES_SHORT_FULL.put("川崎", "川崎フロンターレ");
		TEAM_NAMES_SHORT_FULL.put("川崎F", "川崎フロンターレ");
		TEAM_NAMES_SHORT_FULL.put("川崎Ｆ", "川崎フロンターレ");
		TEAM_NAMES_SHORT_FULL.put("鳥栖", "サガン鳥栖");
		TEAM_NAMES_SHORT_FULL.put("横浜FM", "横浜F・マリノス");
		TEAM_NAMES_SHORT_FULL.put("横浜ＦＭ", "横浜F・マリノス");
		TEAM_NAMES_SHORT_FULL.put("鹿島", "鹿島アントラーズ");
		TEAM_NAMES_SHORT_FULL.put("C大阪", "セレッソ大阪");
		TEAM_NAMES_SHORT_FULL.put("Ｃ大阪", "セレッソ大阪");
		TEAM_NAMES_SHORT_FULL.put("柏", "柏レイソル");
		TEAM_NAMES_SHORT_FULL.put("名古屋", "名古屋グランパス");
		TEAM_NAMES_SHORT_FULL.put("神戸", "ヴィッセル神戸");
		TEAM_NAMES_SHORT_FULL.put("大宮", "大宮アルディージャ");
		TEAM_NAMES_SHORT_FULL.put("G大阪", "ガンバ大阪");
		TEAM_NAMES_SHORT_FULL.put("Ｇ大阪", "ガンバ大阪");
		TEAM_NAMES_SHORT_FULL.put("新潟", "アルビレックス新潟");
		TEAM_NAMES_SHORT_FULL.put("札幌", "北海道コンサドーレ札幌");
		TEAM_NAMES_SHORT_FULL.put("甲府", "ヴァンフォーレ甲府");
		TEAM_NAMES_SHORT_FULL.put("湘南", "湘南ベルマーレ");
		TEAM_NAMES_SHORT_FULL.put("大分", "大分トリニータ");
		TEAM_NAMES_SHORT_FULL.put("京都", "京都サンガF.C");
		TEAM_NAMES_SHORT_FULL.put("千葉", "ジェフユナイテッド千葉");
		TEAM_NAMES_SHORT_FULL.put("東京V", "東京ヴェルディ");
		TEAM_NAMES_SHORT_FULL.put("東京Ｖ", "東京ヴェルディ");
		TEAM_NAMES_SHORT_FULL.put("山形", "モンテディオ山形");
		TEAM_NAMES_SHORT_FULL.put("横浜FC", "横浜FC");
		TEAM_NAMES_SHORT_FULL.put("横浜ＦＣ", "横浜FC");
		TEAM_NAMES_SHORT_FULL.put("栃木", "栃木FC");
		TEAM_NAMES_SHORT_FULL.put("北九州", "ギラヴァンツ北九州");
		TEAM_NAMES_SHORT_FULL.put("岡山", "ファジアーノ岡山");
		TEAM_NAMES_SHORT_FULL.put("水戸", "水戸ホーリーホック");
		TEAM_NAMES_SHORT_FULL.put("松本山雅", "松本山雅FC");
		TEAM_NAMES_SHORT_FULL.put("松本", "松本山雅FC");
		TEAM_NAMES_SHORT_FULL.put("徳島", "徳島ヴォルティス");
		TEAM_NAMES_SHORT_FULL.put("群馬", "ザスパクサツ群馬");
		TEAM_NAMES_SHORT_FULL.put("熊本", "ロアッソ熊本");
		TEAM_NAMES_SHORT_FULL.put("福岡", "アビスパ福岡");
		TEAM_NAMES_SHORT_FULL.put("愛媛", "愛媛FC");
		TEAM_NAMES_SHORT_FULL.put("岐阜", "FC岐阜");
		TEAM_NAMES_SHORT_FULL.put("富山", "カターレ富山");
		TEAM_NAMES_SHORT_FULL.put("鳥取", "ガイナーレ鳥取");
		TEAM_NAMES_SHORT_FULL.put("町田", "FC町田ゼルビア");
		TEAM_NAMES_SHORT_FULL.put("金沢", "ツエーゲン金沢");
		TEAM_NAMES_SHORT_FULL.put("長野", "AC長野パルセイロ");
		TEAM_NAMES_SHORT_FULL.put("盛岡", "グルージャ盛岡");
		TEAM_NAMES_SHORT_FULL.put("相模原", "SC相模原");
		TEAM_NAMES_SHORT_FULL.put("琉球", "FC琉球");
		TEAM_NAMES_SHORT_FULL.put("福島", "福島ユナイテッドFC");
		TEAM_NAMES_SHORT_FULL.put("秋田", "ブラウブリッツ秋田");
		TEAM_NAMES_SHORT_FULL.put("J-22", "Jリーグ・アンダー22選抜");
		TEAM_NAMES_SHORT_FULL.put("藤枝", "藤枝MYFC");
		TEAM_NAMES_SHORT_FULL.put("YS横浜", "Y.S.C.C.横浜");
		TEAM_NAMES_SHORT_FULL.put("ＹＳ横浜", "Y.S.C.C.横浜");
		TEAM_NAMES_SHORT_FULL.put("長崎", "V・ファーレン長崎");
		TEAM_NAMES_SHORT_FULL.put("讃岐", "カマタマーレ讃岐");
		TEAM_NAMES_SHORT_FULL.put("山口", "レノファ山口FC");
		
		// TEAM_NAMES_FULL_SHORTと同期
		TEAM_NAMES_SHORT_FULL.put("SHA", "上海上港");//中国
		TEAM_NAMES_SHORT_FULL.put("SHD", "山東魯能");//中国
		TEAM_NAMES_SHORT_FULL.put("GUA", "広州恒大");//中国
		TEAM_NAMES_SHORT_FULL.put("JIA", "江蘇蘇寧");//中国
		TEAM_NAMES_SHORT_FULL.put("SEO", "ＦＣソウル");//韓国
		TEAM_NAMES_SHORT_FULL.put("JEO", "全北現代");//韓国
		TEAM_NAMES_SHORT_FULL.put("SUW", "水原三星");//韓国
		TEAM_NAMES_SHORT_FULL.put("POH", "浦項");//韓国
		TEAM_NAMES_SHORT_FULL.put("CHO", "チョンブリ");//タイ
		TEAM_NAMES_SHORT_FULL.put("BIN", "ビンズオン");	//タイ
		TEAM_NAMES_SHORT_FULL.put("MUA", "ムアントン");	//タイ
		TEAM_NAMES_SHORT_FULL.put("BUR", "ブリーラム");//タイ
		TEAM_NAMES_SHORT_FULL.put("SYD", "シドニーＦＣ");	//オーストラリア
		TEAM_NAMES_SHORT_FULL.put("WSW", "ウェスタンシドニー");	//オーストラリア
		TEAM_NAMES_SHORT_FULL.put("MEL", "メルボルンビクトリー");//オーストラリア
		TEAM_NAMES_SHORT_FULL.put("EST", "イースタンSC");//香港
		TEAM_NAMES_SHORT_FULL.put("BVB", "ドルトムント");//ドイツ
		TEAM_NAMES_SHORT_FULL.put("SFC", "セビージャ");//スペイン
		TEAM_NAMES_SHORT_FULL.put("シャペ", "シャペコエンセ");//ブラジル
	}

	/**
	 * チーム名ととチーム略称を紐付けるマップ
	 */
	public static final Map<String, String> TEAM_NAMES_FULL_SHORT = new HashMap<String, String>();
	static {
		TEAM_NAMES_FULL_SHORT.put("ベガルタ仙台", "仙台");
		TEAM_NAMES_FULL_SHORT.put("サンフレッチェ広島", "広島");
		TEAM_NAMES_FULL_SHORT.put("ジュビロ磐田", "磐田");
		TEAM_NAMES_FULL_SHORT.put("清水エスパルス", "清水");
		TEAM_NAMES_FULL_SHORT.put("浦和レッズ", "浦和");
		TEAM_NAMES_FULL_SHORT.put("FC東京", "F東京");
		TEAM_NAMES_FULL_SHORT.put("ＦＣ東京", "F東京");
		TEAM_NAMES_FULL_SHORT.put("川崎フロンターレ", "川崎");
		TEAM_NAMES_FULL_SHORT.put("サガン鳥栖", "鳥栖");
		TEAM_NAMES_FULL_SHORT.put("横浜F・マリノス", "横浜FM");
		TEAM_NAMES_FULL_SHORT.put("横浜Ｆ・マリノス", "横浜FM");
		TEAM_NAMES_FULL_SHORT.put("鹿島アントラーズ", "鹿島");
		TEAM_NAMES_FULL_SHORT.put("セレッソ大阪", "C大阪");
		TEAM_NAMES_FULL_SHORT.put("柏レイソル", "柏");
		TEAM_NAMES_FULL_SHORT.put("名古屋グランパス", "名古屋");
		TEAM_NAMES_FULL_SHORT.put("ヴィッセル神戸", "神戸");
		TEAM_NAMES_FULL_SHORT.put("大宮アルディージャ", "大宮");
		TEAM_NAMES_FULL_SHORT.put("ガンバ大阪", "G大阪");
		TEAM_NAMES_FULL_SHORT.put("アルビレックス新潟", "新潟");
		TEAM_NAMES_FULL_SHORT.put("北海道コンサドーレ札幌", "札幌");
		TEAM_NAMES_FULL_SHORT.put("ヴァンフォーレ甲府", "甲府");
		TEAM_NAMES_FULL_SHORT.put("湘南ベルマーレ", "湘南");
		TEAM_NAMES_FULL_SHORT.put("大分トリニータ", "大分");
		TEAM_NAMES_FULL_SHORT.put("京都サンガF.C.", "京都");
		TEAM_NAMES_FULL_SHORT.put("千葉ジェフユナイテッド", "千葉");
		TEAM_NAMES_FULL_SHORT.put("東京ヴェルディ", "東京V");
		TEAM_NAMES_FULL_SHORT.put("モンテディオ山形", "山形");
		TEAM_NAMES_FULL_SHORT.put("横浜FC", "横浜FC");
		TEAM_NAMES_FULL_SHORT.put("横浜ＦＣ", "横浜FC");
		TEAM_NAMES_FULL_SHORT.put("栃木FC", "栃木");
		TEAM_NAMES_FULL_SHORT.put("栃木ＦＣ", "栃木");
		TEAM_NAMES_FULL_SHORT.put("北九州ギラヴァンツ", "北九州");
		TEAM_NAMES_FULL_SHORT.put("ファジアーノ岡山", "岡山");
		TEAM_NAMES_FULL_SHORT.put("水戸ホーリーホック", "水戸");
		TEAM_NAMES_FULL_SHORT.put("松本山雅FC", "松本山雅");
		TEAM_NAMES_FULL_SHORT.put("松本山雅ＦＣ", "松本山雅");
		TEAM_NAMES_FULL_SHORT.put("徳島ヴォルティス", "徳島");
		TEAM_NAMES_FULL_SHORT.put("群馬ザスパクサツ", "群馬");
		
		
		TEAM_NAMES_FULL_SHORT.put("ロアッソ熊本", "熊本");
		TEAM_NAMES_FULL_SHORT.put("アビスパ福岡", "福岡");
		TEAM_NAMES_FULL_SHORT.put("愛媛FC", "愛媛");
		TEAM_NAMES_FULL_SHORT.put("愛媛ＦＣ", "愛媛");
		TEAM_NAMES_FULL_SHORT.put("岐阜FC", "岐阜");
		TEAM_NAMES_FULL_SHORT.put("岐阜ＦＣ", "岐阜");
		TEAM_NAMES_FULL_SHORT.put("カターレ富山", "富山");
		TEAM_NAMES_FULL_SHORT.put("ガイナーレ鳥取", "鳥取");
		TEAM_NAMES_FULL_SHORT.put("FC町田ゼルビア", "町田");
		TEAM_NAMES_FULL_SHORT.put("ＦＣ町田ゼルビア", "町田");
		TEAM_NAMES_FULL_SHORT.put("ツエーゲン金沢", "金沢");
		TEAM_NAMES_FULL_SHORT.put("AC長野パルセイロ", "長野");
		TEAM_NAMES_FULL_SHORT.put("AＣ長野パルセイロ", "長野");
		TEAM_NAMES_FULL_SHORT.put("グルージャ盛岡", "盛岡");
		TEAM_NAMES_FULL_SHORT.put("SC相模原", "相模原");
		TEAM_NAMES_FULL_SHORT.put("ＳＣ相模原", "相模原");
		TEAM_NAMES_FULL_SHORT.put("FC琉球", "琉球");
		TEAM_NAMES_FULL_SHORT.put("ＦＣ琉球", "琉球");
		TEAM_NAMES_FULL_SHORT.put("福島ユナイテッドFC", "福島");
		TEAM_NAMES_FULL_SHORT.put("秋田ブラウブリッツ", "秋田");
		TEAM_NAMES_FULL_SHORT.put("Jリーグ・アンダー22選抜", "J-22");
		TEAM_NAMES_FULL_SHORT.put("藤枝MYFC", "藤枝");
		TEAM_NAMES_FULL_SHORT.put("Y.S.C.C.横浜", "YS横浜");
		TEAM_NAMES_FULL_SHORT.put("V・ファーレン長崎", "長崎");
		TEAM_NAMES_FULL_SHORT.put("カマタマーレ讃岐", "讃岐");
		TEAM_NAMES_FULL_SHORT.put("レノファ山口FC", "山口");
		
		// TEAM_NAMES_SHORT_FULLと同期
		TEAM_NAMES_FULL_SHORT.put("SHA", "上海上港");//中国
		TEAM_NAMES_FULL_SHORT.put("SHD", "山東魯能");//中国
		TEAM_NAMES_FULL_SHORT.put("GUA", "広州恒大");//中国
		TEAM_NAMES_FULL_SHORT.put("JIA", "江蘇蘇寧");//中国
		TEAM_NAMES_FULL_SHORT.put("SEO", "ＦＣソウル");//韓国
		TEAM_NAMES_FULL_SHORT.put("JEO", "全北現代");//韓国
		TEAM_NAMES_FULL_SHORT.put("SUW", "水原三星");//韓国
		TEAM_NAMES_FULL_SHORT.put("水原三星ブルーウイングス", "水原三星");//韓国
		TEAM_NAMES_FULL_SHORT.put("POH", "浦項");//韓国
		TEAM_NAMES_FULL_SHORT.put("済州ユナイテッド", "済州");//韓国
		TEAM_NAMES_FULL_SHORT.put("済州ユナイテッドＦＣ", "済州");//韓国
		TEAM_NAMES_FULL_SHORT.put("CHO", "チョンブリ");//タイ
		TEAM_NAMES_FULL_SHORT.put("BIN", "ビンズオン");	//タイ
		TEAM_NAMES_FULL_SHORT.put("MUA", "ムアントン");	//タイ
		TEAM_NAMES_FULL_SHORT.put("ムアントン・ユナイテッド", "ムアントン");	//タイ
		TEAM_NAMES_FULL_SHORT.put("BUR", "ブリーラム");//タイ
		TEAM_NAMES_FULL_SHORT.put("SYD", "シドニーＦＣ");	//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("WSW", "Wシドニー");	//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("ウェスタン・シドニー・ワンダラーズ", "Wシドニー");	//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("ブリスベン ロアー", "ブリスベン");	//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("MEL", "メルボルン");//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("ADE", "アデレード");//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("アデレード・ユナイテッド", "アデレード");//オーストラリア
		TEAM_NAMES_FULL_SHORT.put("EST", "イースタンSC");//香港
		TEAM_NAMES_FULL_SHORT.put("BVB", "ドルトムント");//ドイツ
		TEAM_NAMES_FULL_SHORT.put("SFC", "セビージャ");//スペイン
		TEAM_NAMES_FULL_SHORT.put("シャペ", "シャペコエンセ");//ブラジル
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
		String officialTeamName = TEAM_NAMES_SHORT_FULL.get(shortTeamName);
		if (officialTeamName == null) {
			return shortTeamName;
		}
		return officialTeamName;
	}

	/**
	 * 引数の正式チーム名に相当する短いチーム名を返す。
	 */
	public static final String getShortTeamName(String officialTeamName) {
		String shortName = TEAM_NAMES_FULL_SHORT.get(officialTeamName);
		if (shortName == null) {
			shortName = TEAM_NAMES_FULL_SHORT.get(officialTeamName.replaceAll("　", "・"));
			if (shortName == null) {
				return officialTeamName;
			}
		}
		return shortName;
	}

}
