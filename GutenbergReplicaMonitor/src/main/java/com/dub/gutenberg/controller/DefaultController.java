package com.dub.gutenberg.controller;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dub.gutenberg.config.SimpleWebSocketsProperties;



@Controller
@EnableConfigurationProperties(SimpleWebSocketsProperties.class)
public class DefaultController {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(DefaultController.class);
		
	
	SimpleWebSocketsProperties props;
	
	public DefaultController(SimpleWebSocketsProperties props){
		this.props = props;
	}
	
	@RequestMapping({"/", "/backHome", "/index"})
    public String greeting(Model model, HttpServletRequest request) throws ParseException {
       		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	
		String username = auth.getName();

		model.addAttribute("username", username);
	
        return "index";
    }
	

    @GetMapping("/login")
    public String login(Model model,
    		HttpServletRequest request) {
    	
    	try {
    		
    		if(SecurityContextHolder.getContext().getAuthentication() instanceof
                UsernamePasswordAuthenticationToken) {
    			return "index";
    		}
    	
    		Enumeration<String> params = request.getParameterNames();
    	
    		while (params.hasMoreElements()) {
    			if (params.nextElement().equals("loginFailed")) {
    				model.addAttribute("loginFailed", "loginFailed");
    			}
    		}
    	
    	} catch (NoSuchElementException e) {
    		logger.warn("Exception caught " + e);
    	}
    	return "login";
    }
    
    @GetMapping(value="/logout")
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
    	if (auth != null) { 
            new SecurityContextLogoutHandler().logout(request, response, auth);
    	}

        return "redirect:/login?logout";
    }
    
    @GetMapping(value="/replicaSetMonitor")
    public String getReplicaSetMonitor(Model model) {
   	
    	String monitorUrl = "http://" + props.getHost()
    	+ ":" + props.getPort()   			
    			+ props.getContextPath() 
    						+ props.getEndpoint();
    	
    	model.addAttribute("monitorUrl", monitorUrl);
    			
    	model.addAttribute("mappingReply", props.getMappingReply());
   	
    	return "admin/mongodb";
    }
    
}