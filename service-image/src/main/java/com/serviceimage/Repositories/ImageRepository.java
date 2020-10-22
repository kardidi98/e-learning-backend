package com.serviceimage.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.serviceimage.Entities.Image;

@RepositoryRestResource
public interface ImageRepository extends JpaRepository<Image, Long> {

	
}
