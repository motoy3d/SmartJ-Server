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
import org.apache.commons.lang.time.DateUtils;
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
 * YouTubeからキーワード検索で試合動画を検索し、DBに保存する。
 * 本処理はバッチで定期的に実行する。
 * @author motoy3d
 */
public class YouTubeSaver {
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;

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
    	Logger logger = Logger.getLogger(YouTubeSaver.class.getName());
        try {
            QueryRunner qr = DB.createQueryRunner();
			String sql = "SELECT team_id, team_name FROM teamMaster ORDER BY team_id";
            //TODO 
//			String sql = "SELECT team_id, team_name FROM teamMaster WHERE team_id='gamba' ORDER BY team_id";

            List<Map<String, Object>> teamList = qr.query(sql, new MapListHandler());
			for(Map<String, Object> team : teamList) {
				String teamId = (String)team.get("team_id");
				String teamName = (String)team.get("team_name");
				YouTubeSaver srv = new YouTubeSaver(teamId, teamName);
				srv.collectVideos();
			}
        } catch (Exception ex) {
            logger.error("YouTubeSaver error.", ex);
            System.exit(-1);
        }
	}

	/**
	 * 試合関連動画を検索し、DBに保存する。
	 */
	private void collectVideos() {
        try {
            QueryRunner qr = DB.createQueryRunner();
            String season = new SimpleDateFormat("yyyy").format(new Date());
			String gamesSql = "SELECT game_date1, compe, vs_team FROM " + teamId + "Results"
					+ " WHERE season= " + season + " AND result IS NOT NULL AND result != '' ORDER BY game_date1";
			List<Map<String, Object>> gameList = qr.query(gamesSql, new MapListHandler());
			for(Map<String, Object> game : gameList) {
				String vsTeamName = (String)game.get("vs_team");
				String compe = (String)game.get("compe");
				if (compe.contains("YNC")) {
					compe = compe.replace("YNC", "ナビスコカップ");
				}
				Date gameDate = (Date)game.get("game_date1");
				searchYouTube(teamName, vsTeamName, gameDate, compe);
				
			}
        } catch(Exception ex) {
        	logger.error("", ex);
        }
	}
	
	/**
	 * YouTubeで検索してDB保存する
	 * @param teamName
	 * @param vsTeamName
	 * @param gameDate
	 * @param compe
	 */
	private void searchYouTube(String teamName, String vsTeamName, Date gameDate, String compe) {
		logger.info("🌟" + teamName + " vs " + vsTeamName + " (" + gameDate + ") " + compe + " -----------------------------------------");
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
	        String queryTerm2 = "スカパー ハイライト " + teamName + " " + vsTeamName + " " + compe;
	
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
	
	        // To increase efficiency, only retrieve the fields that the
	        // application uses.
	        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/medium/url)");
	        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	
	        saveDb(search, teamName, vsTeamName, gameDate);
	        
	        search.setQ(queryTerm2);
	        saveDb(search, teamName, vsTeamName, gameDate);

		} catch (GoogleJsonResponseException e) {
	        System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	                + e.getDetails().getMessage());
	    } catch (IOException e) {
	        System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
	    } catch (Throwable t) {
	        t.printStackTrace();
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
	private void saveDb(
			YouTube.Search.List search, String teamName, String vsTeamName, Date gameDate)
					throws IOException, SQLException {
		// Call the API and print results.
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResultList = searchResponse.getItems();
		if (searchResultList == null) {
			logger.info("");
			return;
		}
        QueryRunner qr = DB.createQueryRunner();
	    Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
	    while (iteratorSearchResults.hasNext()) {
	        SearchResult singleVideo = iteratorSearchResults.next();
	        ResourceId rId = singleVideo.getId();
	        // Confirm that the result represents a video. Otherwise, the
	        // item will not contain a video ID.
	        if (rId.getKind().equals("youtube#video")) {
	            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getMedium();
	            String title = singleVideo.getSnippet().getTitle();
//	            DateTime publishedAt = singleVideo.getSnippet().getPublishedAt();
	            // スカパーハイライトの場合は、タイトルに両チーム名がない場合は除外
	            if (title.contains("【ハイライト】")) {
	            	if (!title.contains(teamName) || !title.contains(vsTeamName)){
//		            	System.out.println(".....スカパーハイライトの場合は、タイトルに両チーム名がない場合は除外 " + title);
		            	continue;
	            	}
	            } else {
	            	//どちらのチーム名も入っていない場合は除外
	            	if (!title.contains(teamName) && !title.contains(vsTeamName)){
		            	continue;
	            	}
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
				// DB保存
				String insertSql = "INSERT IGNORE INTO " + teamId + "Video VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
				qr.update(insertSql, videoId, title, gameDate, thumbnail.getUrl(), 
						viewCount, likeCount, dislikeCount, new Date());
	        }
	    }
	}
}
