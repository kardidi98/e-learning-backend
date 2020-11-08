package com.servicecours.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.servicecours.Entities.Categorie;
import com.servicecours.Entities.Cours;

@RepositoryRestResource
public interface CoursRepository extends JpaRepository<Cours, Long> {

	List<Cours> findByNomContaining( String keyword);

	List<Cours> findByCategorie(Categorie categorie);

}
