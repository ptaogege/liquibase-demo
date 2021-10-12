package com.liquibase;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.CatalogAndSchema;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.serializer.ChangeLogSerializer;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.*;
import org.apache.tools.ant.util.StringUtils;
import org.assertj.core.internal.Diff;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class runLiquibaseTest {

    private static Logger logger = LoggerFactory.getLogger(runLiquibaseTest.class);
    String changeLogFile = (String) yaml().get("changeLogFile");

    /**
     * 获取yaml文件数据
     * @return
     */
    @Test
    public Map yaml() {
        Yaml yaml = new Yaml();
        URL url = runLiquibaseTest.class.getClassLoader().getResource("liquibase.yaml");
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

    /**
     * 生成对比差异脚本
     * @throws LiquibaseException
     * @throws SQLException
     */
    @Test
    public void testDiff() throws LiquibaseException, SQLException {
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
        String diffChangeLogFile = (String) yaml().get("diffChangeLogFile");
        Connection connection = DriverManager.getConnection((String) yaml().get("url"), (String) yaml().get("username"),(String) yaml().get("password"));
        Connection refConnection = DriverManager.getConnection((String) yaml().get("referenceUrl"), (String) yaml().get("referenceUsername"),(String) yaml().get("referencePassword"));

        try {
            Database targetDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Database referenceDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(refConnection));
            Liquibase liquibase = new Liquibase("", new ClassLoaderResourceAccessor(), referenceDatabase);

            /*CommandLineUtils.doDiff(referenceDatabase,targetDatabase,StringUtils.trimToNull((String) yaml().get("diffTypes")),null, new PrintStream("src/main/resources/db_changelog/mydiff.txt"));
                CommandLineUtils.doDiffToChangeLog(diffChangeLogFile,referenceDatabase,targetDatabase,new DiffOutputControl(),null, StringUtils.trimToNull((String) yaml().get("diffTypes")));
            System.out.println("diff差异已写入——>" + diffChangeLogFile);*/

            DiffResult diffResult = liquibase.diff(referenceDatabase, targetDatabase, new CompareControl(compareTypes));

            //final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PrintStream printStream = new PrintStream(diffChangeLogFile);
            final DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult, new DiffOutputControl());
            diffToChangeLog.print(printStream);
            System.out.println("diff差异已写入——>" + diffChangeLogFile);

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
     * 自动生成数据库脚本
     * @throws SQLException
     */
    @Test
    public void testGenerateChangeLog() throws SQLException {
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

        String outputChangeLogFile = (String) yaml().get("outputChangeLogFile");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("generateUrl"), (String) yaml().get("username"), (String) yaml().get("password"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(outputChangeLogFile, new ClassLoaderResourceAccessor(), database);

            //CommandLineUtils.doGenerateChangeLog(outputChangeLogFile,database,null,StringUtils.trimToNull((String) yaml().get("diffTypes")),"penguin","",outputChangeLogFile,new DiffOutputControl());

            DiffToChangeLog diffToChangeLog = new DiffToChangeLog(new DiffOutputControl());
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputChangeLogFile);

            liquibase.generateChangeLog(database.getDefaultSchema(), diffToChangeLog, printStream,compareTypes.toArray(new Class[compareTypes.size()]));
            System.out.println("********** GENERATED CHANGELOG START **********");
            diffToChangeLog.print(printStream);
            System.out.println("********** GENERATED CHANGELOG END **********");

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
     * 更新、标签、回滚操作
     * @throws SQLException
     */
    @Resource
    private DataSource dataSource;
    @Test
    public void testUpdate() throws SQLException, ParseException {

        String migrationSqlOutputFile = (String) yaml().get("migrationSqlOutputFile");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection((String) yaml().get("url"), (String) yaml().get("username"), (String) yaml().get("password"));
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            /*connection = dataSource.getConnection();
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), new JdbcConnection(connection));*/

            //命令行输出日志
            /*StringWriter writer = new StringWriter();
            liquibase.update("", writer);
            logger.info('\n'+writer.toString());*/

            //将即将要执行的执行日志写入文件，不更新数据库
            /*PrintWriter printWriter = new PrintWriter(migrationSqlOutputFile);
            liquibase.update("", printWriter);
            logger.info("记录已写入" + migrationSqlOutputFile);*/

            liquibase.update("");   //更新数据库
            logger.info("--------------数据库已更新-------------");
            liquibase.tag("V3.0.2");   //打标签

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

    @Test
    public void rollback(Liquibase liquibase) throws FileNotFoundException, LiquibaseException {
        PrintStream printStream = new PrintStream("");
        StringWriter stringWriter = new StringWriter();
        liquibase.futureRollbackSQL(stringWriter);
        System.out.println(stringWriter.toString());
    }
}
