package com.wp.ownerMS.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// needs the @Configuration annotation
@Configuration
public class OwnerConfig {
	
	@Bean
	// @LoadBalanced will return a load balanced rest template to use
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
