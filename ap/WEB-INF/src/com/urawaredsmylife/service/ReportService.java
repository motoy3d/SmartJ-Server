package com.urawaredsmylife.service;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.urawaredsmylife.dto.NoDataResult;
import com.urawaredsmylife.util.DB;

/**
 * ユーザーからの報告受付サービス
 * @author motoy3d
 */
public class ReportService {
	private Logger logger = Logger.getLogger(ReportService.class.getName());
	
	/**
	 * ユーザーからの報告受付
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String userId = (String)params.get("uid");
			String url = (String)params.get("url");
			String type = (String)params.get("type");
			if (StringUtils.isBlank(userId) || StringUtils.isBlank(url) || StringUtils.isBlank(type)) {
				return "Invalid parameter";
			}
			logger.info("ユーザーからの報告.  userId=" + userId + ", url=" + url + ", type=" + type);
			return "ok";
		} catch (SQLException e) {
			logger.error("メッセージ読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
