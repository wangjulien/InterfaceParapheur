package com.telino.iparapheur.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Workflow {

	@Id
	private Long workflowNeogedId;

	private int workflowId;
	
	@ManyToOne
	@JoinColumn(name = "app_user_id")
	private AppUser appUser;

	@Enumerated(EnumType.STRING)
	private WorkflowStatus workflowStatus;

	@ManyToOne
	@JoinColumn(name = "dossier_id")
	private Dossier dossier;

	public Workflow() {
		super();
		this.workflowStatus = WorkflowStatus.INIT;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public Long getWorkflowNeogedId() {
		return workflowNeogedId;
	}

	public void setWorkflowNeogedId(Long workflowNeogedId) {
		this.workflowNeogedId = workflowNeogedId;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public WorkflowStatus getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(WorkflowStatus workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

}
