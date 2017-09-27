package com.jephy;

import com.jephy.aop.aspect.AuthAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class BaseProjectApplication {

	@Bean
	public AuthAspect authAspect(){
		return new AuthAspect();
	}

	public static void main(String[] args) {
		SpringApplication.run(BaseProjectApplication.class, args);
	}
}
