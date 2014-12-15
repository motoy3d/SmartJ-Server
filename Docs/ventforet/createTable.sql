/**
 * 検索結果ツイート
 */
create table ventforetSearchTweets(
	tweet_id long /*ツイートID*/
	,user_name varchar(256) /*ユーザ名*/
	,user_screen_name varchar(256) /* ユーザ表示名*/
	,user_profile_image_url text /* ユーザプロフィール画像URL*/
	,tweet text /* ツイート本文*/
	,retweeted_count int /* リツイート数*/
	,created_at datetime /* 作成日時*/
	,primary key(tweet_id(100))
);
/**
 * 選手・関係者ツイート
 */
create table ventforetPlayerTweets(
	tweet_id long /*ツイートID*/
	,user_name varchar(256) /*ユーザ名*/
	,user_screen_name varchar(256) /* ユーザ表示名*/
	,user_profile_image_url text /* ユーザプロフィール画像URL*/
	,tweet text /* ツイート本文*/
	,retweeted_count int /* リツイート数*/
	,created_at datetime /* 作成日時*/
	,primary key(tweet_id(100))
);
/**
 * フィードマスター
 */
create table ventforetFeedMaster(
 feed_id	 int not null auto_increment primary key
 ,feed_url varchar(200)
 ,feed_name varchar(200)
 ,site_name varchar(200)
 ,up_date timestamp
);
/**
 * フィードエントリ
 */
create table ventforetEntry(
 entry_id int auto_increment primary key
 ,entry_url varchar(500)
 ,entry_title varchar(1000)
 ,content text
 ,image_url varchar(500)
 ,feed_id int
 ,site_name varchar(200)
 ,published_date datetime
 ,up_date timestamp
);

/**
 * ニュース一覧に表示しないフィードエントリ
 */
create table ventforetAvoidFeed(
 feed_id int primary key
);

/**
 * 日程・結果
 */
create table ventforetResults(
 season year
 ,compe varchar(20)
 ,game_date1 date
 ,game_date2 varchar(10)
 ,kickoff_time varchar(5)
 ,stadium varchar(50)
 ,home_flg boolean
 ,vs_team varchar(50)
 ,tv varchar(100)
 ,result char(1)
 ,score varchar(20)
 ,detail_url varchar(200)
 ,up_date timestamp
 ,primary key(season, compe)
);