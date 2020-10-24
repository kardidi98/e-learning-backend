package com.servicecours.config.service;


import java.util.Collection;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.servicecours.Entities.Utilisateur;


@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	RestTemplate restTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//System.out.println(username);
		Utilisateur user= restTemplate.getForObject("http://service-utilisateur/users/"+username, Utilisateur.class);
		
		if(user==null) {
			throw new UsernameNotFoundException("Invalid email or password !");
		}
		return new User(user.getEmail(),user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole()));
	}
	
}
