package com.servicecours.Entities;






public class Professeur extends Utilisateur{
	private Long idimage;
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
