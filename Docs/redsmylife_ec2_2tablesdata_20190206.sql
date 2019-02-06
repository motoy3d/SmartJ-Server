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
-- Dumping data for table `configs`
--

LOCK TABLES `configs` WRITE;
/*!40000 ALTER TABLE `configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `configs` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `teamMaster`
--

LOCK TABLES `teamMaster` WRITE;
/*!40000 ALTER TABLE `teamMaster` DISABLE KEYS */;
INSERT INTO `teamMaster` VALUES ('reds','浦和レッズ','レッズ','浦和','ur','J1',0,'#urawareds -redsurw -twiccer -japanfootballnews',80604313,0,'2018-03-08 01:28:30'),('albirex','アルビレックス新潟','アルビレックス','新潟','an','J2',0,'#albirex -nalbirex -twiccer -japanfootballnews',707563087809765376,2,'2018-01-26 20:42:48'),('frontale','川崎フロンターレ','フロンターレ','川崎','kf','J1',1,'#frontale -frontalek -twiccer -japanfootballnews',165218254,2,'2017-01-28 21:43:25'),('marinos','横浜F・マリノス','マリノス','横浜FM','ym','J1',0,'#fmarinos -fmarinosy -twiccer -japanfootballnews',168845167,2,'2016-09-20 13:17:03'),('fctokyo','FC東京','FC東京','FC東京','to','J1',0,'#fctokyo -f_c_tokyo -twiccer -japanfootballnews',170342542,2,'2017-01-28 21:43:25'),('grampus','名古屋グランパス','グランパス','名古屋','ng','J1',0,'#grampus -ngygrampus -twiccer -japanfootballnews',170535113,2,'2018-03-08 01:28:00'),('spulse','清水エスパルス','エスパルス','清水','ss','J1',0,'#spulse -spulses -twiccer -japanfootballnews',170624677,2,'2017-01-28 13:38:58'),('gamba','ガンバ大阪','ガンバ','G大阪','go','J1',0,'#gamba -gamgambao -gamba_sponsor -twiccer -karaagechan2012 -japanfootballnews',171197134,2,'2018-03-08 01:28:30'),('antlers','鹿島アントラーズ','アントラーズ','鹿島','ka','J1',1,'#antlers -antlersksm -twiccer -aaantlers09 -kashima_sponsor -japanfootballnews',197391089,2,'2017-01-28 21:43:25'),('vissel','ヴィッセル神戸','ヴィッセル','神戸','vi','J1',0,'#vissel -visselk -twiccer -japanfootballnews',171709329,2,'2016-09-20 13:17:03'),('sanfrecce','サンフレッチェ広島','サンフレッチェ','広島','sh','J1',0,'#sanfrecce -sanfrecceh -twiccer -japanfootballnews -sanfre_sponsor',171709349,2,'2017-01-28 21:43:25'),('vegalta','ベガルタ仙台','ベガルタ','仙台','vs','J1',0,'#vegalta -svegalta -vegalta_sponsor -twiccer -japanfootballnews',173515792,2,'2016-09-20 13:17:03'),('cerezo','セレッソ大阪','セレッソ','C大阪','co','J1',1,'#cerezo -cerezoosk -cerezo_sponsor -twiccer -japanfootballnews',173744082,2,'2018-03-08 01:28:43'),('ardija','大宮アルディージャ','アルディージャ','大宮','oa','J2',0,'#ardija -omiya_sponsor -twiccer -japanfootballnews',171709388,2,'2018-01-26 20:42:48'),('ventforet','ヴァンフォーレ甲府','ヴァンフォーレ','甲府','ve','J2',0,'#vfk -ventforetk -kofu_sponsor -twiccer -japanfootballnews',173942028,2,'2018-01-26 20:42:48'),('sagan','サガン鳥栖','サガン','鳥栖','st','J1',0,'#sagantosu -tosusagan -tosu_sponsor -twiccer -japanfootballnews',171709364,2,'2016-09-20 13:17:03'),('reysol','柏レイソル','レイソル','柏','kr','J1',1,'#reysol -reysolksw -kashiwa_sponsor -twiccer -japanfootballnews',171709341,2,'2018-03-08 01:28:43'),('vortis','徳島ヴォルティス','ヴォルティス','徳島','vo','J2',0,'#vortis -vortis_sponsor -twiccer -japanfootballnews',175190201,2,'2016-09-20 13:17:03'),('montedio','モンテディオ山形','モンテディオ','山形','my','J2',0,'#montedio -yamagatasponsor -montedioymgt  -twiccer -japanfootballnews',NULL,2,'2016-09-20 13:17:03'),('yamaga','松本山雅FC','松本山雅','松本山雅','yg','J2',0,'#yamaga -twiccer -japanfootballnews',NULL,2,'2016-09-20 13:17:03'),('bellmare','湘南ベルマーレ','ベルマーレ','湘南','bm','J1',0,'#bellmare -shonan_sponsor -twiccer -japanfootballnews',NULL,2,'2018-01-26 20:43:27'),('jubilo','ジュビロ磐田','ジュビロ','磐田','ju','J1',0,'#jubilo -twiccer',NULL,2,'2016-09-20 13:17:02'),('avispa','アビスパ福岡','アビスパ','福岡','af','J2',0,'#avispa -twiccer',NULL,2,'2017-01-28 13:38:58'),('ehimefc','愛媛FC','愛媛FC','愛媛','eh','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('fagiano','ファジアーノ岡山','ファジアーノ','岡山','fo','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('fcgifu','FC岐阜','FC岐阜','岐阜','fg','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('trinita','大分トリニータ','トリニータ','大分','ot','J2',0,NULL,NULL,2,'2017-01-28 13:45:57'),('hollyhock','水戸ホーリーホック','ホーリーホック','水戸','mh','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('jef','ジェフユナイテッド千葉','ジェフユナイテッド千葉','ジェフ千葉','je','J2',0,NULL,NULL,2,'2017-01-28 13:51:46'),('kamatamare','カマタマーレ讃岐','カマタマーレ','讃岐','km','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('renofa','レノファ山口FC','レノファ山口','レノファ','ry','J2',0,NULL,NULL,2,'2016-10-08 06:54:17'),('sanga','京都サンガF.C','京都サンガ','京都','ks','J2',0,NULL,NULL,2,'2017-01-28 13:55:42'),('consadole','北海道コンサドーレ札幌','コンサドーレ','札幌','cs','J1',0,NULL,NULL,2,'2017-01-28 13:38:58'),('v_varen','V・ファーレン長崎','V・ファーレン','長崎','vv','J1',0,NULL,NULL,2,'2018-01-26 20:43:27'),('verdy','東京ヴェルディ','ヴェルディ','東京V','vn','J2',0,NULL,NULL,2,'2017-01-28 13:57:35'),('yokohamafc','横浜FC','横浜FC','横浜ＦＣ','yk','J2',0,NULL,NULL,2,'2016-10-08 06:54:18'),('zelvia','FC町田ゼルビア','ゼルビア','町田','mz','J2',0,'#zelvia -machida_sponsor',885509689017892864,2,'2017-07-13 14:48:30'),('zweigen','ツエーゲン金沢','ツエーゲン','金沢','zk','J2',0,NULL,NULL,2,'2016-10-08 06:54:18');
/*!40000 ALTER TABLE `teamMaster` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-06  6:41:12
