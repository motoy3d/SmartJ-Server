package com.urawaredsmylife;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.urawaredsmylife.dto.googlefeedapi.Feed;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.RemoveUnderscoreBeanProcessor;

/**
 * 全チーム共通のfeedMasterからフィードリストを取得して、
 * ROMEを使用して各フィードのエントリリストを取得し、
 * feedKeywordMasterのキーワードで抽出・除外してentryテーブルに格納する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class FeedEntrySaver2 extends FeedEntrySaver {
	private static Logger logger = Logger.getLogger(FeedEntrySaver2.class.getName());
	/**
	 * チームID
	 */
	private String teamId;
	/**
	 * チーム名１(例：川崎フロンターレ)
	 */
	private String teamName1;
	/**
	 * チーム名２(例：フロンターレ)
	 */
	private String teamName2;
	/**
	 * チーム名３(例：川崎)
	 */
	private String teamName3;

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			// イメージNGキーワードリスト取得
			String sqlNgImage = "SELECT url_keyword FROM ngImageSite";
			List<Map<String, Object>> ngImageKeywordList = qr.query(sqlNgImage, new MapListHandler());

			// ROMEを使用してRSS/ATOMフィードからエントリリストを取得(全チーム用)
			List<SyndFeedHolder> entryList = collectFeedEntriesForAllTeams();
			String sql = "SELECT * FROM teamMaster ORDER BY team_id";
			List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			// 各チーム毎にキーワードにヒットした内容をDB保存する。
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				String teamName1 = (String)team.get("team_name");
				String teamName2 = (String)team.get("team_name2");
				String teamName3 = (String)team.get("team_name3");
				FeedEntrySaver2 srv = new FeedEntrySaver2(teamId, teamName1, teamName2, teamName3);
				srv.saveEntry(entryList, ngImageKeywordList, qr);
			}
		} catch(Exception ex) {
			logger.error("フィード取得エラー", ex);
		}
	}

	/**
	 * コンストラクタ
	 * @param teamId
	 * @param teamName1
	 * @param teamName2
	 * @param teamName3
	 */
	public FeedEntrySaver2(String teamId, String teamName1, String teamName2, String teamName3) {
		System.out.println("★チーム=" + teamId);
		this.teamId = teamId;
		this.teamName1 = teamName1;
		this.teamName2 = teamName2;
		this.teamName3 = teamName3;
	}

	/**
	 * 全チーム用のfeedMasterからフィードリストを取得して、
	 * ROMEを使用して各フィードのエントリリストを取得して返す。
	 * @param params
	 * @return
	 */
	public static List<SyndFeedHolder> collectFeedEntriesForAllTeams() {
		StopWatch sw = new StopWatch();
		sw.start();
		Logger logger = Logger.getLogger(FeedEntrySaver2.class);
		List<SyndFeedHolder> entryList  = new ArrayList<SyndFeedHolder>();
		try {
			QueryRunner qr = DB.createQueryRunner();
			// 対象フィードリストをマスターから取得
			List<Feed> feedMasterList = getFeedListFromDB(qr);
			for(Feed targetFeed : feedMasterList) {
				String feedUrl = targetFeed.getFeedUrl();
				URL url = new URL(feedUrl);
				logger.info("targetFeed=" + targetFeed.getSiteName() + " : " + url.toString());
				try {
					SyndFeed feed = new SyndFeedInput().build(new XmlReader(url));
					SyndFeedHolder feedHolder = new SyndFeedHolder(
							feed, targetFeed.getFeedId(), targetFeed.getSiteName());	
					logger.info("★件数＝" + feed.getEntries().size() + "  " + targetFeed.getSiteName());
					entryList.add(feedHolder);
				} catch (Exception ex0) {
					saveFailedFeed(feedUrl, targetFeed.getSiteName(), null, qr);
					logger.warn("フィード読み込みエラー(" + feedUrl + ")", ex0);
//					Mail.send("フィード読み込みエラー(" + teamId + " : " + feedUrl + ")\n " +
//							ExceptionUtils.getFullStackTrace(ex0));
				}
			}
		} catch (Exception e) {
			logger.error("フィード読み込みエラー", e);
		} finally {
			sw.stop();
			logger.info((sw.getTime()/1000.0) + " 秒");
		}
		return entryList;
	}

	/**
	 * 全チーム共通フィードマスターからフィードリストを取得して返す。
	 * @param qr
	 * @return
	 * @throws SQLException
	 */
	private static List<Feed> getFeedListFromDB(QueryRunner qr) throws SQLException {
		String sql = "select * from feedMaster";
		Logger logger = Logger.getLogger(FeedEntrySaver2.class);
		logger.info(sql);
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new RemoveUnderscoreBeanProcessor());
		return qr.query(sql, new BeanListHandler<Feed>(Feed.class, rowProcessor));
	}

	/**
	 * 取得したエントリリストをDBに保存する
	 * @param feedResults ROMEから取得した読み込み結果
	 * @param ngImageKeywordList イメージURL保存NGなサイトURLのキーワードリスト
	 * @param qr
	 * @throws SQLException
	 */
	private void saveEntry(List<SyndFeedHolder> feedResults
			, List<Map<String, Object>> ngImageKeywordList, QueryRunner qr) throws SQLException {
		//OK・NGワードリスト
		String ngSql = "SELECT word FROM feedKeywordMaster WHERE (team_id=? OR team_id='all') AND ok_flg=false";
		List<Map<String, Object>> ngWordList = qr.query(ngSql, new MapListHandler(), teamId);
		
		String okSql = "SELECT word FROM feedKeywordMaster WHERE (team_id=? OR team_id='all') AND ok_flg=true";
		List<Map<String, Object>>  okWordList = qr.query(okSql, new MapListHandler(), teamId);
		
		Map<String, Object> teamName1Map = new HashMap<>();
		teamName1Map.put("word", teamName1);
		okWordList.add(teamName1Map);

		Map<String, Object> teamName2Map = new HashMap<>();
		teamName2Map.put("word", teamName2);
		okWordList.add(teamName2Map);

		Map<String, Object> teamName3Map = new HashMap<>();
		teamName3Map.put("word", teamName3);
		okWordList.add(teamName3Map);

		for(SyndFeedHolder feedResultHolder : feedResults) {
			List<SyndEntry> entries = feedResultHolder.syndFeed.getEntries();
			String entryTable = teamId + "Entry";
			for(SyndEntry entry : entries) {
				String entryTitle = StringEscapeUtils.unescapeHtml(entry.getTitle());
				String entryDescription = entry.getDescription() == null? "" : entry.getDescription().getValue();
				// エントリタイトルにNGワードがある
				if(isNgEntry(ngWordList, entryTitle)) {
					continue;
				}
				// OKワード・チーム名チェック
				if(! isOKEntry(okWordList, entryTitle, entryDescription)) {
					continue;
				}
				Date pubDate = entry.getPublishedDate();
				if (pubDate == null || entry.getLink().startsWith("http://www.soccerdigestweb.com/")) {
					pubDate = new Date();
				}
				logger.info("(" + teamName2 + ") "
						+ new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle
						+ " : " + entry.getLink() /*+ " : " + entryContent*/);
				String siteName = feedResultHolder.siteName.replace("（", "").replace("）", "");
				// 既に同一URLが登録済みの場合は登録しない
				if(isEntryExistsInDB(qr, entryTable, entryTitle, entry.getLink())) {
					String insertSql = "INSERT INTO " + entryTable 
							+ " VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
					ImageInfo img = getImageInContent(entry, ngImageKeywordList);	//画像情報取得
					Object[] inseartParams = new Object[] {
							entry.getLink()
							,entryTitle
							,entryDescription
							,img.url
							,img.width
							,img.height
							//チームごとのフィードのIDと全チーム共通フィードのIDが重複しないよう10000を足す。
							,10000 + Integer.parseInt(feedResultHolder.feedId)
							,siteName
							,pubDate
					};
					logger.info("🌟" + insertSql);
					try {
						int count = qr.update(insertSql, inseartParams);
						logger.info("結果：" + count);
					} catch(Exception ex) {
						logger.error("フィード読み込みエラー", ex);
					}
				} else {
					logger.info("DB登録済み");
				}
			}
		}
	}

	/**
	 * エントリタイトルまたはDescription(本文の一部)にOKワードが含まれている場合にtrueを返す。
	 * @param okWordList
	 * @param entryTitle
	 * @param entryDescription
	 * @return
	 */
	private boolean isOKEntry(List<Map<String, Object>> okWordList, String entryTitle, 
			String entryDescription) {
		for (Map<String, Object> okMap : okWordList) {
			String okWord = (String)okMap.get("word");
			if (entryTitle == null || entryDescription == null) {
				continue;
			}
			if (entryTitle.contains(okWord) || entryDescription.contains(okWord)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * エントリタイトルにNGワードが含まれる場合にtrueを返す。
	 * @param ngWordList
	 * @param entryTitle
	 * @return
	 */
	private boolean isNgEntry(List<Map<String, Object>> ngWordList, String entryTitle) {
		for(Map<String, Object> ngMap : ngWordList) {
			if(entryTitle.contains((String)ngMap.get("word"))) {
//						logger.info("NGワード:" + entryTitle);
				return true;
			}
		}
		return false;
	}

	/**
	 * エントリが既にDBに保存されている場合にtrueを返す。
	 * @param qr
	 * @param entryTable
	 * @param entryTitle
	 * @param entryLink
	 * @return
	 * @throws SQLException
	 */
	private boolean isEntryExistsInDB(QueryRunner qr, String entryTable
			, String entryTitle, String entryLink) throws SQLException {
		// タイトルまたはURLが既に登録済みかどうか
		String selectSql = "SELECT COUNT(*) AS CNT FROM " + entryTable
				+ " WHERE entry_url=? OR entry_title=?";
		Map<String, Object> cntMap = qr.query(selectSql, new MapHandler(), entryLink, entryTitle);
		Long cnt = (Long)cntMap.get("CNT");
		return cnt.intValue() == 0;
	}
}

/**
 * SyndFeedとfeedIdをセットで保持するクラス
 * @author motoy3d
 */
class SyndFeedHolder {
	public SyndFeed syndFeed;
	public String feedId;
	public String siteName;
	public SyndFeedHolder(SyndFeed syndFeed, String feedId, String siteName) {
		this.syndFeed = syndFeed;
		this.feedId = feedId;
		this.siteName = siteName;
	}
}
