package org.sid.users.repositories;

import java.util.List;

import org.sid.users.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
	public List<Utilisateur> findByEmail(String Email);
	
	@Query(value="select * from utilisateur where username like :x",nativeQuery = true)
	public Utilisateur findByUsername(@Param("x") String email);
}
