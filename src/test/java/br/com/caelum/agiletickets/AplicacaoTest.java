package br.com.caelum.agiletickets;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AplicacaoTest {

	static WebDriver browser;
	
	@BeforeClass
	public static void init(){
		browser = new FirefoxDriver();
		browser.get("https://login.yahoo.com/config/login_verify2?.intl=br&.src=ym");
	}
	
	@Test
	public void logarNoEmail() throws Exception {
		browser.findElement(By.id("username")).sendKeys("andersonlof");
		browser.findElement(By.id("passwd")).sendKeys("n4th4l14");
		browser.findElement(By.id("login_form")).submit();
		
		Thread.sleep(10000);
		
		WebElement elemento = browser.findElement(By.className("yuhead-name-greeting"));
		Assert.assertTrue(elemento.getText().contains("Oi"));
	}
	
	@AfterClass
	public static void dispose(){
		browser.quit();
	}
}
