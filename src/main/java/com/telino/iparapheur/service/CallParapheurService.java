package com.telino.iparapheur.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.domain.Workflow;
import com.telino.iparapheur.domain.WorkflowStatus;
import com.telino.iparapheur.repository.AppUserRepository;
import com.telino.iparapheur.repository.WorkflowRepository;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.InterfaceProtocol.ComObjectKey;
import com.telino.iparapheur.utils.InterfaceProtocol.ReturnCode;
import com.telino.iparapheur.utils.InterfaceProtocol.WorkflowType;
import com.telino.iparapheur.utils.ParapheurException;
import com.telino.iparapheur.utils.ServletCaller;

@Service
public class CallParapheurService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallParapheurService.class);

	@Value("${meta-parapheur.servlet-url}")
	private String servletUrl;

	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private ServletCaller servletCaller;

	public CallParapheurService() {
		super();
	}

	public Workflow callParapheur(final Document document, final AppUser user) throws ParapheurException {

		Workflow workflow = new Workflow();
		workflow.setDossier(document.getDossier());
		workflow.setAppUser(user);
		workflow.setWorkflowId(getWorkflowTypeId(user));

		// Appele servlet parapheur et recupere retour
		Map<String, String> result = null;
		try {
			result = servletCaller.call(buildPayload(workflow, document, user), servletUrl);
		} catch (Exception e) {
			LOGGER.debug("Can not research servlet : {} ", servletUrl, e.getCause());
			throw new ParapheurException(e);
		}

		Objects.requireNonNull(result, "Servlet indisponible :" + servletUrl);

		LOGGER.debug("Parapheur's return info : {} ", result);

		// Si OK
		if (ReturnCode.OK.toString().equals(result.get(ComObjectKey.RETURN_CODE.toString()))) {

			workflow.setWorkflowStatus(WorkflowStatus.INIT);
			workflow.setWorkflowNeogedId(Long.valueOf(result.get(ComObjectKey.WORKFLOW_INS_ID.toString())));

			// Persister workflow
			return workflowRepository.save(workflow);

		} else { // Sinon, monte l'erreur
			String messageErreur = result.get(ComObjectKey.ERROR_MESSAGE.toString());
			throw new ParapheurException(messageErreur);
		}

	}

	private int getWorkflowTypeId(final AppUser user) {

		switch (user.getUserAppli()) {
		case CIVIL:
			if (user.getUserLogin().equals("PARAPHEUR")) {
				return 12;
			} else {
				return 13;
			}
		case OXALIS:
			return WorkflowType.OXALYS.getVal();
		case NEOGED:
			return WorkflowType.NEOGED_TEST.getVal();

		default:
			LOGGER.error("Can not resolve workflow type by {} ", user.getUserAppli().toString());
			return -1;
		}
	}

	private List<Map<String, Object>> buildPayload(final Workflow workflow, final Document document,
			final AppUser user) {
		
		AppUser admin = appUserRepository.getByUserAppli(ApplicationMetier.NEOGED).get();
		
		Map<String, Object> map = new HashMap<>();

		map.put("nomBase", "TELINO");
		map.put("user", admin.getUserLogin());
		map.put("password", admin.getUserPassword());
		map.put("encryptedPassword",
				String.valueOf((admin.getUserLogin() + admin.getUserPassword() + "MailObserver1").hashCode()));
		map.put("elasticType", "documents");
		map.put("secuUsers", admin.getUserLogin());
		map.put("mailOwner", admin.getUserMail()); // emetteur du workflow
		map.put("mailid", admin.getUserMail()); // emetteur du workflow
		map.put("elasticContentType", document.getContentType());
		map.put("startWorkflow", true);
		map.put("workflowid", workflow.getWorkflowId()); // id du workflow
		map.put("elasticDocType", "document"); // Type de document
		// map.put("elasticDocComment", "<Type=Demabox><Catégorie=Facture>"); à préciser
		// si on veut des métadonnées - doit être cohérent avec elasticDocType
		map.put("domaineOwner", "*"); // domaine de départ
		map.put("elasticDocName", document.getDocumentTitle());
		map.put("fileContent", document.getDocumentContent());
		map.put("elasticTaille", String.valueOf(document.getDocumentContent().length));

		map.put("elasticCommand", "put()");
		map.put("elasticRequest", "put()");
		map.put("secuLevel", 9);

		List<Map<String, Object>> lmap = new LinkedList<>();

		lmap.add(map);

		LOGGER.debug("Input call map : {} ", lmap);

		return lmap;
	}
}
