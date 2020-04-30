package com.elasticsearch.poc.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.EntityMapper;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.elasticsearch.poc.repository")
public class RestClientConfig {
//extends AbstractElasticsearchConfiguration {
 /* 
  @Override
  public RestHighLevelClient elasticsearchClient() {       
    return RestClients.create(ClientConfiguration.localhost()).rest();
  } 
  
  @Autowired 
  ElasticsearchOperations elasticsearchOperations;

  // use the ElasticsearchEntityMapper
  @Bean
  @Override
  public EntityMapper entityMapper() {                     
    ElasticsearchEntityMapper entityMapper = new ElasticsearchEntityMapper(elasticsearchMappingContext(),
        new DefaultConversionService());
    entityMapper.setConversions(elasticsearchCustomConversions());

    return entityMapper;
  } */
}