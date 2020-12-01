package Unicam.SPM2020_FMS;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class SeleniumInformationManagement {
	
	static String projectPath;
	static String pathToDriver;
	static WebDriver driver;
	static String runningOS;
	static String URLbase;
	static String user;
	static String password;
	static String newPhoneNumber;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		//Checking running OS
		runningOS = (System.getProperty("os.name"));
		
		//Setting up system properties
		projectPath = System.getProperty("user.dir");
		URLbase = "http://localhost:8080/SPM2020-FMS/";
		
		//Reading data from a configuration file
		try (InputStream input = new FileInputStream( projectPath+"/src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            
            if (runningOS.equals("Linux")) {
            	pathToDriver = prop.getProperty("pathToLinuxDriver");
            }
            else if (runningOS.equals("Windows")) {
            	pathToDriver = prop.getProperty("pathToWindowsDriver");
            }
            
            user=prop.getProperty("newUserEmail");
		    password=prop.getProperty("newUserPassword");
		    newPhoneNumber=prop.getProperty("newPhoneNumber");
		} catch (IOException ex) {
            ex.printStackTrace();
		}	       
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Thread.sleep(5000);  //Just for showing purpose
		driver.close();
		driver.quit();
	}

	@BeforeEach
	void setUp() throws Exception {
		//Setting up WebDriver options
		System.setProperty("webdriver.chrome.driver", projectPath+pathToDriver);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--no-sandbox");
		
		//Remove comment prefix on the next line if you want to run test in headless mode
		//options.addArguments("--headless");
		
		driver = new ChromeDriver(options);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	// Waits are properly managed with the WebDriverWait class
	// Every sleep in the following code can be easily removed without compromising the test
	// Sleeps are there just for showing purpose
	@DisplayName("Check whether changing phone number succeeds")
	void checkChangingOfPhoneNumber() throws InterruptedException {
		//Logging in
		driver.get(URLbase);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Assert.assertEquals("Login", driver.getTitle());
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
		element.sendKeys(user);
		Thread.sleep(1500);  //Just for showing purpose
		driver.findElement(By.id("password")).sendKeys(password);
		Thread.sleep(1500);  //Just for showing purpose
		driver.findElement(By.id("login")).click();
		Thread.sleep(1500);  //Just for showing purpose
		
		//Waiting for the Welcome Page and requesting the Information Management Page
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='profile']"))).click();
				
		//Updating phone number
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone")));
		Thread.sleep(1500);  //Just for showing purpose
		element.clear();
		Thread.sleep(1500);  //Just for showing purpose
		element.sendKeys(newPhoneNumber);
		Thread.sleep(1500);  //Just for showing purpose
		driver.findElement(By.id("register")).click();
		
		//Reloading the page and verifying the change
		Thread.sleep(1500);  //Just for showing purpose
		driver.get(URLbase+"profile");
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone")));
		Assert.assertEquals(newPhoneNumber, element.getAttribute("value"));
	}

}
