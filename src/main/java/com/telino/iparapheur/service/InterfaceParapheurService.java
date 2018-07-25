package com.telino.iparapheur.service;

import org.adullact.spring_ws.iparapheur._1.CreerDossierRequest;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;
import com.telino.iparapheur.utils.ParapheurException;


public interface InterfaceParapheurService {

	public Document saveDocument(final CreerDossierRequest request);

	public AppUser getAppUserByAppli(ApplicationMetier neoged) throws ParapheurException;

}
