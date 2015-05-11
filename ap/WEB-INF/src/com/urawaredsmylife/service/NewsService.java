package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;

/**
 * DBに格納されているフィードエントリデータから検索し、JSONを返す。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 *
 */
public class NewsService {
	private Logger logger = Logger.getLogger(NewsService.class.getName());
	private static final int DEFAULT_COUNT = 30;
	private static final int MAX_COUNT = 100;
	
	/**
	 * DBに格納されているフィードエントリデータから検索し、JSONを返す。
	 * パラメータにmaxが存在する場合、maxより古いデータを返す。
	 * minが存在する場合、minより新しいデータを返す。
	 * 
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			String teamId = StringUtils.defaultIfEmpty((String)params.get("teamId"), "reds");
			QueryRunner qr = DB.createQueryRunner();
			String count1 = (String)params.get("count");
			int count = DEFAULT_COUNT;
			if(count1 != null && NumberUtils.isDigits(count1)) {
				count = Integer.parseInt(count1);
				if(MAX_COUNT < count) {
					count = MAX_COUNT;
				}
			}
			String maxDatetime = (String)params.get("max");		//この日時より古いデータを取得
			String minDatetime = (String)params.get("min");	//この日時より新しいを取得
//			logger.info("max_id=" + maxId + ", min_id=" + minId);
			// DBからツイートを取得
			String table = teamId + "Entry";
			String avoidTable = teamId + "AvoidFeed";
			String sql = "SELECT "
					+ " entry_url"
					+ ", entry_title"
					+ ", content"
					+ ", site_name"
					+ ", DATE_FORMAT(published_date, '%Y/%m/%d %T') published_date"
					+ ", UNIX_TIMESTAMP(published_date) as published_date_num"
					+ ", IFNULL(image_url, '') image_url"
					+ ", image_width"
					+ ", image_height"
					+ " FROM " + table
					+ " WHERE feed_id NOT IN (SELECT feed_id FROM " + avoidTable + ")";
			if(maxDatetime != null) {
				sql += " AND published_date < FROM_UNIXTIME(" + maxDatetime + ")";	//maxより古いデータを読み込む
			} else if(minDatetime != null) {
				sql += " AND published_date > FROM_UNIXTIME(" + minDatetime + ")";	//minより新しいデータを読み込む
			}
			sql += " ORDER BY published_date DESC LIMIT " + count;
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
		}
	}
}
