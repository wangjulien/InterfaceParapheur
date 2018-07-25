package com.telino.iparapheur.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dossier {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long dossierId;

	private int nbDoc;
	
	private String dossierAppliText;

	private String dossierAppliId;

	public Dossier() {
		super();
	}

	public Long getDossierId() {
		return dossierId;
	}

	public void setDossierId(Long dossierId) {
		this.dossierId = dossierId;
	}

	public int getNbDoc() {
		return nbDoc;
	}

	public void setNbDoc(int nbDoc) {
		this.nbDoc = nbDoc;
	}

	public String getDossierAppliText() {
		return dossierAppliText;
	}

	public void setDossierAppliText(String dossierAppliText) {
		this.dossierAppliText = dossierAppliText;
	}

	public String getDossierAppliId() {
		return dossierAppliId;
	}

	public void setDossierAppliId(String dossierAppliId) {
		this.dossierAppliId = dossierAppliId;
	}
}
