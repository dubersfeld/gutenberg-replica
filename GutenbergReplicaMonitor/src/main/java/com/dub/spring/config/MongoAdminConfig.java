package com.dub.spring.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


@Configuration
@EnableMongoRepositories(
		basePackages = "com.dub.spring.repository",
		mongoTemplateRef = "authenticationMongoTemplate")
//@EnableConfigurationProperties(MongoProperties.class)
public class MongoAdminConfig extends AbstractMongoClientConfiguration {

	private static final Logger logger = 
			LoggerFactory.getLogger(MongoAdminConfig.class);
	
	//private MongoProperties props;
	
	@Value("${dub.mongo.admin-database}")
	private String database;
	
	@Value("${dub.mongo.hostports}")
	private String hostports;
	/*
	public MongoAdminConfig(MongoProperties props) {
		this.props = props;
	}
	*/

	@Override
	protected String getDatabaseName() {
		return database;
	}

	
	@Override
	public MongoClient mongoClient() {
		
		logger.info("MongoAdminConfig database " + database);
		
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");		
		
		return mongoClient;
	}
	
	
	@Bean(name = "authenticationMongoTemplate")
	public MongoOperations authMongoTemplate() throws Exception {
		return new MongoTemplate(mongoClient(), database);
	}	
}