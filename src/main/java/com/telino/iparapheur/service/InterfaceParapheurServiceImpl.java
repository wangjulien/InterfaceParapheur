package com.telino.iparapheur.service;

import org.adullact.spring_ws.iparapheur._1.CreerDossierRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.domain.Dossier;
import com.telino.iparapheur.repository.AppUserRepository;
import com.telino.iparapheur.repository.DocumentRepository;
import com.telino.iparapheur.repository.DossierRepository;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.ParapheurException;

@Service
public class InterfaceParapheurServiceImpl implements InterfaceParapheurService {
	
	private static final String PDF_EXT = ".pdf";

	private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceParapheurServiceImpl.class);

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private DossierRepository dossierRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	public InterfaceParapheurServiceImpl() {
		super();
	}

	@Override
	public Document saveDocument(final CreerDossierRequest request) {

		LOGGER.debug("{} Attached file length : {}", request.getDocumentPrincipal().getContentType(),
				request.getDocumentPrincipal().getValue().length);

		// Chercher dans DB si Dossier existe pour meta-donnee DossierID, s'il n'y a
		// pas, cree un
		Dossier dossier = dossierRepository.findByDossierAppliId(request.getDossierID()).orElseGet(() -> {
			Dossier newDossier = new Dossier();
			newDossier.setDossierAppliText(ApplicationMetier.OXALIS.toString());
			newDossier.setDossierAppliId(request.getDossierID());

			return newDossier;
		});

		dossier.setNbDoc(dossier.getNbDoc() + 1);
		dossierRepository.save(dossier);

		Document document = new Document();
		document.setDocumentTitle(buildDocumentTitle(request));
		document.setDocumentContent(request.getDocumentPrincipal().getValue());
		document.setContentType(request.getDocumentPrincipal().getContentType());
		document.setDossier(dossier);
	
		return documentRepository.save(document);
	}

	private String buildDocumentTitle(CreerDossierRequest request) {
		StringBuilder docName = new StringBuilder(request.getDossierID());
		
		switch(request.getDocumentPrincipal().getContentType()) {
		
		case MediaType.APPLICATION_PDF_VALUE :
			docName.append(PDF_EXT);
			break;
		
		default :
			LOGGER.error("Document MIME type non compatible : {}", request.getDocumentPrincipal().getContentType());
			docName.append(PDF_EXT);
			break;
		}
		
		return docName.toString();
	}

	@Override
	public AppUser getAppUserByAppli(ApplicationMetier appliName) throws ParapheurException {
		return appUserRepository.getByUserAppli(appliName).orElseThrow(ParapheurException::new);

	}
}
