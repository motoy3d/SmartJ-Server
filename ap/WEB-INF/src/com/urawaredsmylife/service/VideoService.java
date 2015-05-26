package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;

/**
 * DBに格納されている試合関連動画から検索し、JSONを返す。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 */
public class VideoService {
	private Logger logger = Logger.getLogger(VideoService.class.getName());
	
	/**
	 * DBに格納されている順位表から検索し、JSONを返す。
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String teamId = (String)params.get("teamId");
			String gameDate = (String)params.get("gameDate");
			String otherTeamId = (String)params.get("otherTeamId");
			if (StringUtils.isNotBlank(otherTeamId)) {
				teamId = otherTeamId;
			}
//			String youtubeUrlBase = "https://www.youtube.com/watch?v=";
	        String youtubeUrlBase1 = "http://www.youtube.com/embed/";
	        String youtubeUrlBase2 = "?fs=1&autoplay=1";

			String sql = "SELECT CONCAT('" + youtubeUrlBase1 + "', video_id, '" + youtubeUrlBase2 + "') video_url"
					+ ",video_title, thumbnail_url, view_count, like_count, dislike_count FROM " + teamId + "Video"
					+ " WHERE game_date=" + DB.quote(gameDate)
					+ " ORDER BY view_count DESC";
			logger.info(sql);
			List<Map<String, Object>> videoList = qr.query(sql, new MapListHandler());
			return videoList;
		} catch (SQLException e) {
			logger.error("試合関連動画読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
