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
public class AccesClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccesClient.class);

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public String call(String param1, String param2, String param3, String param4, int index) throws ParapheurException {

		String message = "<cir:getAccessList"
				+ " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:cir=\"Ciril:Ciril:AccessInfo\">"
				+ "<param-1 xsi:type=\"xsd:string\">" + param1 + "</param-1>" 
				+ "<param-2 xsi:type=\"xsd:string\">" + param2 + "</param-2>"
				+ "<param-3 xsi:type=\"xsd:string\"></param-3>"
				+ "<param-4 xsi:type=\"xsd:string\">" + param4 + "</param-4>"
				+ "</cir:getAccessList>";
		StreamSource source = new StreamSource(new StringReader(message));

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			webServiceTemplate.sendSourceAndReceiveToResult(source, new StreamResult(outputStream));
			
			try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
				return getURL(is, index);
			} catch (IOException e) {
				LOGGER.debug(e.getCause().toString());
				throw new ParapheurException(e);
			}
			
		} catch (XmlMappingException | ParserConfigurationException | SAXException | IOException e) {
			LOGGER.debug(e.getCause().toString());
			throw new ParapheurException(e);
		}
	}

	private String getURL(InputStream is, int index) throws ParserConfigurationException, SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		List<String> listUrl = new ArrayList<String>();
		DefaultHandler handler = new DefaultHandler() {
			boolean burl = false;

			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				//LOGGER.debug("Start Element :" + qName);
				if (qName.equalsIgnoreCase("URL")) {
					burl = true;
				}
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				//LOGGER.debug("End Element :" + qName);
			}

			public void characters(char ch[], int start, int length) throws SAXException {
				if (burl) {
					listUrl.add(new String(ch, start, length));
					burl = false;
				}
			}
		};

		try {
			saxParser.parse(is, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listUrl.get(index);

	}
}
