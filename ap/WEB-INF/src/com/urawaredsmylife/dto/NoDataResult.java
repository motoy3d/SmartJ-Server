package com.urawaredsmylife.dto;

/**
 * 結果０件用オブジェクト
 * @author motoy3d
 *
 */
public class NoDataResult {
	private String json = "no data";
	/**
	 * @return the result
	 */
	public String getJson() {
		return json;
	}
	/**
	 * @param result the result to set
	 */
	public void setJson(String json) {
		this.json = json;
	}
}