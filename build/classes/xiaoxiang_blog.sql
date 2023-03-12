/*
 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50562
 Source Host           : localhost:3306
 Source Schema         : xiaoxiang_blog

 Target Server Type    : MySQL
 Target Server Version : 50562
 File Encoding         : 65001

 Date: 08/03/2023 11:34:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_blog
-- ----------------------------
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识',
  `active` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否有效，1有效，0无效',
  `author_id` int(10) UNSIGNED NOT NULL COMMENT '外键，发布者ID',
  `title` varchar(210) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '博客标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '博客内容',
  `read_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问量',
  `like_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞量',
  `comment_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `like_msg_send_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞消息下发计数，防止重复发点赞消息',
  `create_timestamp` bigint(20) UNSIGNED NOT NULL COMMENT '发布时间',
  `update_timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识',
  `active` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '评论是否存在，1：存在，0：已删除',
  `blog_id` int(10) UNSIGNED NOT NULL COMMENT '博文ID，在哪篇博文下的评论',
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '评论发送者ID',
  `reply_id` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '被回复的评论ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `create_timestamp` bigint(20) UNSIGNED NOT NULL COMMENT '回复时间',
  `update_timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_like
-- ----------------------------
DROP TABLE IF EXISTS `t_like`;
CREATE TABLE `t_like`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态：1点赞，-1反对，0取消',
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '点赞的用户',
  `blog_id` int(10) UNSIGNED NOT NULL COMMENT '被点赞的文章',
  `create_timestamp` bigint(20) UNSIGNED NOT NULL COMMENT '点赞的时间',
  `update_timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最后一次修改的时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，唯一',
  `sender_id` int(10) UNSIGNED NOT NULL COMMENT '发送者ID，0 为 系统发送',
  `receiver_id` int(10) UNSIGNED NOT NULL COMMENT '接收者ID',
  `state` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '查看状态：1已查看，0未查看',
  `target_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '目标链接',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '消息内容',
  `msg_type` tinyint(3) UNSIGNED NOT NULL COMMENT '消息类型：1回复，2提到，3点赞，4系统消息',
  `create_timestamp` bigint(20) UNSIGNED NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 381 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID，自动生成，唯一',
  `active` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '账号是否激活：1激活，0禁用',
  `account` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号，登录用，不可修改',
  `passwd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码，登录用，MD5加密',
  `role` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色，0：普通，1：管理员',
  `nick` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `signature` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '签名',
  `sex` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '性别，0：未设置，1：男，2：女',
  `hometown` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '家乡',
  `link` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '联系方式',
  `avatar` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '头像ID',
  `avatar_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像URL',
  `has_new_msg` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '新消息计数',
  `blog_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '博客数量计数',
  `blog_read_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '博客阅读计数',
  `blog_like_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '博客被点赞计数',
  `create_timestamp` bigint(20) UNSIGNED NOT NULL COMMENT '创建时间',
  `update_timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最后修改时间',
  `last_login_timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最后登录时间',
  `last_login_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后登录IP',
  PRIMARY KEY (`id`, `account`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
