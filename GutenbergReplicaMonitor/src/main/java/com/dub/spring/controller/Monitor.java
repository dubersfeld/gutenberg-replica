package com.dub.spring.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.dub.spring.config.SimpleWebSocketsProperties;


@Configuration
@EnableScheduling
@EnableConfigurationProperties(SimpleWebSocketsProperties.class)
public class Monitor {
	
	@Autowired
	private ExceptionHandler exceptionHandler;
	
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
    	Map<String, Object> map = new HashMap<>();
    	String sendTo = props.getTopic() + props.getMapping();
       	
    	try {
    		// unique replica set status request
    		Document status = mongoOperations.executeCommand("{replSetGetStatus : 1}");
    		
    		map.put("code", Code.OK);
    		map.put("payload", status);
    		
    		//this.template.convertAndSend(sendTo, status);
    		this.template.convertAndSend(sendTo, new Document(map));
    	} catch (DataAccessResourceFailureException e) {
    	
    		System.out.println("Exception caught "
    				+ e.getMessage());
    		
    		List<ExceptionParseResult> list = exceptionHandler.parse(e.getMessage());
    		
    		System.out.println("list " + list.size());
    	
    		map.put("code", Code.ALARM);
    		map.put("payload", list);
    		
    		//this.template.convertAndSend(sendTo, status);
    		this.template.convertAndSend(sendTo, new Document(map));
    
    	} catch (Exception e) {
    		System.out.println("Exception caught " + e);
    		map.put("code", Code.ALARM);
    		map.put("payload", null);
    		this.template.convertAndSend(sendTo, new Document(map));
    	    
    	}
    }
    
    
    
    public static enum Code {
    	OK, ALARM
    }
}