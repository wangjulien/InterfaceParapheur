package com.telino.iparapheur.civil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Base64;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.telino.iparapheur.domain.Document;
import com.telino.iparapheur.utils.ParapheurException;

@Component
public class ImportPjMouvementClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportPjMouvementClient.class);

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public String call(String acces, Document document) throws ParapheurException {

		String message = "<cir:ImportPjMouvement"
				+ " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" 
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:cir=\"Ciril:Finances:importFinances\">"
				+ " <acces xsi:type=\"xsd:string\">" + acces + "</acces>"
				+ " <pjMouvement xsi:type=\"cir:pjMouvement\">"
				+ "   <typeMouvement xsi:type=\"xsd:string\">BDC</typeMouvement>"
				+ "   <cle1Mouvement xsi:type=\"xsd:string\">" + document.getDocumentAppliId() + "</cle1Mouvement>"
				+ "   <budgetMouvement xsi:type=\"xsd:string\">01</budgetMouvement>"
				+ "   <exerciceMouvement xsi:type=\"xsd:string\">2017</exerciceMouvement>"
				+ "   <pieceJustificative xsi:type=\"cir:PieceJustificative\">"
				+ "     <nomPiece xsi:type=\"xsd:string\">Bon de commande " + document.getDocumentAppliId() + "</nomPiece>"
				+ "     <nomFichier xsi:type=\"xsd:string\">" + document.getDocumentTitle() + "</nomFichier>"
				+ "     <typeMime xsi:type=\"xsd:string\">application/pdf</typeMime>\r\n"
				+ "     <fichierPJ xsi:type=\"xsd:base64Binary\">" + Base64.getEncoder().encodeToString(document.getDocumentContent()) + "</fichierPJ>"
				+ "   </pieceJustificative>\r\n"
				+ " </pjMouvement>\r\n"
				+ " </cir:ImportPjMouvement>";
				
		StreamSource source = new StreamSource(new StringReader(message));
		
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			webServiceTemplate.sendSourceAndReceiveToResult("https://gpseoprod.ciril.net/cgi-bin/ws.exe/cgi-bin/fin_import_finances.exe?ws",
					source, new StreamResult(outputStream));
			
			try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
				return "OK";
			} catch (IOException e) {
				LOGGER.debug(e.getCause().toString());
				throw new ParapheurException(e);
			}
			
		} catch (IOException e) {
			LOGGER.debug(e.getCause().toString());
			throw new ParapheurException(e);
		}
	}

}
