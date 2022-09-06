package com.smoke.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.automation.controllers.BaseActions;
import com.automation.controllers.DriverFactory;
import com.automation.listeners.CustomListener;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExcelTestDataReader;
import com.automation.utils.RandomGenerator;
import com.smoke.mobile.RegistrationPage;
import com.smoke.mobile.WelcomePage;
@Listeners(CustomListener.class)
public class SignUpTest extends DriverFactory
{	

	@Test(dataProvider="getExcelTestData",description ="Verify the Sign up with newuser")
	public void createNewUser(HashMap<String, String> data) 
	{
		
			WelcomePage welcome =new WelcomePage(getDriver());
			welcome.gotoSignUp();
			RegistrationPage reg =new RegistrationPage(getDriver());
			reg.registerNewUser(data.get("Name"), RandomGenerator.GenerateRandomEMAILIDs(data.get("Email")), data.get("Password"), data.get("ConfirmPassword"));
			Assert.assertTrue(reg.verifyPuzzle(), "Verify solve puzzle page");
			Assert.assertEquals(false, reg.verifyPuzzle());
	}
	
	@DataProvider
	public Iterator<Object[]> getExcelTestData() 
	{
		String sheetname = this.getClass().getSimpleName();
		ExcelTestDataReader excelReader = new ExcelTestDataReader();
		LinkedList<Object[]> dataBeans = excelReader.getRowDataMap(PWD+ConfigReader.getValue("TestData"),sheetname);
		return dataBeans.iterator();
	}
}
