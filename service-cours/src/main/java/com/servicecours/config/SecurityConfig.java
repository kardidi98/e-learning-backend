package com.servicecours.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*")
		.allowedMethods("GET", "PUT", "POST",
		"DELETE").allowedHeaders("*");
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests().antMatchers("/courses/**").permitAll()
		.antMatchers(HttpMethod.POST,"/courses/add")
		.hasAuthority("ROLE_PROFESSUER")
		.antMatchers(HttpMethod.POST,"/courses/subscribe")
		.hasAuthority("ROLE_ETUDIANT")
		.antMatchers(HttpMethod.GET,"/courses/**")
		.hasAuthority("ROLE_PROFESSEUR")
		.anyRequest().authenticated()
		.and()
		.httpBasic()
		.and()
		.csrf().disable();
	}
}
