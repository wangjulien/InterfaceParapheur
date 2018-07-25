package com.telino.iparapheur.web;

import org.adullact.spring_ws.iparapheur._1.CreerDossierRequest;
import org.adullact.spring_ws.iparapheur._1.CreerDossierResponse;
import org.adullact.spring_ws.iparapheur._1.MessageRetour;
import org.adullact.spring_ws.iparapheur._1.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.service.CallParapheurService;
import com.telino.iparapheur.service.InterfaceParapheurService;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.InterfaceProtocol.ReturnCode;

/**
 * 
 * Point d'entre de SOAP WS
 * 
 * @author jwang
 *
 */
@Endpoint
public class InterfaceParapheurServiceEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceParapheurServiceEndpoint.class);

	private static final String NAMESPACE_URI = "http://www.adullact.org/spring-ws/iparapheur/1.0";

	@Autowired
	private InterfaceParapheurService interfaceParapheurService;

	@Autowired
	private CallParapheurService callParapheurService;

	/** JAXB object factory */
	private final ObjectFactory objectFactory = new ObjectFactory();

	public InterfaceParapheurServiceEndpoint() {
		super();
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreerDossierRequest")
	@ResponsePayload
	public CreerDossierResponse creerDossier(@RequestPayload CreerDossierRequest request) {

		LOGGER.info("Received request = {}", request.getDossierID());

		CreerDossierResponse response = objectFactory.createCreerDossierResponse();
		MessageRetour returnMsg = new MessageRetour();

		try {
			// Enregistre le document dans DB
			Document document = interfaceParapheurService.saveDocument(request);

			// TODO : Comment valoriser AppUser ?
			AppUser user = interfaceParapheurService.getAppUserByAppli(ApplicationMetier.OXALIS);

			// Appel le parapheur pour lancer un workflow
			callParapheurService.callParapheur(document, user);

			returnMsg.setCodeRetour(ReturnCode.OK.toString());

		} catch (Exception e) {
			returnMsg.setCodeRetour(ReturnCode.KO.toString());
			returnMsg.setMessage(e.getMessage());
			LOGGER.error("Erreur lors de enregistre document et lancer workflow", e);
		}

		response.setMessageRetour(returnMsg);
		return response;
	}
}
