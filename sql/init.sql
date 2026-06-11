CREATE TABLE sys_user (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(100)    NOT NULL,
    nickname    VARCHAR(50),
    phone       VARCHAR(20),
    email       VARCHAR(100),
    status      SMALLINT        NOT NULL DEFAULT 1,
    is_deleted  SMALLINT        NOT NULL DEFAULT 0,
    create_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  sys_user            IS '系统用户表';
COMMENT ON COLUMN sys_user.id         IS '用户ID';
COMMENT ON COLUMN sys_user.username   IS '用户名';
COMMENT ON COLUMN sys_user.password   IS '密码（BCrypt 加密）';
COMMENT ON COLUMN sys_user.nickname   IS '昵称';
COMMENT ON COLUMN sys_user.phone      IS '手机号';
COMMENT ON COLUMN sys_user.email      IS '邮箱';
COMMENT ON COLUMN sys_user.status     IS '状态：1=正常，0=禁用';
COMMENT ON COLUMN sys_user.is_deleted IS '逻辑删除：0=正常，1=删除';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sys_user_update_time
    BEFORE UPDATE ON sys_user
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

INSERT INTO sys_user (username, password, nickname, phone, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '13800138000', 'admin@example.com', 1),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', '13800138001', 'test@example.com', 1);
