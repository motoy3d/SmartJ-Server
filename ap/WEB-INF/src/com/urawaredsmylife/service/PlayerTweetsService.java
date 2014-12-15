package com.urawaredsmylife.service;

/**
 * DBに格納されているTwitterデータから検索し、JSONを返す。
 * JSONへの変換はJSONICが行う。
 * @author motoy3d
 *
 */
public class PlayerTweetsService extends TweetsService {

	@Override
	public String getTarget() {
		return "PlayerTweets";
	}
}