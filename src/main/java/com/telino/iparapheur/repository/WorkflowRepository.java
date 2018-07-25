package com.telino.iparapheur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.iparapheur.domain.Workflow;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

}
