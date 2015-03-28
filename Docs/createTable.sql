/**
 * システム設定
 */
create table configs(
 season year primary key
 ,j1FirstStageOpenDate date /*J1 1st開幕日*/
 ,j1SecondStageOpenDate date /*J1 2nd開幕日*/
 ,up_date timestamp
);

/**
 * チームマスター
 */
create table teamMaster(
	team_id varchar(20) /*チームID*/
	,team_name varchar(50) /*チーム名*/
	,category varchar(10)/*リーグカテゴリ(J1,J2,J3)*/
	,aclFlg boolean /*ACL出場フラグ*/
	,search_tweets_keyword varchar(200) /* Twitter検索キーワード*/
	,player_tweets_list_id int /*Twitter選手リストID*/
	,adType int /*広告タイプ(1:アイコン、2:バナー)*/
	,up_date timestamp /* 作成日時*/
	,primary key(team_id)
);
/**
 *  Jリーグ順位表
 */
create table standings(
  season year
  ,league varchar(4)
  ,stage varchar(5) comment '1st/2nd/total'
  ,seq int
  ,rank int
  ,team_id varchar(20)
  ,team_name varchar(50)
  ,point int
  ,games int
  ,win int
  ,draw int
  ,lose int
  ,got_goal int
  ,lost_goal int
  ,diff int
  ,up_date timestamp
	,primary key(season, league, stage, seq)
);

/**
 *  ナビスコカップ　グループリーグ順位表
 */
create table nabiscoStandings(
  season year
  ,group_name varchar(2)
  ,seq int
  ,rank int
  ,team_name varchar(50)
  ,point int
  ,games int
  ,win int
  ,draw int
  ,lose int
  ,got_goal int
  ,lost_goal int
  ,diff int
  ,up_date timestamp
	,primary key(season, group_name, seq)
);

/**
 *  ACL　グループリーグ順位表
 */
create table aclStandings(
  season year
  ,group_name varchar(2)
  ,seq int
  ,rank int
  ,team_name varchar(50)
  ,point int
  ,games int
  ,win int
  ,draw int
  ,lose int
  ,got_goal int
  ,lost_goal int
  ,diff int
  ,up_date timestamp
	,primary key(season, group_name, seq)
);

/**
 * アプリ起動時メッセージ
 */
create table message(
 team_id varchar(20) /*チームID*/
 ,message_id int
 ,message varchar(200)
 ,os varchar(10)
 ,start_date date
 ,end_date date
 ,min_ver varchar(8)
 ,max_ver varchar(8)
 ,up_date timestamp
);
insert into message values('albirex', 1, '最新版にアップデートお願いします！', 'iphone', '2014-08-04', '2014-12-31', '0.9.1', '9.9.9', now());
