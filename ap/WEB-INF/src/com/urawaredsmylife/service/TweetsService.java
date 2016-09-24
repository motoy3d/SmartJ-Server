package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;

/**
 * DBに格納されているTwitterデータから検索し、JSONを返す。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 *
 */
public abstract class TweetsService {
	private Logger logger = Logger.getLogger(TweetsService.class.getName());
	private static final int DEFAULT_COUNT = 50;
	/**
	 * 検索対象テーブル名を返す (searchTweets or playerTweets)
	 * @return
	 */
	public abstract String getTarget();
	
	/**
	 * DBに格納されているTwitterデータから検索し、JSONを返す。
	 * パラメータにmax_idが存在する場合、max_idより古いデータを返す。
	 * since_idが存在する場合、since_idより新しいデータを返す。
	 * 
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			String teamId = StringUtils.defaultIfEmpty((String)params.get("teamId"), "reds");
			QueryRunner qr = DB.createQueryRunner();
			String count1 = (String)params.get("count");
			int count = DEFAULT_COUNT;
			if(count1 != null && NumberUtils.isDigits(count1)) {
				count = Integer.parseInt(count1);
				if(200 <= count) {
					count = DEFAULT_COUNT;
				}
			}
			String maxId = (String)params.get("max_id");
			String sinceId = (String)params.get("since_id");
			logger.info("max_id=" + maxId + ", since_id=" + sinceId);
			
			String table = teamId + getTarget();
			// DBからツイートを取得
			String sql = "SELECT "
					+ " tweet_id"
					+ ", user_name"
					+ ", user_screen_name"
					+ ", user_profile_image_url"
					+ ", tweet"
					+ ", retweeted_count"
					+ ", DATE_FORMAT(created_at, '%Y/%m/%d %T') created_at"
					+ " FROM " + table;
			if(maxId != null) {
				sql += " WHERE tweet_id < " + maxId;	//maxIdより古いツイートを読み込む
			} else if(sinceId != null) {
				sql += " WHERE tweet_id > " + sinceId;	//sinceIdより新しいツイートを読み込む
			}
			sql += " ORDER BY created_at DESC LIMIT " + count;
			logger.info(sql);
			List<Map<String, Object>> resultList = qr.query(sql, new MapListHandler());
			if(resultList.isEmpty()) {
				logger.info("no data");
				return new Object[] {new NoDataResult()};
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("ツイート読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		} finally {
			sw.stop();
			logger.info("ツイート取得時間= " + (sw.getTime()/1000.0) + "秒");
		}
	}
}
