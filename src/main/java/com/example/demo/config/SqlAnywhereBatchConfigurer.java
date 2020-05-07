package com.example.demo.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@EnableBatchProcessing
@Configuration
public class SqlAnywhereBatchConfigurer extends DefaultBatchConfigurer {

  @Autowired
  private DataSource dataSource;
  @Autowired
  private PlatformTransactionManager transactionManager;

  public SqlAnywhereBatchConfigurer() {
      super();
  }

  public SqlAnywhereBatchConfigurer(DataSource dataSource) {
      super(dataSource);
  }

  @Override
  protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource);
    factory.setTransactionManager(transactionManager);
    factory.setDatabaseType("ORACLE");
    factory.afterPropertiesSet();
    return factory.getObject();
  }
}
