package com.servicecours.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.servicecours.Entities.Etudiant;
import com.servicecours.Entities.Image;
import com.servicecours.Entities.Inscription;
import com.servicecours.Entities.Professeur;
import com.servicecours.Repositories.CoursRepository;
import com.servicecours.Repositories.InscriptionReposiroty;
@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequestMapping("/courses")
public class coursController {
	@Autowired
	private CoursRepository coursRepository;
	@Autowired
	private InscriptionReposiroty inscriptionRepository;
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
	
	@GetMapping("/inscription/All")
	public List<Inscription> getSubscriptionsByCourse() {
		return inscriptionRepository.findAll();
	}
	
	
	@PostMapping(value="/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String addCours(@RequestHeader("Authorization") String token ,@RequestPart(value="image",required = false) MultipartFile image,
			@RequestPart("cours") Cours cours,@RequestPart("professeur") String profUsername) throws RestClientException, IOException {
		
		Professeur prof = restTemplate.getForObject("http://service-utilisateur/users/professor/"+profUsername, Professeur.class);
		
		if(image!=null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add(HttpHeaders.AUTHORIZATION, token);
			HttpEntity< Image > imgEntity = new HttpEntity<>(new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes()), headers);

			Image img = restTemplate.postForObject("http://service-image/images/addImage",imgEntity,Image.class);
			coursRepository.save(new Cours(cours.getNom(), cours.getCategorie(), cours.getDateDeb(),
					cours.getDateFin(),cours.getDescription(), prof.getIduser(), img.getId()));
		}
		else {
			coursRepository.save(new Cours(cours.getNom(), cours.getCategorie(), cours.getDateDeb(),
					cours.getDateFin(),cours.getDescription(), prof.getIduser()));
		}

		
		
		return "Course added";
	}
	
	@PutMapping(value="/update/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String update(@PathVariable("id") Long id,
			@RequestPart(value="image",required = false) MultipartFile image,
			@RequestPart("cours") Cours cours,@RequestPart("professeur") String profUsername) throws IOException {
		
		Cours coursToUpdate = coursRepository.findById(id).get();
		
		coursToUpdate.setCategorie(cours.getCategorie());
		coursToUpdate.setNom(cours.getNom());
		coursToUpdate.setDateDeb(cours.getDateDeb());
		coursToUpdate.setDateFin(cours.getDateFin());
		coursToUpdate.setDescription(cours.getDescription());
		
		if(image!=null) {
			Image imgEntity= null;
			if(coursToUpdate.getImageId() != null) {
				 imgEntity = new Image(coursToUpdate.getImageId(),image.getOriginalFilename(),
						 image.getContentType(),image.getBytes());

			}
			else {
				 imgEntity = new Image(image.getOriginalFilename(),image.getContentType(),image.getBytes());	
			}

			Image img = restTemplate.postForObject("http://service-image/images/addImage",imgEntity,Image.class);
			
			coursToUpdate.setImageId(img.getId());
		}
		else {
			coursToUpdate.setImageId(coursToUpdate.getImageId());
		}
		coursRepository.save(coursToUpdate);		
		return "MAJ réussie";
	}
	
	@DeleteMapping(value="/delete/{id}")
	public String delete(@PathVariable("id") Long id,@RequestHeader("Authorization") String token ) {
		
		Long imageId =coursRepository.findById(id).get().getImageId();
		
		if(imageId != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add(HttpHeaders.AUTHORIZATION, token);
			HttpEntity<?> request = new HttpEntity<Object>(headers);
			restTemplate.exchange("http://service-image/images/delete/"+imageId,HttpMethod.DELETE, request, String.class);
		}
		
		
		coursRepository.deleteById(id);
		
		return "suppression réussie";
	}
	
	@PostMapping(value="/subscribe/{username}/{id}")
	public String subscribe(@PathVariable("id") Long id,@PathVariable("username") String username ){

		Etudiant etudiant = restTemplate.getForObject("http://service-utilisateur/users/student/"+username, Etudiant.class);
		Cours cours = coursRepository.findById(id).get();
		Inscription inscription = new Inscription(etudiant.getIduser(), cours);
		
		inscriptionRepository.save(inscription);
		
		return "Inscription réussie";
	}
	
	@DeleteMapping(value="/unsubscribe/{username}/{id}")
	public String unsubscribe(@PathVariable("id") Long id,@PathVariable("username") String username ){

		Etudiant etudiant = restTemplate.getForObject("http://service-utilisateur/users/student/"+username, Etudiant.class);
		Cours cours = coursRepository.findById(id).get();
		
		inscriptionRepository.delete(inscriptionRepository.findByEtudiantIdAndCourId(cours.getId(), etudiant.getIduser()));
		
		return "Desinscription réussie";
	}

}
