package com.telino.iparapheur.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.repository.AppUserRepository;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private boolean alreadySetup = false;

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (alreadySetup) return;

		AppUser neogedUser = new AppUser();
		neogedUser.setUserAppli(ApplicationMetier.NEOGED);
		neogedUser.setUserAppliId("julie");
		neogedUser.setUserLogin("julie");
		neogedUser.setUserPassword("julie");
		neogedUser.setUserMail("julie.maran@telino.com");

		createUserIfNotFound(neogedUser);
		
		AppUser parapheurUser = new AppUser();
		parapheurUser.setUserAppli(ApplicationMetier.CIVIL);
		parapheurUser.setUserAppliId("PARAPHEUR");
		parapheurUser.setUserLogin("PARAPHEUR");
		parapheurUser.setUserPassword("parapheur");
		parapheurUser.setUserMail("parapheur@civil.com");
		
		createUserIfNotFound(parapheurUser);
		
		AppUser oxalisUser = new AppUser();
		oxalisUser.setUserAppli(ApplicationMetier.OXALIS);
		oxalisUser.setUserAppliId("OXALIS");
		oxalisUser.setUserLogin("OXALIS");
		oxalisUser.setUserPassword("OXALIS");
		oxalisUser.setUserMail("oxalis@operis.com");
		
		createUserIfNotFound(oxalisUser);
		
		alreadySetup = true;
	}


	private AppUser createUserIfNotFound(AppUser newUser) {

		Optional<AppUser> optUser = appUserRepository.findByUserLogin(newUser.getUserLogin());

		return optUser.orElseGet(() -> appUserRepository.save(newUser));
	}
}