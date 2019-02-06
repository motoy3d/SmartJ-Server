-- MySQL dump 10.13  Distrib 5.7.24, for Linux (x86_64)
--
-- Host: localhost    Database: redsmylife
-- ------------------------------------------------------
-- Server version	5.7.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aclStandings`
--

DROP TABLE IF EXISTS `aclStandings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aclStandings` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `group_name` varchar(2) NOT NULL DEFAULT '',
  `seq` int(11) NOT NULL DEFAULT '0',
  `rank` int(11) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `games` int(11) DEFAULT NULL,
  `win` int(11) DEFAULT NULL,
  `draw` int(11) DEFAULT NULL,
  `lose` int(11) DEFAULT NULL,
  `got_goal` int(11) DEFAULT NULL,
  `lost_goal` int(11) DEFAULT NULL,
  `diff` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`group_name`,`seq`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexAvoidFeed`
--

DROP TABLE IF EXISTS `albirexAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexEntry`
--

DROP TABLE IF EXISTS `albirexEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=17221 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexFeedMaster`
--

DROP TABLE IF EXISTS `albirexFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexPlayerTweets`
--

DROP TABLE IF EXISTS `albirexPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexResults`
--

DROP TABLE IF EXISTS `albirexResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexSearchTweets`
--

DROP TABLE IF EXISTS `albirexSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `albirexVideo`
--

DROP TABLE IF EXISTS `albirexVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `albirexVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersAvoidFeed`
--

DROP TABLE IF EXISTS `antlersAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersEntry`
--

DROP TABLE IF EXISTS `antlersEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=30350 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersFeedMaster`
--

DROP TABLE IF EXISTS `antlersFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersPlayerTweets`
--

DROP TABLE IF EXISTS `antlersPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersResults`
--

DROP TABLE IF EXISTS `antlersResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(50) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersSearchTweets`
--

DROP TABLE IF EXISTS `antlersSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `antlersVideo`
--

DROP TABLE IF EXISTS `antlersVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `antlersVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaAvoidFeed`
--

DROP TABLE IF EXISTS `ardijaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaEntry`
--

DROP TABLE IF EXISTS `ardijaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=12131 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaFeedMaster`
--

DROP TABLE IF EXISTS `ardijaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaPlayerTweets`
--

DROP TABLE IF EXISTS `ardijaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaResults`
--

DROP TABLE IF EXISTS `ardijaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaSearchTweets`
--

DROP TABLE IF EXISTS `ardijaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ardijaVideo`
--

DROP TABLE IF EXISTS `ardijaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ardijaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaAvoidFeed`
--

DROP TABLE IF EXISTS `avispaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaEntry`
--

DROP TABLE IF EXISTS `avispaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5627 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaFeedMaster`
--

DROP TABLE IF EXISTS `avispaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaPlayerTweets`
--

DROP TABLE IF EXISTS `avispaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaResults`
--

DROP TABLE IF EXISTS `avispaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaSearchTweets`
--

DROP TABLE IF EXISTS `avispaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avispaVideo`
--

DROP TABLE IF EXISTS `avispaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avispaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareAvoidFeed`
--

DROP TABLE IF EXISTS `bellmareAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareEntry`
--

DROP TABLE IF EXISTS `bellmareEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10054 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareFeedMaster`
--

DROP TABLE IF EXISTS `bellmareFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmarePlayerTweets`
--

DROP TABLE IF EXISTS `bellmarePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmarePlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareResults`
--

DROP TABLE IF EXISTS `bellmareResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareSearchTweets`
--

DROP TABLE IF EXISTS `bellmareSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bellmareVideo`
--

DROP TABLE IF EXISTS `bellmareVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bellmareVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoAvoidFeed`
--

DROP TABLE IF EXISTS `cerezoAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoEntry`
--

DROP TABLE IF EXISTS `cerezoEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13290 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoFeedMaster`
--

DROP TABLE IF EXISTS `cerezoFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoPlayerTweets`
--

DROP TABLE IF EXISTS `cerezoPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoResults`
--

DROP TABLE IF EXISTS `cerezoResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) DEFAULT NULL,
  `game_date1` date NOT NULL DEFAULT '0000-00-00',
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`game_date1`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoSearchTweets`
--

DROP TABLE IF EXISTS `cerezoSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cerezoVideo`
--

DROP TABLE IF EXISTS `cerezoVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cerezoVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configs`
--

DROP TABLE IF EXISTS `configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configs` (
  `season` year(4) NOT NULL,
  `j1FirstStageOpenDate` date DEFAULT NULL,
  `j1SecondStageOpenDate` date DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleAvoidFeed`
--

DROP TABLE IF EXISTS `consadoleAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleEntry`
--

DROP TABLE IF EXISTS `consadoleEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4952 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleFeedMaster`
--

DROP TABLE IF EXISTS `consadoleFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadolePlayerTweets`
--

DROP TABLE IF EXISTS `consadolePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadolePlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleResults`
--

DROP TABLE IF EXISTS `consadoleResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleSearchTweets`
--

DROP TABLE IF EXISTS `consadoleSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consadoleVideo`
--

DROP TABLE IF EXISTS `consadoleVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consadoleVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcAvoidFeed`
--

DROP TABLE IF EXISTS `ehimefcAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcEntry`
--

DROP TABLE IF EXISTS `ehimefcEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1726 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcFeedMaster`
--

DROP TABLE IF EXISTS `ehimefcFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcPlayerTweets`
--

DROP TABLE IF EXISTS `ehimefcPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcResults`
--

DROP TABLE IF EXISTS `ehimefcResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcSearchTweets`
--

DROP TABLE IF EXISTS `ehimefcSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ehimefcVideo`
--

DROP TABLE IF EXISTS `ehimefcVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ehimefcVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entry`
--

DROP TABLE IF EXISTS `entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4710 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoAvoidFeed`
--

DROP TABLE IF EXISTS `fagianoAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoEntry`
--

DROP TABLE IF EXISTS `fagianoEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2610 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoFeedMaster`
--

DROP TABLE IF EXISTS `fagianoFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoPlayerTweets`
--

DROP TABLE IF EXISTS `fagianoPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoResults`
--

DROP TABLE IF EXISTS `fagianoResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoSearchTweets`
--

DROP TABLE IF EXISTS `fagianoSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fagianoVideo`
--

DROP TABLE IF EXISTS `fagianoVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fagianoVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `failedFeed`
--

DROP TABLE IF EXISTS `failedFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `failedFeed` (
  `feed_url` varchar(200) DEFAULT NULL,
  `team_id` varchar(20) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuAvoidFeed`
--

DROP TABLE IF EXISTS `fcgifuAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuEntry`
--

DROP TABLE IF EXISTS `fcgifuEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2112 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuFeedMaster`
--

DROP TABLE IF EXISTS `fcgifuFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuPlayerTweets`
--

DROP TABLE IF EXISTS `fcgifuPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuResults`
--

DROP TABLE IF EXISTS `fcgifuResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuSearchTweets`
--

DROP TABLE IF EXISTS `fcgifuSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcgifuVideo`
--

DROP TABLE IF EXISTS `fcgifuVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fcgifuVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoAvoidFeed`
--

DROP TABLE IF EXISTS `fctokyoAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoEntry`
--

DROP TABLE IF EXISTS `fctokyoEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=20148 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoFeedMaster`
--

DROP TABLE IF EXISTS `fctokyoFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoPlayerTweets`
--

DROP TABLE IF EXISTS `fctokyoPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoResults`
--

DROP TABLE IF EXISTS `fctokyoResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoSearchTweets`
--

DROP TABLE IF EXISTS `fctokyoSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fctokyoVideo`
--

DROP TABLE IF EXISTS `fctokyoVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fctokyoVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedKeywordMaster`
--

DROP TABLE IF EXISTS `feedKeywordMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedKeywordMaster` (
  `keyword_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(20) DEFAULT NULL,
  `word` varchar(30) DEFAULT NULL,
  `ok_flg` tinyint(1) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`keyword_id`)
) ENGINE=MyISAM AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedMaster`
--

DROP TABLE IF EXISTS `feedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleAvoidFeed`
--

DROP TABLE IF EXISTS `frontaleAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleEntry`
--

DROP TABLE IF EXISTS `frontaleEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=19340 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleFeedMaster`
--

DROP TABLE IF EXISTS `frontaleFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontalePlayerTweets`
--

DROP TABLE IF EXISTS `frontalePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontalePlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleResults`
--

DROP TABLE IF EXISTS `frontaleResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleSearchTweets`
--

DROP TABLE IF EXISTS `frontaleSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frontaleVideo`
--

DROP TABLE IF EXISTS `frontaleVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frontaleVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaAvoidFeed`
--

DROP TABLE IF EXISTS `gambaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaEntry`
--

DROP TABLE IF EXISTS `gambaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=26051 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaFeedMaster`
--

DROP TABLE IF EXISTS `gambaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaPlayerTweets`
--

DROP TABLE IF EXISTS `gambaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaResults`
--

DROP TABLE IF EXISTS `gambaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaSearchTweets`
--

DROP TABLE IF EXISTS `gambaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gambaVideo`
--

DROP TABLE IF EXISTS `gambaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gambaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzAvoidFeed`
--

DROP TABLE IF EXISTS `giravanzAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzEntry`
--

DROP TABLE IF EXISTS `giravanzEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzFeedMaster`
--

DROP TABLE IF EXISTS `giravanzFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzPlayerTweets`
--

DROP TABLE IF EXISTS `giravanzPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzResults`
--

DROP TABLE IF EXISTS `giravanzResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(10) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzSearchTweets`
--

DROP TABLE IF EXISTS `giravanzSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `giravanzVideo`
--

DROP TABLE IF EXISTS `giravanzVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giravanzVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goalRanking`
--

DROP TABLE IF EXISTS `goalRanking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goalRanking` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `league` varchar(4) NOT NULL DEFAULT '',
  `seq` int(11) NOT NULL DEFAULT '0',
  `rank` int(11) DEFAULT NULL,
  `player_name` varchar(30) DEFAULT NULL,
  `goals` int(11) DEFAULT NULL,
  `position` varchar(4) DEFAULT NULL,
  `team` varchar(30) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`league`,`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusAvoidFeed`
--

DROP TABLE IF EXISTS `grampusAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusEntry`
--

DROP TABLE IF EXISTS `grampusEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=14618 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusFeedMaster`
--

DROP TABLE IF EXISTS `grampusFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusPlayerTweets`
--

DROP TABLE IF EXISTS `grampusPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusResults`
--

DROP TABLE IF EXISTS `grampusResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusSearchTweets`
--

DROP TABLE IF EXISTS `grampusSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grampusVideo`
--

DROP TABLE IF EXISTS `grampusVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grampusVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockAvoidFeed`
--

DROP TABLE IF EXISTS `hollyhockAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockEntry`
--

DROP TABLE IF EXISTS `hollyhockEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2103 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockFeedMaster`
--

DROP TABLE IF EXISTS `hollyhockFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockPlayerTweets`
--

DROP TABLE IF EXISTS `hollyhockPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockResults`
--

DROP TABLE IF EXISTS `hollyhockResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockSearchTweets`
--

DROP TABLE IF EXISTS `hollyhockSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hollyhockVideo`
--

DROP TABLE IF EXISTS `hollyhockVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hollyhockVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefAvoidFeed`
--

DROP TABLE IF EXISTS `jefAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefEntry`
--

DROP TABLE IF EXISTS `jefEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1897 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefFeedMaster`
--

DROP TABLE IF EXISTS `jefFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefPlayerTweets`
--

DROP TABLE IF EXISTS `jefPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefResults`
--

DROP TABLE IF EXISTS `jefResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefSearchTweets`
--

DROP TABLE IF EXISTS `jefSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jefVideo`
--

DROP TABLE IF EXISTS `jefVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jefVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloAvoidFeed`
--

DROP TABLE IF EXISTS `jubiloAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloEntry`
--

DROP TABLE IF EXISTS `jubiloEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7793 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloFeedMaster`
--

DROP TABLE IF EXISTS `jubiloFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloPlayerTweets`
--

DROP TABLE IF EXISTS `jubiloPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloResults`
--

DROP TABLE IF EXISTS `jubiloResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloSearchTweets`
--

DROP TABLE IF EXISTS `jubiloSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jubiloVideo`
--

DROP TABLE IF EXISTS `jubiloVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jubiloVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareAvoidFeed`
--

DROP TABLE IF EXISTS `kamatamareAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareEntry`
--

DROP TABLE IF EXISTS `kamatamareEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1702 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareFeedMaster`
--

DROP TABLE IF EXISTS `kamatamareFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamarePlayerTweets`
--

DROP TABLE IF EXISTS `kamatamarePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamarePlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareResults`
--

DROP TABLE IF EXISTS `kamatamareResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareSearchTweets`
--

DROP TABLE IF EXISTS `kamatamareSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kamatamareVideo`
--

DROP TABLE IF EXISTS `kamatamareVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kamatamareVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosAvoidFeed`
--

DROP TABLE IF EXISTS `marinosAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosEntry`
--

DROP TABLE IF EXISTS `marinosEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=22627 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosFeedMaster`
--

DROP TABLE IF EXISTS `marinosFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosPlayerTweets`
--

DROP TABLE IF EXISTS `marinosPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosResults`
--

DROP TABLE IF EXISTS `marinosResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosSearchTweets`
--

DROP TABLE IF EXISTS `marinosSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marinosVideo`
--

DROP TABLE IF EXISTS `marinosVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marinosVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `team_id` varchar(20) DEFAULT NULL,
  `message_id` int(11) DEFAULT NULL,
  `message` varchar(200) DEFAULT NULL,
  `os` varchar(10) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `min_ver` varchar(8) DEFAULT NULL,
  `max_ver` varchar(8) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioAvoidFeed`
--

DROP TABLE IF EXISTS `montedioAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioEntry`
--

DROP TABLE IF EXISTS `montedioEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6050 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioFeedMaster`
--

DROP TABLE IF EXISTS `montedioFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioPlayerTweets`
--

DROP TABLE IF EXISTS `montedioPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioResults`
--

DROP TABLE IF EXISTS `montedioResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioSearchTweets`
--

DROP TABLE IF EXISTS `montedioSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `montedioVideo`
--

DROP TABLE IF EXISTS `montedioVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `montedioVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `nabiscoStandings`
--

DROP TABLE IF EXISTS `nabiscoStandings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nabiscoStandings` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `group_name` varchar(2) NOT NULL DEFAULT '',
  `seq` int(11) NOT NULL DEFAULT '0',
  `rank` int(11) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `games` int(11) DEFAULT NULL,
  `win` int(11) DEFAULT NULL,
  `draw` int(11) DEFAULT NULL,
  `lose` int(11) DEFAULT NULL,
  `got_goal` int(11) DEFAULT NULL,
  `lost_goal` int(11) DEFAULT NULL,
  `diff` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`group_name`,`seq`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ngImageSite`
--

DROP TABLE IF EXISTS `ngImageSite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ngImageSite` (
  `url_keyword` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ngSite`
--

DROP TABLE IF EXISTS `ngSite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ngSite` (
  `domain` varchar(40) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ngWord`
--

DROP TABLE IF EXISTS `ngWord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ngWord` (
  `team_id` varchar(20) NOT NULL DEFAULT 'all',
  `word` varchar(50) NOT NULL,
  PRIMARY KEY (`team_id`,`word`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playerTweets`
--

DROP TABLE IF EXISTS `playerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `players` (
  `player_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(20) DEFAULT NULL,
  `position` varchar(3) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `height_weight` varchar(7) DEFAULT NULL,
  `place` varchar(20) DEFAULT NULL,
  `previous_team` varchar(40) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM AUTO_INCREMENT=566 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsAvoidFeed`
--

DROP TABLE IF EXISTS `redsAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsEntry`
--

DROP TABLE IF EXISTS `redsEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=57450 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsFeedMaster`
--

DROP TABLE IF EXISTS `redsFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=138 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsPlayerTweets`
--

DROP TABLE IF EXISTS `redsPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsResults`
--

DROP TABLE IF EXISTS `redsResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsSearchTweets`
--

DROP TABLE IF EXISTS `redsSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100)),
  KEY `idx_redsSearchTweets_created_at` (`created_at`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `redsVideo`
--

DROP TABLE IF EXISTS `redsVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redsVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaAvoidFeed`
--

DROP TABLE IF EXISTS `renofaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaEntry`
--

DROP TABLE IF EXISTS `renofaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1419 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaFeedMaster`
--

DROP TABLE IF EXISTS `renofaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaPlayerTweets`
--

DROP TABLE IF EXISTS `renofaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaResults`
--

DROP TABLE IF EXISTS `renofaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaSearchTweets`
--

DROP TABLE IF EXISTS `renofaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renofaVideo`
--

DROP TABLE IF EXISTS `renofaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renofaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `results`
--

DROP TABLE IF EXISTS `results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results` (
  `season` year(4) DEFAULT NULL,
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date NOT NULL DEFAULT '0000-00-00',
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_team` varchar(50) NOT NULL DEFAULT '',
  `away_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `home_score` int(11) DEFAULT NULL,
  `away_score` int(11) DEFAULT NULL,
  `home_pk` int(11) DEFAULT NULL,
  `away_pk` int(11) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`compe`,`game_date1`,`home_team`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolAvoidFeed`
--

DROP TABLE IF EXISTS `reysolAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolEntry`
--

DROP TABLE IF EXISTS `reysolEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=14005 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolFeedMaster`
--

DROP TABLE IF EXISTS `reysolFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolPlayerTweets`
--

DROP TABLE IF EXISTS `reysolPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolResults`
--

DROP TABLE IF EXISTS `reysolResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolSearchTweets`
--

DROP TABLE IF EXISTS `reysolSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reysolVideo`
--

DROP TABLE IF EXISTS `reysolVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reysolVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoAvoidFeed`
--

DROP TABLE IF EXISTS `roassoAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoEntry`
--

DROP TABLE IF EXISTS `roassoEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1652 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoFeedMaster`
--

DROP TABLE IF EXISTS `roassoFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoPlayerTweets`
--

DROP TABLE IF EXISTS `roassoPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoResults`
--

DROP TABLE IF EXISTS `roassoResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoSearchTweets`
--

DROP TABLE IF EXISTS `roassoSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roassoVideo`
--

DROP TABLE IF EXISTS `roassoVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roassoVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganAvoidFeed`
--

DROP TABLE IF EXISTS `saganAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganEntry`
--

DROP TABLE IF EXISTS `saganEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13096 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganFeedMaster`
--

DROP TABLE IF EXISTS `saganFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganPlayerTweets`
--

DROP TABLE IF EXISTS `saganPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganResults`
--

DROP TABLE IF EXISTS `saganResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) DEFAULT NULL,
  `game_date1` date NOT NULL DEFAULT '0000-00-00',
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`game_date1`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganSearchTweets`
--

DROP TABLE IF EXISTS `saganSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saganVideo`
--

DROP TABLE IF EXISTS `saganVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saganVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceAvoidFeed`
--

DROP TABLE IF EXISTS `sanfrecceAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceEntry`
--

DROP TABLE IF EXISTS `sanfrecceEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=18702 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceFeedMaster`
--

DROP TABLE IF EXISTS `sanfrecceFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfreccePlayerTweets`
--

DROP TABLE IF EXISTS `sanfreccePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfreccePlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceResults`
--

DROP TABLE IF EXISTS `sanfrecceResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceSearchTweets`
--

DROP TABLE IF EXISTS `sanfrecceSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanfrecceVideo`
--

DROP TABLE IF EXISTS `sanfrecceVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sanfrecceVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaAvoidFeed`
--

DROP TABLE IF EXISTS `sangaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaEntry`
--

DROP TABLE IF EXISTS `sangaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4067 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaFeedMaster`
--

DROP TABLE IF EXISTS `sangaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaPlayerTweets`
--

DROP TABLE IF EXISTS `sangaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaResults`
--

DROP TABLE IF EXISTS `sangaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaSearchTweets`
--

DROP TABLE IF EXISTS `sangaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sangaVideo`
--

DROP TABLE IF EXISTS `sangaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sangaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `searchTweets`
--

DROP TABLE IF EXISTS `searchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `searchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseAvoidFeed`
--

DROP TABLE IF EXISTS `spulseAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseEntry`
--

DROP TABLE IF EXISTS `spulseEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=19883 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseFeedMaster`
--

DROP TABLE IF EXISTS `spulseFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulsePlayerTweets`
--

DROP TABLE IF EXISTS `spulsePlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulsePlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseResults`
--

DROP TABLE IF EXISTS `spulseResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseSearchTweets`
--

DROP TABLE IF EXISTS `spulseSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spulseVideo`
--

DROP TABLE IF EXISTS `spulseVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spulseVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `standings`
--

DROP TABLE IF EXISTS `standings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `standings` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `league` varchar(4) NOT NULL DEFAULT '',
  `stage` varchar(5) NOT NULL DEFAULT '' COMMENT '1st/2nd/total',
  `seq` int(11) NOT NULL DEFAULT '0',
  `rank` int(11) DEFAULT NULL,
  `team_id` varchar(20) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `games` int(11) DEFAULT NULL,
  `win` int(11) DEFAULT NULL,
  `draw` int(11) DEFAULT NULL,
  `lose` int(11) DEFAULT NULL,
  `got_goal` int(11) DEFAULT NULL,
  `lost_goal` int(11) DEFAULT NULL,
  `diff` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`league`,`stage`,`seq`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `standings_back`
--

DROP TABLE IF EXISTS `standings_back`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `standings_back` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `league` varchar(4) NOT NULL DEFAULT '',
  `stage` varchar(5) DEFAULT NULL,
  `seq` int(11) NOT NULL DEFAULT '0',
  `rank` int(11) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `games` int(11) DEFAULT NULL,
  `win` int(11) DEFAULT NULL,
  `draw` int(11) DEFAULT NULL,
  `lose` int(11) DEFAULT NULL,
  `got_goal` int(11) DEFAULT NULL,
  `lost_goal` int(11) DEFAULT NULL,
  `diff` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`league`,`seq`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `teamMaster`
--

DROP TABLE IF EXISTS `teamMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teamMaster` (
  `team_id` varchar(20) NOT NULL DEFAULT '',
  `team_name` varchar(50) DEFAULT NULL,
  `team_name2` varchar(50) DEFAULT NULL,
  `team_name3` varchar(50) DEFAULT NULL,
  `team_name4` varchar(8) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `aclFlg` tinyint(1) DEFAULT NULL,
  `search_tweets_keyword` varchar(200) DEFAULT NULL,
  `player_tweets_list_id` bigint(20) DEFAULT NULL,
  `adType` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaAvoidFeed`
--

DROP TABLE IF EXISTS `thespaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaEntry`
--

DROP TABLE IF EXISTS `thespaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=800 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaFeedMaster`
--

DROP TABLE IF EXISTS `thespaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaPlayerTweets`
--

DROP TABLE IF EXISTS `thespaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaResults`
--

DROP TABLE IF EXISTS `thespaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaSearchTweets`
--

DROP TABLE IF EXISTS `thespaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thespaVideo`
--

DROP TABLE IF EXISTS `thespaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thespaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaAvoidFeed`
--

DROP TABLE IF EXISTS `trinitaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaEntry`
--

DROP TABLE IF EXISTS `trinitaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaFeedMaster`
--

DROP TABLE IF EXISTS `trinitaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaPlayerTweets`
--

DROP TABLE IF EXISTS `trinitaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaResults`
--

DROP TABLE IF EXISTS `trinitaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaSearchTweets`
--

DROP TABLE IF EXISTS `trinitaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trinitaVideo`
--

DROP TABLE IF EXISTS `trinitaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trinitaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenAvoidFeed`
--

DROP TABLE IF EXISTS `v_varenAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenEntry`
--

DROP TABLE IF EXISTS `v_varenEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3753 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenFeedMaster`
--

DROP TABLE IF EXISTS `v_varenFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenPlayerTweets`
--

DROP TABLE IF EXISTS `v_varenPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenResults`
--

DROP TABLE IF EXISTS `v_varenResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenSearchTweets`
--

DROP TABLE IF EXISTS `v_varenSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `v_varenVideo`
--

DROP TABLE IF EXISTS `v_varenVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `v_varenVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaAvoidFeed`
--

DROP TABLE IF EXISTS `vegaltaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaEntry`
--

DROP TABLE IF EXISTS `vegaltaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=19782 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaFeedMaster`
--

DROP TABLE IF EXISTS `vegaltaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaPlayerTweets`
--

DROP TABLE IF EXISTS `vegaltaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaResults`
--

DROP TABLE IF EXISTS `vegaltaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaSearchTweets`
--

DROP TABLE IF EXISTS `vegaltaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vegaltaVideo`
--

DROP TABLE IF EXISTS `vegaltaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vegaltaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetAvoidFeed`
--

DROP TABLE IF EXISTS `ventforetAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetEntry`
--

DROP TABLE IF EXISTS `ventforetEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8290 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetFeedMaster`
--

DROP TABLE IF EXISTS `ventforetFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetPlayerTweets`
--

DROP TABLE IF EXISTS `ventforetPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetResults`
--

DROP TABLE IF EXISTS `ventforetResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetSearchTweets`
--

DROP TABLE IF EXISTS `ventforetSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventforetVideo`
--

DROP TABLE IF EXISTS `ventforetVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventforetVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyAvoidFeed`
--

DROP TABLE IF EXISTS `verdyAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyEntry`
--

DROP TABLE IF EXISTS `verdyEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3028 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyFeedMaster`
--

DROP TABLE IF EXISTS `verdyFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyPlayerTweets`
--

DROP TABLE IF EXISTS `verdyPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyResults`
--

DROP TABLE IF EXISTS `verdyResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdySearchTweets`
--

DROP TABLE IF EXISTS `verdySearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdySearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verdyVideo`
--

DROP TABLE IF EXISTS `verdyVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verdyVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselAvoidFeed`
--

DROP TABLE IF EXISTS `visselAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselEntry`
--

DROP TABLE IF EXISTS `visselEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=14510 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselFeedMaster`
--

DROP TABLE IF EXISTS `visselFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselPlayerTweets`
--

DROP TABLE IF EXISTS `visselPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselResults`
--

DROP TABLE IF EXISTS `visselResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselSearchTweets`
--

DROP TABLE IF EXISTS `visselSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visselVideo`
--

DROP TABLE IF EXISTS `visselVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visselVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisAvoidFeed`
--

DROP TABLE IF EXISTS `vortisAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisEntry`
--

DROP TABLE IF EXISTS `vortisEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7103 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisFeedMaster`
--

DROP TABLE IF EXISTS `vortisFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisPlayerTweets`
--

DROP TABLE IF EXISTS `vortisPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisResults`
--

DROP TABLE IF EXISTS `vortisResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisSearchTweets`
--

DROP TABLE IF EXISTS `vortisSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vortisVideo`
--

DROP TABLE IF EXISTS `vortisVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vortisVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaAvoidFeed`
--

DROP TABLE IF EXISTS `yamagaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaEntry`
--

DROP TABLE IF EXISTS `yamagaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2634 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaFeedMaster`
--

DROP TABLE IF EXISTS `yamagaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feed_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaPlayerTweets`
--

DROP TABLE IF EXISTS `yamagaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaPlayerTweets` (
  `tweet_id` mediumtext CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_screen_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `user_profile_image_url` text CHARACTER SET utf8,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaResults`
--

DROP TABLE IF EXISTS `yamagaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(20) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(20) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaSearchTweets`
--

DROP TABLE IF EXISTS `yamagaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text CHARACTER SET utf8mb4,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yamagaVideo`
--

DROP TABLE IF EXISTS `yamagaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yamagaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcAvoidFeed`
--

DROP TABLE IF EXISTS `yokohamafcAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcEntry`
--

DROP TABLE IF EXISTS `yokohamafcEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2609 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcFeedMaster`
--

DROP TABLE IF EXISTS `yokohamafcFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcPlayerTweets`
--

DROP TABLE IF EXISTS `yokohamafcPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcResults`
--

DROP TABLE IF EXISTS `yokohamafcResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcSearchTweets`
--

DROP TABLE IF EXISTS `yokohamafcSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yokohamafcVideo`
--

DROP TABLE IF EXISTS `yokohamafcVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yokohamafcVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaAvoidFeed`
--

DROP TABLE IF EXISTS `zelviaAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaEntry`
--

DROP TABLE IF EXISTS `zelviaEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3219 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaFeedMaster`
--

DROP TABLE IF EXISTS `zelviaFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaPlayerTweets`
--

DROP TABLE IF EXISTS `zelviaPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaResults`
--

DROP TABLE IF EXISTS `zelviaResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaSearchTweets`
--

DROP TABLE IF EXISTS `zelviaSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zelviaVideo`
--

DROP TABLE IF EXISTS `zelviaVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zelviaVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenAvoidFeed`
--

DROP TABLE IF EXISTS `zweigenAvoidFeed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenAvoidFeed` (
  `feed_id` int(11) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenEntry`
--

DROP TABLE IF EXISTS `zweigenEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenEntry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_url` varchar(500) DEFAULT NULL,
  `entry_title` varchar(1000) DEFAULT NULL,
  `content` text,
  `image_url` varchar(500) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `up_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1781 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenFeedMaster`
--

DROP TABLE IF EXISTS `zweigenFeedMaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenFeedMaster` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(200) DEFAULT NULL,
  `feed_name` varchar(200) DEFAULT NULL,
  `site_name` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenPlayerTweets`
--

DROP TABLE IF EXISTS `zweigenPlayerTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenPlayerTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenResults`
--

DROP TABLE IF EXISTS `zweigenResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenResults` (
  `season` year(4) NOT NULL DEFAULT '0000',
  `compe` varchar(40) NOT NULL DEFAULT '',
  `game_date1` date DEFAULT NULL,
  `game_date2` varchar(30) DEFAULT NULL,
  `kickoff_time` varchar(5) DEFAULT NULL,
  `stadium` varchar(50) DEFAULT NULL,
  `home_flg` tinyint(1) DEFAULT NULL,
  `vs_team` varchar(50) DEFAULT NULL,
  `tv` varchar(100) DEFAULT NULL,
  `result` char(1) DEFAULT NULL,
  `score` varchar(20) DEFAULT NULL,
  `detail_url` varchar(200) DEFAULT NULL,
  `ticket_url` varchar(200) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`season`,`compe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenSearchTweets`
--

DROP TABLE IF EXISTS `zweigenSearchTweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenSearchTweets` (
  `tweet_id` mediumtext NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `user_screen_name` varchar(256) DEFAULT NULL,
  `user_profile_image_url` text,
  `tweet` text,
  `retweeted_count` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`tweet_id`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zweigenVideo`
--

DROP TABLE IF EXISTS `zweigenVideo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zweigenVideo` (
  `video_id` varchar(20) NOT NULL DEFAULT '',
  `video_title` varchar(200) DEFAULT NULL,
  `game_date` date NOT NULL DEFAULT '0000-00-00',
  `thumbnail_url` varchar(100) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `dislike_count` int(11) DEFAULT NULL,
  `up_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`video_id`,`game_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-06  6:31:07
