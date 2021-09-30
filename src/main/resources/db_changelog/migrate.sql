--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: db_changelog/changelog-master.yaml
--  Ran at: 21-9-30 上午11:28
--  Against: root@localhost@jdbc:mysql://localhost:3306/liquibase?serverTimezone=UTC
--  Liquibase version: 4.4.3
--  *********************************************************************

--  Lock Database
UPDATE liquibase.DATABASECHANGELOGLOCK SET `LOCKED` = 1, LOCKEDBY = 'DESKTOP-TB940I5 (192.168.23.1)', LOCKGRANTED = '2021-09-30 11:28:20.033' WHERE ID = 1 AND `LOCKED` = 0;

--  Changeset db_changelog/liquibase.mysql/changelog-202109-dml.xml::2532533::penguin
INSERT INTO liquibase.distributor (id, name, address, tel) VALUES (1, 'zhangsan', '北京', '123123');

INSERT INTO liquibase.distributor (id, name, address, tel) VALUES (2, 'lisi', '上海', '123123');

INSERT INTO liquibase.DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, `DESCRIPTION`, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('2532533', 'penguin', 'db_changelog/liquibase.mysql/changelog-202109-dml.xml', NOW(), 6, '8:c0b2c852a519b19d945bd18551f0a28c', 'insert tableName=distributor; insert tableName=distributor', '', 'EXECUTED', NULL, NULL, '4.4.3', '2972501780');

--  Release Database Lock
UPDATE liquibase.DATABASECHANGELOGLOCK SET `LOCKED` = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

