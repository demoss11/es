/*
package com.example.demo.config;

import dm.jdbc.pool.DmdbDataSource_bs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dmDataSource(){
        DmdbDataSource_bs dataSource = new DmdbDataSource_bs();
        //DmdbConnectionPoolDataSource dataSource = new DmdbConnectionPoolDataSource();
        dataSource.setURL("jdbc:dm://localhost:5236");
        dataSource.setUser("SYSDBA");
        dataSource.setPassword("SYSDBA");
        //HikariDataSource dataSource = new HikariDataSource();
        //dataSource.setDriverClassName("dm.jdbc.driver.DmDriver");

        //dataSource.setDataSourceClassName("dm.jdbc.driver.DmDriver");
        //dataSource.setJdbcUrl("jdbc:dm://localhost:5236");
        //dataSource.setUsername("SYSDBA");
        //dataSource.setPassword("SYSDBA");
        //dataSource.setDatabase("SYSDBA");
        return dataSource;
    }
}
*/
