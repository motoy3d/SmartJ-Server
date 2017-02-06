package com.urawaredsmylife;

import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.urawaredsmylife.util.DB;
import com.urawaredsmylife.util.Mail;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * レッズ選手・関係者のツイートをデータベースに格納する。
 *
 * @author Motoi Kataoka
 */
public class PlayerTweetsSaver {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(PlayerTweetsSaver.class.getName());
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			String teamIdCond = "";
			for (int i=0; i<args.length; i++) {
				if (!"".equals(teamIdCond)) {
					teamIdCond += ",";
				}
				teamIdCond += DB.quote(args[i]);
			}
			if (!"".equals(teamIdCond)) {
				teamIdCond = " WHERE team_id IN (" + teamIdCond + ")";
			}
			QueryRunner qr = DB.createQueryRunner();
			// リストIDをチームマスターから取得
			String teamMasterSql = "SELECT team_id, player_tweets_list_id FROM teamMaster " 
					+ teamIdCond + " ORDER BY team_id";
			List<Map<String, Object>> teamList = qr.query(teamMasterSql, new MapListHandler());
			for (Map<String, Object> team : teamList) {
				String teamId = (String) team.get("team_id");
				Long playerTweetsListId = (Long) team.get("player_tweets_list_id");
				if (playerTweetsListId == null) {
					continue;
				}
				logger.info(teamId + " : " + playerTweetsListId);

				Paging page = new Paging(1, 100);
				ResponseList<Status> tweets = twitter.getUserListStatuses(playerTweetsListId, page);
				logger.info("取得件数：" + tweets.size());

				String table = teamId + "PlayerTweets";
				String insertSql = "INSERT IGNORE INTO " + table + " VALUES(?, ?, ?, ?, ?, ?, ?)";
				Object[][] insertDataList = new Object[tweets.size()][];
				int idx = 0;
				for (Status tweet : tweets) {
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
