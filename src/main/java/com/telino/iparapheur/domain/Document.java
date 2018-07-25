package com.telino.iparapheur.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long documentId;

	private byte[] documentContent;
	
	private String documentTitle;
	
	private String documentAppliId;

	private boolean documentMaster;

	private String contentType;

	@ManyToOne
	@JoinColumn(name = "dossier_id")
	private Dossier dossier;

	public Document() {
		super();
		this.documentMaster = false;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public byte[] getDocumentContent() {
		return documentContent;
	}

	public void setDocumentContent(byte[] documentContent) {
		this.documentContent = documentContent;
	}

	public boolean isDocumentMaster() {
		return documentMaster;
	}

	public void setDocumentMaster(boolean documentMaster) {
		this.documentMaster = documentMaster;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public String getDocumentAppliId() {
		return documentAppliId;
	}

	public void setDocumentAppliId(String documentAppliId) {
		this.documentAppliId = documentAppliId;
	}
}
