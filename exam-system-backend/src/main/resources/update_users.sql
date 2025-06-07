-- 更新用户激活状态
UPDATE user SET active = 1 WHERE username IN ('admin', 'student1', 'test1');

-- 验证更新结果
SELECT id, username, role, active FROM user;
