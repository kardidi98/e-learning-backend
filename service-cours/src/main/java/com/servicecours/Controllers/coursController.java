package com.servicecours.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.servicecours.Entities.Cours;
import com.servicecours.Entities.Image;
import com.servicecours.Entities.Professeur;
import com.servicecours.Repositories.CoursRepository;
@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequestMapping("/courses")
public class coursController {
	@Autowired
	private CoursRepository coursRepository;
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/{id}")
	public Cours getCourse(@PathVariable("id") Long id) {
		return coursRepository.findById(id).get();
	}
	
	@GetMapping("/All")
	public List<Cours> getAllCourse() {
		return coursRepository.findAll();
	}
	
	
	@PostMapping(value="/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String addCours(@RequestHeader("Authorization") String token ,@RequestPart(value="image",required = false) MultipartFile image,
			@RequestPart("cours") Cours cours,@RequestPart("professeur") String profUsername) throws RestClientException, IOException {
		
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, token);
		HttpEntity< Image > imgEntity = new HttpEntity<>(new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes()), headers);

		Image img = restTemplate.postForObject("http://service-image/images/addImage",imgEntity,Image.class);
		
		Professeur prof = restTemplate.getForObject("http://service-utilisateur/users/professor/"+profUsername, Professeur.class);
		
		coursRepository.save(new Cours(cours.getNom(), cours.getCategorie(), cours.getDateDeb(),
				cours.getDateFin(),cours.getDescription(), prof.getIduser(), img.getId()));
		
		return "Course added";
	}
	
	@PutMapping(value="/update/{id}")
	public String update(@PathVariable("id") Long id) {
		Cours cours = coursRepository.findById(id).get();
		
		return "MAJ réussie";
	}
	
	@DeleteMapping(value="/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		return "suppression réussie";
	}
	
	@PostMapping(value="/subscribe")
	public String subscribe(){
		return "Inscription réussie";
	}

}
