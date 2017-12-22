package com.dub.gutenberg.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.dub.gutenberg.service.UserServiceImpl;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;


@Configuration
@EnableMongoRepositories(
		basePackages = "com.dub.gutenberg.admin.repository",
		mongoTemplateRef = "authenticationMongoTemplate")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoAdminConfig extends AbstractMongoConfiguration {

	private static final Logger logger = 
			LoggerFactory.getLogger(MongoAdminConfig.class);
	
	
	@Value("${spring.data.mongodb.authentication-database}")
	private String database;
	
	@Override
	protected String getDatabaseName() {
		return database;
	}

	MongoProperties props;
	
	public MongoAdminConfig(MongoProperties props) {
		this.props = props;
	}
	

	@Override
	public Mongo mongo() throws Exception {
		
		logger.info("MongoAdminConfig database " + database);
		
		return new MongoClient(props.authenticationHost, 
							Integer.parseInt(props.authenticationPort));
	}
	
	@Bean(autowire = Autowire.BY_NAME, name = "authenticationMongoTemplate")
	public MongoOperations authMongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), props.getAdminDatabase());
	}

}
