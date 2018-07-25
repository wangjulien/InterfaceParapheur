package com.telino.iparapheur.civil;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.telino.iparapheur.utils.ParapheurException;

import ciril.ciril.accessinfo.AccessPoint;
import ciril.ciril.accessinfo.ArrayOfAccessPoint;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccesClientTest {
	
	@Autowired
	private AccesClient accesClient;

	@Test
	public void test() {
		String url = null;
		try {
			 url = accesClient.call("PARAPHEUR", "PARAPHEUR", "", "192.168.4.9",2);
			System.out.println(url);
			
				
		} catch (ParapheurException e) {
			e.printStackTrace();
		}
		
		assertTrue(url.startsWith("/cgi-bin/web2java.exe?"));
	}

}
