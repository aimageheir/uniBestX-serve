-- sys_user 增加手机号、头像字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS phone  VARCHAR(20);
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS avatar VARCHAR(255);

COMMENT ON COLUMN sys_user.phone  IS '手机号';
COMMENT ON COLUMN sys_user.avatar IS '头像地址';
