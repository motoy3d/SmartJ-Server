package com.urawaredsmylife.dto.googlefeedapi;

import java.util.Date;


/**
 * Google Feed APIで取得したフィードアイテムオブジェクト
 * GoogleFeedAPIResponse
 *   - GoogleFeedAPIResponseData
 *     - Feed
 *       - FeedEntry
 *  https://developers.google.com/feed/v1/jsondevguide
 * @author motoy3d
 *
 */
public class FeedEntry {
	/**
	 * エントリID
	 */
	private String entryId;
	/**
	 * リンク
	 */
	private String link;
	/**
	 * タイトル
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 公開日時
	 */
	private Date publishedDate;
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the publishedDate
	 */
//	public String getPublishedDate() {
	public Date getPublishedDate() {
		return publishedDate;
	}
	/**
	 * @param publishedDate the publishedDate to set
	 */
	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}
	/**
	 * @return the entryId
	 */
	public String getEntryId() {
		return entryId;
	}
	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	
	
}