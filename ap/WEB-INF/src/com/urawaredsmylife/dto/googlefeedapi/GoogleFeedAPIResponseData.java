package com.urawaredsmylife.dto.googlefeedapi;

/**
 * Google Feed APIで取得したresponseDataオブジェクト
 * GoogleFeedAPIResponse
 *   - GoogleFeedAPIResponseData
 *     - Feed
 *       - FeedEntry
 *  https://developers.google.com/feed/v1/jsondevguide
 * @author motoy3d
 *
 */
public class GoogleFeedAPIResponseData {
	/**
	 * FeedResult
	 */
	private Feed feed;

	/**
	 * @return the feed
	 */
	public Feed getFeed() {
		return feed;
	}

	/**
	 * @param feed the feed to set
	 */
	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	
}