package org.sid.users.config.service;


import java.util.Collection;
import java.util.stream.Collectors;

import org.sid.users.entities.Utilisateur;
import org.sid.users.repositories.UtilisateurRepository;
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


@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UtilisateurRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Utilisateur user= userRepository.findByUsername(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("Invalid email or password !");
		}
		return new User(user.getEmail(),user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole()));
	}
	
}
