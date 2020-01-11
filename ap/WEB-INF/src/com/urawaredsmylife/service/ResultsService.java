package com.urawaredsmylife.service;

import java.text.SimpleDateFormat;
import java.util.Date;
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
 * DBに格納されている日程・結果から検索し、JSONを返す。
 * チームごとにテーブルは別。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 *
 */
public class ResultsService {
	private Logger logger = Logger.getLogger(ResultsService.class.getName());

	/**
	 * DBに格納されている日程・結果から検索し、JSONを返す。
	 * チームごとにテーブルは別。
	 * @param params
	 * @return
	 */
	public Object find(Map<String, Object> params) {
		try {
			QueryRunner qr = DB.createQueryRunner();
			String season = (String)params.get("season");
			season = new SimpleDateFormat("yyyy").format(new Date());	//パラメータに関わらず今年をセット
			String month = new SimpleDateFormat("MM").format(new Date());
//			logger.info(">>>>month=" + month);
			if (month.equals("01")) {
				season = String.valueOf(NumberUtils.toInt(season) - 1);
			}
			String teamId = StringUtils.defaultIfEmpty((String)params.get("teamId"), "reds");
			if (StringUtils.isNotBlank((String)params.get("otherTeamId"))) {
				teamId = (String)params.get("otherTeamId");
			}
			String table = teamId + "Results";
			String sql = "SELECT * FROM " + table + " WHERE season=" + season + " ORDER BY game_date1";
			logger.info(sql);
			List<Map<String, Object>> resultList = qr.query(sql, new MapListHandler());
			return resultList;
		} catch (Exception e) {
			logger.error("日程・結果読み込みエラー", e);
			return new Object[] {new NoDataResult()};
		}
	}
}
