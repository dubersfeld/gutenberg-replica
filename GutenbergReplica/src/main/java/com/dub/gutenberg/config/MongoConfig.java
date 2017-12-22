package com.dub.gutenberg.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.dub.gutenberg.service.UserServiceImpl;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


/**
 * Configuration class for replica set monitoring 
 * */

@Configuration
@EnableMongoRepositories(basePackages = "com.dub.gutenberg.repository")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig extends AbstractMongoConfiguration {

	private static final Logger logger = 
			LoggerFactory.getLogger(MongoConfig.class);
	
	
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	MongoProperties props;
	
	public MongoConfig(MongoProperties props) {
		this.props = props;
	}
	

	@Override
	protected String getDatabaseName() {
		return database;
	}


	@Override
	public Mongo mongo() throws Exception {
			
		List<Elem> elems = parseHosts(props.getHostports());
		List<ServerAddress> serverAddresses = new ArrayList<>();
	
		for (Elem elem : elems) {
			serverAddresses.add(new ServerAddress(elem.getHost(), elem.getPort()));
		}
		
		logger.warn("MongoConfig database " + database);
		return new MongoClient(serverAddresses);
		
	}
	
	
	private List<Elem> parseHosts(String listStr) {
		
		List<Elem> elems = new ArrayList<>();
		String[] elemStrs = listStr.split(",");
		for (int i = 0; i < elemStrs.length; i++) {
			String[] details = elemStrs[i].split(":");
			logger.warn(details[0] + " " + details[1]);
			elems.add(new Elem(details[0], Integer.parseInt(details[1])));
		}
		return elems;
	}
	

	private static class Elem {
		private String host;
		private int port;
		
		public Elem(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
	} 
}
