package com.telino.iparapheur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.domain.Dossier;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	public List<Document> findByDocumentAppliId(final String documentAppliId);
	
	public List<Document> findByDossier(final Dossier dossier);
	
}
