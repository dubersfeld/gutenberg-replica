package com.dub.spring.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dub.mongo")
public class MongoProperties {
	
	String hostports;
		
	public String getHostports() {
		return hostports;
	}

	public void setHostports(String hostports) {
		this.hostports = hostports;
	}


}