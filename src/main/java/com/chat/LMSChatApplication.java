package com.chat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LMSChatApplication  extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LMSChatApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(LMSChatApplication.class, args);
	}

	@Bean
	CommandLineRunner init(){
		return args -> System.out.println("<================>");
	}



}
