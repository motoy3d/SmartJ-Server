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
 * google feed apiを使用して各フィードのエントリリストを取得し、
 * feedKeywordMasterのキーワードで抽出・除外してentryテーブルに格納する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class FeedEntrySaver2 extends FeedEntrySaver {
	private Logger logger = Logger.getLogger(FeedEntrySaver2.class.getName());
	/**
	 * チームID
	 */
	private String teamId;
	/**
	 * チーム名１
	 */
	private String teamName1;
	/**
	 * チーム名２
	 */
	private String teamName2;
	/**
	 * チーム名３
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

			String sql = "SELECT * FROM teamMaster ORDER BY team_id";
			List<SyndFeedHolder> entryList = collectFeedEntriesForAllTeams();
			List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				String teamName1 = (String)team.get("team_name");
				String teamName2 = (String)team.get("team_name2");
				String teamName3 = (String)team.get("team_name3");
				FeedEntrySaver2 srv = new FeedEntrySaver2(teamId, teamName1, teamName2, teamName3);
				srv.saveEntry(entryList, ngImageKeywordList, qr);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
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
	 * google feed apiを使用して各フィードのエントリリストを取得して返す。
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

				SyndFeed feed = new SyndFeedInput().build(new XmlReader(url));
				SyndFeedHolder feedHolder = new SyndFeedHolder(feed, targetFeed.getFeedId(), targetFeed.getSiteName());

				logger.info("★件数＝" + feed.getEntries().size() + "  " + targetFeed.getSiteName());
				entryList.add(feedHolder);
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
//
//	/**
//	 * 取得したエントリリストをDBに保存する
////	 * @param targetFeed マスターから取得した読み込み対象フィード
//	 * @param feedResult Google Feed APIから取得した読み込み結果
//	 * @param qr
//	 * @throws SQLException
//	 */
//	public void saveEntries(/*Feed targetFeed,*/ List<Feed> entryList, QueryRunner qr) throws SQLException {
//		//TODO
//		for(Feed entry : entryList) {
//			saveEntry(entry, qr);
//		}
//	}

	/**
	 * 取得したエントリリストをDBに保存する
//	 * @param targetFeed マスターから取得した読み込み対象フィード
	 * @param feedResults Google Feed APIから取得した読み込み結果
	 * @param ngImageKeywordList イメージURL保存NGなサイトURLのキーワードリスト
	 * @param qr
	 * @throws SQLException
	 */
	private void saveEntry(/*Feed targetFeed,*/ List<SyndFeedHolder> feedResults
			, List<Map<String, Object>> ngImageKeywordList, QueryRunner qr) throws SQLException {
		//OK・NGワードリスト
		String ngSql = "SELECT word FROM feedKeywordMaster WHERE team_id=? OR team_id='all' AND ok_flg=false";
		List<Map<String, Object>> ngWordList = qr.query(ngSql, new MapListHandler(), teamId);
		String okSql = "SELECT word FROM feedKeywordMaster WHERE team_id=? OR team_id='all' AND ok_flg=true";
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
			for(SyndEntry e : entries) {
				String entryTitle = StringEscapeUtils.unescapeHtml(e.getTitle());
				String entryContent = e.getDescription() == null? "" : e.getDescription().getValue();
				boolean isNg = false;
				// NGワードチェック
				for(Map<String, Object> ngMap : ngWordList) {
					if(entryTitle.contains((String)ngMap.get("word"))) {
//						logger.info("NGワード:" + entryTitle);
						isNg = true; break;
					}
				}
				if(isNg) {
					continue;
				}
				// OKワード・チーム名チェック
				boolean isOk = false;
				for (Map<String, Object> okMap : okWordList) {
					String ok = (String)okMap.get("word");
					if (entryTitle == null || entryContent == null) {
						continue;
					}
					if (entryTitle.contains(ok) || entryContent.contains(ok)) {
						isOk = true;
						break;
					}
				}
				if(!isOk) {
					continue;
				}
				Date pubDate = e.getPublishedDate();
				if (pubDate == null || e.getLink().startsWith("http://www.soccerdigestweb.com/")) {
					pubDate = new Date();
				}
//				System.out.println((isNg? "🔴" : "🔵") + "(" + teamName2 + ") "
//						+ new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle/*+ " : " + entryContent*/);

				System.out.println("(" + teamName2 + ") "
						+ new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle
						+ " : " + e.getLink() /*+ " : " + entryContent*/);
				String siteName = feedResultHolder.siteName;
				siteName = siteName.replace("（", "").replace("）", "");
				// 既に同一URLが登録済みの場合は登録しない
				String selectSql = "SELECT COUNT(*) AS CNT FROM " + entryTable
						+ " WHERE entry_url=? OR entry_title=?";
				Map<String, Object> cntMap = qr.query(selectSql, new MapHandler(), e.getLink(), entryTitle);
				Long cnt = (Long)cntMap.get("CNT");
				if(cnt.intValue() == 0) {
					String insertSql = "INSERT INTO " + entryTable + " VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
					ImageInfo img = getImageInContent(e.getLink(), entryContent, ngImageKeywordList);
					Object[] inseartParams = new Object[] {
							e.getLink()
							,entryTitle
							,entryContent
							,img.url
							,img.width
							,img.height
							,10000 + Integer.parseInt(feedResultHolder.feedId)	//チームごとのフィードのIDと全チーム共通フィードのIDが重複しないよう10000を足す。
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
}


/**
 * SyndFeedとfeedIdをセットで保持するクラス
 * @author nob
 *
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
