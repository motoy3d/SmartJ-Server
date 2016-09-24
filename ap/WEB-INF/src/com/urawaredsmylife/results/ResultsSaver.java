package com.urawaredsmylife.results;

import org.apache.log4j.Logger;

/**
 * 各チーム公式サイトから試合日程・結果を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class ResultsSaver {
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(ResultsSaver.class.getName());
		logger.info("######## 日程・結果データ取得　開始 #########");
		//レッズ
		RedsResultsSaver redsSaver = new RedsResultsSaver();
		redsSaver.extractResults();
		//アルビレックス
		AlbirexResultsSaver albirexSaver = new AlbirexResultsSaver();
		albirexSaver.extractResults();
		//フロンターレ
		FrontaleResultsSaver frontaleSaver = new FrontaleResultsSaver();
		frontaleSaver.extractResults();
		//マリノス
		MarinosResultsSaver marinosSaver = new MarinosResultsSaver();
		marinosSaver.extractResults();
		//FC東京
//		FCTokyoResultsSaver fctokyoSaver = new FCTokyoResultsSaver();
//		fctokyoSaver.updateResults();
		//名古屋グランパス
//		GrampusResultsSaver grampusSaver = new GrampusResultsSaver();
//		grampusSaver.updateResults();
		//清水エスパルス
		SpulseResultsSaver spulseSaver = new SpulseResultsSaver();
		spulseSaver.extractResults();
		//ガンバ大阪
		GambaResultsSaver gambaSaver = new GambaResultsSaver();
		gambaSaver.extractResults();
		//鹿島アントラーズ
		AntlersResultsSaver antlersSaver = new AntlersResultsSaver();
		antlersSaver.extractResults();
		//ヴィッセル神戸
//		VisselResultsSaver visselSaver = new VisselResultsSaver();
//		visselSaver.extractResults();
		//サンフレッチェ広島
		SanfrecceResultsSaver sanfrecceSaver = new SanfrecceResultsSaver();
		sanfrecceSaver.extractResults();
		//ベガルタ仙台
		VegaltaResultsSaver vegaltaSaver = new VegaltaResultsSaver();
		vegaltaSaver.extractResults();
		//セレッソ大阪
//		CerezoResultsSaver cerezoSaver = new CerezoResultsSaver();
//		cerezoSaver.updateResults();
		// 大宮アルディージャ
//		ArdijaResultsSaver ardijaSaver = new ArdijaResultsSaver();
//		ardijaSaver.extractResults();
		//ヴァンフォーレ甲府
		VentforetResultsSaver ventforetSaver = new VentforetResultsSaver();
		ventforetSaver.extractResults();
		//サガン鳥栖
		SaganResultsSaver saganSaver = new SaganResultsSaver();
		saganSaver.extractResults();
		//柏レイソル
		ReysolResultsSaver reysolSaver = new ReysolResultsSaver();
		reysolSaver.extractResults();
		// 徳島ヴォルティス
//		VortisResultsSaver vortisSaver = new VortisResultsSaver();
//		vortisSaver.extractResults();
		//松本山雅FC
//		YamagaResultsSaver yamagaSaver = new YamagaResultsSaver();
//		yamagaSaver.extractResults();
		//モンテディオ山形
//		MontedioResultsSaver montedioSaver = new MontedioResultsSaver();
//		montedioSaver.extractResults();
		//ジュビロ磐田
//		JubiloResultsSaver jubiloSaver = new JubiloResultsSaver();
//		jubiloSaver.extractResults();
		// アビスパ福岡
//		AvispaResultsSaver avispaSaver = new AvispaResultsSaver();
//		avispaSaver.extractResults();

		
		logger.info("######## 日程・結果データ取得　終了 #########");
	}
}
