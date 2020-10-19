package org.sid.users.controllers;

import org.sid.users.entities.Etudiant;
import org.sid.users.entities.Professeur;
import org.sid.users.entities.Utilisateur;
import org.sid.users.repositories.EtudiantRepository;
import org.sid.users.repositories.ProfesseurRepository;
import org.sid.users.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
	
	@Autowired
	UtilisateurRepository userRepository;
	@Autowired
	EtudiantRepository etudiantRepository;
	@Autowired
	ProfesseurRepository professeurRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/api/home")
	public String getHome() {
		return "welcome home";
	}
	@GetMapping("/user/{username}")
	public String getUser(@PathVariable("username") String username) {
		if(userRepository.findByEmail(username).size()>0)
			return "exist";
		return "don't exist";
	}
	@PostMapping("/signup")
	public String signup(@RequestBody Utilisateur user) {
		if(userRepository.findByEmail(user.getEmail()).size()>0) {
			return "Already exists.";
		}
		else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			if (user.getRole().equals("ROLE_ETUDIANT"))
				etudiantRepository.save(new Etudiant(user));
			if (user.getRole().equals("ROLE_PROFESSEUR"))
				professeurRepository.save(new Professeur(user));
			return "User is added successfully.";
		}
			
		
		
	}
	@PutMapping("/modify/{id}")
	public String modify(@RequestBody Utilisateur user, @PathVariable("id") Long id) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setIduser(id);
		userRepository.save(user);
		if (user.getRole().equals("ROLE_ETUDIANT"))
			etudiantRepository.save(new Etudiant(user));
		if (user.getRole().equals("ROLE_PROFESSEUR"))
			professeurRepository.save(new Professeur(user));
		return "User is added successfully.";
	}
}
