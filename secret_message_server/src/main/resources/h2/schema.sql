DROP TABLE IF EXISTS user;

CREATE TABLE tb_user
(
    user_name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    password VARCHAR(50) NULL DEFAULT NULL COMMENT '密码',
    PRIMARY KEY (user_name)
);