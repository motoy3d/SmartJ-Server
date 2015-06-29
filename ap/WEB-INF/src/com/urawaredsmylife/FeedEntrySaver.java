package com.urawaredsmylife;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.arnx.jsonic.JSON;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.urawaredsmylife.dto.googlefeedapi.Feed;
import com.urawaredsmylife.dto.googlefeedapi.FeedEntry;
import com.urawaredsmylife.dto.googlefeedapi.GoogleFeedAPIResponse;
import com.urawaredsmylife.dto.googlefeedapi.GoogleFeedAPIResponseData;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.RemoveUnderscoreBeanProcessor;

/**
 * feed_masterからフィードリストを取得して、
 * google feed apiを使用して各フィードのエントリリストを取得し、
 * entryテーブルに格納する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 *
 */
public class FeedEntrySaver {
	private Logger logger = Logger.getLogger(FeedEntrySaver.class.getName());
	/**
	 * Google Feed API のURLベース
	 */
	private static final String URL_BASE = "https://ajax.googleapis.com/ajax/services/feed/load?" 
			+ "v=1.0&q=%s&num=%s&userip=%s";
	/**
	 * デフォルトのフィード取得件数
	 */
	private static final int DEFAULT_FEED_COUNT = 10;
	/**
	 * Yahoo Pipesのフィード取得件数
	 */
	private static final int PIPES_FEED_COUNT = 50;
	/**
	 * NGサイト（保存しない）
	 */
	private static final String[] NG_SITES = new String[] {
		"（ゲキサカ）", "（SOCCER"
	};
	/**
	 * NGワード（エントリタイトルに含まれていたら保存しない）
	 */
	private static final String[] NG_WORDS = new String[] {
		"レディース", "なでしこ", "PR:", ": PR", "ラーメン", "拉麺", "ヴァンラーレ"
	};
	/**
	 * チームID
	 */
	private String teamId;

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String sql = "SELECT team_id FROM teamMaster ORDER BY team_id";
			List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				FeedEntrySaver srv = new FeedEntrySaver(teamId);
				srv.collectFeedEntries();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public FeedEntrySaver(String teamId) {
		this.teamId = teamId;
	}
	
	/**
	 * feed_masterからフィードリストを取得して、
	 * google feed apiを使用して各フィードのエントリリストを取得し、
	 * 各チームごとのentryテーブルに格納する。
	 * 本処理はバッチで定期的に実行する。
	 * 
	 * @param params
	 * @return
	 */
	public void collectFeedEntries() {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			QueryRunner qr = DB.createQueryRunner();
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			// 対象フィードリストをマスターから取得
			List<Feed> feedList = getFeedListFromDB(qr);
			for(Feed targetFeed : feedList) {
				String feedUrl = targetFeed.getFeedUrl();
				int feedCount = DEFAULT_FEED_COUNT;
				if (feedUrl.startsWith("http://pipes.yahoo.com")) {
					feedCount = PIPES_FEED_COUNT;
				}
				feedUrl = URLEncoder.encode(feedUrl, "UTF-8");
				URL url = new URL(String.format(URL_BASE, feedUrl, String.valueOf(feedCount), ipAddress));
				logger.info("targetFeed=" + targetFeed.getTitle() + " : " + url.toString());
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
				logger.info("★結果＝" + ToStringBuilder.reflectionToString(jsonResult));
				
				GoogleFeedAPIResponseData responseData = jsonResult.getResponseData();
				if(responseData == null) {
					logger.error("レスポンスnull.");
					continue;
				}
				Feed feedResult = responseData.getFeed();
				logger.info("★feed＝" + feedResult.getTitle() + "  " + feedResult.getFeedUrl());
				saveEntry(targetFeed, feedResult, qr);
			}
		} catch (Exception e) {
			logger.error("フィード読み込みエラー", e);
		} finally {
			sw.stop();
			logger.info((sw.getTime()/1000.0) + " 秒");
		}
	}
	
	/**
	 * フィードマスターからフィードリストを取得して返す。
	 * @param qr
	 * @return
	 * @throws SQLException
	 */
	private List<Feed> getFeedListFromDB(QueryRunner qr) throws SQLException {
		String table = teamId + "FeedMaster";
		String sql = "select * from " + table;
		logger.info(sql);
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new RemoveUnderscoreBeanProcessor());
		return qr.query(sql, new BeanListHandler<Feed>(Feed.class, rowProcessor));
	}
	
	/**
	 * 取得したエントリリストをDBに保存する
	 * @param targetFeed マスターから取得した読み込み対象フィード
	 * @param feedResult Google Feed APIから取得した読み込み結果
	 * @param qr
	 * @throws SQLException
	 */
	private void saveEntry(Feed targetFeed, Feed feedResult, QueryRunner qr) throws SQLException {
		FeedEntry[] entries = feedResult.getEntries();
		String entryTable = teamId + "Entry";
		for(FeedEntry e : entries) {
			String entryTitle = StringEscapeUtils.unescapeHtml(e.getTitle());
			// エントリタイトルからサイト名を抽出
			String siteName = targetFeed.getSiteName();
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
			Date pubDate = e.getPublishedDate();
			if (pubDate == null || e.getLink().startsWith("http://www.soccerdigestweb.com/")) {
				pubDate = new Date();
			}
			logger.info("■" + new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle + "  -  " + siteName);
			if(ArrayUtils.contains(NG_SITES, siteName)) {
				logger.info("NGサイト:" + siteName);
				continue;
			}
			boolean isNg = false;
			for(String ng : NG_WORDS) {
				if(entryTitle.contains(ng)) {
					logger.info("NGワード:" + entryTitle);
					isNg = true; break;
				}
			}
			if(isNg) {
				continue;
			}
			siteName = siteName.replace("（", "").replace("）", "");
			// 既に同一URLが登録済みの場合は登録しない
			String selectSql = "SELECT COUNT(*) AS CNT FROM " + entryTable 
					+ " WHERE entry_url=? OR entry_title=?";
			Map<String, Object> cntMap = qr.query(selectSql, new MapHandler(), e.getLink(), entryTitle);
			Long cnt = (Long)cntMap.get("CNT");
			if(cnt == 0) {
				String insertSql = "INSERT INTO " + entryTable + " VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
				ImageInfo img = getImageInContent(e.getLink(), e.getContent());
				Object[] inseartParams = new Object[] {
						e.getLink()
						,entryTitle
						,e.getContent()
						,img.url
						,img.width
						,img.height
						,targetFeed.getFeedId()
						,siteName
						,pubDate
				};
				logger.info(insertSql);
				try {
					int count = qr.update(insertSql, inseartParams);
					logger.info("結果：" + count);			
				} catch(Exception ex) {
					logger.error("フィード読み込みエラー", ex);
				}
			}
		}
	}

	/**
	 * エントリのURLにアクセスし、
	 * og:site_nameまたはtitleタグからサイト名を抽出する
	 * @param entryUrl
	 * @return
	 */
	private String extractSiteName(String entryUrl) {
		logger.info("extractSiteName>>>>>>>>>>" + entryUrl);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(entryUrl);
		try {
			WebResponse res = wc.getResponse(req);
			// og:site_name (Open Graph Protocol)からサイト名を取得
			String[] ogSiteName = res.getMetaTagContent("property", "og:site_name");
			if(!ArrayUtils.isEmpty(ogSiteName) && !"スポーツナビ＋".equals(ogSiteName[0])) {
//				System.out.println("結果0=" + ogSiteName[0]);
				return ogSiteName[0];
			}
			// titleタグからサイト名を抽出
			// デリミタ「 | 」
			String title = res.getTitle();
			System.out.println("title=" + title);
			if(StringUtils.contains(title, " | ")) {
				String[] split = StringUtils.split(title, " ¥| ");
//				System.out.println("★" + ToStringBuilder.reflectionToString(split, ToStringStyle.MULTI_LINE_STYLE));
				if(1 < split.length) {
//					System.out.println("結果1＝" + split[1]);
					return split[1];
				}
			}
			// デリミタ「 - 」
			if(StringUtils.contains(title, " - ")) {
				String[] split = StringUtils.split(title, " - ");
				if(1 < split.length) {
//					System.out.println("結果2＝" + split[1]);
					return split[1];
				}
			}
//			// h1 ※ウェブリブログ用
//			HTMLElement[] h1 = res.getElementsByTagName("h1");
//			if(h1.length != 0) {
//				String siteName = h1[0].getNode().getChildNodes().item(0).getTextContent();
//				System.out.println("結果３＝" + h1[0].getNode().getChildNodes());
//			}
		} catch (Exception e) {
			logger.warn("サイト名抽出エラー", e);
		}
		return "";
	}
	
	/**
	 * コンテンツ内のイメージ情報を返す。
	 * @param sourceUrl
	 * @param content
	 * @return
	 */
	private ImageInfo getImageInContent(String sourceUrl, String content) {
		ImageInfo img = new ImageInfo();
        int imgTagIdx = content.indexOf("<img");
        if(imgTagIdx != -1) {
	        int srcIdx = content.indexOf("src=", imgTagIdx);
	        if(srcIdx != -1) {
	            int urlStartIdx = srcIdx + 5;
	            int urlEndIdx = content.indexOf('"', urlStartIdx);
	            String imgUrl = content.substring(urlStartIdx, urlEndIdx);
	            imgUrl = imgUrl.replaceAll("&amp;", "&");
	            if(imgUrl.endsWith(".gif") ||
	                    imgUrl.indexOf("http://hbb.afl.rakuten.co.jp") == 0 ||
	                    imgUrl.indexOf("http://uragi.com/bfb320100.jpg") == 0 ||
	                    imgUrl.indexOf("http://counter2.blog.livedoor.com") == 0 ||
	                    imgUrl.indexOf("fbcdn") != -1 || //facebook(直接表示できない)
	                    imgUrl.indexOf("http://measure.kuchikomi.ameba.jp") == 0 || //ameba
	                    imgUrl.indexOf("rssad") != -1 || //rssad(直接表示できない)
                		imgUrl.endsWith("money_yen.png") ||  //浦和フットボール通信
                		imgUrl.endsWith("/btn_share_now.png") || //なう
                		imgUrl.endsWith("/btn_share_mixi.png")  //mixi
                ) {
	                imgUrl = "";
	            } else {
	        		try {
	        			if (imgUrl.startsWith("/")) {
	        				int idx1 = sourceUrl.indexOf("//");
							imgUrl = sourceUrl.substring(0, sourceUrl.indexOf("/", idx1+2)) + imgUrl;
	        				logger.debug("🌟" + imgUrl);
	        			}
	        			URL u = new URL(imgUrl);
	        			BufferedImage bimg = ImageIO.read(u);
	        			if (bimg != null) {
		        			img.url = imgUrl;
			        		img.width = bimg.getWidth();
			        		img.height = bimg.getHeight();
	        			}
	        		} catch (IOException e) {
	        			logger.warn("image loading exception", e);
	        		}
	            }
	        }
        }
		return img;
	}
	
	/**
	 * コンテンツの中のイメージ情報
	 * @author motoy3d
	 */
	class ImageInfo {
		/**
		 * イメージURL
		 */
		public String url;
		/**
		 * width
		 */
		public int width;
		/**
		 * height
		 */
		public int height;
	}
}
