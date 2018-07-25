package com.telino.iparapheur.civil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.telino.iparapheur.utils.ParapheurException;

@Component
public class GetBonsCommandes {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetBonsCommandes.class);

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public List<Commande> call(String acces, String userCivilLogin) throws ParapheurException {
		
		String requete = "f_getbdcaviser('"+userCivilLogin+"',aecnumcom) = 1 and aecetat='A' and aecorigin='IN' and aecvisa = 'N' and aecnumexe ='2017'";

		String message = "<cir:getBonsCommandes soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cir=\"Ciril:Finances:ExportFinances\">"
				+ "<acces xsi:type=\"xsd:string\">" + acces + "</acces>"
				+ "<requete xsi:type=\"xsd:string\">" + requete + "</requete>"
				+ "</cir:getBonsCommandes>";
		StreamSource source = new StreamSource(new StringReader(message));

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			webServiceTemplate.sendSourceAndReceiveToResult("https://gpseoprod.ciril.net/cgi-bin/ws.exe/cgi-bin/fin_export_finances.exe?ws",
					source, new StreamResult(outputStream));
			
			try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
				return getCommandesAViser(is);
			} catch (IOException e) {
				LOGGER.debug(e.getCause().toString());
				throw new ParapheurException(e);
			}
			
		} catch (XmlMappingException | ParserConfigurationException | SAXException | IOException e) {
			LOGGER.debug(e.getCause().toString());
			throw new ParapheurException(e);
		}
	}

	private List<Commande> getCommandesAViser(InputStream is) throws ParserConfigurationException, SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		List<Commande> listCommande = new ArrayList<Commande>();
		DefaultHandler handler = new DefaultHandler() {
			boolean bnumeroCommande = false;
			boolean bligneCommande = false;
			boolean bitem = false;
			boolean burl = false;
			boolean bnomFichier = false;
			boolean bpiecesJustificatives = false;
			boolean bstart=false;
			Commande commande = new Commande();

			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				//LOGGER.debug("Start Element :" + qName);
				if (qName.equalsIgnoreCase("numTache")) {
					bnumeroCommande = true;
				}
				if (qName.equalsIgnoreCase("numeroCommande")) {
					bnumeroCommande = true;
				}

				if (qName.equalsIgnoreCase("ligneCommande")) {
					bligneCommande = true;
				}

				if (qName.equalsIgnoreCase("piecesJustificatives")) {
					bpiecesJustificatives = true;
				}
				
				if (qName.equalsIgnoreCase("item")) {
					bitem = true;
				}
				
				if (qName.equalsIgnoreCase("nomFichier")) {
					bnomFichier = true;
				}
				
				if (qName.equalsIgnoreCase("url")) {
					burl = true;
				}
				
				if (qName.equalsIgnoreCase("tailleFichier")) {
					bstart = true;
				}
				
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				//LOGGER.debug("End Element :" + qName);
			}

			public void characters(char ch[], int start, int length) throws SAXException {
				if (bnumeroCommande) {
					commande.setNumeroCommande(new String(ch, start, length));
					bnumeroCommande = false;
				}
				
				if (burl) {
					commande.setUrlFichier(new String(ch, start, length));
					burl = false;
				}
				
				if (bnomFichier) {
					commande.setNomFichier(new String(ch, start, length));
					bnomFichier = false;
				}
				
				if (bligneCommande) {
					bligneCommande = false;
				}

				
				if (bpiecesJustificatives) {
					bpiecesJustificatives = false;
				}
				
				if (bitem && !bligneCommande && !bpiecesJustificatives && bstart ) {
					if (commande!= null && commande.getNumeroCommande()!= null) listCommande.add(commande);
					commande = new Commande();
					bitem = false;
					bstart = false;
				}
			}
		};

		try {
			saxParser.parse(is, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listCommande;

	}

}
