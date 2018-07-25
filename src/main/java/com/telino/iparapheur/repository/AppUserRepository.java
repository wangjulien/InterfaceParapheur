package com.telino.iparapheur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.iparapheur.domain.AppUser;
import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	public Optional<AppUser> getByUserAppli(final ApplicationMetier appliName);

	public Optional<AppUser> findByUserLogin(final String userLogin);

	public List<AppUser> findAllByUserAppli(ApplicationMetier appMetier);

}
