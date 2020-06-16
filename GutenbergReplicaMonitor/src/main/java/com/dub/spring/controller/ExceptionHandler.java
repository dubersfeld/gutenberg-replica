package com.dub.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {
	
	public List<ExceptionParseResult> parse(String message) {
			
		List<ExceptionParseResult> list = new ArrayList<>();
		
		String patternString1 = ".*servers=(\\[.*\\]).*";
		
		Pattern pattern = Pattern.compile(patternString1);
		
		Matcher matcher = pattern.matcher(message);
		
        //boolean matches = matcher.find();
        System.out.println(matcher.group(1));
        String tmp = matcher.group(1);
        
        System.out.println();
        
        // Step 2
        String patternString2 = ".*(address=[^,]*).*(type=[^,]*).*(state=[^,\\}]*).*";
        
        pattern = Pattern.compile(patternString2);
		
		matcher = pattern.matcher(tmp);
		System.out.println("Before while "
				+ matcher.find() + " " + matcher.groupCount());
		boolean finished = false;

		while (!finished) {
			
			/*
			System.out.println("group 1 " + matcher.group(1));
			System.out.println("group 2 " + matcher.group(2));
			System.out.println("group 3 " + matcher.group(3));
			
			System.out.println("more matches " + matcher.start(1));
			System.out.println("last: " + tmp.substring(matcher.start()));
		*/
			String address = matcher.group(1).substring(8);
			String type = matcher.group(2).substring(5);
			String state = matcher.group(3).substring(6);
				
			// storing results
			ExceptionParseResult result 
					= new ExceptionParseResult(address, type, state);
			list.add(result);
					
			
			// prepare next search
			tmp = tmp.substring(0, matcher.start(1));	
			//System.out.println("new tmp: " + tmp);
		
			matcher = pattern.matcher(tmp);
			finished = !matcher.find();
			System.out.println("match step 2 " 
					+ finished + " " + matcher.groupCount());
			
		}// while
      
		return list;
	} 

}
