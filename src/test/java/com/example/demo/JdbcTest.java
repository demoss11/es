package com.example.demo;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@SpringBootTest
public class JdbcTest {
    @Autowired private JdbcTemplate jdbcTemplate;
    @Test
    void test001() throws IOException {
        Resource resource = new ClassPathResource("script/sql.txt");
        InputStream in = resource.getInputStream();
        String s = FileUtil.readString(resource.getURL(), "utf-8");
        jdbcTemplate.execute(s);
        /*String[] split = s.split(";");
        for (String s1 : split) {
            jdbcTemplate.execute(s1);
        }*/


    }

    @Test
    public void test002() throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName();
        System.out.println(databaseProductName);
    }
}
