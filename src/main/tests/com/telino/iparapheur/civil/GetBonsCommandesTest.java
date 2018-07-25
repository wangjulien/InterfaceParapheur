package com.telino.iparapheur.civil;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.telino.iparapheur.config.WebServiceConfig;
import com.telino.iparapheur.utils.ParapheurException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetBonsCommandesTest {
	@Autowired
	AccesClient acces;
	
	@Autowired
	GetBonsCommandes getBC;
	
	@Autowired
	GetTaskClient getTask;

	@Test
	public void test() throws ParapheurException, MalformedURLException, IOException {
		
		String urlAcces = acces.call("PARAPHEUR", "PARAPHEUR", "", "192.164.0.14",2);
		
		List<Commande> listCommande = getBC.call(urlAcces, "PARAPHEUR");
		for (Commande c : listCommande) {
			System.out.println("Commande " + c.getNumeroCommande() + " : " + " "+c.getIdUnique()+" "+c.getNomFichier()+" "+c.getUrlFichier());
			File file = new File("BC" + c.getNumeroCommande()+".pdf");
			 Authenticator.setDefault(new CustomAuthenticator());
			FileUtils.copyURLToFile(new URL("https://gpseoprod.ciril.net/"+c.getUrlFichier()), file);
			if (file!= null ) System.out.println(file.getName());
		}
		
		List<String> listTaches = getTask.call(urlAcces);
		System.out.println(listTaches.toString());
	}
	
	public static class CustomAuthenticator extends Authenticator {  
	    protected PasswordAuthentication getPasswordAuthentication() {  
	        String username = "gpseo";  
	            String password = "a_definir";  
	            return new PasswordAuthentication(username, password.toCharArray());  
	        }
	}

}
