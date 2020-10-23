package org.sid.users.repositories;

import org.sid.users.entities.Professeur;
import org.sid.users.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource
public interface ProfesseurRepository extends JpaRepository<Professeur, Long>{

	@Query(value="select * from professeur where username like :x",nativeQuery = true)
	public Professeur findByUsername(@Param("x") String email);
}
