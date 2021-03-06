package com.dub.spring.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(SimpleWebSocketsProperties.class)
public class WebSocketsConfig implements WebSocketMessageBrokerConfigurer {
	
	SimpleWebSocketsProperties props;
	
	public WebSocketsConfig(SimpleWebSocketsProperties props){
		this.props = props;
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry.addEndpoint(props.getEndpoint()).withSockJS();
	}

	@Override
	public void configureMessageBroker(
							MessageBrokerRegistry config) {
		// Application Prefix:
		config.setApplicationDestinationPrefixes(
						props.getAppDestinationPrefix());
		
		// Simple broker
		config.enableSimpleBroker(props.getTopic());
	}
}