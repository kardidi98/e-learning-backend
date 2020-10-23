package com.servicecours.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@PostMapping(value="/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String addCours(@RequestPart("image") MultipartFile image, @RequestPart("cours") Cours cours,@RequestPart("professeur") String profUsername) throws RestClientException, IOException {
		
		Image img = restTemplate.postForObject("http://service-image/images/addImage",new Image(null,image.getOriginalFilename(),image.getContentType(),image.getBytes()),Image.class);
		
		Professeur prof = restTemplate.getForObject("http://service-utilisateur/users/professor/"+profUsername, Professeur.class);
		
		coursRepository.save(new Cours(cours.getNom(), cours.getCategorie(), cours.getDateDeb(),
				cours.getDateFin(), prof.getIduser(), img.getId()));
		
		return "Course added";
	}

}
