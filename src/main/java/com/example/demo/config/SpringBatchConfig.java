/*
package com.example.demo.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Bean
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSourceTransactionManager txManager, DataSource dataSource ) throws Exception{

        JobRepositoryFactoryBean job=new JobRepositoryFactoryBean();
        job.setDataSource(dataSource);
        job.setDatabaseType("oracle");
        job.setTransactionManager(txManager);
        job.afterPropertiesSet();
        return job;
    }
}
*/
