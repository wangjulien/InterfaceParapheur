package com.telino.iparapheur.civil;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringSource;

@Component
public class SetActionTachesClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetActionTachesClient.class);
	
	@Value("${client.civil-tache-uri}")
	private String tacheUri;

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public void setActionTaches(final String acces, final String numTache, final String codAtt, final String valAtt) throws IOException {

		String message = "<cir:setActionTaches"
				+ " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:cir=\"Ciril:Ciril:PanierTache\">"
				+ "<acces xsi:type=\"xsd:string\">" + acces + "</acces>"
				+ "<numTache xsi:type=\"xsd:int\">"	+ numTache + "</numTache>"
				+ "<codAtt xsi:type=\"xsd:string\">" + codAtt + "</codAtt>"
				+ "<valAtt xsi:type=\"xsd:string\">" + valAtt + "</valAtt>"
				+ "</cir:setActionTaches>";
		StringSource source = new StringSource(message);
		
		try (StringWriter sw = new StringWriter()) {
			LOGGER.debug(message);
			webServiceTemplate.sendSourceAndReceiveToResult(tacheUri, source, new StreamResult(sw));
			
			LOGGER.info(sw.toString());
			
		}
	}

}
