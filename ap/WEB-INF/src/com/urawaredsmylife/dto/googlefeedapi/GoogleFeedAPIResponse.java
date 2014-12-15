package com.urawaredsmylife.dto.googlefeedapi;

/**
 * Google Feed APIで取得したトップレベルオブジェクト
 * GoogleFeedAPIResponse
 *   - GoogleFeedAPIResponseData
 *     - Feed
 *       - FeedEntry
 *  https://developers.google.com/feed/v1/jsondevguide
 * @author motoy3d
 *
 */
public class GoogleFeedAPIResponse {
	/**
	 * responseData
	 */
	private GoogleFeedAPIResponseData responseData;
	/**
	 * ステータス
	 */
	private String responseStatus;
	/**
	 * @return the responseData
	 */
	public GoogleFeedAPIResponseData getResponseData() {
		return responseData;
	}
	/**
	 * @param responseData the responseData to set
	 */
	public void setResponseData(GoogleFeedAPIResponseData responseData) {
		this.responseData = responseData;
	}
	/**
	 * @return the responseStatus
	 */
	public String getResponseStatus() {
		return responseStatus;
	}
	/**
	 * @param responseStatus the responseStatus to set
	 */
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	
	
}