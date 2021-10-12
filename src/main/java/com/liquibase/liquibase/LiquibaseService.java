package com.liquibase.liquibase;

import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.*;
import org.apache.tools.ant.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class LiquibaseService {
    private static Logger logger = LoggerFactory.getLogger(LiquibaseService.class);
    String changeLogFile = (String) yaml().get("changeLogFile");

    /**
     * 更新数据库
     * @throws SQLException
     */
    public void update(String tag) throws SQLException {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("targetUrl"), (String) yaml().get("referenceUsername"), (String) yaml().get("referencePassword"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);

            //命令行输出日志
            /*StringWriter writer = new StringWriter();
            liquibase.update("", writer);
            logger.info('\n'+writer.toString());*/

            //将即将要执行的执行日志写入文件，不更新数据库
            /*String migrationSqlOutputFile = (String) yaml().get("migrationSqlOutputFile");
            PrintWriter printWriter = new PrintWriter(migrationSqlOutputFile);
            liquibase.update("", printWriter);
            logger.info("记录已写入" + migrationSqlOutputFile);*/

            liquibase.update("");   //更新数据库
            liquibase.tag(tag);   //打标签
            logger.info("<<<<<<<<<<数据库已更新，新标签为 " + tag + " >>>>>>>>>>");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }
    }

    /**
     * 自动生成数据库执行脚本,全量导出
     * @throws SQLException
     */
    public void GenerateChangeLog(String outputChangeLogFile) throws SQLException {

        final Set<Class<? extends DatabaseObject>> compareTypes = new HashSet<>();
        compareTypes.add(Column.class);
        compareTypes.add(Data.class);
        compareTypes.add(ForeignKey.class);
        compareTypes.add(Index.class);
        compareTypes.add(PrimaryKey.class);
        compareTypes.add(Sequence.class);
        compareTypes.add(Table.class);
        compareTypes.add(UniqueConstraint.class);
        compareTypes.add(View.class);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("targetUrl"), (String) yaml().get("referenceUsername"), (String) yaml().get("referencePassword"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(outputChangeLogFile, new ClassLoaderResourceAccessor(), database);

            DiffToChangeLog diffToChangeLog = new DiffToChangeLog(new DiffOutputControl());
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputChangeLogFile);

            liquibase.generateChangeLog(database.getDefaultSchema(), diffToChangeLog, printStream, compareTypes.toArray(new Class[compareTypes.size()]));
            logger.info("<<<<<<<<<< 开始获取 >>>>>>>>>>");
            diffToChangeLog.print(printStream);
            logger.info("<<<<<<<<<< 获取完成 >>>>>>>>>>");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }
    }

    /**
     * 生成两数据库对比差异
     * @throws SQLException
     */
    public void diffChangeLog(String diffChangeLogFile) throws SQLException {
        String outputFile = (String) yaml().get("outputFile");
        Connection tarConnection = DriverManager.getConnection((String) yaml().get("targetUrl"), (String) yaml().get("targetUsername"),(String) yaml().get("targetPassword"));
        Connection refConnection = DriverManager.getConnection((String) yaml().get("referenceUrl"), (String) yaml().get("referenceUsername"),(String) yaml().get("referencePassword"));
        try {
            Database targetDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(tarConnection));
            Database referenceDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(refConnection));
            Liquibase liquibase = new Liquibase("", new ClassLoaderResourceAccessor(), targetDatabase);

            CommandLineUtils.doDiff(referenceDatabase, targetDatabase, StringUtils.trimToNull((String) yaml().get("diffTypes")), null, new PrintStream(outputFile));
            CommandLineUtils.doDiffToChangeLog(diffChangeLogFile, referenceDatabase, targetDatabase,
                    new DiffOutputControl().setIncludeCatalog(false).setIncludeSchema(false), null,
                    StringUtils.trimToNull((String) yaml().get("diffTypes")));
            logger.info("<<<<<<<<<<数据库差异已写入 " + diffChangeLogFile + " >>>>>>>>>>");

            /*DiffResult diffResult = liquibase.diff(referenceDatabase, targetDatabase, new CompareControl());
            //final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PrintStream printStream = new PrintStream(diffChangeLogFile);
            final DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult, new DiffOutputControl().setIncludeSchema(false).setIncludeCatalog(false));
            diffToChangeLog.print(printStream);
            logger.info("<<<<<<<<<<数据库差异已写入 " + diffChangeLogFile + " >>>>>>>>>>");*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (refConnection != null) {
                refConnection.close();
                tarConnection.close();
            }
        }
    }

    /**
     * 回滚
     * @throws SQLException
     */
    public void rollback() throws SQLException {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("url"), (String) yaml().get("username"), (String) yaml().get("password"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);

            //liquibase.rollback("V2.0","");   //回滚
            /*PrintStream printStream = new PrintStream("src/main/resources/db_changelog/test.xml");
            liquibase.rollback("",new OutputStreamWriter());*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }
    }

    /**
     * 同步数据库版本号 changeLogSync
     * @throws SQLException
     */
    public void changeLogSync(String tag) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("targetUrl"), (String) yaml().get("targetUsername"), (String) yaml().get("targetPassword"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);

            liquibase.changeLogSync("");
            logger.info("<<<<<<<<<< 已同步 >>>>>>>>>>");
            liquibase.tag(tag);
            logger.info("<<<<<<<<<<标签为 " + tag + " >>>>>>>>>>");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }


    /**
     * 获取yaml数据
     * @return
     */
    public Map yaml() {
        Yaml yaml = new Yaml();
        URL url = LiquibaseController.class.getClassLoader().getResource("liquibase.yaml");
        if (url != null) {
            try {
                Map map = yaml.load(new FileInputStream(url.getFile()));
                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
