package com.urawaredsmylife;

import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * Twitterの検索で「urawareds」を検索し、結果をデータベースに格納する。
 *
 * @author Motoi Kataoka
 */
public class SearchTweetsSaver {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(SearchTweetsSaver.class.getName());
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			QueryRunner qr = DB.createQueryRunner();
			// リストIDをチームマスターから取得
			String teamMasterSql = "SELECT team_id, search_tweets_keyword FROM teamMaster ORDER BY team_id";
			List<Map<String, Object>> teamList = qr.query(teamMasterSql, new MapListHandler());
			for (Map<String, Object> team : teamList) {
				String teamId = (String) team.get("team_id");
				String searchTweetsKeyword = (String) team.get("search_tweets_keyword");

				Query query = new Query(searchTweetsKeyword + " -ヤフオク");
				query.setCount(100);
				query.setLang("ja");
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				logger.info("取得件数：" + tweets.size());

				String table = teamId + "SearchTweets";
				String insertSql = "INSERT IGNORE INTO " + table + " VALUES(?, ?, ?, ?, ?, ?, ?)";
				Object[][] insertDataList = new Object[tweets.size()][];
				int idx = 0;
				for (Status tweet : tweets) {
					// if(tweet.getText().indexOf(searchTweetsKeyword) == -1) {
					// continue;
					// }
					Object[] insertData = new Object[7];
					User user = tweet.getUser();
					String profileImageUrl = user.getProfileImageURL().replace("_normal.", "_bigger.");
					insertData[0] = tweet.getId();
					insertData[1] = user.getName();
					insertData[2] = "@" + user.getScreenName();
					insertData[3] = profileImageUrl;
					insertData[4] = tweet.getText();
					insertData[5] = tweet.getRetweetCount();
					insertData[6] = tweet.getCreatedAt();
					insertDataList[idx++] = insertData;

					// logger.info(
					// tweet.getId() + " - "
					// + user.getName() + " - "
					// + "@" + user.getScreenName() + " - "
					// + user.getProfileImageURL() + " - "
					// + tweet.getText() + " - "
					// + tweet.getCreatedAt()
					// );
//					logger.info("★" + user.getName() + " / " + tweet.getText());
				}
				int[] resultCount = qr.batch(insertSql, insertDataList);
				logger.info(teamId + " 登録件数：" + ToStringBuilder.reflectionToString(resultCount));
			}
		} catch (Exception ex) {
			Mail.send(ex);
			ex.printStackTrace();
			System.out.println("Failed to search tweets: " + ex.getMessage());
			System.exit(-1);
		}
	}
}
