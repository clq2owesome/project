/*
Navicat MySQL Data Transfer

Source Server         : clq
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : clq

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2015-11-27 17:34:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `clq_user`
-- ----------------------------
DROP TABLE IF EXISTS `clq_user`;
CREATE TABLE `clq_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id2` int(10) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createBy` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  `updateBy` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`id2`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of clq_user
-- ----------------------------
INSERT INTO `clq_user` VALUES ('95', '0', '尼玛', '123', null, null, null, null);
INSERT INTO `clq_user` VALUES ('98', '0', 'xxxx1', 'cccc', null, null, null, null);
INSERT INTO `clq_user` VALUES ('99', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('100', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('101', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('102', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('103', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('104', '0', null, '放到', null, null, null, null);
INSERT INTO `clq_user` VALUES ('105', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('106', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('107', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('108', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('109', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('110', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('111', '0', null, '45454', null, null, null, null);
INSERT INTO `clq_user` VALUES ('114', '0', null, '撒大声地', null, null, null, null);
INSERT INTO `clq_user` VALUES ('115', '0', null, '67567567', null, null, null, null);
INSERT INTO `clq_user` VALUES ('117', '0', null, '女包', null, null, null, null);
INSERT INTO `clq_user` VALUES ('118', '0', null, '2323', null, null, null, null);
INSERT INTO `clq_user` VALUES ('119', '0', 'xxxx1', 'dsfsdfd', null, null, null, null);
INSERT INTO `clq_user` VALUES ('120', '0', '传销自残水电费水电费', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('121', '0', 'ooo', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('122', '0', '传销自残水电费水电费', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('123', '0', '传销自残水电费水电费', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('124', '0', '传销自残水电费水电费', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('125', '0', '传销自残水电费水电费', '把电饭锅电饭锅', null, null, null, null);
INSERT INTO `clq_user` VALUES ('126', '0', '下次下次', '阿萨', null, null, null, null);
INSERT INTO `clq_user` VALUES ('127', '0', '下次下次', '阿萨', null, null, null, null);
INSERT INTO `clq_user` VALUES ('128', '0', '23232sdda3', '2323', '尼玛', '2015-10-08 14:58:37', null, null);
INSERT INTO `clq_user` VALUES ('130', '1', 'xxxx1', 'dsfsdfd', '尼玛', '2015-10-08 15:05:15', null, null);
