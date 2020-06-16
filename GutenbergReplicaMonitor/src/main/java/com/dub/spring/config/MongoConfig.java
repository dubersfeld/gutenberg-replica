package com.dub.spring.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


/**
 * Configuration class for replica set monitoring 
 * */

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	private static final Logger logger = 
			LoggerFactory.getLogger(MongoAdminConfig.class);
	
	@Value("${dub.mongo.admin-database}")
	private String database;
	
	@Value("${dub.mongo.hostports}")
	private String hostports;
	

	@Override
	protected String getDatabaseName() {
		return database;
	}


	@Override
	public MongoClient mongoClient() {
	
		MongoClient mongoClient = MongoClients.create("mongodb://" + hostports);
				
		logger.info("MongoConfig database " + database);
		//return new MongoClient(serverAddresses);	
		return mongoClient;
	}
	

	@Bean(name = "monitorMongoTemplate")
	public MongoOperations authMongoTemplate() throws Exception {
		// admin required here, not gutenberRS
		return new MongoTemplate(this.mongoClient(), "admin");
	}
	
}