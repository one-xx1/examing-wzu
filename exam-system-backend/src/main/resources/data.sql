CREATE DATABASE exam_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use exam_system;
/*
MySQL Backup
Database: exam_system
Backup Time: 2025-06-05 10:00:25
*/

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `exam_system`.`exam`;
DROP TABLE IF EXISTS `exam_system`.`exam_result`;
DROP TABLE IF EXISTS `exam_system`.`question`;
DROP TABLE IF EXISTS `exam_system`.`student_answer`;
DROP TABLE IF EXISTS `exam_system`.`system_settings`;
DROP TABLE IF EXISTS `exam_system`.`user`;
CREATE TABLE `exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `duration_minutes` int NULL DEFAULT NULL,
  `end_time` datetime(6) NULL DEFAULT NULL,
  `passing_score` int NULL DEFAULT NULL,
  `start_time` datetime(6) NULL DEFAULT NULL,
  `total_score` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
CREATE TABLE `exam_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `score` int NOT NULL,
  `duration` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `exam_id`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `exam_result_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `exam_system`.`user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exam_result_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exam_system`.`exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `option_a` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `option_b` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `option_c` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `option_d` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `score` int NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_question_exam`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_system`.`exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exam_system`.`exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
CREATE TABLE `student_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `is_correct` bit(1) NOT NULL,
  `selected_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `exam_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK9ar1y3e3sbdegryolkqta89k5`(`exam_id` ASC) USING BTREE,
  INDEX `FKsvdyurb450wbvvwcd261efibw`(`question_id` ASC) USING BTREE,
  INDEX `FK20mrv6r77tm750sp7oha616e1`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK20mrv6r77tm750sp7oha616e1` FOREIGN KEY (`user_id`) REFERENCES `exam_system`.`user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK9ar1y3e3sbdegryolkqta89k5` FOREIGN KEY (`exam_id`) REFERENCES `exam_system`.`exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKsvdyurb450wbvvwcd261efibw` FOREIGN KEY (`question_id`) REFERENCES `exam_system`.`question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
CREATE TABLE `system_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `default_exam_duration` int NULL DEFAULT NULL,
  `default_pass_score` int NULL DEFAULT NULL,
  `system_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `system_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `active` bit(1) NOT NULL,
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
BEGIN;
LOCK TABLES `exam_system`.`exam` WRITE;
DELETE FROM `exam_system`.`exam`;
INSERT INTO `exam_system`.`exam` () VALUES (3, '测试1', 1, '2025-06-02 15:33:15', '1', 60, NULL, 60, NULL, 100),(4, '222', 1, '2025-06-02 15:40:39', '222', 60, NULL, 60, NULL, 100)
;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `exam_system`.`exam_result` WRITE;
DELETE FROM `exam_system`.`exam_result`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `exam_system`.`question` WRITE;
DELETE FROM `exam_system`.`question`;
INSERT INTO `exam_system`.`question` () VALUES (1, 3, 'A', '1', '1', '12', '2', 'A', '2025-06-04 11:16:48', NULL, 1, 'SINGLE_CHOICE'),(2, 3, '测试A', '1', '1', '1', '1', 'A', '2025-06-04 13:40:52', NULL, 1, 'SINGLE_CHOICE')
;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `exam_system`.`student_answer` WRITE;
DELETE FROM `exam_system`.`student_answer`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `exam_system`.`system_settings` WRITE;
DELETE FROM `exam_system`.`system_settings`;
INSERT INTO `exam_system`.`system_settings` () VALUES (1, 'admin@example.com', 60, 60, '为学生提供便捷的在线考试体验', '在线考试系统')
;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `exam_system`.`user` WRITE;
DELETE FROM `exam_system`.`user`;
INSERT INTO `exam_system`.`user` () VALUES (1, 'admin', '$2a$10$urMXyYgPLUxNFpC4p1MGIO9tmuSEld0./rw1qmiv/JwXu.sR01gO2', 'ADMIN', '2025-06-02 16:49:15', b'1', NULL, NULL, NULL, NULL, NULL),(2, 'student1', '$2a$10$urMXyYgPLUxNFpC4p1MGIO9tmuSEld0./rw1qmiv/JwXu.sR01gO2', 'STUDENT', '2025-06-02 16:49:15', b'1', NULL, NULL, NULL, NULL, NULL),(3, 'test1', '$2a$10$MX8svY4ZjYthzRQCqGPa2.DxHFV/9ln.ouD/rGeQNoHhRqT2q979m', 'STUDENT', '2025-06-04 13:44:23', b'1', NULL, NULL, NULL, NULL, NULL)
;
UNLOCK TABLES;
COMMIT;

-- 更新用户激活状态
UPDATE user SET active = 1 WHERE username IN ('admin', 'student1', 'test1');
