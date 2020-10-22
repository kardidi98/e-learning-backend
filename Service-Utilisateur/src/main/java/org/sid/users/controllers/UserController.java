package org.sid.users.controllers;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.sid.users.config.service.CustomUserDetailsService;
import org.sid.users.entities.Etudiant;
import org.sid.users.entities.Professeur;
import org.sid.users.entities.Image;
import org.sid.users.entities.Utilisateur;
import org.sid.users.repositories.EtudiantRepository;
import org.sid.users.repositories.ProfesseurRepository;
import org.sid.users.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class UserController {
	
	@Autowired
	UtilisateurRepository userRepository;
	@Autowired
	EtudiantRepository etudiantRepository;
	@Autowired
	ProfesseurRepository professeurRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	CustomUserDetailsService userDetailsService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/api/home")
	public String getHome() {
		return "welcome home";
	}
	@GetMapping("/users/{username}")
	public String getUser(@PathVariable("username") String username) {
		if(userRepository.findByEmail(username).size()>0)
			return "exist";
		return "dosen't exist";
	}
	@PostMapping(value="/users/signup",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String signup(@RequestPart("image") MultipartFile image, @RequestPart("utilisateur") Utilisateur user) throws IOException {

		if(userRepository.findByEmail(user.getEmail()).size()>0) {
			return "Already exists.";
		}
		else {
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			if (user.getRole().equals("ROLE_ETUDIANT"))
				etudiantRepository.save(new Etudiant(user));
			if (user.getRole().equals("ROLE_PROFESSEUR")) {
				System.out.println(user.getRole());
				Image img = restTemplate.postForObject("http://service-image/images/addImage",new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes()),Image.class);
				professeurRepository.save(new Professeur(user,img.getId()));
			}
			
			return "User is added successfully.";
		}
			
		
		
	}
	
	@PostMapping(value="/users/login")
	public ResponseEntity<?> login(@RequestBody Utilisateur user) throws IOException {

		String credentials = "client:secret";
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
		
		if(userRepository.findByEmail(user.getEmail()).size() == 0) {
			return new ResponseEntity<>("dosen't exist.",HttpStatus.OK);
		}
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		
		
		if(passwordEncoder.matches(user.getPassword(),userDetails.getPassword())){
			
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("Authorization", "Basic " + encodedCredentials);
			HttpEntity<String> request = new HttpEntity<String>(headers);
			ResponseEntity<String> reponse = restTemplate.exchange("http://service-utilisateur/oauth/token?grant_type=password&username="+userDetails.getUsername()+"&password="+user.getPassword(), HttpMethod.POST, request, String.class);

			return ResponseEntity.ok(reponse.getBody());
			
		}
		else {
			return new ResponseEntity<>("Incorrect Password",HttpStatus.OK);
		}
			
		
		
	}
	
	@PutMapping("/users/{id}")
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
