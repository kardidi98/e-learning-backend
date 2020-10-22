package com.servicecours.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.servicecours.Entities.Cours;

@RepositoryRestResource
public interface CoursRepository extends JpaRepository<Cours, Long> {

}
