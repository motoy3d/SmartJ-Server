package com.urawaredsmylife;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.MediaModule;
import com.rometools.modules.mediarss.types.MediaGroup;
import com.rometools.modules.mediarss.types.Thumbnail;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.urawaredsmylife.dto.Feed;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.RemoveUnderscoreBeanProcessor;

/**
 * 各チームのxxxFeedMasterからフィードリストを取得して、
 * google feed apiを使用して各フィードのエントリリストを取得し、
 * xxxEntryテーブルに格納する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class FeedEntrySaver {
	private Logger logger = Logger.getLogger(FeedEntrySaver.class.getName());
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
			if (0 < args.length) {	//コマンド引数でチームID指定
				for(String teamId : args) {
					FeedEntrySaver srv = new FeedEntrySaver(teamId);
					srv.collectFeedEntries();
				}
			} else {	//チームマスター分実行
				QueryRunner qr = DB.createQueryRunner();
				String sql = "SELECT team_id FROM teamMaster ORDER BY team_id";
				List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
				for(Map<String, Object> team : teamList) {
					String teamId = (String)team.get("team_id");
					FeedEntrySaver srv = new FeedEntrySaver(teamId);
					srv.collectFeedEntries();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * コンストラクタ
	 * @param teamId
	 */
	public FeedEntrySaver() {
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
			// イメージNGキーワードリスト取得
			String sql = "SELECT url_keyword FROM ngImageSite";
			List<Map<String, Object>> ngImageKeywordList = qr.query(sql, new MapListHandler());

			// 対象フィードリストをマスターから取得
			List<Feed> feedList = getFeedListFromDB(qr);
			for(Feed targetFeed : feedList) {
				String feedUrl = targetFeed.getFeedUrl();
				URL url = new URL(feedUrl);
				logger.info("targetFeed=" + targetFeed.getSiteName() + " : " + url.toString());
				try {
					SyndFeed feed = new SyndFeedInput().build(new XmlReader(url));
					//logger.info("★feed＝" + feedResult.getTitle() + "  " + feedResult.getFeedUrl());
					saveEntry(targetFeed, feed, ngImageKeywordList, qr);
				} catch (Exception ex0) {
					saveFailedFeed(feedUrl, targetFeed.getSiteName(), teamId, qr);
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
		//logger.info(sql);
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new RemoveUnderscoreBeanProcessor());
		return qr.query(sql, new BeanListHandler<Feed>(Feed.class, rowProcessor));
	}

	/**
	 * 取得したエントリリストをDBに保存する
	 * @param targetFeed マスターから取得した読み込み対象フィード
	 * @param feedResult Google Feed APIから取得した読み込み結果
	 * @param ngImageKeywordList イメージURL保存NGなサイトURLのキーワードリスト
	 * @param qr
	 * @throws SQLException
	 */
	private void saveEntry(Feed targetFeed, SyndFeed feed
			, List<Map<String, Object>> ngImageKeywordList, QueryRunner qr) throws SQLException {
		List<SyndEntry> entries = feed.getEntries();		//フィードのエントリリスト
		String entryTable = teamId + "Entry";
		for(SyndEntry entry : entries) {
			String entryTitle = StringEscapeUtils.unescapeHtml(entry.getTitle());
			// エントリタイトルからサイト名を抽出
			String siteName = targetFeed.getSiteName();
			String ogImage = null;
			if(StringUtils.isBlank(siteName)) {
				String[] siteNameAndImage = extractSiteNameAndImage(entry.getLink());
				siteName = siteNameAndImage[0];
				ogImage = siteNameAndImage[1];
			}
			Date pubDate = entry.getPublishedDate();
			if (pubDate == null || entry.getLink().startsWith("http://www.soccerdigestweb.com/")) {
				pubDate = new Date();
			}
			//logger.info("■" + new SimpleDateFormat("yyyy/MM/dd").format(pubDate) + "  " + entryTitle + "  -  " + siteName);

			// NGワードが含まれていたら保存しない
			if(isContainsNgWord(qr, entryTitle)) {
				continue;
			}
			siteName = siteName.replace("（", "").replace("）", "");
			// 既に同一URLが登録済みの場合は登録しない
			String selectSql = "SELECT COUNT(*) AS CNT FROM " + entryTable
					+ " WHERE entry_url=? OR entry_title=?";
			Map<String, Object> cntMap = qr.query(selectSql, new MapHandler(), entry.getLink(), entryTitle);
			Long cnt = (Long)cntMap.get("CNT");
			if(cnt.intValue() == 0) {
				String description = entry.getDescription().getValue();
				String insertSql = "INSERT INTO " + entryTable + " VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
				ImageInfo img = getImageInContent(ogImage, entry, ngImageKeywordList);
				Object[] inseartParams = new Object[] {
						entry.getLink()
						,entryTitle
						,description
						,img.url
						,img.width
						,img.height
						,targetFeed.getFeedId()
						,siteName
						,pubDate
				};
				//logger.info(insertSql);
				try {
					int count = qr.update(insertSql, inseartParams);
					logger.info("結果：" + count);
				} catch(Exception ex) {
					logger.error("エントリ登録エラー", ex);
				}
			}
		}
	}

	/**
	 * エントリタイトルにNGワードが含まれていたらtrueを返す。
	 * @param qr
	 * @param entryTitle
	 * @return
	 * @throws SQLException
	 */
	private boolean isContainsNgWord(QueryRunner qr, String entryTitle) throws SQLException {
		String ngSql = "SELECT word FROM feedKeywordMaster"
				+ " WHERE (team_id=? OR team_id='all') AND ok_flg=false";
		List<Map<String, Object>> ngWordList = qr.query(ngSql, new MapListHandler(), teamId);
		boolean isNg = false;
		for(Map<String, Object> ngMap : ngWordList) {
			if(entryTitle.contains((String)ngMap.get("word"))) {
				logger.info("NGワード: [" + ngMap.get("word") + "] " + entryTitle);
				isNg = true;
				break;
			}
		}
		return isNg;
	}
	/**
	 * フィード取得に失敗した情報をDB保存する(failedFeedテーブル)
	 * @param feedUrl
	 * @param feedName
	 * @param teamId
	 * @param qr
	 * @throws SQLException
	 */
	protected static void saveFailedFeed(String feedUrl, String feedName,
			String teamId, QueryRunner qr) throws SQLException {
		String sql = "UPDATE failedFeed SET count=count+1, up_date=now()"
				+ " WHERE feed_url=?";
		int count = qr.update(sql, feedUrl);
		if (count == 0) {
			sql = "INSERT INTO failedFeed VALUES(?, ?, ?, 1, now())";
			qr.update(sql, feedUrl, teamId, feedName);
		}
	}

	/**
	 * エントリのURLにアクセスし、
	 * og:site_nameまたはtitleタグからサイト名を抽出する
	 * og:imageからイメージURLを抽出する。
	 * ２つの値を配列で返す。
	 * @param entryUrl
	 * @return
	 */
	protected String[] extractSiteNameAndImage(String entryUrl) {
		logger.info("extractSiteName>>>>>>>>>>" + entryUrl);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		GetMethodWebRequest req = new GetMethodWebRequest(entryUrl);
		try {
			WebResponse res = wc.getResponse(req);
			// og:site_name (Open Graph Protocol)からサイト名を取得
			String[] ogSiteName = res.getMetaTagContent("property", "og:site_name");
			String[] ogImages = res.getMetaTagContent("property", "og:image");
			if(!ArrayUtils.isEmpty(ogSiteName) && !"スポーツナビ＋".equals(ogSiteName[0])) {
//				System.out.println("結果0=" + ogSiteName[0]);
				String ogImage = ogImages[0];
				if (ogImage.contains("https://f.image.geki.jp/data/image/news/2560/")) {
					ogImage = ogImage.replace("https://f.image.geki.jp/data/image/news/2560/", "https://f.image.geki.jp/data/image/news/800/");
				}
				return new String[]{ogSiteName[0], ogImage};
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
					return new String[] {split[1], null};
				}
			}
			// デリミタ「 - 」
			if(StringUtils.contains(title, " - ")) {
				String[] split = StringUtils.split(title, " - ");
				if(1 < split.length) {
//					System.out.println("結果2＝" + split[1]);
					return new String[] {split[1], null};
				}
			}
		} catch (Exception e) {
			logger.warn("サイト名抽出エラー", e);
		}
		return new String[] {"", null};
	}

	/**
	 * エントリコンテンツ内のイメージ情報を返す。
	 * @param sourceUrl
	 * @param content
	 * @param ngImageKeywordList イメージURL保存NGなサイトURLのキーワードリスト
	 * @return
	 */
	protected ImageInfo getImageInContent(String ogImage, SyndEntry entry
			//String sourceUrl, String content
			, List<Map<String, Object>> ngImageKeywordList) {
		String sourceUrl = entry.getLink();
		ImageInfo img = new ImageInfo();

        String imgUrl = ogImage;
        if (imgUrl != null && imgUrl.contains("www.soccerdigestweb.com")) {
        	imgUrl = imgUrl.replaceAll("ext_03_0", "ext_04_0");
        }

        if (imgUrl == null) {
    		List<SyndContent> contents = entry.getContents();
    		String content = "";
    		for (SyndContent con : contents) {
    			content += con.getValue();
    		}
            int imgTagIdx = content.indexOf("<img");
            logger.info("getImageContent.  sourceUrl=" + sourceUrl + "★ imgTagIdx=" + imgTagIdx);
            if(imgTagIdx != -1) {
    	        int srcIdx = content.indexOf("src=", imgTagIdx);
    	        if(srcIdx != -1) {
    	        	logger.debug("◯srcIdx=" + srcIdx);
    	            int urlStartIdx = srcIdx + 5;
    	            int urlEndIdx = content.indexOf('"', urlStartIdx);
    	            imgUrl = content.substring(urlStartIdx, urlEndIdx);
    	            imgUrl = imgUrl.replaceAll("&amp;", "&");
    	        }
            }
        }
        if (imgUrl == null) {
        	// <media:thumbnail... から画像取得
        	final MediaEntryModule module = (MediaEntryModule) entry.getModule(MediaModule.URI);
//            Thumbnail[] thumbnails = module.getMetadata().getThumbnail();		//NullPointerが発生した
//            if (0 < thumbnails.length) {
//            	imgUrl = thumbnails[0].getUrl().toString();
//            	logger.info("🌟サムネイル=" + imgUrl);
//            } else {
        	// YouTube RSSでは<media:group...内に<media:thumbnail..がある
        	if (module != null) {
            	MediaGroup[] mediaGroups = module.getMediaGroups();
            	for (MediaGroup mg : mediaGroups) {
            		if (mg.getMetadata() == null) {continue;}
            		Thumbnail[] thumbnails = mg.getMetadata().getThumbnail();
            		if (0 < thumbnails.length) {
	                	imgUrl = thumbnails[0].getUrl().toString();
	                	logger.info("🌟サムネイル=" + imgUrl);
	                	break;
            		}
            	}
            }

        	// <enclosure...から画像取得
        	List<SyndEnclosure> enclosures = entry.getEnclosures();	//画像等の関連ファイル
        	for (SyndEnclosure enc : enclosures) {
        		if (enc.getType().startsWith("image")) {
        			imgUrl = enc.getUrl();
        			break;
        		}
        	}
        }

        if (imgUrl != null) {
            // イメージ保存NGチェック
            boolean isNgImage = false;
            for (Map<String, Object> ngWord : ngImageKeywordList) {
            	if (imgUrl.contains((String)ngWord.get("url_keyword"))) {
            		logger.info("isNgImage. " + imgUrl + " ★ keyword=" + ngWord.get("url_keyword"));
            		isNgImage = true;
            		break;
            	}
            }
            if(isNgImage) {
                imgUrl = "";
            } else {
            	// 画像を読み込んでサイズ(縦横)をimgにセットする。
        		setImageSize(sourceUrl, imgUrl, img);
            }
        }
		return img;
	}

	/**
	 * 画像を読み込んでサイズ(縦横)をimgにセットする。
	 * @param sourceUrl
	 * @param imgUrl
	 * @param img
	 */
	private void setImageSize(String sourceUrl, String imgUrl, ImageInfo img) {
		try {
			if (imgUrl.startsWith("/")) {
				int idx1 = sourceUrl.indexOf("//");
				imgUrl = sourceUrl.substring(0, sourceUrl.indexOf("/", idx1+2)) + imgUrl;
				logger.debug("🌟" + imgUrl);
			}
			URL url = new URL(imgUrl);
			BufferedImage bimg = ImageIO.read(url);
			if (bimg != null) {
				img.url = imgUrl;
				img.width = bimg.getWidth();
				img.height = bimg.getHeight();
			}
		} catch (IOException e) {
			logger.warn("イメージ読み込み失敗", e);
		}
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
