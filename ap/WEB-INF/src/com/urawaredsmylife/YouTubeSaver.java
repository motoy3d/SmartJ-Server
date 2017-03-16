package com.urawaredsmylife;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.youtube.Auth;

/**
 * YouTubeから試合日程に沿ってキーワード検索で試合動画を検索し、DBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class YouTubeSaver {
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
	private Logger logger = Logger.getLogger(YouTubeSaver.class.getName());
	private String teamId;
	private String teamName;
	
	/**
	 * コンストラクタ
	 * @param teamId
	 * @param teamName
	 */
	public YouTubeSaver(String teamId, String teamName) {
		this.teamId = teamId;
		this.teamName = teamName;
	}
	
	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		StopWatch sw = new StopWatch();
		sw.start();
    	Logger logger = Logger.getLogger(YouTubeSaver.class.getName());
        try {
            QueryRunner qr = DB.createQueryRunner();
            // チームマスタから全チーム取得
			String sql = "SELECT team_id, team_name FROM teamMaster ORDER BY team_id";
            List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				String teamName = (String)team.get("team_name");
				// 各チームごとにYoutube APIで試合日程に沿って動画検索
				YouTubeSaver srv = new YouTubeSaver(teamId, teamName);
				srv.collectVideos();
			}
        } catch (Exception ex) {
            logger.error("YouTubeSaver error.", ex);
            System.exit(-1);
        } finally {
        	sw.stop();
        	logger.info("処理時間=" + sw.getTime()/1000.0 + "秒");
        }
	}

	/**
	 * 試合日程に沿って試合関連動画を検索し、DBに保存する。
	 */
	private void collectVideos() {
        try {
            QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
            // 試合日程取得
			String gamesSql = String.format(
					"SELECT game_date1, compe, vs_team, score FROM %sResults"
					+ " WHERE season=%s AND result IS NOT NULL AND result != '' ORDER BY game_date1"
					, teamId, season);
			logger.info(gamesSql);
			List<Map<String, Object>> gameList = qr.query(gamesSql, new MapListHandler());
			for(Map<String, Object> game : gameList) {
				String score = (String)game.get("score");
				if (StringUtils.isBlank(score)) {
					continue;
				}
				String vsTeamName = (String)game.get("vs_team");
				String compe = (String)game.get("compe");
				if (compe.contains("YNC") || compe.contains("ルヴァン")) {
					compe = compe.replace("YNC", "ルヴァンカップ");
				}
				Date gameDate = (Date)game.get("game_date1");
				// Youtube APIで検索
				searchYouTube(teamName, vsTeamName, gameDate, compe, score);
			}
        } catch(Exception ex) {
        	logger.error("", ex);
        }
	}
	
	/**
	 * YouTube APIで検索してDB保存する
	 * @param teamName
	 * @param vsTeamName
	 * @param gameDate
	 * @param compe
	 * @param score
	 */
	private void searchYouTube(String teamName, String vsTeamName, Date gameDate, 
			String compe, String score) {
		logger.info("🌟" + teamName + " vs " + vsTeamName + " (" + gameDate + ") " + compe 
				+ "  " + score + " -----------------------------------------");
		try {
	        // This object is used to make YouTube Data API requests. The last
	        // argument is required, but since we don't need anything
	        // initialized when the HttpRequest is initialized, we override
	        // the interface and provide a no-op function.
	        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
	            public void initialize(HttpRequest request) throws IOException {
	            }
	        }).setApplicationName("youtube-cmdline-search").build();
	
	        // Prompt the user to enter a query term.
	        String gameDate1 = new SimpleDateFormat("yyyy/MM/dd").format(gameDate);
	        String queryTerm1 = teamName + " " + vsTeamName + " " + gameDate1;
	        String queryTerm2 = "【公式】 " + teamName + " " + vsTeamName + " " + compe;
	        logger.info("queryTerm1=" + queryTerm1 + ",  queryTerm2=" + queryTerm2);
	        // Define the API request for retrieving search results.
	        YouTube.Search.List search = youtube.search().list("id,snippet");
	
	        // Set your developer key from the {{ Google Cloud Console }} for
	        // non-authenticated requests. See:
	        // {{ https://cloud.google.com/console }}
	        String apiKey = ResourceBundle.getBundle("youtube").getString("youtube.apikey");
	        search.setKey(apiKey);
	        search.setQ(queryTerm1);
	        // 試合日〜１か月後にアップされた動画を検索対象とする
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String gameDateS = dateFormat.format(gameDate) + "T00:00:00Z";
			Date oneMonthLater = DateUtils.addMonths(gameDate, 1);
			String oneMonthLaterS = dateFormat.format(oneMonthLater) + "T00:00:00Z";
			search.setPublishedAfter(DateTime.parseRfc3339(gameDateS));
			search.setPublishedBefore(DateTime.parseRfc3339(oneMonthLaterS));

	        // Restrict the search results to only include videos. See:
	        // https://developers.google.com/youtube/v3/docs/search/list#type
	        search.setType("video");
	
	        // To increase efficiency, only retrieve the fields that the application uses.
	        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/high/url)");
	        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	
			QueryRunner qr = DB.createQueryRunner();
		    // DBから一旦動画情報を削除
			String deleteAllSql = "DELETE FROM " + teamId + "Video WHERE game_date=?";
			logger.info("DBから一旦削除：" + deleteAllSql + " -- " + gameDate);
			qr.update(deleteAllSql, gameDate);
			// 第1キーワードで検索
	        saveDb(search, teamName, vsTeamName, gameDate);
			// 第2キーワードで検索	        
	        search.setQ(queryTerm2);
	        saveDb(search, teamName, vsTeamName, gameDate);
		} catch (GoogleJsonResponseException e) {
	        logger.error("There was a service error: " + e.getDetails().getCode() + " : "
	                + e.getDetails().getMessage());
	    } catch (IOException e) {
	        logger.error("There was an IO error: " + e);
	    } catch (Throwable t) {
	    	logger.error("", t);
	    }
	}

	/**
	 * 動画データをDB保存する
	 * @param search
	 * @param teamName
	 * @param vsTeamName
	 * @param gameDate
	 * @throws IOException
	 * @throws SQLException
	 */
	private void saveDb(YouTube.Search.List search, String teamName, String vsTeamName, Date gameDate)
					throws IOException, SQLException {
		// Call the API and print results.
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResultList = searchResponse.getItems();
		if (searchResultList == null || searchResultList.isEmpty()) {
			logger.info("検索結果0件");
			return;
		}
		QueryRunner qr = DB.createQueryRunner();

		// 検索結果ループ
		Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
	    while (iteratorSearchResults.hasNext()) {
	        SearchResult singleVideo = iteratorSearchResults.next();
	        ResourceId rId = singleVideo.getId();
	        // Confirm that the result represents a video. Otherwise, the
	        // item will not contain a video ID.
	        if (rId.getKind().equals("youtube#video")) {
	        	// Highクオリティのサムネイル
	            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();
	            String title = singleVideo.getSnippet().getTitle();
//	            DateTime publishedAt = singleVideo.getSnippet().getPublishedAt();
            	//どちらのチーム名も入っていない場合は除外
            	if (!title.contains(teamName) && !title.contains(vsTeamName)){
	            	continue;
            	}
				String videoId = singleVideo.getId().getVideoId();
				// 再生回数取得
		        YouTube.Videos.List videos = youtube.videos().list("id,statistics");			    	
		        String apiKey = ResourceBundle.getBundle("youtube").getString("youtube.apikey");
		        videos.setKey(apiKey);
		        videos.setId(videoId);
		        // データ取得実行
		        VideoListResponse videoListRes = videos.execute();
		        List<Video> videoList = videoListRes.getItems();
		        BigInteger viewCount = null;
		        BigInteger likeCount = null;
		        BigInteger dislikeCount = null;
		        for(Video v : videoList) {
		        	viewCount = v.getStatistics().getViewCount();
		        	likeCount = v.getStatistics().getLikeCount();
		        	dislikeCount = v.getStatistics().getDislikeCount();
		        }
				logger.info("    " /*+ publishedAt + "  "*/ + title + "  " + thumbnail.getUrl() 
						+ "   viewCount:" + viewCount + "  " + videoId);
				// DB登録
				String insertSql = "INSERT IGNORE INTO " + teamId + "Video VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
				qr.update(insertSql, videoId, title, gameDate, thumbnail.getUrl(), 
						viewCount, likeCount, dislikeCount, new Date());
	        }
	    }
	}
}
