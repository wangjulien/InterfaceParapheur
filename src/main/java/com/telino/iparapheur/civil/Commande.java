package com.telino.iparapheur.civil;

public class Commande {

	private String numeroCommande;
	
	private String nomFichier;
	
	private String urlFichier;
	
	private String idUnique;
	
	public Commande() {
		super();
	}

	public String getNumeroCommande() {
		return numeroCommande;
	}

	public void setNumeroCommande(String numeroCommande) {
		this.numeroCommande = numeroCommande;
	}

	public String getNomFichier() {
		return nomFichier;
	}

	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}

	public String getUrlFichier() {
		return urlFichier;
	}

	public void setUrlFichier(String urlFichier) {
		this.urlFichier = urlFichier;
	}

	public String getIdUnique() {
		return idUnique;
	}

	public void setIdUnique(String idUnique) {
		this.idUnique = idUnique;
	}
}
