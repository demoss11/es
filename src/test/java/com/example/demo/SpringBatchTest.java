package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dm.jdbc.pool.DmdbDataSource_bs;
import dm.jdbc.rowset.DmdbJdbcRowSet;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import javax.sql.rowset.JdbcRowSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class SpringBatchTest {
    @Autowired
    private  StepBuilderFactory step;
    @Autowired private JobBuilderFactory job;
    @Autowired private JobLauncher launcher;
    @Autowired private DataSource dataSource;
    @Autowired private RestHighLevelClient client;

    private ResultSet rs;

    /*@Qualifier(value = "dmDataSource")
    @Autowired private DataSource dmDataSource;*/
    @Test
    <R>
    void   test001(){
        r1();
        TaskletStep step = this.step.get("222")
                .<String,String>chunk(1000)
                .reader(new ItemReader<String>() {
                            @Override
                            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                                boolean next = rs.next();
                                ObjectMapper mapper = new ObjectMapper();
                                mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                if(next){
                                    ResultSetMetaData metaData = rs.getMetaData();
                                    int columnCount = metaData.getColumnCount();
                                    Map<String,Object> map = new HashMap<>();
                                    for (int i = 1; i < columnCount+1; i++) {
                                        String columnName = JdbcUtils.lookupColumnName(metaData, i);
                                        Object value = JdbcUtils.getResultSetValue(rs, i);
                                        map.put(columnName,value);


                                    }
                                    String s = mapper.writeValueAsString(map);
                                    String s1 = gson.toJson(map);
                                    return s;
                                }
                                return null;
                            }
                        }
                )
                .writer(new ItemWriter<String>() {


                    @Override
                    public void write(List<? extends String> items) throws Exception {
                        BulkRequest bulk = new BulkRequest();
                        for (String item : items) {
                            IndexRequest request = new IndexRequest("sb");
                            //request.id((String) map.get("id"));
                            request.source(item,XContentType.JSON);
                            //request.source(map, XContentType.JSON);
                            bulk.add(request);
                        }
                        client.bulkAsync(bulk, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                            @Override
                            public void onResponse(BulkResponse bulkItemResponses) {
                                System.out.println("哈哈,高玩了");
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                    }

                })
                /*.processor(new ItemProcessor<Object, Object>() {
                    @Override
                    public Object process(Object item) throws Exception {
                        System.out.println(item);
                        return null;
                    }
                })*/
                .build();
        Job job = this.job.get(System.currentTimeMillis() + "")
                .start(step)
                .build();
        try {
            launcher.run(job,new JobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
        while(true){

        }

    }

    /*@Bean
    public Job job(@Qualifier("step1") Step step1, @Qualifier("step2") Step step2) {
        return jobs.get("myJob").start(step1).next(step2).build();
    }

    @Bean
    protected Step step1(ItemReader<Person> reader,
                         ItemProcessor<Person, Person> processor,
                         ItemWriter<Person> writer) {
        return steps.get("step1")
                .<Person, Person> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step step2(Tasklet tasklet) {
        return steps.get("step2")
                .tasklet(tasklet)
                .build();
    }*/

    @Bean
    public  ItemReader reader (){
        JdbcCursorItemReaderBuilder builder = new JdbcCursorItemReaderBuilder();
        builder.name("hehe");
        builder.dataSource(dataSource());
        builder.fetchSize(1000);
        builder.useSharedExtendedConnection(true);

        builder.rowMapper(new ColumnMapRowMapper());
        builder.sql("select * from ga_car_baseinfo");
        JdbcCursorItemReader itemReader = builder.build();
        return itemReader;
    }

    public DataSource dataSource(){
        DmdbDataSource_bs dataSource = new DmdbDataSource_bs();
        //DmdbConnectionPoolDataSource dataSource = new DmdbConnectionPoolDataSource();
        dataSource.setURL("jdbc:dm://localhost:5236");
        dataSource.setUser("SYSDBA");
        dataSource.setPassword("SYSDBA");
        return dataSource;
    }

    void r1(){
        String cmd = "select * from GA_CAR_BASEINFO";
        JdbcRowSet rowSet = new DmdbJdbcRowSet();
        try {
            rowSet.setUrl("jdbc:dm://localhost:5236");
            rowSet.setUsername("SYSDBA");
            rowSet.setPassword("SYSDBA");
            rowSet.setCommand(cmd);
            rowSet.setFetchSize(1000);
            rowSet.setFetchDirection(1000);
            rowSet.execute();
            this.rs = rowSet;



            /*int fetchDirection = rowSet.getFetchDirection();
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
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /*@Autowired private ResourceLoader resourceLoader;
    @Autowired private Environment environment;
    @PostConstruct
    protected void initialize() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(resourceLoader.getResource(environment.getProperty("batch.schema.script")));
        populator.setContinueOnError(true);
        DatabasePopulatorUtils.execute(populator , dataSource());
    }*/
}
