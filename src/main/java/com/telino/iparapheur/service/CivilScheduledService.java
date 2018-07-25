package com.telino.iparapheur.service;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.telino.iparapheur.civil.AccesClient;
import com.telino.iparapheur.civil.Commande;
import com.telino.iparapheur.civil.GetBonsCommandes;
import com.telino.iparapheur.civil.GetTaskClient;
import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.domain.Dossier;
import com.telino.iparapheur.repository.AppUserRepository;
import com.telino.iparapheur.repository.DocumentRepository;
import com.telino.iparapheur.repository.DossierRepository;
import com.telino.iparapheur.utils.InterfaceProtocol.AppUserAccesIndex;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.ParapheurException;

@Service
public class CivilScheduledService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CivilScheduledService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private DossierRepository dossierRepository;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private AccesClient accesClient;
	
	@Autowired
	private GetBonsCommandes getBC;
	
	@Autowired
	private GetTaskClient getTask;
	
	@Autowired
	private CallParapheurService callParapheurService;

	@Scheduled(cron = "${client.call-rate}")
	public void callSoapWsAndLaunchWorkflow() {

		// Recupere all the Civil User
		List<AppUser> civilUsers = appUserRepository.findAllByUserAppli(ApplicationMetier.CIVIL);

		for (AppUser user : civilUsers) {
			LOGGER.debug("Utilisateur {} de {}", user.getUserLogin(), user.getUserAppli());
			
			try {

				// Appel Soap AccessInfo et recupere URL
				String accessUrl = accesClient.call(user.getUserLogin(), user.getUserPassword(), "",
						InetAddress.getLocalHost().getHostAddress(), AppUserAccesIndex.valueOf(user.getUserLogin()).getIndex());
				LOGGER.debug("Access URL : {}", accessUrl);

				List<String> listTask = getTask.call(accessUrl);
				
				List<Commande> listCommandes = getBC.call(accessUrl, user.getUserLogin());
				List<Document> listDoc = new ArrayList<Document>();
				for (Commande c : listCommandes) {
					Document doc = new Document();
					doc.setDocumentAppliId(c.getNumeroCommande());
					
					File file = new File("BC" + c.getNumeroCommande()+".pdf");
					Authenticator.setDefault(new CustomAuthenticator());
					FileUtils.copyURLToFile(new URL("https://gpseoprod.ciril.net/"+c.getUrlFichier()), file);
					Path path = Paths.get(file.getAbsolutePath());
					doc.setContentType("application/pdf");
					doc.setDocumentContent(Files.readAllBytes(path));
					doc.setDocumentTitle(file.getName());
					listDoc.add(doc);
				}
				
				// Recupere les Task (Dossier et Document) a partir Url 
				Map<String, List<Document>> dosDocMap = new HashMap<>();
				
//				// TEST DATA
//				Document txt = new Document();
//				txt.setContentType("application/pdf");
//				txt.setDocumentContent(new byte[0]);
				for (String taskId : listTask) {
					dosDocMap.put(taskId,listDoc);
				}
				
				//
				// end
				//
				

				// Filtrer les Task dont workflow sont lancer
				List<Document> docToUpdate = new ArrayList<>();
				dosDocMap.entrySet().stream()
						.filter(e -> !dossierRepository.findByDossierAppliId(e.getKey()).isPresent())
						.flatMap(e -> {
							Dossier newDossier = new Dossier();
							newDossier.setDossierAppliText(ApplicationMetier.CIVIL.toString());
							newDossier.setDossierAppliId(e.getKey());
							newDossier.setNbDoc(e.getValue().size());
							
							dossierRepository.save(newDossier);
							if (e.getValue().size()==1) {
								for (Document doc : e.getValue()) {
									doc.setDossier(newDossier);
									docToUpdate.add(doc);
								}
							} else {
								for (Document doc : e.getValue()) {
									if (!(documentRepository.findByDocumentAppliId(doc.getDocumentAppliId()).size()>0)) {
										doc.setDossier(newDossier);
										docToUpdate.add(doc);
									}
								}
							}
							
							return documentRepository.saveAll(e.getValue()).stream();
						})
						.collect(Collectors.toList());

				// Lancer Workflow si c'est pas lancer
				for (Document doc : docToUpdate)
					callParapheurService.callParapheur(doc, user);
				

			} catch (UnknownHostException e) {
				LOGGER.error("Localhost IP inconnu ", e);
			} catch (ParapheurException e) {
				LOGGER.error("Erreur lors de appel client Soap WS : ", e);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public static class CustomAuthenticator extends Authenticator {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("gpseo", "a_definir".toCharArray());
		}
	}
}
