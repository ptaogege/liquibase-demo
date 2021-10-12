-- liquibase formatted sql

-- changeset PANSOFT:1634019692058-1
CREATE TABLE changerollback (id INT NOT NULL, name VARCHAR(255) NOT NULL, pass VARCHAR(255) NOT NULL, CONSTRAINT PK_CHANGEROLLBACK PRIMARY KEY (id));

CREATE TABLE distributor (id INT NOT NULL, name VARCHAR(255) NOT NULL, address VARCHAR(255) NULL, tel VARCHAR(255) NULL COMMENT '电话', CONSTRAINT PK_DISTRIBUTOR PRIMARY KEY (id));

INSERT INTO distributor (id, name, address, tel) VALUES (1, 'zhangsan', '北京', '123123');
INSERT INTO distributor (id, name, address, tel) VALUES (2, 'lisi', '上海', '123123');
INSERT INTO distributor (id, name, address, tel) VALUES (3, 'Pan', '山东济南', '9988998');
INSERT INTO distributor (id, name, address, tel) VALUES (4, 'Soft', '山东青岛', '9933428');
INSERT INTO distributor (id, name, address, tel) VALUES (5, 'niu', '山东枣庄', '998998');

-- changeset peng:13413451
INSERT INTO changerollback (id, name, pass) VALUES (1, 'tao', '666');

