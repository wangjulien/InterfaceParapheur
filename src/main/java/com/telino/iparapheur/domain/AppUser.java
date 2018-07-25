package com.telino.iparapheur.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.telino.iparapheur.utils.InterfaceProtocol.ApplicationMetier;

@Entity
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;
	
	@Enumerated(EnumType.STRING)
	private ApplicationMetier userAppli;

	private String userAppliId;

	private String userLogin;

	private String userPassword;

	private String userMail;
	

	public AppUser() {
		super();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public ApplicationMetier getUserAppli() {
		return userAppli;
	}

	public void setUserAppli(ApplicationMetier userAppli) {
		this.userAppli = userAppli;
	}

	public String getUserAppliId() {
		return userAppliId;
	}

	public void setUserAppliId(String userAppliId) {
		this.userAppliId = userAppliId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}
}
