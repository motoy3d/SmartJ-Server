package com.urawaredsmylife;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.urawaredsmylife.util.Const;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;
import com.urawaredsmylife.util.TeamUtils;

/**
 * Jリーグ公式サイトからJリーグ、ルヴァンカップ、ACLの順位表を取得してDBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class StandingsSaver {
	private Logger logger = Logger.getLogger(StandingsSaver.class.getName());
	/**
	 * 順位表URL
	 */
	private static final String SRC_URL_J1 = "http://www.jleague.jp/standings/j1/";
	private static final String SRC_URL_J2 = "http://www.jleague.jp/standings/j2/";
	private static final String SRC_URL_LEVAIN = "http://www.jleague.jp/standings/leaguecup/";
	private static final String SRC_URL_ACL = "http://www.jleague.jp/standings/acl/";

	private static final int J1_TEAM_COUNT = 20;
	private static final int J2_TEAM_COUNT = 20;

	/**
	 * ルヴァンカップ参加チーム数（年によって変わる可能性あり）
	 */
	private static final int LEVAIN_TEAM_COUNT = 16;
	private static final String[] LEVAIN_GROUPS = new String[] {"A", "B", "C", "D"};

	/**
	 * ACLチーム数（年によって変わる可能性あり）
	 */
	private static final int ACL_TEAM_COUNT = 16;

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StandingsSaver srv = new StandingsSaver();
		int status = srv.extractStandings();
		System.exit(status);
	}

	/**
	 * コンストラクタ
	 */
	public StandingsSaver() {
	}

	/**
	 * Yahooスポーツにアクセスし、順位表を抽出する
	 * @return
	 * @throws ParseException
	 */
	private int extractStandings() {
		try {
			// J1
			Date j1OpenDate = DateUtils.parseDate(Const.J1_OPEN_DATE, new String[] {"yyyy/MM/dd"});
			int j1Result = 0;
			if (j1OpenDate.getTime() < new Date().getTime()) {
				j1Result = insertJ(SRC_URL_J1, "J1", "", J1_TEAM_COUNT);
			}
			// J2
			Date j2OpenDate = DateUtils.parseDate(Const.J2_OPEN_DATE, new String[] {"yyyy/MM/dd"});
			int j2Result = 0;
			if (j2OpenDate.getTime() < new Date().getTime()) {
				j2Result = insertJ(SRC_URL_J2, "J2", "", J2_TEAM_COUNT);
			}
			// ルヴァンカップ
			// 形式変更未対応のためコメントアウト
//			Date levainOpenDate = DateUtils.parseDate(Const.LEVAIN_OPEN_DATE, new String[] {"yyyy/MM/dd"});
//			int levainResult = 0;
//			if (levainOpenDate.getTime() < new Date().getTime()) {
//				levainResult = insertLevain();
//			}
//			//ACL
//			Date aclOpenDate = DateUtils.parseDate(Const.ACL_OPEN_DATE, new String[] {"yyyy/MM/dd"});
//			int aclResult = 0;
//			if (aclOpenDate.getTime() < new Date().getTime()) {
//				aclResult = insertACL();
//			}

			return j1Result + j2Result /*+ levainResult + aclResult*/;
		} catch(Exception ex) {
			logger.error("順位表取得エラー", ex);
			Mail.send(ex);
			return 1;
		}
	}

	/**
	 * Jリーグ順位表URLにアクセスして解析し、standingsテーブルにINSERTする。
	 * @param srcUrl
	 * @param league
	 * @param stage
	 * @param teamCount
	 * @return
	 */
    private int insertJ(String srcUrl, String league, String stage, int teamCount) {
        try {
            logger.info("----------------------------------------");
            logger.info(srcUrl);
            logger.info("----------------------------------------");

            // HTMLを取得してパース
            Document doc = Jsoup.connect(srcUrl).get();
            Element table = doc.getElementsByTag("table").get(0); // 最初のtable要素を取得
            if (table == null) {
                logger.warn("順位表テーブルが見つかりませんでした");
                return -1;
            }

            Elements rows = table.select("tr");
            if (rows.size() <= 1) {
                logger.warn("順位表データが取得出来ませんでした");
                return -1;
            }

            String insertSql = "INSERT INTO standings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[teamCount][];
            String season = new SimpleDateFormat("yyyy").format(new Date());

            for (int r = 1; r < rows.size(); r++) {
                Element row = rows.get(r);
                Elements cells = row.select("td");

                if (cells.size() < 11) {
                    logger.warn("不正な行データをスキップします: " + row.text());
                    continue;
                }

                String rank = cells.get(1).text().replace("-", "0");
                String team = cells.get(2).text();
                team = team.length() / 2 > 0 ? team.substring(0, team.length() / 2) : team; // チーム名が2回繰り返されている場合に対応
                team = Normalizer.normalize(team, Normalizer.Form.NFKC);
                String point = cells.get(3).text();
                String games = cells.get(4).text();
                String win = cells.get(5).text();
                String draw = cells.get(6).text();
                String lose = cells.get(7).text();
                String gotGoal = cells.get(8).text();
                String lostGoal = cells.get(9).text();
                String diff = cells.get(10).text();

                String teamId = TeamUtils.getTeamId(team);
                if ("V・ファーレン長崎".equals(team)) {
                    teamId = "v_varen";
                }

                int c = 0;
                insertDataList[r - 1] = new Object[15];
                insertDataList[r - 1][c++] = season;
                insertDataList[r - 1][c++] = league;
                insertDataList[r - 1][c++] = "J1".equals(league) ? stage : "-";
                insertDataList[r - 1][c++] = r;
                insertDataList[r - 1][c++] = rank;
                insertDataList[r - 1][c++] = teamId;
                insertDataList[r - 1][c++] = team;
                insertDataList[r - 1][c++] = point;
                insertDataList[r - 1][c++] = games;
                insertDataList[r - 1][c++] = win;
                insertDataList[r - 1][c++] = draw;
                insertDataList[r - 1][c++] = lose;
                insertDataList[r - 1][c++] = gotGoal;
                insertDataList[r - 1][c++] = lostGoal;
                insertDataList[r - 1][c++] = diff;
            }

            QueryRunner qr = DB.createQueryRunner();
            String delSql = "DELETE FROM standings WHERE season=? AND league=?";
            logger.info("順位表一旦削除=" + delSql);
            int deletedCount = qr.update(delSql, season, league);
            logger.info("削除件数: " + deletedCount);
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + Arrays.toString(resultCount));
        } catch (Exception e) {
            logger.error("J1/J2順位表抽出エラー", e);
            Mail.send(e);
            return 1;
        }
        return 0;
    }

	/**
	 * ルヴァンカップ順位表URLにアクセスして解析し、nabiscoStandingsテーブルにINSERTする。
	 * @return
	 */
	private int insertLevain() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		logger.info("----------------------------------------");
		logger.info(SRC_URL_LEVAIN);
		logger.info("----------------------------------------");
		GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL_LEVAIN);
		try {
			WebResponse res = wc.getResponse(req);
			WebTable[] tables = res.getTables();
			System.out.println("テーブル数：" + tables.length);

			String insertSql = "INSERT INTO nabiscoStandings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[LEVAIN_TEAM_COUNT][];
            String season = new SimpleDateFormat("yyyy").format(new Date());
            // tables = グループごと
            int rowIdx = 0;
			for(int g=0; g<tables.length; g++) {
				WebTable table = tables[g];
				TableRow[] rows = table.getRows();
				for(int r=1; r<rows.length; r++) {
					System.out.println("-----------------------------");
					String rank = table.getCellAsText(r, 1).replace("-", "1");
					if ("-".equals(rank)) rank = "0";
					TableCell teamCell = table.getTableCell(r, 2);
					String team = teamCell.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
					String point = table.getCellAsText(r, 3);
					String games = table.getCellAsText(r, 4);
					String win = table.getCellAsText(r, 5);
					String draw = table.getCellAsText(r, 6);
					String lose = table.getCellAsText(r, 7);
					String gotGoal = table.getCellAsText(r, 8);
					String lostGoal = table.getCellAsText(r, 9);
					String diff = table.getCellAsText(r, 10);
					String group = LEVAIN_GROUPS[g];
					System.out.println(group + "-" + r + ", " + rank + " : " + team);
					int c = 0;
					insertDataList[rowIdx] = new Object[13];
					insertDataList[rowIdx][c++] = season;
					insertDataList[rowIdx][c++] = group;	//グループ
					insertDataList[rowIdx][c++] = r;
					insertDataList[rowIdx][c++] = rank;
					insertDataList[rowIdx][c++] = team;
					insertDataList[rowIdx][c++] = point;
					insertDataList[rowIdx][c++] = games;
					insertDataList[rowIdx][c++] = win;
					insertDataList[rowIdx][c++] = draw;
					insertDataList[rowIdx][c++] = lose;
					insertDataList[rowIdx][c++] = gotGoal;
					insertDataList[rowIdx][c++] = lostGoal;
					insertDataList[rowIdx][c++] = diff;
					rowIdx++;
					logger.info("Levain: " + season + ", " + group + ", " + r + ", " + rank + ", " + team + ", " + point + ", "
							+ games + ", " + win + ", " + draw + ", " + lose + ", " + gotGoal + ", " + lostGoal + ", " + diff);
				}
			}

			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM nabiscoStandings WHERE season=" + season);
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("ルヴァンカップ順位表抽出エラー", e);
			Mail.send(e);
			return 1;
		}
		return 0;
	}

	/**
	 * ACL順位表URLにアクセスして解析し、aclStandingsテーブルにINSERTする。
	 * @return
	 */
	private int insertACL() {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		try {
			logger.info("----------------------------------------");
			logger.info(SRC_URL_ACL);
			logger.info("----------------------------------------");
			String[] groupNameList = new String[] {"E", "F", "G", "H"};
            String insertSql = "INSERT INTO aclStandings VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            Object[][] insertDataList = new Object[ACL_TEAM_COUNT][];
            String season = new SimpleDateFormat("yyyy").format(new Date());

            int rowIdx = 0;
			GetMethodWebRequest req = new GetMethodWebRequest(SRC_URL_ACL);
			WebResponse res = wc.getResponse(req);
			WebTable[] tables = res.getTables();	//4グループ分のテーブル
			for (int grpIdx = 0; grpIdx<tables.length; grpIdx++) {
				WebTable table = tables[grpIdx];
				TableRow[] rows = table.getRows();
				for(int r=1; r<rows.length; r++) {
					System.out.println("-----------------------------");
					String rank = table.getCellAsText(r, 0).replace("-", "1");
					if ("-".equals(rank)) rank = "0";
					TableCell teamCell = table.getTableCell(r, 1);
					NodeList nodes = teamCell.getDOM().getChildNodes();
					logger.info("t=" + nodes);
					String team = null;
//					if (1 < nodes.getLength()) {
//						team = nodes.item(1).getNodeValue();
//					} else {
						team = table.getCellAsText(r, 1);
//					}
					team = team.substring(0, team.length()/2);	//チーム名が２回連続したテキストが返ってくるため。例：「浦和レッズ浦和レッズ」
					team = TeamUtils.getShortTeamName(team);
					if ("F東京".equals(team)) {
						team = "FC東京";
					}
					String point = table.getCellAsText(r, 2);
					String games = table.getCellAsText(r, 3);
					String win = table.getCellAsText(r, 4);
					String draw = table.getCellAsText(r, 5);
					String lose = table.getCellAsText(r, 6);
					String gotGoal = table.getCellAsText(r, 7);
					String lostGoal = table.getCellAsText(r, 8);
					String diff = table.getCellAsText(r, 9);
					String group = groupNameList[grpIdx];
					System.out.println(group + "-" + rank + " : " + team);
					int c = 0;
					insertDataList[rowIdx] = new Object[13];
					insertDataList[rowIdx][c++] = season;
					insertDataList[rowIdx][c++] = group;	//グループ
					insertDataList[rowIdx][c++] = r;
					insertDataList[rowIdx][c++] = rank;
					insertDataList[rowIdx][c++] = team;
					insertDataList[rowIdx][c++] = point;
					insertDataList[rowIdx][c++] = games;
					insertDataList[rowIdx][c++] = win;
					insertDataList[rowIdx][c++] = draw;
					insertDataList[rowIdx][c++] = lose;
					insertDataList[rowIdx][c++] = gotGoal;
					insertDataList[rowIdx][c++] = lostGoal;
					insertDataList[rowIdx][c++] = diff;
					rowIdx++;
				}
			}

			QueryRunner qr = DB.createQueryRunner();
			qr.update("DELETE FROM aclStandings WHERE season=" + season);
            int[] resultCount = qr.batch(insertSql, insertDataList);
            logger.info("登録件数：" + ToStringBuilder.reflectionToString(resultCount));
		} catch (Exception e) {
			logger.error("ACL順位表抽出エラー", e);
			Mail.send(e);
			return 1;
		}
		return 0;
	}
}
