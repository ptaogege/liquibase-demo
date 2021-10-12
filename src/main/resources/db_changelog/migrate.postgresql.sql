-- liquibase formatted sql

-- changeset PANSOFT:1634021765395-1
CREATE TABLE changerollback (id INTEGER NOT NULL, name VARCHAR(255) NOT NULL, pass VARCHAR(255) NOT NULL, CONSTRAINT changerollback_pkey PRIMARY KEY (id));

-- changeset PANSOFT:1634021765395-2
CREATE TABLE distributor (id INTEGER NOT NULL, name VARCHAR(255) NOT NULL, address VARCHAR(255), tel VARCHAR(255), CONSTRAINT distributor_pkey PRIMARY KEY (id));
COMMENT ON COLUMN distributor.tel IS '电话';

-- changeset PANSOFT:1634021765395-3
INSERT INTO distributor (id, name, address, tel) VALUES (1, 'zhangsan', '北京', '123123');
INSERT INTO distributor (id, name, address, tel) VALUES (2, 'lisi', '上海', '123123');
INSERT INTO distributor (id, name, address, tel) VALUES (3, 'Pan', '山东济南', '9988998');
INSERT INTO distributor (id, name, address, tel) VALUES (4, 'Soft', '山东青岛', '9933428');
INSERT INTO distributor (id, name, address, tel) VALUES (5, 'niu', '山东枣庄', '998998');

