package com.telino.iparapheur.civil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class AccesMarshaller extends Jaxb2Marshaller {

	public AccesMarshaller() {
		String[] pckgs = { "ciril.ciril.accessinfo" };
		setPackagesToScan(pckgs);
		Map<String, Object> props = new HashMap<String, Object>();
        props.put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
        setMarshallerProperties(props);
	}
	
	/**
	 * Generic unmarshaller
	 * @param 	resource	Generic Spring resource
	 * @return 	Object
	 * @throws IOException 
	 */
	public Object unmarshall(Resource resource) throws IOException {
		Object unmarshalledObject = null;
        StreamSource source = new StreamSource();
        source.setInputStream(resource.getInputStream());
		unmarshalledObject = unmarshal(source);
		return unmarshalledObject;
	}

	
	/**
	 * Generic marshaller
	 * @param 	obj Generic Object
	 * @return 	xml
	 */
	public String marshall(Object obj) {
		java.io.StringWriter sw = new StringWriter();
		marshal(obj, new StreamResult(sw));
		return sw.toString();
	}
	
}
