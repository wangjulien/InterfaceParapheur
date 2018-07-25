package com.telino.iparapheur.web;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.telino.iparapheur.civil.AccesClient;
import com.telino.iparapheur.civil.ImportPjMouvementClient;
import com.telino.iparapheur.civil.SetActionTachesClient;
import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.domain.Dossier;
import com.telino.iparapheur.domain.Workflow;
import com.telino.iparapheur.domain.WorkflowStatus;
import com.telino.iparapheur.repository.DocumentRepository;
import com.telino.iparapheur.repository.WorkflowRepository;
import com.telino.iparapheur.utils.InterfaceProtocol.AppUserAccesIndex;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.ParapheurException;

@Controller
@RequestMapping(value = "/workflow")
public class InterfaceParapheurServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceParapheurServlet.class);

	private static final String CODATT = "CVsignat";

	@Autowired
	private WorkflowRepository workflowRepository;

	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private ImportPjMouvementClient importPJ;
	
	@Autowired
	private AccesClient accesClient;

	@Autowired
	private SetActionTachesClient setActionTachesClient;

	public InterfaceParapheurServlet() {
		super();
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> recieveWorkflowStatus(@RequestParam("workflowinstanceid") Long id,
			@RequestParam("statut") int statutCode) {
		LOGGER.info("Appel recu : Workflow instanceid={} and status={} ", id, statutCode);

		try {
			Workflow workflow = workflowRepository.findById(id).orElseThrow(ParapheurException::new);

			if (statutCode == WorkflowStatus.ACCORD.getStatusCode())
				workflow.setWorkflowStatus(WorkflowStatus.ACCORD);
			else
				workflow.setWorkflowStatus(WorkflowStatus.REFUS);

			workflowRepository.save(workflow);

			// Si workflow CIVL, appel setActionTaches
			if (ApplicationMetier.CIVIL.toString().equals(workflow.getDossier().getDossierAppliText())) {

				AppUser user = workflow.getAppUser();
				
				LOGGER.debug("AppUser {}", user);
				
				try {
					String acces = accesClient.call(user.getUserLogin(), user.getUserPassword(), "",
							InetAddress.getLocalHost().getHostAddress(),
							AppUserAccesIndex.valueOf(user.getUserLogin()).getIndex());

					setActionTachesClient.setActionTaches(acces, workflow.getDossier().getDossierAppliId(), CODATT,
							workflow.getWorkflowStatus().getCivilCode());
					
					Dossier dossier = workflow.getDossier();
					List<Document> doc = documentRepository.findByDossier(dossier);
					importPJ.call(acces, doc.get(0));

				} catch (ParapheurException | IOException e) {
					LOGGER.error("Erreur lors de soumettre a WS Civil 'setActionTaches' pour utilisateur {}",
							user.getUserAppliId());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			}
			return ResponseEntity.ok().build();

			
		} catch (ParapheurException e) {

			LOGGER.error("Workflow id={} n'est pas retrouve dans la base ", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
