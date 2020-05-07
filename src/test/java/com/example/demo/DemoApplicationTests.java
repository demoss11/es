package com.example.demo;

import cn.hutool.json.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {
    @Autowired private JdbcTemplate template;
    @Qualifier("restHighLevelClient")
    @Autowired private RestHighLevelClient client;
    @Test
    void contextLoads() {
        Connection connection = null;
        try {
            connection = template.getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from GA_CAR_BASEINFO");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                //resultSet.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testBatch(){
        template.query("select * from GA_CAR_BASEINFO", new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Map<String,Object> map = new HashMap<>();
                for (int i = 1; i < columnCount+1; i++) {
                    Object object = rs.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    map.put(columnName,object);
                }
                IndexRequest request = new IndexRequest("sa");
                request.source(map,XContentType.JSON);
                request.id((String) map.get("ID"));
                IndexResponse response = null;
                try {
                    response = client.index(request, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(response.getId()+"创建成功");

                return null;
            }
        });
    }

}
