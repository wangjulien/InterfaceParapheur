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
public class GetTaskClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetTaskClient.class);

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public List<String> call(String acces) throws ParapheurException {

		String message = "<cir:getTaches soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cir=\"Ciril:Ciril:PanierTache\">"
				+ "<acces xsi:type=\"xsd:string\">" + acces + "</acces>"
				+ "</cir:getTaches>";
		StreamSource source = new StreamSource(new StringReader(message));

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			webServiceTemplate.sendSourceAndReceiveToResult("https://gpseoprod.ciril.net/cgi-bin/ws.exe/cgi-bin/ciril_panier_tache.exe?ws",
					source, new StreamResult(outputStream));
			
			try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
				return getTaches(is);
			} catch (IOException e) {
				LOGGER.debug(e.getCause().toString());
				throw new ParapheurException(e);
			}
			
		} catch (XmlMappingException | ParserConfigurationException | SAXException | IOException e) {
			LOGGER.debug(e.getCause().toString());
			throw new ParapheurException(e);
		}
	}

	private List<String> getTaches(InputStream is) throws ParserConfigurationException, SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		List<String> listNumTache = new ArrayList<String>();
		DefaultHandler handler = new DefaultHandler() {
			boolean bnumTache = false;

			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				//LOGGER.debug("Start Element :" + qName);
				if (qName.equalsIgnoreCase("numTache")) {
					bnumTache = true;
				}
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				//LOGGER.debug("End Element :" + qName);
			}

			public void characters(char ch[], int start, int length) throws SAXException {
				if (bnumTache) {
					listNumTache.add(new String(ch, start, length));
					bnumTache = false;
				}
			}
		};

		try {
			saxParser.parse(is, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listNumTache;

	}

}
