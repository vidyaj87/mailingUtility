package com.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/* Copyright(c) Mobomo, All rights reserved. 
 * This is the configuration class file which contains the getUrl and closeUrl function.
 * @author      Vidya J
 * @version     1.0
 * @since       2013
 */
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

public class Configure {

	protected WebDriver driver;
	protected WebDriverBackedSelenium selenium;
	protected String url;

	/*
	 * Method Name : getCurrentDate() Description: This function returns time
	 * stamp in HH:mm:ss format.
	 */

	public static String getCurrentTimeStamp() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/*
	 * Method Name : getCurrentDate() Description: This function returns the
	 * date in the format yyyy-MM-dd
	 */
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// get current date time with Date()
		Date date = new Date();
		return (dateFormat.format(date));
	}

	/**
	 * Method Name: getProperty() Description: This function returns a property
	 * value when the property name is passed as parameter from
	 * config.properties file
	 */
	public static String getProperty(String propertyName) {
		Properties prop = new Properties();
		String propertyValue = null;
		try {
			prop.load(new FileInputStream("config.properties"));
			propertyValue = prop.getProperty(propertyName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return propertyValue;
	}

	/**
	 * Method Name: setProperty() Description: This function is used to set the
	 * property value for a property inside config.properties file
	 */
	public static void setProperty(String propertyName, String propertyValue)
			throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream("config.properties"));
		prop.setProperty(propertyName, propertyValue);
		prop.store(new FileOutputStream("config.properties"), null);

	}

	/*
	 * The function to get the url in the browser
	 */
	public void get(String url) {
		driver.get(url);
	}

	public void getUrl(String url) {
		selenium = new WebDriverBackedSelenium(driver, url);
		driver.get(url);
	}

	/*
	 * The function to generate time stamp till milli second count
	 */
	public String getTime() {
		Calendar cal = Calendar.getInstance();
		long time = cal.getTimeInMillis();
		String timeMilliSeconds;
		timeMilliSeconds = String.valueOf(time);
		return (timeMilliSeconds);
	}

	/*
	 * The function to wait the program till an element is loaded in the browser
	 */
	public boolean waitforElementLoad(String element, int second)
			throws InterruptedException {
		for (int i = 0;; i++) {
			if (i >= second)
				return false;
			try {
				if (selenium.isElementPresent(element))
					return true;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
	}

	/*
	 * Function to read from the excel sheet
	 */
	public String[][] getTableArray(String xlFilePath, String sheetName,
			String tableName) {
		String[][] tabArray = null;
		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			int startRow, startCol, endRow, endCol, ci, cj;
			Cell tableStart = sheet.findCell(tableName);
			startRow = tableStart.getRow();
			startCol = tableStart.getColumn();
			Cell tableEnd = sheet.findCell(tableName, startCol + 1,
					startRow + 1, 100, 64000, false);
			endRow = tableEnd.getRow();
			endCol = tableEnd.getColumn();
			tabArray = new String[endRow - startRow - 1][endCol - startCol - 1];
			ci = 0;
			for (int i = startRow + 1; i < endRow; i++, ci++) {
				cj = 0;
				for (int j = startCol + 1; j < endCol; j++, cj++) {
					tabArray[ci][cj] = sheet.getCell(j, i).getContents();
				}
			}
		} catch (Exception e) {
			System.out.println("error in getTableArray()");
			e.printStackTrace();
		}
		return (tabArray);

	}

	// To return the response status of the URL
	public int testStatus(String givenUrl) throws IOException {
		URL url = new URL(givenUrl);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		int statusCode = http.getResponseCode();
		return (statusCode);
		// System.out.println(statusCode);
	}

	// To compare the status of the response code
	public boolean compareStatus(int status) throws IOException {
		boolean value = false;
		if (status == 200) {
			value = true;
		}
		return value;
	}

	/*
	 * To make the webdriver wait for the element to load till a maximum of 10
	 * secs
	 */
	public void waitElement(String path) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.id(path)));
	}

	// To highlight the element
	public void highlightElement(WebDriver driver, WebElement element) {
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "color: yellow; border: 2px solid yellow;");
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "");
		}
	}

	@BeforeClass
	/*
	 * To define Firefox profile
	 */
	public void openUrl() {
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setAcceptUntrustedCertificates(true);
		driver = new FirefoxDriver(firefoxProfile);
		driver.manage().timeouts().implicitlyWait(300, TimeUnit.SECONDS);
		WebElement html = driver.findElement(By.tagName("html"));
		html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
		driver.manage().window().maximize();
	}

	@AfterClass
	/*
	 * The function to close the browser
	 */
	public void closeUrl() {
		driver.quit();
	}

	@AfterSuite
	/*
	 * To ensure that the driver has been closed or not
	 */
	public void tearDown() {
		boolean hasQuit = (driver.toString().contains("null")) ? true : false;
		if (hasQuit == false) {
			driver.quit();
		}
	}
}