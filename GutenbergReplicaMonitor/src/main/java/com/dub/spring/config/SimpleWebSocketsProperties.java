package com.dub.spring.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dub.ws")
public class SimpleWebSocketsProperties {

	private String appDestinationPrefix;
	private String endpoint;
	private String topic;
	private String mapping;
	private String mappingReply;
	private String rate;
	private String contextPath;
	private String host;
	private int port;

	public String getMappingReply() {
		return mappingReply;
	}

	public void setMappingReply(String mappingReply) {
		this.mappingReply = mappingReply;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getAppDestinationPrefix() {
		return appDestinationPrefix;
	}

	public void setAppDestinationPrefix(String appDestinationPrefix) {
		this.appDestinationPrefix = appDestinationPrefix;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
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