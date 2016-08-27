package com.urawaredsmylife;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.googlefeedapi.Feed;
import com.urawaredsmylife.dto.googlefeedapi.FeedEntry;
import com.urawaredsmylife.dto.googlefeedapi.GoogleFeedAPIResponse;
import com.urawaredsmylife.dto.googlefeedapi.GoogleFeedAPIResponseData;
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
	 * デフォルトのフィード取得件数
	 */
	private static final int DEFAULT_FEED_COUNT = 30;
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
			String sql = "SELECT * FROM teamMaster ORDER BY team_id";
			List<Feed> entryList = collectFeedEntriesForAllTeams();
			List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				String teamName1 = (String)team.get("team_name");
				String teamName2 = (String)team.get("team_name2");
				String teamName3 = (String)team.get("team_name3");
				FeedEntrySaver2 srv = new FeedEntrySaver2(teamId, teamName1, teamName2, teamName3);
				srv.saveEntry(entryList, qr);
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
	public static List<Feed> collectFeedEntriesForAllTeams() {
		StopWatch sw = new StopWatch();
		sw.start();
		Logger logger = Logger.getLogger(FeedEntrySaver2.class);
		List<Feed> entryList  = new ArrayList<Feed>();
		try {
			QueryRunner qr = DB.createQueryRunner();
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			// 対象フィードリストをマスターから取得
			List<Feed> feedMasterList = getFeedListFromDB(qr);
			for(Feed targetFeed : feedMasterList) {
				String feedUrl = targetFeed.getFeedUrl();
				feedUrl = URLEncoder.encode(feedUrl, "UTF-8");
				URL url = new URL(String.format(URL_BASE, feedUrl, String.valueOf(DEFAULT_FEED_COUNT), ipAddress));
				logger.info("targetFeed=" + targetFeed.getSiteName() + " : " + url.toString());
				URLConnection connection = url.openConnection();
				connection.addRequestProperty("Referer", "http://motoy3d.blogspot.jp");
				
//				String line;
//				StringBuilder builder = new StringBuilder();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//				while((line = reader.readLine()) != null) {
//					builder.append(line);
//				}
//				logger.info("-----" + builder.toString());
				
				GoogleFeedAPIResponse jsonResult = JSON.decode(
						connection.getInputStream(), GoogleFeedAPIResponse.class);				
				GoogleFeedAPIResponseData responseData = jsonResult.getResponseData();
				if(responseData == null) {
					logger.error("レスポンスnull.");
					continue;
				}
				Feed feedResult = responseData.getFeed();
				feedResult.setFeedId(targetFeed.getFeedId());
				logger.info("★件数＝" + responseData.getFeed().getEntries().length);
				if (StringUtils.isBlank(feedResult.getSiteName())) {
					feedResult.setSiteName(targetFeed.getSiteName());
				}
//				logger.info("★feedResult＝" + feedResult.getSiteName() + ".  " +  feedResult.getTitle() + ".  " 
//						+ feedResult.getFeedUrl() + " count=" + responseData.getFeed().getEntries().length);
				entryList.add(feedResult);
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
	 * @param qr
	 * @throws SQLException
	 */
	private void saveEntry(/*Feed targetFeed,*/ List<Feed> feedResults, QueryRunner qr) throws SQLException {
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

		for(Feed feedResult : feedResults) {
			FeedEntry[] entries = feedResult.getEntries();
			String entryTable = teamId + "Entry";
			for(FeedEntry e : entries) {
				String entryTitle = StringEscapeUtils.unescapeHtml(e.getTitle());
				String entryContent = e.getContent();
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
				// エントリタイトルからサイト名を抽出
				String siteName = feedResult.getSiteName();
				if(e.getLink().startsWith("http://web.gekisaka.jp")) {
					siteName = "ゲキサカ";
				}
				else if(e.getLink().startsWith("http://www.nikkansports.com")) {
					siteName = "日刊スポーツ";
				}
				else if(e.getLink().startsWith("http://www.soccerdigestweb.com/")) {
					siteName = "サッカーダイジェストWeb";
				}
				if(StringUtils.isBlank(siteName)) {
					siteName = extractSiteName(e.getLink());
				}
				if(ArrayUtils.contains(NG_SITES, siteName)) {
					logger.info("NGサイト:" + siteName);
					continue;
				}
				System.out.println("(" + teamName2 + ") " 
						+ new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle
						+ " : " + e.getLink() /*+ " : " + entryContent*/);
				siteName = siteName.replace("（", "").replace("）", "");
				// 既に同一URLが登録済みの場合は登録しない
				String selectSql = "SELECT COUNT(*) AS CNT FROM " + entryTable 
						+ " WHERE entry_url=? OR entry_title=?";
				Map<String, Object> cntMap = qr.query(selectSql, new MapHandler(), e.getLink(), entryTitle);
				Long cnt = (Long)cntMap.get("CNT");
				if(cnt.intValue() == 0) {
					String insertSql = "INSERT INTO " + entryTable + " VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
					ImageInfo img = getImageInContent(e.getLink(), e.getContent());
					Object[] inseartParams = new Object[] {
							e.getLink()
							,entryTitle
							,entryContent
							,img.url
							,img.width
							,img.height
							,10000 + Integer.parseInt(feedResult.getFeedId())	//チームごとのフィードのIDと全チーム共通フィードのIDが重複しないよう10000を足す。
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
