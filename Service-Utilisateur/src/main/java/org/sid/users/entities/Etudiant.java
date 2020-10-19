package org.sid.users.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper=false)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Data
@Entity @NoArgsConstructor

public class Etudiant extends Utilisateur{
	public Etudiant(Utilisateur user) {
		super(user.getIduser(), user.getEmail(), user.getPassword(), "ROLE_ETUDIANT", user.getNom(), user.getPrenom(), user.getAdresse(), user.getTel(), user.getDateInscrip());
	}

	public Etudiant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Etudiant(Long iduser, String email, String password, String role, String nom, String prenom, String adresse,
			String tel, LocalDate dateInscrip) {
		super(iduser, email, password, role, nom, prenom, adresse, tel, dateInscrip);
		// TODO Auto-generated constructor stub
	}
	
	
}
