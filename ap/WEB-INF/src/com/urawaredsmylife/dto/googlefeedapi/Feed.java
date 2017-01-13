package com.urawaredsmylife.dto.googlefeedapi;

/**
 * Google Feed APIで取得したフィードオブジェクト
 * GoogleFeedAPIResponse
 *   - GoogleFeedAPIResponseData
 *     - Feed
 *       - FeedEntry
 *  https://developers.google.com/feed/v1/jsondevguide
 * @author motoy3d
 *
 */
public class Feed {
	/**
	 * フィードID
	 */
	private String feedId;
	/**
	 * フィードURL
	 */
	private String feedUrl;
	/**
	 * フィードタイトル(フィード取得結果用)
	 */
	private String title;
	/**
	 * サイト名(マスター用)
	 */
	private String siteName;

	/**
	 * @return the feedId
	 */
	public String getFeedId() {
		return feedId;
	}
	/**
	 * @param feedId the feedId to set
	 */
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	/**
	 * @return the feedUrl
	 */
	public String getFeedUrl() {
		return feedUrl;
	}
	/**
	 * @param feedUrl the feedUrl to set
	 */
	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}
	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

}