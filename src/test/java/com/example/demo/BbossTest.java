package com.example.demo;

import com.example.demo.domain.TAgentInfo;
import dm.jdbc.rowset.DmdbJdbcRowSet;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.tran.DataStream;
import org.frameworkset.tran.db.input.es.DB2ESImportBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import javax.sql.rowset.JdbcRowSet;
import java.sql.*;

public class BbossTest {

    @Test
    void test001(){
        TAgentInfo agentInfo = new TAgentInfo() ;
        agentInfo.setIp("192.168.137.1");//ip属性作为文档唯一标识，根据ip值对应的索引文档存在与否来决定添加或者修改操作
//设置地理位置坐标
        agentInfo.setLocation("28.292781,117.238963");
//设置其他属性
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
//添加/修改文档
        clientUtil.addDocument("agentinfo",//索引名称
                "agentinfo",//索引类型
                agentInfo);//索引数据对象

        clientUtil.addDocument("agentinfo",//索引名称
                "agentinfo",//索引类型
                agentInfo,//索引数据对象
                "refresh=true");//强制实时刷新
    }

    @Test
    void test002(){
        String schema = "TEST20190102";
    }

    @Test
    public void testSimpleImportBuilder(){
        DB2ESImportBuilder importBuilder = DB2ESImportBuilder.newInstance();
        try {
            //清除测试表数据
            ElasticSearchHelper.getRestClientUtil().dropIndice("dbclobdemo");
        }
        catch (Exception e){

        }
        //数据源相关配置，可选项，可以在外部启动数据源
        importBuilder
                //.setDbName("TEST20190102")
                .setDbDriver("dm.jdbc.driver.DmDriver") //数据库驱动程序，必须导入相关数据库的驱动jar包
                .setDbUrl("jdbc:dm://localhost:5236") //通过useCursorFetch=true启用mysql的游标fetch机制，否则会有严重的性能隐患，useCursorFetch必须和jdbcFetchSize参数配合使用，否则不会生效
                .setDbUser("SYSDBA")
                .setDbPassword("SYSDBA")
                .setValidateSQL("select 1")
                .setUsePool(false)
                .setJdbcFetchSize(1000);//是否使用连接池


        //指定导入数据的sql语句，必填项，可以设置自己的提取逻辑
        importBuilder.setSql("select * from GA_CAR_BASEINFO");
        /**
         * es相关配置
         */
        importBuilder
                .setIndex("sa") //必填项
                .setIndexType("sa") //es 7以后的版本不需要设置indexType，es7以前的版本必需设置indexType
                .setRefreshOption(null)//可选项，null表示不实时刷新，importBuilder.setRefreshOption("refresh");表示实时刷新
                .setUseJavaName(true) //可选项,将数据库字段名称转换为java驼峰规范的名称，例如:doc_id -> docId
                .setBatchSize(5000)  //可选项,批量导入es的记录数，默认为-1，逐条处理，> 0时批量处理
                .setJdbcFetchSize(10000);//设置数据库的查询fetchsize，同时在mysql url上设置useCursorFetch=true启用mysql的游标fetch机制，否则会有严重的性能隐患，jdbcFetchSize必须和useCursorFetch参数配合使用，否则不会生效


        /**
         * 执行数据库表数据导入es操作
         */
        DataStream dataStream = importBuilder.builder();
        dataStream.execute();
        dataStream.destroy();//执行完毕后释放资源
    }

    @Test
    public void test003(){
        StopWatch sw = new StopWatch();
        sw.start();
        String cmd = "select * from GA_CAR_BASEINFO";
        JdbcRowSet rowSet = new DmdbJdbcRowSet();
        try {
            rowSet.setUrl("jdbc:dm://58.49.74.66:25236?useCursorFetch=false");
            rowSet.setUsername("TEST20190102");
            rowSet.setPassword("888888888");
            rowSet.setCommand(cmd);
            rowSet.setFetchSize(10);
            rowSet.setFetchDirection(10);
            rowSet.execute();



            int fetchDirection = rowSet.getFetchDirection();
            int fetchSize = rowSet.getFetchSize();
            System.out.println(fetchDirection);
            System.out.println(fetchSize);
            while (rowSet.next()){
                ResultSetMetaData metaData = rowSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i < columnCount+1; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object object = rowSet.getObject(i);
//                    System.out.println(columnName+object);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sw.stop();
        System.out.println(sw.getTotalTimeSeconds());

    }

    @Test
    void test004(){
        StopWatch sw = new StopWatch();
        sw.start();
        String sql = "select * from GA_CAR_BASEINFO";
        try {
            Connection con = DriverManager.getConnection("jdbc:dm://58.49.74.66:25236", "TEST20190102", "888888888");
            /*PreparedStatement preparedStatement = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);*/
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i < columnCount+1; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = resultSet.getString(columnName);
//                    System.out.println(columnName+value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sw.stop();
        System.out.println(sw.getTotalTimeSeconds());
    }
}
