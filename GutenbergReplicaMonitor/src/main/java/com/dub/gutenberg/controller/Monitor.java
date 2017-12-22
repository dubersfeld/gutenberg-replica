package com.dub.gutenberg.controller;

import java.rmi.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.dub.gutenberg.config.SimpleWebSocketsProperties;
import com.mongodb.CommandResult;
import com.mongodb.MongoSocketOpenException;


@Configuration
@EnableScheduling
@EnableConfigurationProperties(SimpleWebSocketsProperties.class)
public class Monitor {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	SimpleWebSocketsProperties props;
	
	public Monitor(SimpleWebSocketsProperties props) {
		logger.info("Monitor constructed");
		this.props = props;
	}
	
	/**
	 * Qualifier needed here for disambiguation 
	 * because we use two different MongoOperations beans: 
	 * one for replica set monitoring the other for authentication 
	 * */
	@Autowired
	@Qualifier("monitorMongoTemplate")
	private MongoOperations mongoOperations;
	
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    @Scheduled(fixedRate = 2000)
    public void sendCurrentStatus() {
    			
    	CommandResult status = mongoOperations.executeCommand("{replSetGetStatus : 1}");
    		
    	String sendTo = props.getTopic() + props.getMapping();
       
        this.template.convertAndSend(sendTo, status);
    }
}