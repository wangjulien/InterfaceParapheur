package com.telino.iparapheur.civil;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;


@Configuration
public class AccesClientConfig {

	@Value("${client.civil-uri}")
	private String defaultUri;

	@Value("${client.civil-login}")
	private String login;

	@Value("${client.civil-password}")
	private String password;

	@Bean
	AccesMarshaller AccesMarshaller() {
		AccesMarshaller accesMarshaller = new AccesMarshaller();
		return accesMarshaller;
	}

	@Bean
	public WebServiceTemplate webServiceTemplate() {
		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
		messageFactory.setSoapVersion(SoapVersion.SOAP_11);
		messageFactory.afterPropertiesSet();
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(AccesMarshaller());
		webServiceTemplate.setMessageFactory(messageFactory);
		webServiceTemplate.setMessageSender(httpComponentsMessageSender());
		webServiceTemplate.setDefaultUri(defaultUri);
		return webServiceTemplate;
	}

	@Bean
	public HttpComponentsMessageSender httpComponentsMessageSender() {
		HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
		// set the basic authorization credentials
		httpComponentsMessageSender.setCredentials(usernamePasswordCredentials());

		return httpComponentsMessageSender;
	}

	@Bean
	public UsernamePasswordCredentials usernamePasswordCredentials() {
		// pass the user name and password to be used
		return new UsernamePasswordCredentials(login, password);
	}

}
