package com.telino.iparapheur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.iparapheur.domain.Dossier;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

	public Optional<Dossier> findByDossierAppliId(final String dossierAppliId);
}
