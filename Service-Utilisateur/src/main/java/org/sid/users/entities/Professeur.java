package org.sid.users.entities;

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
public class Professeur extends Utilisateur{
	private long idimage;
	public Professeur(Utilisateur user) {
		super(user.getIduser(), user.getEmail(), user.getPassword(), "ROLE_PROFESSEUR", user.getNom(), user.getPrenom(), user.getAdresse(), user.getTel(), user.getDateInscrip());
	}
	
	
	
	public Professeur(long idimage) {
		this.idimage = idimage;
	}



	public long getIdimage() {
		return idimage;
	}
	public void setIdimage(long idimage) {
		this.idimage = idimage;
	}
	
}
