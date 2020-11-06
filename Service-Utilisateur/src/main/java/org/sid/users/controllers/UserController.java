package org.sid.users.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.sid.users.config.JwtTokenUtil;
import org.sid.users.config.service.CustomUserDetailsService;
import org.sid.users.entities.Etudiant;
import org.sid.users.entities.Professeur;
import org.sid.users.entities.Image;
import org.sid.users.entities.JwtResponse;
import org.sid.users.entities.Utilisateur;
import org.sid.users.repositories.EtudiantRepository;
import org.sid.users.repositories.ProfesseurRepository;
import org.sid.users.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
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
	

	@GetMapping("/professor/{username}")
	public Professeur getProf(@PathVariable("username") String username) {
		
		return professeurRepository.findByEmail(username);
	}
	
	@GetMapping("/student/{username}")
	public Etudiant getStudent(@PathVariable("username") String username) {
		
		return etudiantRepository.findByEmail(username);
	}
	
	@GetMapping("/students")
	public List<Etudiant> getStudents() {
		
		return etudiantRepository.findAll();
	}
	
	@GetMapping("/professors")
	public List<Professeur> getAllProfessors() {
		
		return professeurRepository.findAll();
	}
	
	@GetMapping("")
	public List<Utilisateur> getAll() {
		
		return userRepository.findAll();
	}
	
	@GetMapping("/{username}")
	public Utilisateur getUser(@PathVariable("username") String username) {
		return userRepository.findByEmail(username).get(0);
	}
	

	@PostMapping(value="/signup",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String signup(@RequestPart(value = "image",required = false) MultipartFile image, 
			@RequestPart("utilisateur") Utilisateur user) throws IOException {

		if(userRepository.findByEmail(user.getEmail()).size()>0) {
			return "Already exists.";
		}
		else {
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			if (user.getRole().equals("ROLE_ETUDIANT")) {
				if(image!=null) {
					Image img = restTemplate.postForObject("http://service-image/images/addImage",
						new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes()),Image.class);
					etudiantRepository.save(new Etudiant(user,img.getId()));
				}
				else {
					etudiantRepository.save(new Etudiant(user));
				}
			}
				
			if (user.getRole().equals("ROLE_PROFESSEUR")) {
				if(image!=null) {
					Image img = restTemplate.postForObject("http://service-image/images/addImage",
						new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes()),Image.class);
					professeurRepository.save(new Professeur(user,img.getId()));
				}
				else {
					professeurRepository.save(new Professeur(user));
				}
				
			}
			
			return "User is added successfully.";
		}
			
		
		
	}
	
	@PostMapping(value="/login")
	public ResponseEntity<?> login(@RequestBody Utilisateur user) throws IOException {

		
		if(userRepository.findByEmail(user.getEmail()).size() == 0) {
			return new ResponseEntity<>("dosen't exist.",HttpStatus.OK);
		}
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		
		
		if(passwordEncoder.matches(user.getPassword(),userDetails.getPassword())){
			
			final String token = jwtTokenUtil.generateToken(userDetails);
			
			return ResponseEntity.ok(new JwtResponse(token));
			
		}
		else {
			return new ResponseEntity<>("Incorrect Password",HttpStatus.OK);
		}
		
	}
	
	@PutMapping("/update/{id}")
	public String modify(@RequestPart(value = "image",required = false) MultipartFile image, @PathVariable("id") Long id
			,@RequestPart("utilisateur") Utilisateur user) throws RestClientException, IOException {
			Utilisateur utilisateur = userRepository.findByEmail(user.getEmail()).get(0);
		
			utilisateur.setDetails(user.getAdresse(),user.getDateInscrip(),user.getEmail(),user.getNom(),
					user.getPrenom(),user.getTel());
			
			userRepository.save(utilisateur);
			if (user.getRole().equals("ROLE_ETUDIANT")) {
				
				
				updateStudent(id, utilisateur,image);
				
			}
				
			if (user.getRole().equals("ROLE_PROFESSEUR")) {
				
				updateProfesseur(id,utilisateur,image);
				
				
			}
		return "MAJ réussie.";
	
  }

	private void updateProfesseur(Long id, Utilisateur utilisateur, MultipartFile image) throws IOException {
		Professeur professeur = null;
		Professeur oldProf = professeurRepository.findById(id).get();
		if(image!=null) {
			Image imgEntity = null;
			if(oldProf.getIdimage() != null) {
				 imgEntity = new Image(oldProf.getIdimage(), image.getOriginalFilename(),
						 image.getContentType(),image.getBytes());
			}
			else {
				imgEntity = new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes());
			}
			
			Image img = restTemplate.postForObject("http://service-image/images/addImage",
								imgEntity,Image.class);
			
			professeur = new Professeur(utilisateur,img.getId());
			professeur.setIduser(id);
			
		}
		else {
			professeur = new Professeur(utilisateur,oldProf.getIdimage());
			professeur.setIduser(id);
			
		}
		professeurRepository.save(professeur);
		
	}

	private void updateStudent(Long id, Utilisateur utilisateur, MultipartFile image) throws IOException {
		
		Etudiant etudiant = null;
		Etudiant oldStudent = etudiantRepository.findById(id).get();
		if(image!=null) {
			Image imgEntity = null;
			if(oldStudent.getIdimage() != null) {
				 imgEntity = new Image(oldStudent.getIdimage(), image.getOriginalFilename(),
						 image.getContentType(),image.getBytes());
			}
			else {
				imgEntity = new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes());
			}
			
			Image img = restTemplate.postForObject("http://service-image/images/addImage",
								imgEntity,Image.class);
			
			etudiant = new Etudiant(utilisateur,img.getId());
			etudiant.setIduser(id);
			
		}
		else {
			etudiant = new Etudiant(utilisateur,oldStudent.getIdimage());
			etudiant.setIduser(id);
			
		}
		etudiantRepository.save(etudiant);
	}
	

}
