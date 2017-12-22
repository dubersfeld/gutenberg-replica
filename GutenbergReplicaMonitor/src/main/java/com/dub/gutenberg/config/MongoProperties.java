package com.dub.gutenberg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dub.mongo")
public class MongoProperties {
	
	String hostports;
	String database;
	String adminDatabase;
	String authenticationHost;
	String authenticationPort;
	
	public String getHostports() {
		return hostports;
	}

	public void setHostports(String hostports) {
		this.hostports = hostports;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAdminDatabase() {
		return adminDatabase;
	}

	public void setAdminDatabase(String adminDatabase) {
		this.adminDatabase = adminDatabase;
	}

	public String getAuthenticationHost() {
		return authenticationHost;
	}

	public void setAuthenticationHost(String authenticationHost) {
		this.authenticationHost = authenticationHost;
	}

	public String getAuthenticationPort() {
		return authenticationPort;
	}

	public void setAuthenticationPort(String authenticationPort) {
		this.authenticationPort = authenticationPort;
	}
	
	
	

}
