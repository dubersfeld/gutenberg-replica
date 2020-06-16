package com.dub.spring.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.dub.spring.service.UserServiceImpl;
import com.mongodb.MongoClientSettings;
//import com.mongodb.Mongo;
//import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;


/**
 * Configuration class for replica set monitoring 
 * */

@Configuration
@EnableMongoRepositories(basePackages = "com.dub.spring.repository")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig extends AbstractMongoClientConfiguration {

	private static final Logger logger = 
			LoggerFactory.getLogger(MongoConfig.class);
	
	/*
	
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
	public MongoClient mongoClient() throws Exception {
			
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
	*/

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

	@Override
    public MongoClient mongoClient() {
		//ServerAddress serverAddress = new ServerAddress("mongodb://localhost", 27017);
		//List<ServerAddress> hosts = Arrays.asList(serverAddress);
		//ClusterSettings clusterSettings = ClusterSettings.builder()
		//										.hosts(hosts).build();
		//MongoClientSettings clientSettings = MongoClientSettings.builder().
		
		/*
		MongoClient mongoClient = MongoClients.create(
	            MongoClientSettings.builder()
	                    .applyToClusterSettings(builder ->
	                            builder.hosts(Arrays.asList(
	                            		new ServerAddress("localhost", 27017)
	                                    //new ServerAddress("host2", 27017),
	                                    //new ServerAddress("host3", 27017)
	                            )))
	                    .build());
			*/
		
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:40000,localhost:40001,localhost:40002");		
		return mongoClient;		
        //return MongoClients.create("mongodb://localhost" + ":" + 27017);
    }
	
	@Override
	protected String getDatabaseName() {
		return "gutenbergRS";
	} 
	
}